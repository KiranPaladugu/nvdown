package com.pack.tools.novdl.service.api;

import java.util.List;

public interface Service extends Operation {
	public Operation[] getSupportedOperations();

	String getServiceName();

	List<Operation> getOperationsList();
}
