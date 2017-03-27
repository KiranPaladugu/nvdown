package com.cont;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLUtilities {
	private String htmlData;
	private Document document;

	private HTMLUtilities(String htmlData) {
		this.htmlData = htmlData;
		document = Jsoup.parse(htmlData);
	}

	public static HTMLUtilities HtmlUtility(String htmlData) {
		if (htmlData == null || htmlData.equals("")) {
			return null;
		}
		return new HTMLUtilities(htmlData);
	}
	
	public Element getElementById(String value){
		return document.getElementById(value);
	}
	
	public Elements getElementByClass(String className){
		return document.getElementsByClass(className);
	}
	
	public Elements getAllElements(){
		return document.getAllElements();
	}
	
	public String getHtmlData(){
		return htmlData;
	}
	
	public Document getHtmlDocument(){
		return document;
	}
}
