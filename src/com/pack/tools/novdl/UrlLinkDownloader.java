package com.pack.tools.novdl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pack.tools.novdl.db.BookData;
import com.pack.tools.novdl.db.ChapterData;
import com.pack.tools.novdl.event.LinkDownloadEvent;
import com.pack.tools.novdl.io.FileManager;
import com.pack.tools.novdl.listener.LinkDownloadListener;
import com.pack.tools.novdl.util.HttpUrlUtilities;
import com.pack.tools.novdl.util.NameValue;

public class UrlLinkDownloader implements Runnable {

	private final HttpUrlUtilities utils = new HttpUrlUtilities();
	private final FileManager fileMgr = new FileManager();
	private final boolean skipDownload;
	private String bookName;
	private final String urlLink, chpId;
	private String bookUrl;
	private File file;
	private LinkDownloadListener listener;
	private BookData bookData;
	private ChapterData chData;

	public UrlLinkDownloader(String bookName, String urlLink, String chpId, String bookUrl, boolean skipDownload,
			LinkDownloadListener listener) {
		super();
		this.skipDownload = skipDownload;
		this.bookName = bookName;
		this.urlLink = urlLink;
		this.bookUrl = bookUrl;
		this.chpId = chpId;
		this.listener = listener;
	}

	public UrlLinkDownloader(BookData bookData, String chapterUrl, String chapterId, boolean skip) {
		this.bookData = bookData;
		this.urlLink = chapterUrl;
		this.chpId = chapterId;
		this.skipDownload = skip;
	}

	public boolean downloadFromUrl() {
		String htmlString = null;
		htmlString = utils.getUrlContents(urlLink);
		return downloadLinkContent(htmlString, bookName, chpId, false);
	}

	private String getData(String data) {
		final String reg = "<.*?>(.*?)</.*?>";
		final StringBuffer buffer = new StringBuffer();
		final Pattern p = Pattern.compile(reg);
		final Matcher m = p.matcher(data);
		while (m.find()) {
			buffer.append(m.group(1));
		}
		// if(buffer.toString().contains("&nbsp;")){
		// String str = buffer.toString().trim().replaceAll("&nbsp;", "");
		// return str;
		// }
		return buffer.toString();
	}

	private String extractTitle(String data) {
		final int end = data.indexOf("</p>");
		final StringBuffer buffer = new StringBuffer();
		if (end != -1 && end < 255) {
			final String dt = data.substring(0, end + "</p>".length());
			final String titl = getData(dt);
			if (titl != null && !titl.trim().equals("")) {
				buffer.append("<h4>" + titl + ".</h4><hr></hr>\n");
				buffer.append("\n" + data.substring(end + "</p>".length() + 1));
			} else {
				return extractTitle(data.substring(end + "</p>".length() + 1));
			}
		}
		return buffer.toString();
	}

	private boolean downloadLinkContent(String htmlString, String targetFileName, String targetFileId, boolean append) {
		if (skipDownload) {
			return true;
		}
		boolean result = false;
		if (htmlString != null && htmlString.trim().length() > 0) {
			// System.out.println(htmlString);
			final String open = "JavaScript!</noscript>";

			// Content between open and close tag.
			final String inside = ".*?";

			// Non capturing close tag.
			final String close = "<div class=\"row\">";

			// Final regex
			final String regex = open + inside + close;

			final String text = htmlString; // you string here
			String content = null;

			// Usage
			final Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(text);
			while (matcher.find()) {
				content = matcher.group().trim();
				final String newOpen = open.replace("\\", "");
				content = content.substring(newOpen.length(), content.length() - close.length());
				break;

			}

			if (content != null) {
				final BufferedReader reader = new BufferedReader(new StringReader(content));
				String input = null;
				int count = 0;
				final StringBuffer buffer = new StringBuffer();
				buffer.append("<HTML><Head>\n" + "<Title></Title>"
						+ "<link href=\"../Styles/Style0001.css\" rel=\"stylesheet\" type=\"text/css\"/>" + "</Head><body>\n");
				try {
					while ((input = reader.readLine()) != null) {
						if (input.trim().equals("")) {
							continue;
						}
						if (input.contains("�")) {
							input = input.replaceAll("�", "!");
						}
						if (count == 0) {
							if (input.contains("Chapter")) {
								if (input.length() > 255) {
									if (input.contains("[Previous Chapter]") && input.contains("[Next Chapter]")) {
										continue;
									}
									buffer.append(extractTitle(input));
								} else {
									buffer.append("<h4>" + input + "</h4><hr></hr>");
								}
							} else {
								String id = targetFileId;
								id = id.replace("_CH_", " Chapter ");
								buffer.append("<h4>" + "Chapter " + id + ".</h4><hr></hr>");
								buffer.append("\n" + input);
							}
						} else {
							if (input.length() < 150 && (input.toLowerCase().contains("translated by")
									|| input.toLowerCase().contains("edited by"))) {
							} else {
								buffer.append("\n" + input);
							}
						}
						// System.out.println(input);
						count++;
					}
					buffer.append("\n</body></HTML>");
				} catch (final IOException e) {
					e.printStackTrace();
				}
				if (count > 0) {
					file = this.fileMgr.writeToFile(targetFileName, targetFileId, buffer.toString());
					System.out.println(String.format("[INFO] Successfully downloaded content for [%s] with id [%s]",
							targetFileName, targetFileId));
					if (chData != null) {
						this.chData.setFile(file);
					}
					result = true;
				} else {
					System.err.println(String.format("[ERROR] Failed to downloaded content for [%s] with id [%s]", targetFileName,
							targetFileId));
				}

			} else {
				System.err.println(
						String.format("[ERROR] No Content available for [%s] wiht id [%s]", targetFileName, targetFileId));
				this.fileMgr.writeErrFile(targetFileName, targetFileId, htmlString, false);

			}
		}
		return result;
	}

	@Override
	public void run() {
		Thread.currentThread().setName(bookName);
		final LinkDownloadEvent event = new LinkDownloadEvent();
		event.setUrl(urlLink);
		event.setReferenceURL(bookUrl);
		event.setName(bookName);
		event.setUrlAndId(new NameValue(chpId, urlLink));
		if (bookData != null && bookData.isChapterDownloaded(urlLink)) {
			listener.onLinkDownloaded(event);
			return;
		}
		ChapterData chDat = null;// bookData.getChapterDetails(urlLink);
		if (chDat == null) {
			chDat = new ChapterData();
			chDat.setDownloadDate(new Date());
			chDat.setUrl(urlLink);
			chDat.setId(chpId);
			chDat.setPublishedDate(new Date());
		}
		this.chData = chDat;
		final boolean status = downloadFromUrl();
		event.addProperty("File", file);
		chDat.setDownloaded(status);
		event.setDownloaded(status);
		event.setEventObject(chDat);
		listener.onLinkDownloaded(event);
	}
}
