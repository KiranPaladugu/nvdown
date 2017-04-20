package com.pack.tools.novdl.ui;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class TablePopup extends JPopupMenu{
    private JMenuItem open,copy,details;
    private ActionListener actionListener;
    
    public TablePopup(ActionListener listener) {
        this.actionListener = listener;
        init();
    }
    
    private void init(){
        open = new JMenuItem("Open");
        open.addActionListener(actionListener);
        copy = new JMenuItem("Copy");
        copy.addActionListener(actionListener);
        details = new JMenuItem("Details");
        details.addActionListener(actionListener);
        
        this.add(open);
        this.addSeparator();
        this.add(copy);
        this.addSeparator();
        this.add(details);
    }
}

