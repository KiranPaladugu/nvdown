package com.pack.tools.novdl.db;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class BookData extends ChapterData implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, ChapterData> chapters = new LinkedHashMap<>();

	public synchronized boolean isChapterAvailable(String url) {
		return chapters.containsKey(url);
	}

	public ChapterData getChapterDetails(String url) {
		ChapterData bookChapter = chapters.get(url);
		return bookChapter;
	}

	public synchronized boolean isChapterDownloaded(String url) {
		ChapterData bc = getChapterDetails(url);
		if (bc != null) {
			if (bc.getFile() != null && bc.getFile().exists()) {
				return true;
			}
		}
		return false;
	}

	public synchronized boolean isChapterNotified(String url) {
		ChapterData bc = getChapterDetails(url);
		if (bc != null) {
			if (bc.isNotified()) {
				return true;
			}
		}
		return false;
	}

	public synchronized Date getChapterNotifiedDate(String url) {
		ChapterData bc = getChapterDetails(url);
		if (bc != null) {
			return bc.getNotifedDate();
		}
		return null;
	}

	public synchronized Date getChapterDownloadedDate(String url) {
		ChapterData bc = getChapterDetails(url);
		if (bc != null) {
			return bc.getPublishedDate();
		}
		return null;
	}

	public synchronized ArrayList<ChapterData> getAllChapters() {
		return new ArrayList<>(chapters.values());
	}

	public synchronized Map<String, ChapterData> getChapters() {
		return chapters;
	}

	public synchronized void setChapters(Map<String, ChapterData> chapters) {
		this.chapters = chapters;
	}

	public synchronized void addChapter(String linkUrl, String filePath) {
		ChapterData chdt = this.getChapterDetails(linkUrl);
		if(chdt == null){
			chdt = new ChapterData();
		}
		chdt.setUrl(linkUrl);
		if (filePath != null) {
			File file = new File(filePath);
			if (file.exists()) {
				chdt.setDownloaded(true);
				chdt.setDownloadDate(new Date(file.lastModified()));
			}
			chdt.setFile(file);
		}

		this.chapters.put(linkUrl, chdt);
	}

	@Override
	public String toString() {
		return "BookData [chapters=" + chapters + "]," + super.toString();
	}

}
