package com.pack.tools.novdl.ui.service;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.pack.tools.novdl.service.api.Operation;

public abstract class AbstractOperation implements Operation, ActionListener {

	@Override
	public abstract String getOperationName();

	@Override
	public abstract void actionPerformed(ActionEvent ae);
}
