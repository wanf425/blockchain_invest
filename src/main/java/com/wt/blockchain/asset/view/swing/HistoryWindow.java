package com.wt.blockchain.asset.view.swing;

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
import com.wt.blockchain.asset.dao.CoinInfoDao;
import com.wt.blockchain.asset.dto.CoinDetail;
import com.wt.blockchain.asset.dto.CoinInfo;
import com.wt.blockchain.asset.util.CommonUtil;
import com.wt.blockchain.asset.util.ConstatnsUtil;
import static com.wt.blockchain.asset.util.CommonUtil.formateNum;

public class HistoryWindow {

	private JFrame frame;
	private JLabel coinNameLA = new JLabel("代币：");
	private JLabel coinNameLA2 = new JLabel("coinName");
	private JButton settelmentBtn = new JButton("结算");
	private JTextPane historyTF = new JTextPane();
	private JScrollPane jsp = new JScrollPane(historyTF);
	private String coinName;

	private CoinDetailDao coinDetailDao = new CoinDetailDao();
	private CoinInfoDao coinInfoDao = new CoinInfoDao();

	// 拼装历史数据
	private String split = "  ";
	private String line = "\n";
	private String space = "      ";

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
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(settelmentBtn)))
						.addContainerGap(19, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(14)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(settelmentBtn)
								.addComponent(coinNameLA).addComponent(coinNameLA2))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(jsp, GroupLayout.PREFERRED_SIZE, 420, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(20, Short.MAX_VALUE)));
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
		List<CoinInfo> coinInfoList = coinInfoDao.queryCoinInfo(this.coinName);

		List<String> printInfo = new ArrayList<>();

		double buyNum = 0.0;
		double sellNum = 0.0;
		double buyMoney = 0.0;
		double sellMoney = 0.0;

		for (int i = 0; i < detailList.size(); i++) {

			StringBuffer sb = new StringBuffer("");
			CoinDetail detail = detailList.get(i);

			// 是结算生成的明细数据
			if (isSettlement(detail.getIs_settlement())) {
				sb.append(line);
				setString(sb, "结算状态:已结算", true);
				sb.append(space);
				sb.append("[" + CommonUtil.formateDate(detail.getCreate_Date()) + "]  ");

				getSettlementLog(buyNum, sellNum, buyMoney, sellMoney, sb, detail.getSettlement_price());

				buyNum = 0.0;
				sellNum = 0.0;
				buyMoney = 0.0;
				sellMoney = 0.0;
			} else {
				// 不是结算生成的明细数据
				sb.append(space);
				sb.append("[" + CommonUtil.formateDate(detail.getOp_time()) + "]  ");
				if (ConstatnsUtil.OpType.buy.equals(detail.getOp_type())) {
					setString(sb, "买:" + formateNum(detail.getCoin_num(), "#.####"));
					buyNum += detail.getCoin_num();
					buyMoney += detail.getTotal_cost();
				} else {
					setString(sb, "卖:" + formateNum(detail.getCoin_num(), "#.####"));
					sellNum += detail.getCoin_num();
					sellMoney += detail.getTotal_cost();
				}

				setString(sb, "总花费:" + formateNum(detail.getTotal_cost(), "#.##"));
				setString(sb, "单价:" + formateNum(detail.getAvarange_price(), "#.##"), true);
			}

			printInfo.add(sb.toString());
		}

		// 存在未结算数据
		if (detailList.size() > 0 && !isSettlement(detailList.get(detailList.size() - 1).getIs_settlement())) {
			StringBuffer sb = new StringBuffer("");
			sb.append(line);
			setString(sb, "结算状态:未结算", true);
			sb.append(space);
			getSettlementLog(buyNum, sellNum, buyMoney, sellMoney, sb, coinInfoList.get(0).getMarket_price());
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
		setString(sb, "买:" + formateNum(buyNum, "#.####"));
		setString(sb, "卖:" + formateNum(sellNum, "#.####"));
		setString(sb, "投入:" + formateNum(buyMoney, "#.##"));
		setString(sb, "收入:" + formateNum(sellMoney, "#.##"));
		setString(sb, "市价:" + formateNum(price, "#.##"));
		setString(sb, "总价值:" + formateNum(totalMoney, "#.##"));
		setString(sb, "收益率:" + formateNum(rate, "#.##") + "%", true);

		// 明细记录头
		sb.append("明细记录：").append(line);
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

	private String getSettlementStauts(String settelmentVersion) {
		return StringUtils.isNullOrEmpty(settelmentVersion) ? "未结算" : "已结算";
	}

	private boolean isSettlement(Integer isSettlement) {

		if (isSettlement != null && ConstatnsUtil.SETTLEMENT_STATE.IS_SETTLEMENT == isSettlement) {
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
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(null, exc.getMessage());
				}

			}
		});
	}
}