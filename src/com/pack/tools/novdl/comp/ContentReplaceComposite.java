package com.pack.tools.novdl.comp;

public class ContentReplaceComposite extends AbstractComposite {
	private String regex = "";
	private String replacement = "";

	public ContentReplaceComposite(String regex, String replacement) {
		this.regex = regex;
		this.replacement = replacement;
	}

	@Override
	public String execute(String data) {
		String tmp = data;
		if ((regex != null && regex.length() > 0) && (replacement != null)) {
			tmp = tmp.replace(regex, replacement);
		}
		return tmp;
	}

}
