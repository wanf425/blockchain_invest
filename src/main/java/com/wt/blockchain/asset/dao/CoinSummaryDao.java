package com.wt.blockchain.asset.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.StringUtils;
import com.wt.blockchain.asset.dto.CoinDetail;
import com.wt.blockchain.asset.dto.CoinSummary;
import com.wt.blockchain.asset.util.CommonUtil;
import com.wt.blockchain.asset.util.ConstatnsUtil;
import com.wt.blockchain.asset.util.LogUtil;
import com.xiaoleilu.hutool.db.Entity;

public class CoinSummaryDao extends BaseDao<CoinSummary> {

	/**
	 * 查询汇总数据
	 * 
	 * @param coinName
	 * @return
	 * @throws SQLException
	 */
	public List<CoinSummary> query(String coinName) {
		List<CoinSummary> result = new ArrayList<>();

		try {
			String sql = "select * from tb_coin_summary where COIN_NAME = ? ORDER BY ID desc";
			List<Entity> list = runner.query(sql, new Object[] { coinName });

			list.forEach(en -> result.add(en.toBeanIgnoreCase(CoinSummary.class)));

		} catch (Exception e) {
			LogUtil.print("query err", e);
		}

		return result;
	}

	public List<CoinSummary> querySummary(String coinName) {
		List<CoinSummary> result = new ArrayList<>();

		try {
			StringBuffer sql = new StringBuffer("SELECT s.*, i.market_price,i.percent as pre_percent ");
			sql.append(" FROM tb_coin_summary s LEFT JOIN tc_coin_info i ON s.coin_name = i.coin_name ");

			Object[] params = new Object[] {};

			if (!StringUtils.isNullOrEmpty(coinName)) {
				sql.append(" WHERE s.COIN_NAME = ?");
				params = new Object[] { coinName };
			}

			sql.append(" ORDER BY s.coin_name ");

			List<Entity> list = runner.query(sql.toString(), params);

			Double total = 0.0;
			for (Entity en : list) {
				CoinSummary cs = en.toBeanIgnoreCase(CoinSummary.class);
				Double marketPrice = Double.valueOf(cs.getMarket_price());
				// 总市值
				total += marketPrice * cs.getCoin_num();

				if (ConstatnsUtil.Currency.USDT.equals(cs.getCoin_name())
						|| ConstatnsUtil.Currency.RMB.equals(cs.getCoin_name())) {
					// 人民币、USDT不计算 总花费、均价收益率和收益数
					cs.setTotal_cost(0.0);
					cs.setAvarange_price(0.0);
					cs.setRate_num(null);
					cs.setRate_percent(0.0);
				} else {
					// 收益率
					cs.setRate_percent(CommonUtil.getRatePercent(cs.getAvarange_price(), marketPrice));
					// 收益数
					cs.setRate_num(CommonUtil.getRateNum(cs.getAvarange_price(), cs.getCoin_num(), marketPrice));
				}

				result.add(cs);
			}

			for (CoinSummary cs : result) {
				Double marketPrice = Double.valueOf(cs.getMarket_price());
				cs.setAsset_percent(CommonUtil.formateNumDouble(marketPrice * cs.getCoin_num() / total * 100));
			}
		} catch (SQLException e) {
			LogUtil.print("querySummary err", e);
		}

		return result;
	}
}
