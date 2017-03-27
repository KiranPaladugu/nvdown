package com.pack.tools.novdl.ui;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pack.tools.novdl.db.NovelData;
import com.pack.tools.novdl.event.UpdateEvent;
import com.pack.tools.novdl.listener.UpdateListener;
import com.pack.tools.novdl.ui.util.LayoutUtils;
import com.pack.tools.novdl.ui.util.ViewPanel;

public class NewUpdatesListView extends ViewPanel implements UpdateListener,ListSelectionListener {
	// private ViewPanel view;
	// private Vector<NovelData> list = new Vector<>();
	JScrollPane scrollPane;
	private LayoutUtils util = LayoutUtils.getUtils("updates");
	private static final NewUpdatesListView updateView = new NewUpdatesListView();
	private DefaultListModel<NovelData> model;
	private JList<NovelData> updatesList;
	private JFrame window;
	private BookDataViewPanel bookViewPanel;
	private int lastKnownIndex=-1;

	public static final NewUpdatesListView get() {
		return updateView;
	}
	
	public void setFrame(JFrame frame){
		this.window= frame;
		window.setAlwaysOnTop(true);
	}
	/*
	 * public void addUpdate(final Component comp) { view.add(comp,
	 * util.getNextRowConstaints()); view.revalidate(); scrollPane.revalidate();
	 * this.revalidate(); this.repaint(); }
	 */

	private NewUpdatesListView() {
		init();
	}

	private void init() {
		model = new DefaultListModel<>();
		updatesList = new JList<NovelData>(model);
		// view = new ViewPanel(true, ViewPanel.VERTICAL_FULL);
		scrollPane = new JScrollPane(updatesList);
		this.add(scrollPane);
		this.setLayout(new GridLayout());
		this.setExpandPolicy(ViewPanel.BOTH);
		this.setBorder(BorderFactory.createTitledBorder("New Updates*"));
		updatesList.addListSelectionListener(this);
	}

	public synchronized void update(NovelData data) {
		DefaultListModel<NovelData> model = (DefaultListModel<NovelData>) updatesList.getModel();
		int size = model.size();
		boolean exist = false;
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (model.get(i).getFilename().equals(data.getFilename())) {
					exist = true;
					break;
				}
			}
		}
		if (!exist) {
			model.addElement(data);
			notifyWindow();
			// ListButtonComponent comp = new
			// ListButtonComponent(data.getName());
			// comp.setData(data);
			// addUpdate(comp);
		}
	}

	private void notifyWindow() {
		if(this.window!=null){
			window.requestFocus();
		}
		
	}

	public synchronized void delete(NovelData data) {
		DefaultListModel<NovelData> model = (DefaultListModel<NovelData>) updatesList.getModel();
		int size = model.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (model.get(i).getFilename().equals(data.getFilename())) {
					model.remove(i);
					break;
				}
			}
		}
	}

	@Override
	public void onUpdate(UpdateEvent ue) {
		if (ue.getEventObject() instanceof NovelData) {
			update((NovelData) ue.getEventObject());
		}
	}

	public BookDataViewPanel getBookViewPanel() {
		return bookViewPanel;
	}

	public void setBookViewPanel(BookDataViewPanel bookViewPanel) {
		this.bookViewPanel = bookViewPanel;
	}

	@Override
	public void valueChanged(ListSelectionEvent lse) {
		if(this.bookViewPanel==null)return;
		int index = this.updatesList.getSelectedIndex();
		if(index!=-1 && index!=lastKnownIndex){
			bookViewPanel.update(updatesList.getSelectedValue(), false, true);
		}
	}
}
