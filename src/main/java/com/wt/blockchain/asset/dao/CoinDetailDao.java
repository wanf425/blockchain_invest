package com.wt.blockchain.asset.dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wt.blockchain.asset.dto.CoinDetail;
import com.wt.blockchain.asset.dto.CoinInfo;
import com.wt.blockchain.asset.dto.CoinSummary;
import com.wt.blockchain.asset.util.ConstatnsUtil;
import static com.wt.blockchain.asset.util.ConstatnsUtil.getCost;
import com.wt.blockchain.asset.util.LogUtil;
import com.xiaoleilu.hutool.db.Entity;

public class CoinDetailDao extends BaseDao<CoinDetail> {
	CoinInfoDao coinInfoDao = new CoinInfoDao();

	/**
	 * 查询明细数据
	 * 
	 * @param coinName
	 * @return
	 * @throws SQLException
	 */
	public List<CoinDetail> query(String coinName) {
		List<CoinDetail> result = new ArrayList<>();

		try {
			String sql = "select * from tb_coin_detail where COIN_NAME = ? ORDER BY ID ";
			List<Entity> list = runner.query(sql, new Object[] { coinName });

			list.forEach(en -> result.add(en.toBeanIgnoreCase(CoinDetail.class)));
		} catch (Exception e) {
			LogUtil.print("query err", e);
		}

		return result;
	}

	public void putMonet(Double money) throws Exception {
		try {
			session.beginTransaction();
			Entity entity = Entity.create("tb_coin_detail").set("COIN_NAME", ConstatnsUtil.Currency.RMB)
					.set("COIN_NUM", money).set("TOTAL_COST", money).set("SERVICE_CHARGE", 0)
					.set("MONETARY_UNIT", ConstatnsUtil.Currency.RMB).set("AVARANGE_PRICE", 1)
					.set("OP_TYPE", ConstatnsUtil.OpType.buy).set("OP_TIME", new Date()).set("OP_MARKET", "");
			session.insert(entity);

			updateSummary(ConstatnsUtil.Currency.RMB);
			session.commit();
		} catch (SQLException e) {
			session.quietRollback();
			LogUtil.print("doSave err", e);
			throw new Exception("操作失败！");
		}
	}

	public void doSave(CoinDetail detail) throws Exception {

		// 代币明细数据
		try {
			session.beginTransaction();
			// 插入代币明细数据
			Entity entity = Entity.create("tb_coin_detail").set("COIN_NAME", detail.getCoin_name())
					.set("COIN_NUM", detail.getCoin_num()).set("TOTAL_COST", detail.getTotal_cost())
					.set("SERVICE_CHARGE", 0).set("MONETARY_UNIT", detail.getMonetary_unit())
					.set("AVARANGE_PRICE", detail.getAvarange_price()).set("OP_TYPE", detail.getOp_type())
					.set("OP_TIME", detail.getOp_time()).set("OP_MARKET", detail.getOp_market())
					.set("MONETARY_UNIT", detail.getMonetary_unit());
			session.insert(entity);

			// 插入货币明细数据
			Entity entity2 = Entity.create("tb_coin_detail").set("COIN_NAME", detail.getMonetary_unit())
					.set("COIN_NUM", detail.getTotal_cost()).set("TOTAL_COST", detail.getTotal_cost())
					.set("SERVICE_CHARGE", detail.getService_charge()).set("MONETARY_UNIT", detail.getMonetary_unit())
					.set("AVARANGE_PRICE", 0).set("OP_TYPE", ConstatnsUtil.reverseOpType(detail.getOp_type()))
					.set("OP_TIME", detail.getOp_time()).set("OP_MARKET", detail.getOp_market())
					.set("MONETARY_UNIT", detail.getMonetary_unit());
			session.insert(entity2);

			// 修改代币及法币的汇总数据
			updateSummary(detail.getCoin_name());
			updateSummary(detail.getTotal_cost_currency());

			session.commit();
		} catch (SQLException e) {
			session.quietRollback();
			LogUtil.print("doSave err", e);
			throw new Exception("操作失败！");
		}
	}

