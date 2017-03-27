package com.pack.tools.novdl.ui;

import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pack.tools.novdl.db.NovelData;

public class ContrllerPanel extends JPanel {
	private JLabel name;
	private JComboBox<NovelData> updatesList;
	private Vector<NovelData> updates;
	private JLabel current;

	public ContrllerPanel() {
		// TODO Auto-generated constructor stub
	}
}
