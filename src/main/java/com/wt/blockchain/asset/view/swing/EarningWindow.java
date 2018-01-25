package com.wt.blockchain.asset.view.swing;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.wt.blockchain.asset.dto.EarningDto;
import com.wt.blockchain.asset.util.CommonUtil;

public class EarningWindow extends BaseWindow {

	private static final long serialVersionUID = 4451854259633603697L;
	private JFrame frame;
	private JTable table;
	private List<EarningDto> earningList = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new EarningWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EarningWindow() {
		initialize();
		refresh();
	}

	public void show() {
		refresh();
	}

	public void refresh() {
		this.frame.setVisible(true);
		// TODO
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		resetFrame(frame);

		JButton button = new JButton("结算");

		table = new JTable();
		TableModel dataModel = getTableModel();
		table = new JTable(dataModel);
		// 添加排序
		table.setRowSorter(new TableRowSorter<TableModel>(dataModel));
		JScrollPane jsp = new JScrollPane(table);

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addContainerGap()
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(jsp,
								GroupLayout.PREFERRED_SIZE, 417, GroupLayout.PREFERRED_SIZE))
						.addComponent(button))
				.addContainerGap(21, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(button)
						.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jsp, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE).addGap(180)));
		frame.getContentPane().setLayout(groupLayout);
	}

	@SuppressWarnings("unchecked")
	public AbstractTableModel getTableModel() {
		return new AbstractTableModel() {
			String[] names = { "结算日期", "总投入金额", "当期投入金额", "总市值", "增长率(去当期)" };

			private static final long serialVersionUID = 4354562018087682852L;

			@SuppressWarnings("rawtypes")
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
					return getData().get(row).getSettlement_date();
				}
				case (1): {
					return CommonUtil.formateNum(getData().get(row).getTotal_invest());
				}
				case (2): {
					return CommonUtil.formateNum(getData().get(row).getCurrent_invest());
				}
				case (3): {
					return CommonUtil.formateNum(getData().get(row).getTotal_value());
				}
				case (4): {
					return getData().get(row).getIncrease_rate() + "%";
				}
				default:
					return "";
				}
			}
		};
	}

	public List<EarningDto> getData() {
		if (earningList == null) {
			// TODO
			earningList = new ArrayList<>();
		}

		return earningList;
	}
}
