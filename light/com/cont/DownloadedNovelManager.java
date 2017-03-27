package com.cont;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DownloadedNovelManager {
	private static Path root = Paths.get(System.getProperty("user.home")).resolve(".novelDownloader");
	private static String dataDir = "data";
	private static String confDir = "conf";
	private static String data_backupDir = "data_back";
	private String dbFile = "data.db";

	public DownloadedNovelManager() {
		init();
	}

	public void init() {
		checkDir(dataDir, confDir, data_backupDir);
	}

	public static void checkDir(String... dirs) {
		if (dirs != null && dirs.length > 0)
			for (String dir : dirs) {
				if (!root.resolve(dir).toFile().exists()) {
					root.resolve(dir).toFile().mkdirs();
				} else if (!root.resolve(dir).toFile().isDirectory()) {
					System.out.println(dir + " directory should be present! at :" + root.toString());
				}
			}
	}

	private Path getDataDir() {
		return root.resolve(dataDir);
	}

	private Path getDataBackupDir() {
		return root.resolve(data_backupDir);
	}

	private String getNewUUID() {
		return UUID.randomUUID().toString();
	}

	public NovelData getNovelData(String name, String urlLink) {
		File file = getDataDir().resolve(dbFile).toFile();
		Map<String, String> map = new HashMap<>();
		map = (Map<String, String>) readDataFromFile(file);
		if (map != null && map.containsKey(urlLink)) {
			String novelDataFilenName = map.get(urlLink);
			File novelDataFile = getDataDir().resolve(novelDataFilenName).toFile();
			return (NovelData) readDataFromFile(novelDataFile);
		}
		return null;
	}

	private Object readDataFromFile(File file) {
		Object obj = null;
		if (file.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				obj = ois.readObject();
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	public void putNoveldata(NovelData data) {
		File file = getDataDir().resolve(dbFile).toFile();
		boolean updateDb = true;
		Map<String, String> map = new HashMap<>();
		map = (Map<String, String>) readDataFromFile(file);
		String novelDataFilenName = getNewUUID();
		if (map != null && map.containsKey(data.getUrlLink())) {
			novelDataFilenName = map.get(data.getUrlLink());
			updateDb = false;
		} else {
			if(map==null)
			map = new HashMap<>();
			map.put(data.getUrlLink(), novelDataFilenName);
		}
		File novelDataFile = getDataDir().resolve(novelDataFilenName).toFile();
		WriteFile(data, novelDataFile);
		WriteFile(map, file);

	}

	private void WriteFile(Object data, File fileToWrite) {
		if (createBackup(fileToWrite)) {
			System.out.println("BackupCreated for : " + fileToWrite);
		} else {
			System.out.println("Creation of backup failed for file:" + fileToWrite);
		}
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileToWrite));
			out.writeObject(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean createBackup(File fileToBackup) {
		if (fileToBackup!=null && fileToBackup.exists()) {
			try {
				Path copy = Files.copy(FileSystems.getDefault().getPath(fileToBackup.toString()),
						getDataBackupDir().resolve(fileToBackup.getName()), StandardCopyOption.REPLACE_EXISTING);
				if (copy != null) {
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public static void main(String args[]) {
		DownloadedNovelManager mgr = new DownloadedNovelManager();
		NovelData data = new NovelData();
		data.setUrlLink("SomeURLLink");
		mgr.putNoveldata(data);
		System.out.println(mgr.getNovelData(null, data.getUrlLink()));
	}
}
