package com.pack.tools.novdl.comp;

import com.pack.tools.novdl.db.BookDbManager;
import com.pack.tools.novdl.db.NovelData;

public class NovDataFixRunner {

	public static void main(String args[]) {
		BookDbManager dbman = BookDbManager.getDbManger();
		NovelData[] alldata = dbman.getAllData();
		for (NovelData data : alldata) {
			FileCompositeExecutor executor = new FileCompositeExecutor(true);
			executor.addComposite(new UpdateNovelDataComposite());
			boolean result = executor.execute(data);
			if (result) {
				result = dbman.update(data);
				if (result) {
					System.out.println(String.format("Updated [%s] for book [%s]", data.getFilename(), data.getName()));
				}
			}
		}
	}

}