	private void updateSummary(String coinName) throws SQLException {
		String sql = " SELECT " + " d.OP_TYPE, " + " sum(d.COIN_NUM) as COIN_NUM, "
				+ " sum(case d.MONETARY_UNIT when 'RMB' then d.TOTAL_COST / 6.64 else d.TOTAL_COST end) as TOTAL_COST, "
				+ " sum(case d.MONETARY_UNIT when 'RMB' then d.SERVICE_CHARGE / 6.64 else d.SERVICE_CHARGE end) as SERVICE_CHARGE "
				+ " FROM tb_coin_detail d " + " WHERE d.SETTLEMENT_VERSION is null and d.COIN_NAME = ? "
				+ " GROUP BY d.OP_TYPE ORDER BY d.OP_TYPE ";
		List<Entity> list = session.query(sql, new Object[] { coinName });

		CoinSummary csSummary = new CoinSummary();
		csSummary.setCoin_name(coinName);
		for (Entity en : list) {
			CoinSummary cs = en.toBeanIgnoreCase(CoinSummary.class);
			if (ConstatnsUtil.OpType.buy.equals(cs.getOp_type())) {
				// 买入
				csSummary.setCoin_num(csSummary.getCoin_num() + cs.getCoin_num()
						- getCost(cs.getMonetary_unit(), cs.getService_charge()));
				csSummary.setTotal_cost(csSummary.getTotal_cost() + getCost(cs.getMonetary_unit(), cs.getTotal_cost()));
				csSummary.setService_charge(
						csSummary.getService_charge() + getCost(cs.getMonetary_unit(), cs.getService_charge()));
			} else {
				// 卖出
				csSummary.setCoin_num(csSummary.getCoin_num() - cs.getCoin_num()
						- getCost(cs.getMonetary_unit(), cs.getService_charge()));
				csSummary.setTotal_cost(csSummary.getTotal_cost() - getCost(cs.getMonetary_unit(), cs.getTotal_cost()));
				csSummary.setService_charge(
						csSummary.getService_charge() - getCost(cs.getMonetary_unit(), cs.getService_charge()));
			}
		}

		csSummary.setAvarange_price(csSummary.getTotal_cost() / csSummary.getCoin_num());
		csSummary.setMonetary_unit(ConstatnsUtil.Currency.USDT);

		// 查询汇总记录是否存在
		String countSQL = "SELECT COUNT(1) as count FROM tb_coin_summary WHERE coin_name = ? ";
		List<Entity> countList = session.query(countSQL, new Object[] { coinName });
		Long count = (Long) countList.get(0).get("count");

		if (count == 0) {
			Entity entity = Entity.create("tb_coin_summary").set("coin_name", coinName)
					.set("coin_num", csSummary.getCoin_num()).set("total_cost", csSummary.getTotal_cost())
					.set("Service_charge", csSummary.getService_charge())
					.set("monetary_unit", csSummary.getMonetary_unit())
					.set("Avarange_price", csSummary.getAvarange_price());
			session.insert(entity);
		} else {
			Entity entity = Entity.create("tb_coin_summary").set("coin_num", csSummary.getCoin_num())
					.set("total_cost", csSummary.getTotal_cost()).set("Service_charge", csSummary.getService_charge())
					.set("monetary_unit", csSummary.getMonetary_unit())
					.set("Avarange_price", csSummary.getAvarange_price()).set("UPDATE_DATE", new Date());
			Entity where = Entity.create("tb_coin_summary").set("coin_name", coinName);
			session.update(entity, where);
		}
	}

	/**
	 * 结算
	 * 
	 * @param coinName
	 * @throws Exception
	 */
	public void doSettlement(String coinName) throws Exception {
		try {
			session.beginTransaction();
			// 结算版本号
			String settlementVersion = new SimpleDateFormat("YYYYMMddHHmmss").format(new Date());
			Date updateDate = new Date();

			// 修改明细数据为已结算
			String sql = "update tb_coin_detail set UPDATE_DATE = ? where SETTLEMENT_VERSION is null and COIN_NAME = ? ";
			session.execute(sql, new Object[] { updateDate, coinName });

			// 新增明细数据
			CoinInfo coinInfo = coinInfoDao.queryCoinInfo(coinName).get(0);

			sql = "select * from tb_coin_summary where COIN_NAME = ? ";
			List<Entity> list = session.query(sql, new Object[] { coinName });
			CoinSummary summary = list.get(0).toBeanIgnoreCase(CoinSummary.class);
			Entity entity = Entity.create("tb_coin_detail").set("COIN_NAME", summary.getCoin_name())
					.set("COIN_NUM", summary.getCoin_num()).set("TOTAL_COST", 0).set("SERVICE_CHARGE", 0)
					.set("MONETARY_UNIT", summary.getMonetary_unit()).set("AVARANGE_PRICE", 0)
					.set("OP_TYPE", ConstatnsUtil.OpType.buy).set("OP_TIME", new Date())
					.set("OP_MARKET", ConstatnsUtil.MARKET.SYSTEM).set("REMARK", "结算生成")
					.set("IS_SETTLEMENT", ConstatnsUtil.SETTLEMENT_STATE.IS_SETTLEMENT)
					.set("SETTLEMENT_PRICE", coinInfo.getMarket_price()).set("SETTLEMENT_VERSION", settlementVersion)
					.set("SETTLEMENT_DATE", updateDate);
			session.insert(entity);

			session.commit();
		} catch (SQLException e) {
			session.quietRollback();
			LogUtil.print("doSettlement err", e);
			throw new Exception("操作失败！");
		}
	}
}
