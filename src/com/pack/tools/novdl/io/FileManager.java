package com.pack.tools.novdl.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.pack.tools.novdl.BookManager;

public class FileManager {
	private Path path, pathFull, downRoot;

	public FileManager() {
		path = BookManager.getPath();
		pathFull = BookManager.getPathFull();
		downRoot = BookManager.getDownloadRoot();
	}

	public File writeToFile(String bookName, String id, String content) {
		File file = null;
		try {
			file = this.path.resolve(bookName).resolve(bookName + "_" + id + ".xhtml").toFile();
			File parent = file.getParentFile();
			if (!parent.exists()) {
				boolean result = parent.mkdirs();
				if (!result) {
					return file;
				}
			}
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
			copyToPool(parent, file);
		} catch (IOException e) {
			e.printStackTrace();
			return file;
		}
		return file;
	}

	public File writeErrFile(String bookName, String id, String content, boolean flag) {
		File file = null;
		try {
			Path errDirPath = downRoot.resolve("errPages");
			File errDir = errDirPath.toFile();
			if (!errDir.exists()) {
				errDir.mkdirs();
			}
			file = errDirPath.resolve(bookName).resolve(bookName + "_" + id + ".html").toFile();
			File parent = file.getParentFile();
			if (!parent.exists()) {
				boolean result = parent.mkdirs();
				if (!result) {
					return file;
				}
			}
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
			copyToPool(parent, file);
		} catch (IOException e) {
			e.printStackTrace();
			return file;
		}
		return file;
	}

	private boolean copyToPool(File sourcePath, File fileToCopy) {
		if (fileToCopy != null && fileToCopy.exists()) {
			try {
				Path dest = this.pathFull.resolve(sourcePath.getName());
				File destDir = dest.toFile();
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				dest = Files.copy(FileSystems.getDefault().getPath(fileToCopy.toString()), dest.resolve(fileToCopy.getName()),
						StandardCopyOption.REPLACE_EXISTING);
				if (dest != null)
					return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
