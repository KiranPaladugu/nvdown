package com.pack.tools.novdl.comp;

import java.util.ArrayList;
import java.util.List;

import com.pack.tools.novdl.db.NovelData;

public class FileCompositeExecutor implements FileComposite {

	private List<FileComposite> compositeList = new ArrayList<>();
	private boolean isUnique;
	private boolean exitOnFail;

	public void setExitOnFail(boolean value) {
		this.exitOnFail = value;
	}

	public boolean isExitOnFail() {
		return this.exitOnFail;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean value) {
		this.isUnique = value;
	}

	public FileCompositeExecutor(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public FileCompositeExecutor() {
	}

	public boolean addComposite(FileComposite composite) {
		if (isUnique) {
			if (!compositeList.contains(composite)) {
				return compositeList.add(composite);
			}
		} else {
			return compositeList.add(composite);
		}
		return false;
	}

	@Override
	public boolean execute(NovelData data) {
		boolean result = true;
		for (FileComposite composite : compositeList) {
			boolean tmpresult = composite.execute(data);
			if (exitOnFail && !tmpresult) {
				result = tmpresult;
				break;
			}
			if (!tmpresult) {
				result = tmpresult;
			}
		}
		return result;
	}

}
