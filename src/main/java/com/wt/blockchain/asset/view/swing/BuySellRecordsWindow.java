package com.wt.blockchain.asset.view.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.mysql.jdbc.StringUtils;
import com.wt.blockchain.asset.dao.CoinDetailDao;
import com.wt.blockchain.asset.dao.CoinSummaryDao;
import com.wt.blockchain.asset.dao.ConstantsDao;
import com.wt.blockchain.asset.dto.CoinDetail;
import com.wt.blockchain.asset.dto.CoinSummary;
import com.wt.blockchain.asset.dto.Constants;
import com.wt.blockchain.asset.util.CommonUtil;
import com.wt.blockchain.asset.util.ConstatnsUtil.ConstatnsKey;
import com.wt.blockchain.asset.util.ConstatnsUtil.Currency;
import com.wt.blockchain.asset.util.ConstatnsUtil.Market;
import com.wt.blockchain.asset.util.ConstatnsUtil.OpType;
import com.wt.blockchain.asset.util.LogUtil;

import net.miginfocom.swing.MigLayout;

public class BuySellRecordsWindow {

	private ConstantsDao constantsDao = new ConstantsDao();
	private CoinDetailDao coinDetailDao = new CoinDetailDao();
	private CoinSummaryDao coinSummaryDao = new CoinSummaryDao();
	private DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	private Map<String, CoinSummary> summaryMap = new HashMap<>();

	private JFrame frame;
	private JLabel opTypeLA;
	private JLabel coinNameLA;
	private JComboBox<Constants> opTypeCB;
	private JComboBox<Constants> coinNameCB;
	private JLabel coinNumLA;
	private JTextField coinNumTF;
	private JLabel totalCostLA;
	private JTextField totalCostTF;
	private JComboBox<Constants> totalCostCB;
	private JLabel serviceChargePercentLA;
	private JTextField serviceChargePercentTF; // 手续费
	private JLabel serviceChargeLA;
	private JTextField serviceChargeTF;
	private JLabel opTimeLA;
	private JTextField opTimeTF;
	private JLabel avarangeLA;
	private JTextField avarangeTF;
	private JLabel opMarketLA;
	private JComboBox<Constants> opMarketCB;
	private JButton saveBtn;
	private JLabel totalCostUSDValue;
	private JLabel accountNumLA;
	private JLabel accountNum;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new BuySellRecordsWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BuySellRecordsWindow() {
		initialize();
		refresh();
	}

	public void show() {
		refresh();
	}

	private void refresh() {
		// 查询汇总数据
		List<CoinSummary> list = coinSummaryDao.queryAll();
		list.forEach(t -> summaryMap.put(t.getCoin_name(), t));

		this.frame.setVisible(true);
		
		Constants c = (Constants)coinNameCB.getSelectedItem();
		CoinSummary cs = summaryMap.get(c.getKey());
	    accountNum.setText(cs != null ? cs.getTotal_cost().toString() : "");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 336, 427);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[][grow][grow]", "[][][][][][][][][][][][][][][]"));

		opTypeLA = new JLabel("操作类型：");
		frame.getContentPane().add(opTypeLA, "cell 0 0,alignx trailing");

		opTypeCB = new JComboBox<Constants>();
		frame.getContentPane().add(opTypeCB, "cell 1 0 2 1,growx");

		coinNameLA = new JLabel("币种：");
		frame.getContentPane().add(coinNameLA, "cell 0 1,alignx trailing");

		coinNameCB = new JComboBox<Constants>();
		frame.getContentPane().add(coinNameCB, "cell 1 1 2 1,growx");

		coinNumLA = new JLabel("交易数量：");
		frame.getContentPane().add(coinNumLA, "cell 0 2,alignx trailing");

		coinNumTF = new JTextField();
		frame.getContentPane().add(coinNumTF, "cell 1 2 2 1,growx");
		coinNumTF.setColumns(10);

		accountNumLA = new JLabel("总数量：");
		frame.getContentPane().add(accountNumLA, "cell 0 3,alignx right");

		accountNum = new JLabel("");
		frame.getContentPane().add(accountNum, "cell 1 3 2 1");

		totalCostLA = new JLabel("交易金额：");
		frame.getContentPane().add(totalCostLA, "cell 0 4,alignx trailing");

		totalCostTF = new JTextField();
		frame.getContentPane().add(totalCostTF, "cell 1 4,growx");
		totalCostTF.setColumns(10);

