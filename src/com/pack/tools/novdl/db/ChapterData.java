package com.pack.tools.novdl.db;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class ChapterData implements Serializable {

	private static final long serialVersionUID = 1L;
	private File file;
	private boolean notified;
	private Date notifiedDate;
	private Date publishedDate;
	private String url;
	private String title;
	private boolean downloaded;
	private Date downloadDate;
	private String id;
	private String volume;
	private String author;
	private String translator;
	private String editor;

	public File getFile() {
		return file;
	}

	public boolean isNotified() {
		return notified;
	}

	public Date getNotifedDate() {
		return notifiedDate;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setUrl(String linkUrl) {
		this.url = linkUrl;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public Date getDownloadDate() {
		return downloadDate;
	}

	public void setDownloadDate(Date downloadDate) {
		this.downloadDate = downloadDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPublishedDate(Date date) {
		this.publishedDate = date;
	}

	public String getUrl() {
		return this.url;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public void setChapterDetails(ChapterData chData) {
		this.file = chData.getFile();
		this.title = chData.getTitle();
		this.author = chData.getAuthor();
		this.editor = chData.getEditor();
		this.translator = chData.getTranslator();
		this.url = chData.getUrl();
		this.id = chData.getId();
		this.downloadDate = chData.getDownloadDate();
		this.publishedDate = chData.getPublishedDate();
		this.notifiedDate = chData.getNotifedDate();
		this.downloaded = chData.isDownloaded();
		this.notified = chData.isNotified();
		this.volume = chData.getVolume();
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

}
