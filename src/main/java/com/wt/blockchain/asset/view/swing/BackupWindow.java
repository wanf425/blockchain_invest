package com.wt.blockchain.asset.view.swing;

import static com.wt.blockchain.asset.util.CommonUtil.formateNum;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.wt.blockchain.asset.dao.BackupDao;
import com.wt.blockchain.asset.dao.CoinDetailDao;
import com.wt.blockchain.asset.dao.ConstantsDao;
import com.wt.blockchain.asset.dto.CoinDetail;
import com.wt.blockchain.asset.dto.Constants;
import com.wt.blockchain.asset.util.CommonUtil;
import com.wt.blockchain.asset.util.Constatns;
import com.wt.blockchain.asset.util.Constatns.ConstatnsKey;

public class BackupWindow extends BaseWindow {

	private JFrame frame;
	private JButton backupBtn = new JButton("备份");
	private JButton rollbackBtn = new JButton("回滚");
	private ConstantsDao constantsDao = new ConstantsDao();
	private CoinDetailDao coinDetailDao = new CoinDetailDao();
	private BackupDao backupDao = new BackupDao();
	private final JEditorPane detailLogPane = new JEditorPane();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new BackupWindow();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public BackupWindow() {
		initialize();
		show();
	}

	public void show() {
		this.frame.setVisible(true);
		refresh();
	}

	private void refresh() {
		initDate();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 308);
		resetFrame(frame);
		JScrollPane jsp = new JScrollPane(detailLogPane);

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(jsp))
								.addGroup(groupLayout.createSequentialGroup().addComponent(backupBtn)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(rollbackBtn)))
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(backupBtn)
								.addComponent(rollbackBtn))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(jsp, GroupLayout.PREFERRED_SIZE, 233, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(17, Short.MAX_VALUE)));
		frame.getContentPane().setLayout(groupLayout);

		initDate();
		addlistener();
	}

	private void addlistener() {
		// 备份
		backupBtn.addActionListener(t -> {
			boolean result = backupDao.doBackUp();
			JOptionPane.showMessageDialog(null, result ? "备份成功！" : "备份失败！");
			refresh();
		});

		// 回滚
		rollbackBtn.addActionListener(t -> {
			boolean result = backupDao.doRollBack();
			JOptionPane.showMessageDialog(null, result ? "回滚成功！" : "回滚失败！");
			refresh();
		});
	}

	public void initDate() {
		Constants maxid = constantsDao.queryByType(ConstatnsKey.MAX_DETAIL_ID).get(0);

		List<CoinDetail> detailList = coinDetailDao.queryById(Integer.valueOf(maxid.getValue()));

		StringBuffer sb = new StringBuffer("");
		for (CoinDetail detail : detailList) {
			sb.append("[" + CommonUtil.formateDate(detail.getCreate_Date()) + "]  ");
			sb.append("币种：" + detail.getCoin_name());

			if (Constatns.OpType.buy.equals(detail.getOp_type())) {
				sb.append(" 买入，数量：" + formateNum(detail.getCoin_num(), "#0.0000"));
			} else {
				sb.append(" 卖出，数量：" + formateNum(detail.getCoin_num(), "#0.0000"));
			}

			sb.append(" 总花费：" + formateNum(detail.getTotal_cost(), "#0.0000")).append(" ")
					.append(detail.getMonetary_unit());
			sb.append("\n");
		}

		detailLogPane.setText(sb.toString());
	}
}
