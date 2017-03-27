package com.cont;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NovelData implements Serializable{
	private String name;
	private String urlLink;
	private int downloaded;
	private Set<String> downloadedLinks = new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlLink() {
		return urlLink;
	}

	public void setUrlLink(String urlLink) {
		this.urlLink = urlLink;
	}

	public int getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}

	public Set<String> getDownloadedLinks() {
		return downloadedLinks;
	}

	public void setDownloadedLinks(Set<String> downloadedLinks) {
		this.downloadedLinks = downloadedLinks;
	}

	public void addDownloadedLink(String urlLink) {
		this.downloadedLinks.add(urlLink);
	}

	public Map<String, Set<String>> getNovelMap() {
		return novelMap;
	}

	public void setNovelMap(Map<String, Set<String>> novelMap) {
		this.novelMap = novelMap;
	}

	private Map<String, Set<String>> novelMap = new HashMap<>();

	public void addChapter(String volume, String chapter) {
		if (novelMap.containsKey(volume)) {
			novelMap.get(volume).add(chapter);
		} else {
			Set<String> set = new HashSet<>();
			set.add(volume);
			novelMap.put(volume, set);
		}
	}

	public boolean isLinkDownloaded(String urlLink) {
		return downloadedLinks.contains(urlLink);
	}

	public boolean isChapterPresent(String volume, String chapter) {
		if (novelMap.containsKey(volume)) {
			return novelMap.get(volume).contains(chapter);
		}
		return false;
	}

	@Override
	public String toString() {
		return "NovelData [name=" + name + ", urlLink=" + urlLink + ", downloadedLinks=" + downloadedLinks + "]";
	}
	
	
}
