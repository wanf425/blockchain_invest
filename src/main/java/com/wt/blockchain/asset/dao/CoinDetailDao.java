package com.wt.blockchain.asset.dao;

import static com.wt.blockchain.asset.util.ConstatnsUtil.getCost;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.StringUtils;
import com.wt.blockchain.asset.dto.CoinDetail;
import com.wt.blockchain.asset.dto.CoinInfo;
import com.wt.blockchain.asset.dto.CoinSummary;
import com.wt.blockchain.asset.util.ConstatnsUtil;
import com.wt.blockchain.asset.util.ConstatnsUtil.Market;
import com.wt.blockchain.asset.util.ConstatnsUtil.OpType;
import com.wt.blockchain.asset.util.LogUtil;
import com.wt.blockchain.asset.util.NumberUtil;
import com.xiaoleilu.hutool.db.Entity;

public class CoinDetailDao extends BaseDao<CoinDetail> {
	CoinInfoDao coinInfoDao = new CoinInfoDao();

	public String doCancel(String coinName) {
		String result = "";
		try {
			session.beginTransaction();
			String sql = "select * from tb_coin_detail where COIN_NAME = ? ORDER BY ID desc limit 1 ";
			List<Entity> list = session.query(sql, new Object[] { coinName });

			if (list.isEmpty()) {
				throw new Exception("明细信息不存在");
			}

			CoinDetail detail = list.get(0).toBeanIgnoreCase(CoinDetail.class);

			if (!StringUtils.isNullOrEmpty(detail.getSettlement_version())) {
				throw new Exception("已结算数据不能撤销");
			}

			// 删除代币和法币的明细记录
			sql = "delete from tb_coin_detail where OP_TIME = ? and TOTAL_COST = ? and CREATE_DATE =? ";
			session.execute(sql, new Object[] { detail.getOp_time(), detail.getTotal_cost(), detail.getCreate_Date() });

			updateSummary(coinName);
			updateSummary(detail.getMonetary_unit());

			session.commit();
		} catch (Exception e) {
			session.quietRollback();
			LogUtil.print("doCancel err", e);
			result = e.getMessage();
		}

		return result;
	}

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

	/**
	 * 补差额
	 * 
	 * @param coinName
	 * @param refund
	 */
	public void doRefund(String coinName, Double refund, String remark) throws Exception {
		try {
			session.beginTransaction();

			String opType = refund >= 0 ? OpType.buy : OpType.sell;
			// 插入代币明细数据
			Entity entity = Entity.create("tb_coin_detail").set("COIN_NAME", coinName).set("COIN_NUM", Math.abs(refund))
					.set("TOTAL_COST", 0).set("SERVICE_CHARGE", 0).set("MONETARY_UNIT", ConstatnsUtil.Currency.USDT)
					.set("AVARANGE_PRICE", 0).set("OP_TYPE", opType).set("OP_TIME", new Date())
					.set("OP_MARKET", Market.OKOEX).set("REMARK", remark);
			session.insert(entity);

			// 修改代币汇总数据
			updateSummary(coinName);

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
					.set("SERVICE_CHARGE", getServiceCharge(detail.getCoin_name(), detail))
					.set("MONETARY_UNIT", detail.getMonetary_unit()).set("AVARANGE_PRICE", detail.getAvarange_price())
					.set("OP_TYPE", detail.getOp_type()).set("OP_TIME", detail.getOp_time())
					.set("OP_MARKET", detail.getOp_market()).set("MONETARY_UNIT", detail.getMonetary_unit());
			session.insert(entity);

			// 修改代币汇总数据
			updateSummary(detail.getCoin_name());

			// 插入货币明细数据
			Entity entity2 = Entity.create("tb_coin_detail").set("COIN_NAME", detail.getMonetary_unit())
					.set("COIN_NUM", detail.getTotal_cost()).set("TOTAL_COST", detail.getTotal_cost())
					.set("SERVICE_CHARGE", getServiceCharge(detail.getTotal_cost_currency(), detail))
					.set("MONETARY_UNIT", detail.getMonetary_unit()).set("AVARANGE_PRICE", 0)
					.set("OP_TYPE", ConstatnsUtil.reverseOpType(detail.getOp_type()))
					.set("OP_TIME", detail.getOp_time()).set("OP_MARKET", detail.getOp_market())
					.set("MONETARY_UNIT", detail.getMonetary_unit());
			session.insert(entity2);

			// 修改法币汇总数据
			updateSummary(detail.getTotal_cost_currency());

			session.commit();
		} catch (SQLException e) {
			session.quietRollback();
			LogUtil.print("doSave err", e);
			throw new Exception("操作失败！");
		}
	}

