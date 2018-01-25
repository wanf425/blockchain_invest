package com.wt.blockchain.asset.view.swing;

import static com.wt.blockchain.asset.util.CommonUtil.formateNum;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.wt.blockchain.asset.dao.CoinSummaryDao;
import com.wt.blockchain.asset.dao.ConstantsDao;
import com.wt.blockchain.asset.dto.CoinSummary;
import com.wt.blockchain.asset.dto.Constants;
import com.wt.blockchain.asset.util.CommonUtil;
import com.wt.blockchain.asset.util.Constatns;
import com.wt.blockchain.asset.util.Constatns.ConstatnsKey;
import com.wt.blockchain.asset.util.LogUtil;

public class BuySellStreamWindow extends BaseWindow {

	private static final long serialVersionUID = 1L;

	private CoinSummaryDao coinSummaryDao = new CoinSummaryDao();
	private ConstantsDao constantsDao = new ConstantsDao();
	private List<CoinSummary> summaryList = null;

	private JPanel contentPane;
	private JTable table;

	private JLabel coinNameLA = new JLabel("币种：");
	private JComboBox<Constants> coinNameCB = new JComboBox<Constants>();
	private JButton queryBtn = new JButton("查询");
	private JButton buySellBtn = new JButton("买卖操作");
	private JButton assetBtn = new JButton("资产统计");
	private JButton infoBtn = new JButton("信息录入");
	private JButton putBtn = new JButton("资金投入");
	private JLabel totalNumLA = new JLabel("资产统计");
	private JButton backUpBtn = new JButton("数据备份");

	private CoinInfoWindow coinInfoWindow = null;
	private BuySellRecordsWindow buySellRecordsWindow = null;
	private PutMoneyWindow putMoneyWindow = null;
	private HistoryWindow historyWindow = null;
	private BackupWindow backupWindow = null;