		totalCostCB = new JComboBox<Constants>();
		frame.getContentPane().add(totalCostCB, "cell 2 4,growx");

		opMarketLA = new JLabel("交易平台：");
		frame.getContentPane().add(opMarketLA, "cell 0 5,alignx trailing");

		opMarketCB = new JComboBox<Constants>();
		frame.getContentPane().add(opMarketCB, "cell 1 5 2 1,growx");

		serviceChargePercentLA = new JLabel("手续费率(千分比)：");
		frame.getContentPane().add(serviceChargePercentLA, "cell 0 6,alignx trailing");

		totalCostUSDValue = new JLabel("");
		frame.getContentPane().add(totalCostUSDValue, "flowx,cell 1 6 2 1");

		serviceChargeLA = new JLabel("手续费：");
		frame.getContentPane().add(serviceChargeLA, "cell 0 7,alignx trailing");

		serviceChargeTF = new JTextField();
		serviceChargeTF.setEditable(false);
		frame.getContentPane().add(serviceChargeTF, "cell 1 7 2 1,growx");
		serviceChargeTF.setColumns(10);

		opTimeLA = new JLabel("操作时间：");
		frame.getContentPane().add(opTimeLA, "cell 0 8,alignx trailing");

		opTimeTF = new JTextField();
		frame.getContentPane().add(opTimeTF, "cell 1 8 2 1,growx");
		opTimeTF.setColumns(10);

		avarangeLA = new JLabel("单价：");
		frame.getContentPane().add(avarangeLA, "cell 0 9,alignx trailing");

		avarangeTF = new JTextField();
		avarangeTF.setEditable(false);
		frame.getContentPane().add(avarangeTF, "cell 1 9 2 1,growx");
		avarangeTF.setColumns(10);

		serviceChargePercentTF = new JTextField();
		frame.getContentPane().add(serviceChargePercentTF, "cell 1 6 2 1,growx");
		serviceChargePercentTF.setColumns(10);

