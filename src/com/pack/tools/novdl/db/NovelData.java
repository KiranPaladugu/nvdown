package com.pack.tools.novdl.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pack.tools.novdl.util.NameValue;

public class NovelData implements Serializable, Comparable<NovelData> {

	private static final long serialVersionUID = 1L;
	private String name;
	private String urlLink;
	private int downloaded;
	private Map<String, String> downloadedUrlLinks = new LinkedHashMap<>();
	private Map<String, Set<String>> novelMap = new HashMap<>();
	private BookData bookData = new BookData();
	private String fileName = null;
	private List<NameValue> updates = null;

	public Map<String, String> getDownloadedUrlLinks() {
		return downloadedUrlLinks;
	}

	public void setDownloadedUrlLinks(Map<String, String> downloadedUrlLinks) {
		this.downloadedUrlLinks = downloadedUrlLinks;
	}

	public BookData getBookData() {
		return bookData;
	}

	public void setBookData(BookData bookData) {
		this.bookData = bookData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.bookData.setTitle(name);
	}

	public String getUrlLink() {
		return urlLink;
	}

	public void setUrlLink(String urlLink) {
		this.urlLink = urlLink;
		this.bookData.setUrl(urlLink);
	}

	public int getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}

	public Set<String> getDownloadedLinks() {
		return downloadedUrlLinks.keySet();
	}

	public void addDownloadedLink(String linkUrl, String filePath) {
		this.downloadedUrlLinks.put(linkUrl, filePath);
		this.bookData.addChapter(linkUrl, filePath);
	}

	public Map<String, Set<String>> getNovelMap() {
		return novelMap;
	}

	public void setNovelMap(Map<String, Set<String>> novelMap) {
		this.novelMap = novelMap;
	}

	public void addChapter(String volume, String chapter, String contentLink) {
		if (novelMap.containsKey(volume)) {
			novelMap.get(volume).add(chapter);
		} else {
			Set<String> set = new HashSet<>();
			set.add(volume);
			novelMap.put(volume, set);
		}
		bookData.addChapter(contentLink, null);
	}

	public boolean isLinkDownloaded(String urlLink) {
		return downloadedUrlLinks.containsKey(urlLink);
	}

	public boolean isChapterPresent(String volume, String chapter) {
		if (novelMap.containsKey(volume)) {
			return novelMap.get(volume).contains(chapter);
		}
		return false;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + downloaded;
		result = prime * result + ((downloadedUrlLinks == null) ? 0 : downloadedUrlLinks.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((novelMap == null) ? 0 : novelMap.hashCode());
		result = prime * result + ((urlLink == null) ? 0 : urlLink.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NovelData other = (NovelData) obj;
		if (downloaded != other.downloaded)
			return false;
		if (downloadedUrlLinks == null) {
			if (other.downloadedUrlLinks != null)
				return false;
		} else if (!downloadedUrlLinks.equals(other.downloadedUrlLinks))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (novelMap == null) {
			if (other.novelMap != null)
				return false;
		} else if (!novelMap.equals(other.novelMap))
			return false;
		if (urlLink == null) {
			if (other.urlLink != null)
				return false;
		} else if (!urlLink.equals(other.urlLink))
			return false;
		return true;
	}

	@Override
	public int compareTo(NovelData data) {
		return 0;
	}

	public void setFilename(String fileName) {
		this.fileName = fileName;
	}

	public String getFilename() {
		return this.fileName;
	}

	public List<NameValue> getUpdates() {
		return updates;
	}

	public void setUpdates(List<NameValue> updates) {
		this.updates = updates;
	}

}
