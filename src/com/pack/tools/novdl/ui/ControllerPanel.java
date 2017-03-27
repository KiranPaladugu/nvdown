package com.pack.tools.novdl.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pack.tools.novdl.db.BookDbManager;
import com.pack.tools.novdl.db.NovelData;
import com.pack.tools.novdl.ui.util.LayoutUtils;
import com.pack.tools.novdl.ui.util.ViewPanel;

public class ControllerPanel extends ViewPanel implements ItemListener {
	private JComboBox<NovelData> updatesList;
	private Vector<NovelData> updates;;
	private JLabel linkUrl;
	private JLabel id;
	// private JButton go;
	private BookDataViewPanel viewPanel;

	public ControllerPanel(BookDataViewPanel viewPanel, JLabel currentLink) {
		this.linkUrl = currentLink;
		this.viewPanel = viewPanel;
		id = new JLabel("<NONE>");
		updates = new Vector<>(BookDbManager.getDbManger().getAllDataList());
		DefaultComboBoxModel<NovelData> model = new DefaultComboBoxModel<>(updates);
		updatesList = new JComboBox<>(model);
		this.setLayout(new GridLayout());
		updatesList.setBorder(BorderFactory.createTitledBorder("Select Book"));
		id.setBorder(BorderFactory.createTitledBorder("Book id"));
		if (updatesList.getSelectedIndex() != -1) {
			linkUrl.setText(((NovelData) updatesList.getSelectedItem()).getUrlLink());
			id.setText(((NovelData) updatesList.getSelectedItem()).getFilename());
		}
		if (viewPanel != null) {
			viewPanel.update((NovelData) updatesList.getSelectedItem(), false ,false);
		}
		updatesList.addItemListener(this);
		this.add(LayoutUtils.arrangeComponantsInRow(updatesList, id));
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		NovelData data = (NovelData) e.getItem();
		this.linkUrl.setText(data.getUrlLink());
		this.id.setText(data.getFilename());
		if (viewPanel != null && e.getStateChange() == ItemEvent.SELECTED) {
			viewPanel.update(data, false ,false);
		}
	}
}
