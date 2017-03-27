/* ********************************************************************************
 * All rights reserved.
 ******************************************************************************* */
package com.pack.tools.novdl.ui.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

public class WindowUtils {

	public synchronized static void addEscapeListener(final JFrame frame) {
		final ActionListener escListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				frame.dispose();
			}
		};

		frame.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public synchronized static void addHideOnEscapeListener(final JFrame frame) {
		final ActionListener escListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				frame.setVisible(false);
			}
		};

		frame.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public synchronized static void setCenterLocation(final Window window) {
		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(dim.width / 2 - window.getSize().width / 2, dim.height / 2 - window.getSize().height / 2);
	}

	public synchronized static GridBagConstraints getDefaultGC() {
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.ipadx = 1;
		gc.ipady = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.insets = new Insets(1, 1, 1, 1);
		return gc;
	}

}
