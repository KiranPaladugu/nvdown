package com.pack.tools.novdl.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import com.pack.tools.novdl.event.LinkDownloadEvent;
import com.pack.tools.novdl.listener.LinkDownloadListener;

public class BookDbManager implements Serializable, LinkDownloadListener {
	private static final long serialVersionUID = 1L;
	private static Path root = Paths.get(System.getProperty("user.home")).resolve(".novdl1");
	private static String dataDir = "data";
	private static String confDir = "conf";
	private static String data_backupDir = "data_back";
	private final String dbFile = "data.db";
	private static BookDbManager bookDbMan;

	private BookDbManager() {
		init();
	}

	public synchronized static BookDbManager getDbManger() {
		if (bookDbMan == null) {
			bookDbMan = new BookDbManager();
		}
		return bookDbMan;
	}

	public void init() {
		checkDir(dataDir, confDir, data_backupDir);
	}

	public synchronized static void checkDir(String... dirs) {
		if (dirs != null && dirs.length > 0) {
			for (final String dir : dirs) {
				if (!root.resolve(dir).toFile().exists()) {
					root.resolve(dir).toFile().mkdirs();
				} else if (!root.resolve(dir).toFile().isDirectory()) {
					System.out.println(dir + " directory should be present! at :" + root.toString());
				}
			}
		}
	}

	public static synchronized Path getDataDir() {
		return root.resolve(dataDir);
	}

	public static synchronized Path getDataBackupDir() {
		return root.resolve(data_backupDir);
	}

	private synchronized String getNewUUID() {
		return UUID.randomUUID().toString();
	}

	@SuppressWarnings("unchecked")
	public synchronized NovelData getNovelData(String name, String urlLink) {
		final File file = getDataDir().resolve(dbFile).toFile();
		Map<String, String> map = new HashMap<>();
		map = (Map<String, String>) readDataFromFile(file);
		if (map != null && map.containsKey(urlLink)) {
			final String novelDataFilenName = map.get(urlLink);
			final File novelDataFile = getDataDir().resolve(novelDataFilenName).toFile();
			NovelData data = (NovelData) readDataFromFile(novelDataFile);
			if (data != null)
				data.setFilename(novelDataFilenName);
		}
		return null;
	}

	private synchronized Object readDataFromFile(File file) {
		Object obj = null;
		if (file.exists()) {
			try {
				final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				obj = ois.readObject();
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public synchronized boolean putNoveldata(NovelData data) {
		final File file = getDataDir().resolve(dbFile).toFile();
		boolean updateDb = true;
		Map<String, String> map = null;
		map = (Map<String, String>) readDataFromFile(file);
		String novelDataFilenName = getNewUUID();
		if (map != null && map.containsKey(data.getUrlLink())) {
			novelDataFilenName = map.get(data.getUrlLink());
			updateDb = false;
		} else {
			if (map == null) {
				map = new HashMap<>();
			}
			map.put(data.getUrlLink(), novelDataFilenName);
		}
		final File novelDataFile = getDataDir().resolve(novelDataFilenName).toFile();
		data.setFilename(novelDataFilenName);
		WriteFile(data, novelDataFile);
		WriteFile(map, file);
		return updateDb;
	}

	private synchronized void WriteFile(Object data, File fileToWrite) {
		if (createBackup(fileToWrite)) {
			System.out.println("BackupCreated for : " + fileToWrite);
		} else {
			System.out.println("Creation of backup failed for file:" + fileToWrite);
		}
		try {
			final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileToWrite));
			out.writeObject(data);
			out.flush();
			out.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean createBackup(File fileToBackup) {
		if (fileToBackup != null && fileToBackup.exists()) {
			try {
				final Path copy = Files.copy(FileSystems.getDefault().getPath(fileToBackup.toString()),
						getDataBackupDir().resolve(fileToBackup.getName()), StandardCopyOption.REPLACE_EXISTING);
				if (copy != null) {
					return true;
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public static void main(String args[]) {
		final BookDbManager mgr = new BookDbManager();
		final NovelData data = new NovelData();
		data.setUrlLink("SomeURLLink");
		mgr.putNoveldata(data);
		System.out.println(mgr.getNovelData(null, data.getUrlLink()));
	}

	@Override
	public synchronized void onLinkDownloaded(LinkDownloadEvent event) {

	}

	public List<NovelData> getAllDataList() {
		Vector<NovelData> allData = new Vector<>();
		final File file = getDataDir().resolve(dbFile).toFile();
		Map<String, String> map = (Map<String, String>) readDataFromFile(file);
		if (map == null)
			return allData;
		Set<String> keys = map.keySet();
		for (String key : keys) {
			final String novelDataFilenName = map.get(key);
			final File novelDataFile = getDataDir().resolve(novelDataFilenName).toFile();
			NovelData data = (NovelData) readDataFromFile(novelDataFile);
			if (data != null) {
				data.setFilename(novelDataFilenName);
				allData.add(data);
			}
		}
		return allData;
	}

	public NovelData[] getAllData() {
		List<NovelData> data = getAllDataList();
		return data.toArray(new NovelData[data.size()]);
	}

	public static Path getConfDir() {
		return root.resolve(confDir);
	}

	public boolean update(NovelData data) {
		if (data != null && data.getFilename() != null) {
			final File file = getDataDir().resolve(dbFile).toFile();
			Map<String, String> map = (Map<String, String>) readDataFromFile(file);
			if (map == null) {
				map = new HashMap<>();
			}
			if (data.getFilename().trim().length() > 0) {
				final File novelDataFile = getDataDir().resolve(data.getFilename()).toFile();
				if (novelDataFile != null && novelDataFile.exists()) {
					this.WriteFile(data, novelDataFile);
					;
					return true;
				}
			}
		}
		return false;
	}
}
