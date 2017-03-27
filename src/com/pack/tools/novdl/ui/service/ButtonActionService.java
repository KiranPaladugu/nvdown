package com.pack.tools.novdl.ui.service;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.pack.tools.novdl.service.annotation.Opeartion;
import com.pack.tools.novdl.service.api.Operation;

@Opeartion
public class ButtonActionService extends JButton implements Operation, ActionListener {

	private String name;

	public ButtonActionService(String name) {
		super(name);
		this.name = name;
		this.addActionListener(this);
	}

	@Override
	public String getOperationName() {
		return name;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		System.out.println("Action Performed:" + this.name);
	}

}
