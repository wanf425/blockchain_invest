package com.wt.blockchain.asset.view.swing;

import javax.swing.JFrame;

public class BaseWindow extends JFrame {

	protected void resetFrame(JFrame frame) {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
	}
}
