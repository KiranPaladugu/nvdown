package com.pack.tools.novdl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.UIManager;

import com.pack.tools.novdl.db.BookDbManager;
import com.pack.tools.novdl.db.NovelData;
import com.pack.tools.novdl.event.BookUpdateEvent;
import com.pack.tools.novdl.event.UpdateEvent;
import com.pack.tools.novdl.event.UpdatesDownloadedEvent;
import com.pack.tools.novdl.listener.BookUpdateListener;
import com.pack.tools.novdl.listener.UpdateDownloadedEventListener;
import com.pack.tools.novdl.listener.UpdateListener;
import com.pack.tools.novdl.listener.UpdateTimerListener;
import com.pack.tools.novdl.ui.NewUpdatesListView;
import com.pack.tools.novdl.ui.UpdateCheckView;
import com.pack.tools.novdl.util.NameValue;

public class BookManager implements BookUpdateListener, UpdateTimerListener, UpdateDownloadedEventListener {

	private static Path downloadRoot = Paths.get(System.getProperty("user.home")).resolve("wbnov1");
	private static Path path = downloadRoot.resolve("downloads");
	private static Path pathFull = downloadRoot.resolve("download_");
	private final PoolExcecutor updChkExecutor;
	private final PoolExcecutor lnkDwnExecutor;
	private final PoolExcecutor managersPool;
	private static BookManager bookMgr;
	boolean continueCheck = false;
	private final List<String> urls = new ArrayList<>();
	private UpdateListener listener;

	public static BookManager getBookManager() {
		if (bookMgr == null) {
			bookMgr = new BookManager();
		}
		return bookMgr;
	}

	public static Path getDownloadRoot() {
		return downloadRoot;
	}

	public static Path getPath() {
		return path;
	}

	public static Path getPathFull() {
		return pathFull;
	}

	public static Path getConfPath() {
		return BookDbManager.getConfDir();
	}

	public void loadBooks() {
		File file = getConfPath().resolve("book.site").toFile();
		List<String> bookList = new ArrayList<String>();
		if (file.exists() && file.isFile() && file.canRead()) {
			try {
				BufferedReader fileReader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = fileReader.readLine()) != null) {
					if (line.trim().length() > 0) {
						String books[] = line.split(",");
						for (String book : books) {
							if (book.trim().length() > 0) {
								book = book.replaceAll("\"", "");
							}
							if (!bookList.contains(book))
								bookList.add(book);
						}
					}
				}
				fileReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		BookManager.getBookManager().checkBookUrls(bookList.toArray(new String[bookList.size()]));
	}

	private BookManager() {
		this.updChkExecutor = new PoolExcecutor(15);
		this.managersPool = new PoolExcecutor(10);
		this.lnkDwnExecutor = new PoolExcecutor(50);

		managersPool.submit(UpdateTimerService.getTimerService());
		// ApplicationServiceProvider.getApplicationServiceProvider()
		// .register(UpdateTimerService.getTimerService());
		managersPool.submit(UrlLinkDownloadManager.getUrlLinkDownloadManager());
		this.listener = NewUpdatesListView.get();
		managersPool.submit(new UpdateCheckView());
		UrlLinkDownloadManager.getUrlLinkDownloadManager().addUpdatDownloadedListener(this);
	}

	public void loadBook(String url) {
		final BookUpdateCheck chk = new BookUpdateCheck(url, this);
		updChkExecutor.submit(chk);

	}

	public static void main(String args[]) {
		System.setProperty("http.agent", "Chrome");
		final String[] bookUrls = { "http://www.readlightnovel.com/zhan-long",
				"http://www.readlightnovel.com/tales-of-demons-and-gods", "http://www.readlightnovel.com/god-and-devil-world",
				"http://www.readlightnovel.com/against-the-gods",
				/*
				 * "http://www.readlightnovel.com/emperors-domination",
				 * "http://www.readlightnovel.com/martial-god-asura",
				 * "http://www.readlightnovel.com/i-shall-seal-the-heavens",
				 * "http://www.readlightnovel.com/true-martial-world",
				 * "http://www.readlightnovel.com/immortal-god-emperor",
				 * "http://www.readlightnovel.com/martial-god-space",
				 * "http://www.readlightnovel.com/chaotic-sword-god",
				 */
				"http://www.readlightnovel.com/great-demon-king" };
		// BookManager.getBookManager().checkBookUrls(bookUrls);
		BookManager.getBookManager().loadBooks();
	}

	public void checkBookUrls(String[] bookUrls) {
		for (final String bookUrl : bookUrls) {
			if (!urls.contains(bookUrl)) {
				urls.add(bookUrl);
				loadBook(bookUrl);
			}
		}
	}

	private void checkBookUrls() {
		for (final String url : urls) {
			loadBook(url);
		}
	}

	@Override
	public synchronized void onBookUpdate(BookUpdateEvent event) {
		if (event.isUpdate()) {
			final List<NameValue> updChapters = event.getUpdateChapters();
			final String bookName = event.getName();
			final String bookUrl = event.getUrl();
			final NovelData novelData = (NovelData) event.getEventObject();
			UrlLinkDownloadManager.getUrlLinkDownloadManager().bookUpdate(event.getUrl(), event.getUpdateChapters(), novelData);
			for (final NameValue nv : updChapters) {
				lnkDwnExecutor.submit(new UrlLinkDownloader(bookName, nv.getValue(), nv.getName(), bookUrl, false,
						UrlLinkDownloadManager.getUrlLinkDownloadManager()));
			}
		}
	}

	public boolean stop() {
		this.updChkExecutor.stop();
		this.lnkDwnExecutor.stop();
		this.managersPool.stop();
		return true;
	}

	@Override
	public synchronized void onTimeout() {
		System.out.println("\n[INFO]: "+new Date().toString()+"Time out ... check all urls.\n");
		if (updChkExecutor.getThreadPoolExcutor().getActiveCount() == 0
				&& lnkDwnExecutor.getThreadPoolExcutor().getActiveCount() == 0) {
			checkBookUrls();
		}
	}

	@Override
	public void onUpdateDownloadedEvent(UpdatesDownloadedEvent ude) {
		if (updChkExecutor.getThreadPoolExcutor().getActiveCount() == 0
				&& lnkDwnExecutor.getThreadPoolExcutor().getActiveCount() == 0) {
		    System.out.println("[INFO ]: Got signal for all DONE@"+new Date().toString());
			System.out.println("******** All Done! **********");
			UpdateTimerService.getTimerService().resetTimer();
			if (!continueCheck) {
				UpdateTimerService.getTimerService().addUpdateTimerListener(this);
				continueCheck = true;
			}
		}
		if (listener != null) {
			UpdateEvent event = new UpdateEvent();
			event.setEventObject(ude.getEventObject());
			listener.onUpdate(event);
		}
	}

	public UpdateListener getListener() {
		return listener;
	}

}
