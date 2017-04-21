package com.pack.tools.novdl.service.api;

import java.util.List;

public interface Service extends Operation {
	public Operation[] getSupportedOperations();
	public String[] getSupportedOperationNames();

	String getServiceName();

	List<Operation> getOperationsList();
}
