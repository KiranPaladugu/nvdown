package com.pack.tools.novdl.ui;

import javax.swing.JButton;

import com.pack.tools.novdl.ui.service.ButtonActionService;
import com.pack.tools.novdl.ui.util.LayoutUtils;
import com.pack.tools.novdl.ui.util.ViewPanel;

public class ActionView extends ViewPanel {
	private ButtonActionService save, clear, hide, close, markAll;

	public ActionView() {
		init();
		this.add(LayoutUtils.arrangeComponantsInColoumn(save, markAll, clear, hide, close));
	}

	public void init() {
		save = new ButtonActionService("Save");
		clear = new ButtonActionService("Clear");
		hide = new ButtonActionService("Hide");
		close = new ButtonActionService("Close");
		markAll = new ButtonActionService("Mark all notified");

	}
}
