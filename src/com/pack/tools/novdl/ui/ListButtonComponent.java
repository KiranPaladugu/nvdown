package com.pack.tools.novdl.ui;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class ListButtonComponent extends JButton {
	public ListButtonComponent(String name) {
		super(name);
	}

	public ListButtonComponent() {
		super();
	}

	public ListButtonComponent(Action action) {
		super(action);
	}

	public ListButtonComponent(String name, Icon icon) {
		super(name, icon);
	}
}
