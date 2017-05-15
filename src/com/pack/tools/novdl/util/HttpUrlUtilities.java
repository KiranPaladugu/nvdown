
package com.pack.tools.novdl.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pack.tools.novdl.BookManager;
import com.pack.tools.novdl.CookieManager;

public class HttpUrlUtilities {
	private Path path, pathFull, downloadRootdir;

	public HttpUrlUtilities() {
		this.path = BookManager.getPath();
		this.pathFull = BookManager.getPathFull();
		this.downloadRootdir = BookManager.getDownloadRoot();
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Path getPathFull() {
		return pathFull;
	}

	public void setPathFull(Path pathFull) {
		this.pathFull = pathFull;
	}

	public Path getDownloadRootdir() {
		return downloadRootdir;
	}

	public void setDownloadRootdir(Path downloadRootdir) {
		this.downloadRootdir = downloadRootdir;
	}

	public String getClippedUrlContent(String httpUrl, String start, String end) {
		return getSubContent(getUrlContents(httpUrl), start, end);
	}

	public String getSubContent(String htmlContent, String start, String end) {
		// System.out.println(htmlString);
		final String open = start;
		// String open = "JavaScript!</noscript>";
		// Content between open and close tag.
		final String inside = ".*?";

		// Non capturing close tag.
		final String close = end;
		// String close = "<div class=\"row\">";

		// Final regex
		final String regex = open + inside + close;

		String content = null;

		// Usage
		final Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(htmlContent);
		while (matcher.find()) {
			content = matcher.group().trim();
			final String newOpen = open.replace("\\", "");
			content = content.substring(newOpen.length(), content.length() - close.length());
			break;

		}
		return content;
	}

	public String getUrlContents(String theUrl) {
		return getUrlContents(theUrl, true);
	}

	private String getUrlContents(String theUrl, boolean flag) {
		String name = Thread.currentThread().getName();
		System.out.println("[INFO]: Reading url :" + theUrl + "[" + name + "]");
		final StringBuilder content = new StringBuilder();
		// many of these calls can throw exceptions, so i've just
		// wrapped them all in one try/catch statement.
		try {
			// create a url object
			final URL url = new URL(theUrl);
			// final String cookieStr =
			// CookieManager.getCoookieManger().getStore()
			// .getGenericCookieString();
			// create a urlconnection object
			final URLConnection urlConnection = url.openConnection();
			HttpURLConnection urlConn = (HttpURLConnection) urlConnection;

			urlConn.setRequestProperty("Accept-Charset", "UTF-8");
			// urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate,
			// sdch");
			// urlConn.setRequestProperty("Accept",
			// "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			// urlConn.setRequestProperty("Upgrade-Insecure-Requests", "1");
			// urlConn.setRequestProperty("Referer", url.toString());
			urlConnection.setRequestProperty("User-Agent",
					"Mozilla/5.0(Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML,like Gecko) Chrome/55.0.2883.95 Safari/537.36");
			// urlConn.setRequestProperty("Cookie", cookieStr);
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
            } else if (flag && urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (theUrl.contains("http://")) {
					theUrl = theUrl.replace("http://", "https://");

				} else if (theUrl.contains("http://")) {
					theUrl = theUrl.replace("https://", "http://");
				}
				return getUrlContents(theUrl, false);
			}

			InputStream urlInputStream = null;
			if (urlConn.getContent() instanceof InputStream) {
				urlInputStream = (InputStream) urlConn.getContent();
			} else {
				urlInputStream = urlConn.getInputStream();
			}

			if (urlInputStream != null) {
				final BufferedInputStream stream = new BufferedInputStream(urlInputStream);
				final byte[] bytes = new byte[8 * 1024];
				int lenght = -1;
				while ((lenght = stream.read(bytes)) != -1) {
					content.append(new String(bytes, 0, lenght));
				}
				stream.close();
			}

			final Map<String, List<String>> headers = urlConn.getHeaderFields();
			final List<String> cookiesUpdate = headers.get("Set-Cookie");
			// CookieManager.getCoookieManger().getStore().updateGenericCookies(cookiesUpdate);
		} catch (final Exception e) {
			System.out.println("[ERROR]- Errol while reading url " + theUrl + "[" + name + "]");
			e.printStackTrace();
		}
		return content.toString();
	}

	public String getNameFromUrl(String urlLink) {
		final int index = urlLink.lastIndexOf('/');
		if (index != -1) {
			final String rawName = urlLink.substring(index + 1);
			if (rawName.length() < 25) {
				return rawName;
			}
			final String[] tokens = rawName.split("-");
			final StringBuffer name = new StringBuffer();
			for (final String token : tokens) {
				final char ch = token.charAt(0);
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

	public String makeId(String value) {
		// Pattern regex = Pattern.compile("[^A-Za-z0-9]");
		final String[] values = value.split("[^A-Za-z0-9]");
		final StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			final String val = values[i];
			buffer.append(val);
			if (i != (values.length - 1)) {
				buffer.append('_');
			}
		}
		return buffer.toString();
	}

	public boolean writeToFile(String path, String name, String id, String content) {
		try {
			final File file = makeFiletmpPath(name, id).toFile();
			final File parent = file.getParentFile();
			if (!parent.exists()) {
				final boolean result = parent.mkdirs();
				if (!result) {
					return false;
				}
			}
			final FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
			copyToPool(parent, file);
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean copyToPool(File sourcePath, File fileToCopy) {
		if (fileToCopy != null && fileToCopy.exists()) {
			try {
				Path dest = pathFull.resolve(sourcePath.getName());
				final File destDir = dest.toFile();
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				dest = Files.copy(FileSystems.getDefault().getPath(fileToCopy.toString()), dest.resolve(fileToCopy.getName()),
						StandardCopyOption.REPLACE_EXISTING);
				if (dest != null) {
					return true;
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public Path makeFilePathFull(String name, String id) {
		return this.getPathFull().resolve(name).resolve(name + "_" + id + ".xhtml");
	}

	public Path makeFiletmpPath(String name, String id) {
		return this.getPath().resolve(name).resolve(name + "_" + id + ".xhtml");
	}
}