	private Double getServiceCharge(String coinName, CoinDetail detail) {
		if (detail.getServcieChargeCurrency().equals(coinName)) {
			return detail.getService_charge();
		} else {
			return 0.0;
		}
	}

	private void updateSummary(String coinName) throws SQLException {
		String sql = "SELECT OP_TYPE,COIN_NUM,TOTAL_COST,SERVICE_CHARGE,MONETARY_UNIT "
				+ " FROM tb_coin_detail WHERE SETTLEMENT_VERSION is null and COIN_NAME = ? ";

		List<Entity> list = session.query(sql, new Object[] { coinName });

		CoinSummary csSummary = new CoinSummary();
		csSummary.setCoin_name(coinName);
		Map<String, CoinInfo> coinInfos = coinInfoDao.queryAllMap();

		for (Entity en : list) {
			CoinSummary cs = en.toBeanIgnoreCase(CoinSummary.class);

			Double totalCost = getCost(cs.getMonetary_unit(), cs.getTotal_cost(), coinInfos.get(cs.getMonetary_unit()));
			Double serviceCharge = getCost(cs.getMonetary_unit(), cs.getService_charge(),
					coinInfos.get(cs.getMonetary_unit()));

			if (ConstatnsUtil.OpType.buy.equals(cs.getOp_type())) {
				// 买入
				csSummary.setCoin_num(csSummary.getCoin_num() + cs.getCoin_num());
				csSummary.setTotal_cost(csSummary.getTotal_cost() + totalCost);
				csSummary.setService_charge(csSummary.getService_charge() + serviceCharge);
			} else {
				// 卖出
				csSummary.setCoin_num(csSummary.getCoin_num() - cs.getCoin_num());
				csSummary.setTotal_cost(csSummary.getTotal_cost() - totalCost);
				csSummary.setService_charge(csSummary.getService_charge() + serviceCharge);
			}
		}

		csSummary.setAvarange_price(NumberUtil.divide(csSummary.getTotal_cost(), csSummary.getCoin_num()));
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
			CoinInfo coinInfo = coinInfoDao.queryCoinInfo(coinName).get(0);

			String sql = "update tb_coin_detail set UPDATE_DATE = ?, SETTLEMENT_DATE = ? , SETTLEMENT_VERSION = ?,  SETTLEMENT_PRICE = ? where SETTLEMENT_VERSION is null and COIN_NAME = ? ";
			session.execute(sql,
					new Object[] { updateDate, updateDate, settlementVersion, coinInfo.getMarket_price(), coinName });

			// 新增明细数据
			sql = "select * from tb_coin_summary where COIN_NAME = ? ";
			List<Entity> list = session.query(sql, new Object[] { coinName });
			CoinSummary summary = list.get(0).toBeanIgnoreCase(CoinSummary.class);
			Entity entity = Entity.create("tb_coin_detail").set("COIN_NAME", summary.getCoin_name())
					.set("COIN_NUM", summary.getCoin_num()).set("TOTAL_COST", 0).set("SERVICE_CHARGE", 0)
					.set("MONETARY_UNIT", summary.getMonetary_unit()).set("AVARANGE_PRICE", 0)
					.set("OP_TYPE", ConstatnsUtil.OpType.buy).set("OP_TIME", new Date())
					.set("OP_MARKET", ConstatnsUtil.MARKET.SYSTEM).set("REMARK", "结算生成")
					.set("IS_SETTLEMENT", ConstatnsUtil.SETTLEMENT_STATE.IS_SETTLEMENT)
					.set("SETTLEMENT_PRICE", coinInfo.getMarket_price());
			session.insert(entity);

			// 修改汇总数据
			updateSummary(coinName);
			session.commit();
		} catch (SQLException e) {
			session.quietRollback();
			LogUtil.print("doSettlement err", e);
			throw new Exception("操作失败！");
		}
	}
}
