package com.wt.blockchain.asset.view.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wt.blockchain.asset.dao.CoinInfoDao;
import com.wt.blockchain.asset.dto.CoinInfo;
import com.wt.blockchain.asset.util.CommonUtil;
import com.wt.blockchain.asset.util.LogUtil;

/**
 * 信息录入
 * 
 * @author wangtao
 *
 */
public class CoinInfoWindow extends BaseWindow {
	private static final long serialVersionUID = -9191839187194468465L;
	private CoinInfoDao coinInfoDao = new CoinInfoDao();
	private JFrame frame;
	private JLabel lbljson = new JLabel("使用JSON格式录入代币信息");
	private JButton formateBtn = new JButton("格式化");
	private JEditorPane cionInfoEP = new JEditorPane();
	private JScrollPane jsp = new JScrollPane(cionInfoEP);
	private final JButton saveBtn = new JButton("保存");
	private BuySellStreamWindow buySellStreamWindow = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new CoinInfoWindow(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CoinInfoWindow(BuySellStreamWindow buySellStreamWindow) {
		this.buySellStreamWindow = buySellStreamWindow;
		initialize();
		refresh();
	}

	public void show() {
		refresh();
	}

	public void refresh() {
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 689, 315);
		resetFrame(frame);

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addGap(22)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lbljson, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addGroup(groupLayout.createSequentialGroup().addComponent(formateBtn)
												.addPreferredGap(ComponentPlacement.RELATED).addComponent(saveBtn))
										.addComponent(jsp, GroupLayout.PREFERRED_SIZE, 647, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)))
				.addContainerGap(17, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(lbljson)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(jsp, GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout
								.createParallelGroup(Alignment.BASELINE).addComponent(saveBtn).addComponent(formateBtn))
						.addGap(14)));
		frame.getContentPane().setLayout(groupLayout);

		initDate();
		addListener();
	}

	private void addListener() {
		formateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cionInfoEP.setText(CommonUtil.formatJson(cionInfoEP.getText()));
			}
		});

		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Gson gson = new Gson();
					List<CoinInfo> list = gson.fromJson(cionInfoEP.getText(), new TypeToken<List<CoinInfo>>() {
					}.getType());
					coinInfoDao.updateAll(list);

					JOptionPane.showMessageDialog(null, "保存成功");
					initDate();
					if (buySellStreamWindow != null) {
						buySellStreamWindow.doQuery();
					}
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(null, "保存失败");
					LogUtil.print("", exc);
				}
			}
		});

	}

	private void initDate() {
		// 货币信息
		List<CoinInfo> list = coinInfoDao.queryAllList();
		Gson gson = new Gson();
		String coinInfo = gson.toJson(list);
		cionInfoEP.setText(CommonUtil.formatJson(coinInfo));
	}
}