	private String queryCoinName = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BuySellStreamWindow frame = new BuySellStreamWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BuySellStreamWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		// 表格
		TableModel dataModel = getTableModel();
		table = new JTable(dataModel);
		// 添加排序
		table.setRowSorter(new TableRowSorter<TableModel>(dataModel));
		JScrollPane jsp = new JScrollPane(table);
		// 右对齐
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JLabel.RIGHT);
		// 左对齐
		DefaultTableCellRenderer l = new DefaultTableCellRenderer();
		l.setHorizontalAlignment(JLabel.LEFT);
		table.setDefaultRenderer(Object.class, r);
		table.getColumnModel().getColumn(0).setCellRenderer(l);
		// 列宽
		table.getColumnModel().getColumn(0).setPreferredWidth(15);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING).addGroup(gl_contentPane
				.createSequentialGroup().addContainerGap()
				.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(totalNumLA, GroupLayout.DEFAULT_SIZE, 1002, Short.MAX_VALUE)
						.addComponent(jsp, GroupLayout.DEFAULT_SIZE, 1002, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup().addComponent(coinNameLA)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(coinNameCB, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(queryBtn, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(buySellBtn)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(putBtn, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(infoBtn)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(backUpBtn)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(assetBtn, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap()));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane
				.createSequentialGroup().addGap(14)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(coinNameCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(queryBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(buySellBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(coinNameLA).addComponent(putBtn).addComponent(infoBtn)
						.addComponent(assetBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(backUpBtn))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(jsp, GroupLayout.PREFERRED_SIZE, 199, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(totalNumLA).addGap(92)));
		contentPane.setLayout(gl_contentPane);

		initDate();
		addListener();
	}

	public AbstractTableModel getTableModel() {
		return new AbstractTableModel() {
			String[] names = { "币种", "总数量", "总花费(USD)", "购买均价(USD)", "当前市价(USD)", "收益率(%)", "收益数(USD)", "资产占比(%)",
					"预分配比例(%)" };

			private static final long serialVersionUID = 4354562018087682852L;

			public Class getColumnClass(int column) {
				Class returnValue;
				if ((column >= 0) && (column < getColumnCount())) {
					returnValue = getValueAt(0, column).getClass();
				} else {
					returnValue = Object.class;
				}
				return returnValue;
			}

			public int getColumnCount() {
				return names.length;
			}

			public int getRowCount() {
				return getData().size();
			}

			public String getColumnName(int column) {
				return names[column];
			}

			public Object getValueAt(int row, int col) {
				switch (col) {
				case (0): {
					return getData().get(row).getCoin_name();
				}
				case (1): {
					return CommonUtil.formateNum(getData().get(row).getCoin_num(), "#.########");
				}
				case (2): {
					return getData().get(row).getTotal_cost();
				}
				case (3): {
					return getData().get(row).getAvarange_price();
				}
				case (4): {
					return getData().get(row).getMarket_price();
				}
				case (5): {
					return getData().get(row).getRate_percent();
				}
				case (6): {
					return getData().get(row).getRate_num();
				}
				case (7): {
					return getData().get(row).getAsset_percent();
				}
				case (8): {
					return getData().get(row).getPre_percent();
				}
				default:
					return "";
				}
			}
		};
	}

	public List<CoinSummary> getData() {
		if (summaryList == null) {
			summaryList = querySummary();
		}

		return summaryList;
	}

	/**
	 * 初始化数据
	 */
	private void initDate() {
		// 币种 下拉框
		List<Constants> coinNames = constantsDao.queryByType(ConstatnsKey.COIN_NAME);
		coinNames.add(0, new Constants("", "全部"));
		CommonUtil.initialComboBox(coinNames, coinNameCB, c -> c.getValue());
	}

	private List<CoinSummary> querySummary() {
		List<CoinSummary> list = coinSummaryDao.querySummary(queryCoinName);
		Double totalNum = 0.0;
		Double coinNum = 0.0;
		Double cash = 0.0;
		Double usdtNum = 0.0;

		for (CoinSummary cs : list) {
			if (Constatns.Currency.RMB.equals(cs.getCoin_name())) {
				cash = cs.getCoin_num() * cs.getMarket_price();
				totalNum += cash;
			} else if (Constatns.Currency.USDT.equals(cs.getCoin_name())) {
				usdtNum = cs.getCoin_num();
				totalNum += usdtNum;
			} else {
				totalNum += cs.getCoin_num() * cs.getMarket_price();
				coinNum += cs.getCoin_num() * cs.getMarket_price();
			}
		}

		Double rate = CommonUtil.getExchangeRate();

		StringBuffer sb = new StringBuffer("");
		sb.append("总资产：").append(formateNum(totalNum)).append("（").append(formateNum(totalNum / rate)).append("）");
		sb.append("  代币现值：").append(formateNum(coinNum)).append("（").append(formateNum(coinNum / rate)).append("）");
		sb.append("  USDT现值：").append(formateNum(usdtNum)).append("（").append(formateNum(usdtNum / rate)).append("）");
		sb.append("  现金：").append(formateNum(cash)).append("（").append(formateNum(cash / rate)).append("）");

		totalNumLA.setText(sb.toString());

		return list;
	}

	/**
	 * 添加监听
	 */
	private void addListener() {
		// 查询按钮
		queryBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				queryCoinName = ((Constants) coinNameCB.getSelectedItem()).getKey();
				summaryList = querySummary();
				table.updateUI();
			}
		});

		// 买卖操作按钮
		buySellBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							if (buySellRecordsWindow == null) {
								buySellRecordsWindow = new BuySellRecordsWindow();
							} else {
								buySellRecordsWindow.show();
							}
						} catch (Exception e) {
							LogUtil.print("open buysell err", e);
						}
					}
				});
			}
		});

		putBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							if (putMoneyWindow == null) {
								putMoneyWindow = new PutMoneyWindow();
							} else {
								putMoneyWindow.show();
							}
						} catch (Exception e) {
							LogUtil.print("open buysell err", e);
						}
					}
				});
			}
		});

		// 信息录入
		infoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							if (coinInfoWindow == null) {
								coinInfoWindow = new CoinInfoWindow();
							} else {
								coinInfoWindow.show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});

		// 备份
		backUpBtn.addActionListener(t -> EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if (backupWindow == null) {
						backupWindow = new BackupWindow();
					} else {
						backupWindow.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));

		// 列表双击
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {// 单击鼠标左键

					String coinName = summaryList.get(getSelectedRowIndex()).getCoin_name();

					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								if (historyWindow == null) {
									historyWindow = new HistoryWindow(coinName);
								} else {
									historyWindow.show(coinName);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				}
			}
		});
	}

	private int getSelectedRowIndex() {
		return table.convertRowIndexToModel(table.getSelectedRow());
	}
}
