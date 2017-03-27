package com.pack.tools.novdl;

public class CookieManager {

	private static final CookieManager manager = new CookieManager();
	private final CookieStore store = new CookieStore();

	public static CookieManager getCoookieManger() {
		return manager;
	}

	public CookieStore getStore() {
		return store;
	}

}
