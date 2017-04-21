package com.pack.tools.novdl.ui.service;

import java.awt.Component;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentFactory {
	public static final String WILD_CARD_CHAR = "*";
	private final static ComponentFactory factory = new ComponentFactory();
	private final Properties properties = new Properties();
	private String fileName = "com/pack/tools/novdl/resource/Service.conf";

	public static ComponentFactory getComponentFactory() {
		return factory;
	}

	private ComponentFactory() {
		init();
	}

	private void init() {
		try {
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			if (inputStream != null) {
				properties.load(inputStream);
			}
		} catch (Exception e) {
		}
	}

	public String getProperty(String key) {
		if (key == null)
			return key;
		return properties.getProperty(key);
	}

	public List<String> getKeysfromPattern(String regex) {
		List<String> keys = new ArrayList<>();
		Pattern pattern = Pattern.compile(regex);
		Set<Object> keyset = properties.keySet();
		for (Object key : keyset) {
			if (key instanceof String) {
				Matcher matcher = pattern.matcher((String) key);
				if (matcher != null && matcher.matches()) {
					keys.add((String) key);
				}
			}
		}
		return keys;
	}

	public Set<Object> getKeys() {
		return properties.keySet();
	}

	public Component[] generateComponents(String view) {

		return null;
	}

	public static void main(String args[]) {
		System.out.println(ComponentFactory.getComponentFactory().getKeysfromPattern("action.view.button.save.*"));
	}

	public static final String makeKey(String... args) {
		if (args == null | args.length == 0)
			return "";
		StringBuffer buffer = new StringBuffer("");
		for (int i = 0; i < args.length; i++) {
			buffer.append(args[i]);
			if (i < (args.length - 1)) {
				buffer.append('.');
			}
		}
		return buffer.toString();
	}

}
