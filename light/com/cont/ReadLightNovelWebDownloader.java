package com.cont;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadLightNovelWebDownloader {
	private static Path userPrivate = Paths.get(System.getProperty("user.home")).resolve("wbnov");
	private Path path = userPrivate.resolve("downloads");
	private static Path pathFull = userPrivate.resolve("download_");
	private boolean download = true;

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	public static void main(String args[]) throws IOException {
		// String link =
		// "http://www.readlightnovel.com/tales-of-demons-and-gods";
		String link = "http://www.readlightnovel.com/dragon-marked-war-god";
		ReadLightNovelWebDownloader rld = new ReadLightNovelWebDownloader();
		// rld.doWork(link);
	}

	public static String getNameFromUrl(String urlLink) {
		int index = urlLink.lastIndexOf('/');
		if (index != -1) {
			String rawName = urlLink.substring(index + 1);
			if (rawName.length() < 25) {
				return rawName;
			}
			String[] tokens = rawName.split("-");
			StringBuffer name = new StringBuffer();
			for (String token : tokens) {
				char ch = token.charAt(0);
				int val = ch;
				if ((val > 47 && val < 58) || (val > 64 && val < 91)) {

				} else if (val > 96 || val < 123) {
					val -= 32;
				} else {
					val = 137;
				}
				name.append((char) val);
			}
			return name.toString();
		}
		return "NO_Name";
	}

	public void doWork(String link, List<UpdateData> list) {
		doWork(link, list, true);
	}

	public void doWork(String link, List<UpdateData> list, boolean download) {
		this.setDownload(download);
		DownloadedNovelManager.checkDir(userPrivate.toString());
		String path = "";
		String name = getNameFromUrl(link);
		String section = "";
		String volName = "V";
		if (name != null && !name.equals("")) {
			path = this.path.toString() + File.separator + name + File.separator;
		}
		int volCount = 0;
		DownloadedNovelManager mgr = new DownloadedNovelManager();
		NovelData data = mgr.getNovelData(name, link);
		if (data == null) {
			data = new NovelData();
			System.out.println("Assuming new Novel..");
		}
		data.setUrlLink(link);
		data.setName(name);
		
		if (list != null) {
			if (!list.isEmpty()) {
				for (UpdateData update : list) {
					download(download, path, name, data, update.getLinkUrl(), update.getId());
				}
			}
		} else {
			System.setProperty("http.agent", "Chrome");
			String htmlString = getUrlContents(link, true);
			Document document = Jsoup.parse(htmlString);
			Elements elements = document.getElementsByClass("tab-content");
			for (Element element : elements) {
				Elements pEleemnts = element.getElementsByClass("tab-pane");
				if (pEleemnts != null && pEleemnts.size() > 0) {
					String sec = pEleemnts.get(0).attr("id");
					if (sec != null && !section.equals(sec)) {
						section = sec;
						int sep = section.indexOf('-');
						if (sep != -1) {
							section = section.substring(0, sep);
						}
						volCount++;
					}

					Elements aElements = element.getElementsByTag("a");
					// int count = 1;
					for (Element aElement : aElements) {
						String contentLink = aElement.attr("href");
						String value = aElement.text();
						String id = volName + volCount + "_" + makeId(value);
						System.out.println(String.format("Found download link for [%s], [%s],[%s] at link [%s]", name,
								volName + volCount, value, contentLink));
						data.addChapter(section, value);
						download(download, path, name, data, contentLink, id);
						// count++;
					}
				}
			}
		}
		mgr.putNoveldata(data);
		System.out.println("Completed under path:" + path);

	}

	private void download(boolean download, String path, String name, NovelData data, String contentLink, String id) {
		File file = new File(path + name + "_" + id + ".xhtml");
		String tmpLink = contentLink;
		if (data.isLinkDownloaded(contentLink) || data.isLinkDownloaded(tmpLink.replace("https:", "http")) || file.exists()) {
			System.out.println("You have already downloaded the link");
			copyToPool(file.getParentFile(), file);
			data.addDownloadedLink(contentLink);
		} else {
			if (downloadLinkContent(getUrlContents(contentLink, download), path, name, id, download)) {
				data.addDownloadedLink(contentLink);
			}
		}
	}

	public static String makeId(String value) {
		// Pattern regex = Pattern.compile("[^A-Za-z0-9]");
		String[] values = value.split("[^A-Za-z0-9]");
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			String val = values[i];
			buffer.append(val);
			if (i != (values.length - 1)) {
				buffer.append('_');
			}
		}
		return buffer.toString();
	}

	private static boolean writeToFile(String path, String name, String id, String content) {
		try {
			File file = new File(path + name + "_" + id + ".xhtml");
			File parent = file.getParentFile();
			if (!parent.exists()) {
				boolean result = parent.mkdirs();
				if (!result) {
					return false;
				}
			}
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
			copyToPool(parent, file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean copyToPool(File sourcePath, File fileToCopy) {
		if (fileToCopy != null && fileToCopy.exists()) {
			try {
				Path dest = pathFull.resolve(sourcePath.getName());
				File destDir = dest.toFile();
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				dest = Files.copy(FileSystems.getDefault().getPath(fileToCopy.toString()), dest.resolve(fileToCopy.getName()),
						StandardCopyOption.REPLACE_EXISTING);
				if (dest != null)
					return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String getUrlContents(String theUrl, boolean download) {
		if (!download) {
			return "";
		}
		StringBuilder content = new StringBuilder();

		// many of these calls can throw exceptions, so i've just
		// wrapped them all in one try/catch statement.
		try {
			// create a url object
			URL url = new URL(theUrl);

			// create a urlconnection object
			URLConnection urlConnection = url.openConnection();

			HttpURLConnection urlConn = (HttpURLConnection) urlConnection;
			urlConn.setRequestProperty("Accept-Charset", "UTF-8");
			InputStream urlInputStream = null;
			boolean redirect = false;
			int status = urlConn.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}
			if (redirect) {
				System.out.println("RedirecteURL :" + urlConn.getHeaderField("Location"));
				String redirectURL= urlConn.getHeaderField("Location");
				if(redirectURL.contains("///")){
				    redirectURL = redirectURL.replace("///", "//");
				}
				urlConn = (HttpURLConnection) new URL(redirectURL).openConnection();
			}
			if (urlConn.getContent() instanceof InputStream) {
				urlInputStream = (InputStream) urlConn.getContent();
			} else {
				urlInputStream = urlConn.getInputStream();
			}

			if (urlInputStream != null) {
				BufferedInputStream stream = new BufferedInputStream(urlInputStream);
				byte[] bytes = new byte[8 * 1024];
				int lenght = -1;
				while ((lenght = stream.read(bytes)) != -1) {
					content.append(new String(bytes, 0, lenght));
				}
				stream.close();
			}
			/*
			 * // wrap the urlconnection in a bufferedreader BufferedReader
			 * bufferedReader = new BufferedReader(new
			 * InputStreamReader(urlConnection.getInputStream())); String line;
			 * // read from the urlconnection via the bufferedreader while
			 * ((line = bufferedReader.readLine()) != null) {
			 * content.append(line + "\n"); } bufferedReader.close();
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}

	public static void downloadResource(String sourceUrl, String targetDirectory, String resName)
			throws MalformedURLException, IOException, FileNotFoundException {
		URL resourceUrl = new URL(sourceUrl);
		try (InputStream resourceReader = new BufferedInputStream(resourceUrl.openStream());
				OutputStream resourceWriter = new BufferedOutputStream(
						new FileOutputStream(targetDirectory + File.separator + resName));) {
			int readByte;

			while ((readByte = resourceReader.read()) != -1) {
				resourceWriter.write(readByte);
			}
		}
	}

	private boolean downloadLinkContent(String htmlString, String targetDir, String targetFileName, String targetFileId,
			boolean append) {
		if (!isDownload()) {
			return true;
		}
		boolean result = false;
		// System.out.println(htmlString);
		String open = "JavaScript!</noscript>";

		// Content between open and close tag.
		String inside = ".*?";

		// Non capturing close tag.
		String close = "<div class=\"row\">";

		// Final regex
		String regex = open + inside + close;

		String text = htmlString; // you string here
		String content = null;

		// Usage
		Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(text);
		while (matcher.find()) {
			content = matcher.group().trim();
			String newOpen = open.replace("\\", "");
			content = content.substring(newOpen.length(), content.length() - close.length());
			break;

		}

		if (content != null) {
			BufferedReader reader = new BufferedReader(new StringReader(content));
			String input = null;
			int count = 0;
			StringBuffer buffer = new StringBuffer();
			buffer.append("<HTML><Head>\n" + "<Title></Title>"
					+ "<link href=\"../Styles/Style0001.css\" rel=\"stylesheet\" type=\"text/css\"/>" + "</Head><body>\n");
			try {
				while ((input = reader.readLine()) != null) {
					if (input.trim().equals(""))
						continue;
					if (input.contains("�")) {
						input = input.replaceAll("�", "!");
					}
					if (count == 0) {
						if (input.contains("Chapter")) {
							if (input.length() > 255) {
								buffer.append(extractTitle(input));
								/*
								 * int end = input.indexOf("</p>"); if (end !=
								 * -1 && end < 255) { buffer.append("<h4>" +
								 * input.substring(0, end + "</p>".length()) +
								 * "</h4><hr></hr>\n"); buffer.append("\n" +
								 * input.substring(end + "</p>".length() + 1));
								 * }
								 */
							} else
								buffer.append("<h4>" + input + "</h4><hr></hr>");
						} else {
							String id = targetFileId;
							id = id.replace("_CH_", " Chapter ");
							buffer.append("<h4>" + "Chapter " + id + ".</h4><hr></hr>");
							buffer.append("\n" + input);
						}
					} else {
						buffer.append("\n" + input);
					}
					// System.out.println(input);
					count++;
				}
				buffer.append("\n</body></HTML>");
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (count > 0) {
				writeToFile(targetDir, targetFileName, targetFileId, buffer.toString());
				System.out.println(
						String.format("Successfully downloaded content for [%s] with id [%s]", targetFileName, targetFileId));
				result = true;
			} else {
				System.err.println(
						String.format("Failed to downloaded content for [%s] with id [%s]", targetFileName, targetFileId));
			}

		} else {
			System.err.println(String.format("No Content available for [%s] wiht id [%s]", targetFileName, targetFileId));
		}
		return result;
	}

	private String getData(String data) {
		String reg = "<.*?>(.*?)</.*?>";
		StringBuffer buffer = new StringBuffer();
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(data);
		while (m.find()) {
			buffer.append(m.group(1));
		}
		return buffer.toString();
	}

	private String extractTitle(String data) {
		int end = data.indexOf("</p>");
		StringBuffer buffer = new StringBuffer();
		if (end != -1 && end < 255) {
			String dt = data.substring(0, end + "</p>".length());
			String titl = getData(dt);
			if (titl != null && !titl.trim().equals("")) {
				buffer.append("<h4>" + titl + ".</h4><hr></hr>\n");
				buffer.append("\n" + data.substring(end + "</p>".length() + 1));
			} else {
				return extractTitle(data.substring(end + "</p>".length() + 1));
			}
		}
		return buffer.toString();
	}
}
