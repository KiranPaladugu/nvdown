package com.pack.tools.novdl.comp;

public class FirstContentRemovalComposite extends AbstractComposite {

	private String start = "";
	private String end = "";

	public FirstContentRemovalComposite(String start, String end) {
		this.start = start;
		this.end = end;
	}

	public String execute(String data) {
		String tmpData = data;
		if ((data != null && data.length() > 0) && (start != null && start.trim().length() > 0)
				&& (end != null && end.trim().length() > 0)) {
			int startIndex = tmpData.indexOf(start);
			if (startIndex != -1) {
				int endIndex = tmpData.indexOf(end, startIndex + start.length());
				if (endIndex != -1) {
					String newData = tmpData.substring(0, startIndex);
					newData += tmpData.substring(endIndex + end.length());
					tmpData = newData;
				}
			}
		}
		return tmpData;
	}

}