		saveBtn = new JButton("保存");
		frame.getContentPane().add(saveBtn, "cell 2 10,alignx right");

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		initDate();
		addListener();
	}

	private void listenerMethod() {
		String serviceChargePercent = serviceChargePercentTF.getText();
		String totalCost = totalCostTF.getText();
		String cointNum = coinNumTF.getText();

		if (!StringUtils.isNullOrEmpty(totalCost)) {
			BigDecimal totalCostBD = new BigDecimal(totalCost.trim());

			if (!StringUtils.isNullOrEmpty(serviceChargePercent.trim())) {
				BigDecimal serviceChargePercentBD = new BigDecimal(serviceChargePercent.trim());
				BigDecimal serviceCharge = totalCostBD.multiply(serviceChargePercentBD).divide(new BigDecimal(1000));
				serviceChargeTF.setText(new DecimalFormat("#.########").format(serviceCharge));
			}

			if (!StringUtils.isNullOrEmpty(cointNum)) {
				BigDecimal cointNumBD = new BigDecimal(cointNum.trim());
				BigDecimal avarange = totalCostBD.divide(cointNumBD, 8, BigDecimal.ROUND_HALF_UP);
				avarangeTF.setText(new DecimalFormat("#.########").format(avarange));
			}
		}
	}

	/**
	 * 修改账户代币总量
	 */
	private void changeAccountNum() {

		// 操作类型
		Constants opType = (Constants) opTypeCB.getSelectedItem();
		// 币种信息

		Constants constants = (Constants) coinNameCB.getSelectedItem();
		CoinSummary cs = summaryMap.get(constants.getValue());

		Double accountNum = 0.0;
		Double coinNum = CommonUtil.toDouble(coinNumTF.getText());

		if (cs != null) {
			if (OpType.buy.equals(opType.getKey())) {
				accountNum = cs.getCoin_num() + coinNum;
			} else {
				accountNum = cs.getCoin_num() - coinNum;
			}
		}

		this.accountNum.setText(CommonUtil.formateNumDouble(accountNum, "#.########").toString());
	}

	/**
	 * 条件监听事件
	 */
	private void addListener() {
		// 操作下拉框
		opTypeCB.addItemListener(e -> changeAccountNum());
		// 币种下拉框
		coinNameCB.addItemListener(e -> changeAccountNum());
		// 交易数量
		coinNumTF.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				listenerMethod();
				changeAccountNum();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		// 交易金额
		totalCostTF.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				listenerMethod();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		// 手续费率
		serviceChargePercentTF.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String serviceChargePercent = serviceChargePercentTF.getText();

				if (!StringUtils.isNullOrEmpty(serviceChargePercent)) {
					// 不能大于1000
					if (Double.valueOf(serviceChargePercent) > 1000) {
						serviceChargePercentTF.setText("1000");
						serviceChargePercent = "1000";
					}

					// 修改手续费
					String totalCost = totalCostTF.getText();

					if (!StringUtils.isNullOrEmpty(totalCost)) {
						BigDecimal totalCostBD = new BigDecimal(totalCost.trim());
						BigDecimal serviceChargePercentBD = new BigDecimal(serviceChargePercent.trim());
						BigDecimal serviceCharge = totalCostBD.multiply(serviceChargePercentBD)
								.divide(new BigDecimal(1000));
						serviceChargeTF.setText(new DecimalFormat("#.########").format(serviceCharge));
					}
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		// 保存按钮
		saveBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		});
	}

	/**
	 * 保存方法
	 */
	private void doSave() {

		String opType = ((Constants) opTypeCB.getSelectedItem()).getKey();
		String coinName = ((Constants) coinNameCB.getSelectedItem()).getKey();
		String coinNum = coinNumTF.getText();
		String totalCost = totalCostTF.getText();
		String totalCostCurrency = ((Constants) totalCostCB.getSelectedItem()).getKey();
		String serviceCharge = serviceChargeTF.getText();
		String opTime = opTimeTF.getText();
		String avarangePrice = avarangeTF.getText();
		String opMarket = ((Constants) opMarketCB.getSelectedItem()).getKey();

		try {
			// 非空判断
			if (StringUtils.isNullOrEmpty(opType) || StringUtils.isNullOrEmpty(coinName)
					|| StringUtils.isNullOrEmpty(coinNum) || StringUtils.isNullOrEmpty(totalCost)
					|| StringUtils.isNullOrEmpty(totalCostCurrency) || StringUtils.isNullOrEmpty(serviceCharge)
					|| StringUtils.isNullOrEmpty(opTime) || StringUtils.isNullOrEmpty(opMarket)) {
				throw new Exception("缺少必填项");
			}

			// 获取页面数据
			CoinDetail cd = new CoinDetail();
			try {
				cd.setOp_type(opType);
				cd.setCoin_name(coinName);
				cd.setCoin_num(Double.valueOf(coinNum));
				cd.setTotal_cost(Double.valueOf(totalCost));
				cd.setTotal_cost_currency(totalCostCurrency);
				cd.setService_charge(Double.valueOf(serviceCharge));
				cd.setOp_time(df.parse(opTime));
				cd.setAvarange_price(Double.valueOf(avarangePrice));
				cd.setOp_market(opMarket);
				cd.setMonetary_unit(totalCostCurrency);
			} catch (Exception e) {
				LogUtil.print("页面数据类型不正确", e);
				throw new Exception("页面数据类型不正确");
			}

			// 保存
			coinDetailDao.doSave(cd);
			JOptionPane.showMessageDialog(null, "保存成功！");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	/**
	 * 初始化数据
	 */
	private void initDate() {
		// 操作类型 下拉框
		List<Constants> opTypes = constantsDao.queryByType(ConstatnsKey.OP_TYPE);
		CommonUtil.initialComboBox(opTypes, opTypeCB, c -> c.getValue());
		// 币种 下拉框
		List<Constants> coinNames = constantsDao.queryByType(ConstatnsKey.COIN_NAME);
		CommonUtil.initialComboBox(coinNames, coinNameCB, c -> c.getValue());

		// 货币类型 下拉框
		List<Constants> currencyType = constantsDao.queryByType(ConstatnsKey.CURRENCY_TYPE);
		CommonUtil.initialComboBox(currencyType, totalCostCB, c -> c.getValue());
		currencyType.forEach(t -> {
			if (Currency.USDT.equals(t.getKey())) {
				totalCostCB.setSelectedItem(t);
			}
		});

		// 交易平台 下拉框
		List<Constants> market = constantsDao.queryByType(ConstatnsKey.MARKET);
		CommonUtil.initialComboBox(market, opMarketCB, c -> c.getValue());
		market.forEach(t -> {
			if (Market.OKOEX.equals(t.getKey())) {
				opMarketCB.setSelectedItem(t);
			}
		});

		// 手续费
		serviceChargePercentTF.setText("1");
		// 操作时间
		opTimeTF.setText(df.format(new Date()));
	}
}
