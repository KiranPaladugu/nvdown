package com.pack.tools.novdl.comp;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.pack.tools.novdl.db.BookData;
import com.pack.tools.novdl.db.ChapterData;
import com.pack.tools.novdl.db.NovelData;

public class UpdateNovelDataComposite implements FileComposite {

	@Override
	public boolean execute(NovelData data) {
		if (data == null) {
			return true;
		}

		BookData bookData = data.getBookData();
		ArrayList<ChapterData> allChapters = bookData.getAllChapters();
		for (ChapterData chdata : allChapters) {
			File file = chdata.getFile();
			if (file != null && file.exists()) {
				chdata.setDownloadDate(new Date(file.lastModified()));
				chdata.setDownloaded(true);
				chdata.setId(getIdFromName(file, data.getName()));
			}
		}
		return true;
	}

	private String getIdFromName(File file, String bookName) {
		if (file == null) {
			return "NA";
		}
		String fileName = file.getName();
		int start = fileName.indexOf(bookName.trim());
		if (start != -1) {
			int end = fileName.lastIndexOf(".xhtml");
			if (end != -1) {
				return fileName.substring(start + bookName.length(), end).replace("_CH_", " Chapter ").replaceAll("_", "");
			}
		} else {
			int end = fileName.lastIndexOf(".xhtml");
			fileName.substring(0, end).replace("_CH_", " Chapter ");
		}
		return file.getName();
	}

}
