package com.wt.blockchain.asset.view.swing;

import static com.wt.blockchain.asset.util.CommonUtil.formateNum;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.mysql.jdbc.StringUtils;
import com.wt.blockchain.asset.dao.CoinDetailDao;
import com.wt.blockchain.asset.dto.CoinDetail;
import com.wt.blockchain.asset.util.CommonUtil;
import com.wt.blockchain.asset.util.Constatns;

public class HistoryWindow {

	private JFrame frame;
	private JLabel coinNameLA = new JLabel("代币：");
	private JLabel coinNameLA2 = new JLabel("coinName");
	private JButton settelmentBtn = new JButton("结算");
	private JButton cancelBtn = new JButton("撤销");
	private JTextPane historyTF = new JTextPane();
	private JScrollPane jsp = new JScrollPane(historyTF);
	private String coinName;

	private CoinDetailDao coinDetailDao = new CoinDetailDao();

	// 拼装历史数据
	private String split = "  ";
	private String line = "\n";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new HistoryWindow("BCH");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void show(String coinName) {
		this.frame.setVisible(true);
		initDate(coinName);
	}

	/**
	 * Create the application.
	 */
	public HistoryWindow(String coinName) {
		initialize(coinName);
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String coinName) {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		historyTF.setEditable(false);

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(16)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(jsp, GroupLayout.PREFERRED_SIZE, 765, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup().addComponent(coinNameLA)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(coinNameLA2)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(settelmentBtn)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(cancelBtn)))
						.addContainerGap(19, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(14)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(settelmentBtn)
								.addComponent(coinNameLA).addComponent(coinNameLA2).addComponent(cancelBtn))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(jsp, GroupLayout.PREFERRED_SIZE, 420, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(109, Short.MAX_VALUE)));
		frame.getContentPane().setLayout(groupLayout);

		initDate(coinName);
		addListener();

	}

	private void initDate(String coinName) {
		this.coinName = coinName;
		coinNameLA2.setText(coinName);
		showHistory();

	}

	private void showHistory() {
		// 获取明细和汇总数据
		List<CoinDetail> detailList = coinDetailDao.query(this.coinName);

		List<String> printInfo = new ArrayList<>();

		double buyNum = 0.0;
		double sellNum = 0.0;
		double buyMoney = 0.0;
		double sellMoney = 0.0;

		for (int i = 0; i < detailList.size(); i++) {

			StringBuffer sb = new StringBuffer("");
			CoinDetail detail = detailList.get(i);

			sb.append("[" + CommonUtil.formateDate(detail.getCreate_Date()) + "]  ");

			// 是结算生成的明细数据
			if (isSettlement(detail.getIs_settlement())) {
				getSettlementLog(buyNum, sellNum, buyMoney, sellMoney, sb, detail.getSettlement_price());

				buyNum = 0.0;
				sellNum = 0.0;
				buyMoney = 0.0;
				sellMoney = 0.0;
			} else {
				// 不是结算生成的明细数据
				if (Constatns.OpType.buy.equals(detail.getOp_type())) {
					setString(sb, "买入，数量：" + formateNum(detail.getCoin_num(), "#0.0000"));
					buyNum += detail.getCoin_num();
					buyMoney += detail.getTotal_cost();
				} else {
					setString(sb, "卖出，数量：" + formateNum(detail.getCoin_num(), "#0.0000"));
					sellNum += detail.getCoin_num();
					sellMoney += detail.getTotal_cost();
				}

				setString(sb, "总花费:" + formateNum(detail.getTotal_cost()));
				setString(sb, "单价:" + formateNum(detail.getAvarange_price()));
				setString(sb, "单位:" + detail.getMonetary_unit(), true);
			}

			printInfo.add(sb.toString());
		}

		StringBuffer printInfoStr = new StringBuffer("");

		for (int i = printInfo.size() - 1; i >= 0; i--) {
			printInfoStr.append(printInfo.get(i));
		}

		historyTF.setText(printInfoStr.toString());
	}

	private void getSettlementLog(double buyNum, double sellNum, double buyMoney, double sellMoney, StringBuffer sb,
			double price) {
		// 汇总
		double totalMoney = (buyNum - sellNum) * price + sellMoney; // 总市值
		double rate = (totalMoney / buyMoney - 1) * 100; // 收益率
		setString(sb, "结算，买:" + formateNum(buyNum, "#.####"));
		setString(sb, "卖:" + formateNum(sellNum, "#.####"));
		setString(sb, "投入:" + formateNum(buyMoney, "#.##"));
		setString(sb, "收入:" + formateNum(sellMoney, "#.##"));
		setString(sb, "总价值:" + formateNum(totalMoney, "#.##"));
		setString(sb, "收益率:" + formateNum(rate, "#.##") + "%", true);

	}

	private void setString(StringBuffer sb, String str) {
		setString(sb, str, false);
	}

	private void setString(StringBuffer sb, String str, Boolean isChangeLine) {
		sb.append(str).append(split);

		if (isChangeLine) {
			sb.append(line);
		}
	}

	private boolean isSettlement(Integer isSettlement) {

		if (isSettlement != null && Constatns.SETTLEMENT_STATE.IS_SETTLEMENT == isSettlement) {
			return true;
		} else {
			return false;
		}
	}

	private void addListener() {
		// 结算
		settelmentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					coinDetailDao.doSettlement(coinName);
					JOptionPane.showMessageDialog(null, "结算成功！");
					showHistory();
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(null, exc.getMessage());
				}
			}
		});

		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String result = coinDetailDao.doCancel(coinName);
					JOptionPane.showMessageDialog(null, StringUtils.isNullOrEmpty(result) ? "撤销成功！" : "撤销失败," + result);
					showHistory();
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(null, "撤销失败！" + exc.getMessage());
				}

			}
		});
	}
}
