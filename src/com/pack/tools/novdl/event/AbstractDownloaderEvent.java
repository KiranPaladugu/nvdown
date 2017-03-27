package com.pack.tools.novdl.event;

import java.util.Properties;

import javax.swing.JComponent;

import com.pack.tools.novdl.util.NameValue;

public abstract class AbstractDownloaderEvent {

	private Object eventObject;
	private String url;
	private String referenceUrl;
	private NameValue urlAndId;
	private String name;
	private Properties properties = new Properties();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReferenceURL() {
		return referenceUrl;
	}

	public void setReferenceURL(String referenceUrl) {
		this.referenceUrl = referenceUrl;
	}

	public NameValue getUrlAndId() {
		return urlAndId;
	}

	public void setUrlAndId(NameValue urlAndId) {
		this.urlAndId = urlAndId;
	}

	public Object getEventObject() {
		return eventObject;
	}

	public void setEventObject(Object eventObject) {
		this.eventObject = eventObject;
	}

	public JComponent getComponent() {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addProperty(Object key, Object value) {
		if (key != null && value != null)
			this.properties.put(key, value);
	}

	public Object getProperty(Object key) {
		if (key == null)
			return null;
		return this.properties.get(key);
	}

}
