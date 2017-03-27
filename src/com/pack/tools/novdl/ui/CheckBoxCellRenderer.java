package com.pack.tools.novdl.ui;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CheckBoxCellRenderer extends DefaultTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		;
		if (column == 1 || column == 2) {
			JCheckBox box = new JCheckBox();
			if (value instanceof Boolean) {
				box.setSelected((boolean) value);
			}
			return box;
		} else
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
