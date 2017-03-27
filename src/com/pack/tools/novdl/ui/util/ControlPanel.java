package com.pack.tools.novdl.ui.util;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public final static int HORIZONTAL_FULL = GridBagConstraints.HORIZONTAL;
	public final static int VERTICAL_FULL = GridBagConstraints.VERTICAL;
	public final static int BOTH = GridBagConstraints.BOTH;
	public final static int NONE = GridBagConstraints.NONE;
	public final static int HALF = 5001;
	public final static int HORIZONTAL_HALF = 5002;
	public final static int VERTICAL_HALF = 5003;
	public static final int QAURTER = 5004;
	public static final int TEN_PERCENT = 1010;
	public static final int TWENTY_PERCENT = 1020;
	public static final int THIRTY_PERCENT = 1030;
	public static final int FOURTY_PERCENT = 1040;
	public static final int FIFTY_PERCENT = 1050;
	public static final int SIXTY_PERCENT = 1060;
	public static final int SEVENTY_PERCENT = 1070;
	public static final int EIGHTY_PERCENT = 1080;
	public static final int NINTY_PERCENT = 1090;
	private boolean isExpandable = true;
	private int expandPolicy = BOTH;

	private double weightX = 1;
	private double weightY = 1;

	/**
	 * @return the weightx
	 */
	public double getWeightX() {
		return weightX;
	}

	/**
	 * @param weightX
	 *            the weightx to set
	 */
	public ControlPanel setWeightX(final double weightX) {
		this.weightX = weightX;
		return this;
	}

	/**
	 * @return the weighty
	 */
	public double getWeightY() {
		return weightY;
	}

	/**
	 * @param weightY
	 *            the weighty to set
	 */
	public ControlPanel setWeightY(final double weightY) {
		this.weightY = weightY;
		return this;
	}

	public ControlPanel() {
	}

	public ControlPanel(final boolean expandble, final int expandPolicy) {
		this.isExpandable = expandble;
		this.expandPolicy = expandPolicy;
	}

	public boolean isExpandable() {
		return this.isExpandable;
	}

	public ControlPanel setExpandable(final boolean expand) {
		this.isExpandable = expand;
		return this;
	}

	public int getExpandPolicy() {
		return this.expandPolicy;
	}

	public ControlPanel setExpandPolicy(final int expandPolicy) {
		if (expandPolicy == NONE)
			this.isExpandable = false;
		else
			this.isExpandable = true;
		this.expandPolicy = expandPolicy;
		return this;
	}
}
