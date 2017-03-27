package com.pack.tools.novdl.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.pack.tools.novdl.service.api.Operation;
import com.pack.tools.novdl.service.api.Service;
import com.pack.tools.novdl.ui.util.LayoutUtils;
import com.pack.tools.novdl.ui.util.ViewPanel;
import com.pack.tools.novdl.ui.util.WindowUtils;

public class UpdateCheckView extends JFrame implements Service, Runnable {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> updatesMap = new LinkedHashMap<>();
	private String[] supportedOperations = { "Save", "Cancel", "Hide", "Exit" };
	private List<Operation> operations = new ArrayList<>();
	private String serviceName = "Main Window";
	private ViewPanel container = new ViewPanel();
	private JLabel currentLink = new JLabel(" ");
	private NewUpdatesListView listener;

	public UpdateCheckView() {

	}

	private void init() {
		setLookAndFeel();
		this.getContentPane().setLayout(new GridLayout());
		container.setLayout(new GridBagLayout());
		BookDataViewPanel bookViewPanel = new BookDataViewPanel();
		currentLink.setBorder(BorderFactory.createTitledBorder("Link url"));
		listener = NewUpdatesListView.get();
		listener.setFrame(this);
		listener.setBookViewPanel(bookViewPanel);
		this.getContentPane()
				.add(LayoutUtils.arrangeComponantsInRow(
						LayoutUtils.arrangeComponantsInColoumn(
								LayoutUtils.arrangeComponantsInRow(false, GridBagConstraints.HORIZONTAL,
										new ControllerPanel(bookViewPanel, currentLink).setExpandPolicy(ViewPanel.HORIZONTAL_HALF), currentLink),
								(NewUpdatesListView) listener).setExpandPolicy(ViewPanel.HORIZONTAL_FULL),
						bookViewPanel, new ActionView().setExpandPolicy(ViewPanel.HORIZONTAL_FULL)));

		setSize(800, 800);
		WindowUtils.setCenterLocation(this);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		WindowUtils.addHideOnEscapeListener(this);
		// this.getContentPane().add(container);
	}

	public void setLookAndFeel() {
		String lnfName = "javax.swing.plaf.metal.MetalLookAndFeel";
		// String lnfName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		// String lnfName =
		// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try {
			UIManager.setLookAndFeel(lnfName);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (UnsupportedLookAndFeelException ex1) {
			System.err.println("Unsupported LookAndFeel: " + lnfName);
		} catch (ClassNotFoundException ex2) {
			System.err.println("LookAndFeel class not found: " + lnfName);
		} catch (InstantiationException ex3) {
			System.err.println("Could not load LookAndFeel: " + lnfName);
		} catch (IllegalAccessException ex4) {
			System.err.println("Cannot use LookAndFeel: " + lnfName);
		}
	}

	@Override
	public Operation[] getSupportedOperations() {
		return operations.toArray(new Operation[operations.size()]);
	}

	@Override
	public List<Operation> getOperationsList() {
		return operations;
	}

	@Override
	public String getOperationName() {
		return serviceName;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	public void start() {
		SwingUtilities.invokeLater(this);
	}

	@Override
	public void run() {
		init();
		this.setVisible(true);
	}

	public static void main(String args[]) {
		UpdateCheckView view = new UpdateCheckView();
		view.start();
	}

}
