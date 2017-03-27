package com.pack.tools.novdl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pack.tools.novdl.db.BookDbManager;
import com.pack.tools.novdl.db.NovelData;
import com.pack.tools.novdl.event.BookUpdateEvent;
import com.pack.tools.novdl.listener.BookUpdateListener;
import com.pack.tools.novdl.listener.UpdateTimerListener;
import com.pack.tools.novdl.util.HttpUrlUtilities;
import com.pack.tools.novdl.util.NameValue;

public class BookUpdateCheck implements Runnable, UpdateTimerListener {

	private String url;
	private String name;
	private boolean active = true;
	private boolean reRunnable;
	private final HttpUrlUtilities utils = new HttpUrlUtilities();
	private final BookUpdateListener bookManager;
	private final List<NameValue> updates = new ArrayList<NameValue>();

	public BookUpdateCheck(BookUpdateListener bookManager) {
		this.bookManager = bookManager;
	}

	public BookUpdateCheck(String url, BookUpdateListener bookUpdateListener) {
		this(bookUpdateListener);
		this.url = url;
	}

	public BookUpdateCheck(String url, BookUpdateListener bookManager, boolean reRunnable) {
		this(bookManager);
		this.url = url;
		this.setSingleRun(reRunnable);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSingleRun() {
		return reRunnable;
	}

	public void setSingleRun(boolean reRunnable) {
		this.reRunnable = reRunnable;
		if (reRunnable) {
			UpdateTimerService.getTimerService().addUpdateTimerListener(this);
		}
	}

	@Override
	public synchronized void run() {

		while (active) {
			this.name = utils.getNameFromUrl(url);
			Thread.currentThread().setName(name);
			System.out.println("running check...for :" + url + "[" + name + "]");
			final String htmlContent = utils.getUrlContents(url);
			final Document document = Jsoup.parse(htmlContent);
			final Elements elements = document.getElementsByClass("tab-content");
			final BookDbManager mgr = BookDbManager.getDbManger();
			NovelData data = mgr.getNovelData(name, url);
			if (data == null) {
				data = new NovelData();
				data.setUrlLink(url);
				data.setName(name);
				System.out.println("[" + name + "] Assuming new Novel..");
			}
			String section = "";
			final String volName = "Vol";
			int volCount = 0;
			boolean update = false;
			String last = "[Unknown]";
			for (final Element element : elements) {
				final Elements pEleemnts = element.getElementsByClass("tab-pane");
				if (pEleemnts != null && pEleemnts.size() > 0) {
					final String sec = pEleemnts.get(0).attr("id");
					if (sec != null && !section.equals(sec)) {
						section = sec;
						final int sep = section.indexOf('-');
						if (sep != -1) {
							section = section.substring(0, sep);
						}
						volCount++;
					}
					final Elements aElements = element.getElementsByTag("a");
					for (final Element aElement : aElements) {
						final String contentLink = aElement.attr("href");
						final String value = aElement.text();
						data.addChapter(section, value, contentLink);
						final String id = volName + volCount + "_" + utils.makeId(value);
						final File file = utils.makeFilePathFull(name, id).toFile();
						if ((contentLink.trim().endsWith("/chapter-") || file.exists())) {
							data.addDownloadedLink(contentLink, file.getAbsolutePath());
							last = id;
						} else {
							updates.add(new NameValue(id, contentLink));
							System.out.println("[" + name + "]" + contentLink);
							update = true;
						}
					}
				}
			}

			if (update) {
				final BookUpdateEvent event = new BookUpdateEvent();
				event.setUpdate(update);
				event.setUpdateChapters(updates);
				data.setUpdates(new ArrayList<>(updates));
				event.addProperty("last", last);
				event.setUrl(this.url);
				event.setName(name);
				event.setEventObject(data);
				// event.setSource(this);
				this.bookManager.onBookUpdate(event);

			} else {
				System.out.println("[" + name + "] No Updates!");
			}

			try {
				if (reRunnable) {
					this.wait();
				} else {
					active = false;
				}
				break;
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void runUpdateCheck() {
		this.notify();
	}

	@Override
	public synchronized void onTimeout() {
		notify();
	}

	public void stop() {
		this.active = false;
	}

}
