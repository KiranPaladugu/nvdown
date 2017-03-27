package com.pack.tools.novdl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pack.tools.novdl.db.BookData;
import com.pack.tools.novdl.db.BookDbManager;
import com.pack.tools.novdl.db.ChapterData;
import com.pack.tools.novdl.db.NovelData;
import com.pack.tools.novdl.event.LinkDownloadEvent;
import com.pack.tools.novdl.event.UpdatesDownloadedEvent;
import com.pack.tools.novdl.listener.LinkDownloadListener;
import com.pack.tools.novdl.listener.UpdateDownloadedEventListener;
import com.pack.tools.novdl.util.NameValue;

public class UrlLinkDownloadManager implements LinkDownloadListener, Runnable {
	private final Map<String, List<NameValue>> updateMap = new HashMap<>();
	private final Map<String, NovelData> bookMap = new HashMap<>();
	private final BlockingQueue<LinkDownloadEvent> eventQueue = new BlockingQueue<>(100);
	private boolean active = true;
	private static UrlLinkDownloadManager lnkDwnMgr;
	private UpdateDownloadedEventListener listener;

	public static UrlLinkDownloadManager getUrlLinkDownloadManager() {
		if (lnkDwnMgr == null) {
			lnkDwnMgr = new UrlLinkDownloadManager();
		}
		return lnkDwnMgr;
	}

	public void bookUpdate(String bookUrl, List<NameValue> updates, NovelData bookData) {
		if (updateMap.containsKey(bookUrl)) {
			final List<NameValue> updts = updateMap.get(bookUrl);
			for (final NameValue nv : updates) {
				if (!updateMap.containsKey(nv)) {
					updts.add(nv);
				}
			}
		} else {
			updateMap.put(bookUrl, updates);
		}
		bookMap.put(bookUrl, bookData);
	}

	@Override
	public void run() {
		while (active) {
			try {
				final LinkDownloadEvent event = eventQueue.remove();
				final String url = event.getUrl();
				// System.out.println("Recieved event:" + url);
				final String bookURL = event.getReferenceURL();
				final NovelData bookData = bookMap.get(bookURL);
				ChapterData chData = bookData.getBookData().getChapterDetails(url);
				if (chData == null) {
					chData = new ChapterData();
					if (event.getProperty("file") != null) {
						File file = (File) event.getProperty("file");
						if (file.exists()) {
							chData.setDownloadDate(new Date(file.lastModified()));
							chData.setDownloaded(true);
						} else {
							chData.setDownloadDate(new Date());
						}
					} else {
						chData.setDownloadDate(new Date());
					}
					chData.setUrl(url);
				}
				chData.setChapterDetails((ChapterData) event.getEventObject());
				// String bookName = event.getName();
				if (updateMap.containsKey(bookURL)) {
					final List<NameValue> updates = updateMap.get(bookURL);
					updates.remove(event.getUrlAndId());
					if (updates.isEmpty()) {
						System.out.println("Notify that all Updates are downloaded with event:" + event.getReferenceURL());
						final NovelData novelData = bookMap.remove(bookURL);

						if (novelData != null) {
							System.out.println("Attempting dbWrite for book:" + novelData.getName());
							BookDbManager.getDbManger().putNoveldata(novelData);
						}
						if (listener != null) {
							UpdatesDownloadedEvent newEve = new UpdatesDownloadedEvent();
							newEve.setEventObject(novelData);
							listener.onUpdateDownloadedEvent(newEve);
						}
					}
				}

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

	}

	private NovelData buildData(BookData bookData) {
		NovelData nvData = null;
		if (bookData != null) {
			nvData = new NovelData();
			nvData.setBookData(bookData);
			nvData.setName(bookData.getTitle());
			nvData.setUrlLink(bookData.getUrl());
			final ArrayList<ChapterData> allCh = bookData.getAllChapters();
			for (final ChapterData chDt : allCh) {
				nvData.addChapter(chDt.getVolume(), chDt.getId(), chDt.getUrl());
				if (chDt.getFile() != null) {
					nvData.addDownloadedLink(chDt.getUrl(), chDt.getFile().getAbsolutePath());
				}
			}
		}
		return nvData;
	}

	public synchronized void stop() {
		this.active = false;
	}

	@Override
	public synchronized void onLinkDownloaded(LinkDownloadEvent event) {
		try {
			eventQueue.insert(event);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addUpdatDownloadedListener(UpdateDownloadedEventListener listener) {
		this.listener = listener;
	}
}
