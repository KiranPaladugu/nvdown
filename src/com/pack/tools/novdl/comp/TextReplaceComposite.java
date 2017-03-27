package com.pack.tools.novdl.comp;

public class TextReplaceComposite extends AbstractComposite {
	private String regex;
	private String replacement;

	public TextReplaceComposite(String regex, String replaceString) {
		this.regex = regex;
		this.replacement = replaceString;
	}

	public String execute(String data) {
		String tmpData = data;
		if (data != null && data.length() > 0 && regex != null && regex.length() > 0 && replacement != null
				&& replacement.length() > 0)
			tmpData = tmpData.replaceAll(regex, replacement);
		return tmpData;
	}
}
