package com.pack.tools.novdl.event;

public final class LinkDownloadEvent extends AbstractDownloaderEvent {

	private boolean downloaded;

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

}
