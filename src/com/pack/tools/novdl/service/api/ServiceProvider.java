package com.pack.tools.novdl.service.api;

public interface ServiceProvider extends Service {
	public Service[] getSupportedServices();
	public String[] getSupportedServiceNames();
}
