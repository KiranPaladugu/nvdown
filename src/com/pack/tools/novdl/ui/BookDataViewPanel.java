package com.pack.tools.novdl.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.pack.tools.novdl.db.BookData;
import com.pack.tools.novdl.db.ChapterData;
import com.pack.tools.novdl.db.NovelData;
import com.pack.tools.novdl.ui.util.ViewPanel;
import com.pack.tools.novdl.util.NameValue;

public class BookDataViewPanel extends ViewPanel implements MouseListener,ActionListener{
	private JTable table;
	private DefaultTableModel tblModel;
	private NovelData displaying;
	private TablePopup popup = new TablePopup(this);

	public BookDataViewPanel() {
		tblModel = new DefaultTableModel(0, 4);
		table = new JTable(tblModel);
		table.addMouseListener(this);
		JScrollPane pane = new JScrollPane(table);
		this.add(pane);
		this.setLayout(new GridLayout());
		this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	}

	public void update(NovelData novData, boolean reload, boolean isUpdate) {
		if (isUpdate) {
			if (novData != null && novData.getUpdates() != null && novData.getUpdates().size() > 0) {
				tblModel = new DefaultTableModel(0, 3) {

					@Override
					public boolean isCellEditable(int row, int column) {
						if (column == 1) {
							return false;
						}
						return true;
					}

					@Override
					public Class<?> getColumnClass(int columnIndex) {
						return super.getColumnClass(columnIndex);
					}

				};

				Object[] identifiers = { "Chapter", "link", "Action" };
				tblModel.setColumnIdentifiers(identifiers);
				table.setModel(tblModel);
				List<NameValue> updates = novData.getUpdates();
				for (NameValue update : updates) {
					Object[] obj = { update.getName().replace("_CH_", " Chapter "), update.getValue(), "Open" };
					tblModel.addRow(obj);
				}
			}
		} else {
			if (novData == null || (displaying != null && displaying.getFilename().equals(novData.getFilename()) && !reload)) {
				return;
			}
			tblModel = new DefaultTableModel(0, 5) {
				@Override
				public boolean isCellEditable(int row, int column) {
					if (column == 1) {
						return false;
					}
					return true;
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 1 || columnIndex == 2) {
						return Boolean.class;
					} else
						return super.getColumnClass(columnIndex);
				}
			};
			Object[] identifiers = { "Chapter", "Downloaded", "Notified", "Available", "Action" };
			tblModel.setColumnIdentifiers(identifiers);
			// table.setDefaultRenderer(table.getColumnClass(1), new
			// CheckBoxCellRenderer());
			// table.setDefaultRenderer(table.getColumnClass(1), new
			// CheckBoxCellRenderer());
			table.setModel(tblModel);
			BookData bookData = novData.getBookData();
			ArrayList<ChapterData> chdata = bookData.getAllChapters();
			System.out.println("Updating Table with :" + novData.getName());
			for (ChapterData ch : chdata) {
				// System.out.println(String.format("Updating Table with [%s]
				// chap
				// [%s] ",
				// novData.getName(), ch.getFile()));
				Date date = null;
				if (ch.getFile() != null && ch.getFile().exists()) {
					date = new Date(ch.getFile().lastModified());
				}
				if (ch.getId() == null) {
					Object obj[] = { getIdFromName(ch.getFile(), novData.getName()), new Boolean(ch.isDownloaded()),
							ch.isNotified(), date != null ? date.toString() : "Not Available", "Open" };
					tblModel.addRow(obj);
				} else {
					Object obj[] = { ch.getId().replace("_CH_", " Chapter "), ch.isDownloaded(), ch.isNotified(),
							(date != null) ? date.toString() : "Not Available", "Open" };
					tblModel.addRow(obj);
				}
			}
		}
	}

	private String getIdFromName(File file, String bookName) {
		if (file == null) {
			return "NA";
		}
		String fileName = file.getName();
		int start = fileName.indexOf(bookName.trim());
		if (start != -1) {
			int end = fileName.lastIndexOf(".xhtml");
			if (end != -1) {
				return fileName.substring(start + bookName.length(), end).replace("_CH_", " Chapter ").replaceAll("_", "");
			}
		} else {
			int end = fileName.lastIndexOf(".xhtml");
			fileName.substring(0, end).replace("_CH_", " Chapter ");
		}
		return file.getName();
	}
	
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource().equals(table) && e.isPopupTrigger()){
            popup.show(table, e.getX(), e.getY());
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getSource().equals(table) && e.isPopupTrigger()){
            popup.show(table, e.getX(), e.getY());
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getSource().equals(table) && e.isPopupTrigger()){
            popup.show(table, e.getX(), e.getY());
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        
    }
    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source instanceof JMenuItem){
            JMenuItem item = (JMenuItem) source;
            if(item.getText().equals("Copy")){
                int row = this.table.getSelectedRow();
                int col = this.table.getSelectedColumn();
                
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(table.getModel().getValueAt(row, col).toString()), null);
            }
        }
    }
}
