package com.pack.tools.novdl.event;

import java.util.List;

import com.pack.tools.novdl.util.NameValue;

public class BookUpdateEvent extends AbstractDownloaderEvent {

	private boolean update;
	private List<NameValue> updateChapters;

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public List<NameValue> getUpdateChapters() {
		return updateChapters;
	}

	public void setUpdateChapters(List<NameValue> updateChapters) {
		this.updateChapters = updateChapters;
	}
}
