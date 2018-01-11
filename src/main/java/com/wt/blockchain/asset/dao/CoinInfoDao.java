package com.wt.blockchain.asset.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wt.blockchain.asset.dto.CoinInfo;
import com.wt.blockchain.asset.util.ConstatnsUtil;
import com.wt.blockchain.asset.util.LogUtil;
import com.xiaoleilu.hutool.db.Entity;

public class CoinInfoDao extends BaseDao<CoinInfo> {

	public List<CoinInfo> queryCoinInfo(String coinName) {
		List<CoinInfo> result = new ArrayList<CoinInfo>();

		try {
			List<Entity> list = runner.findAll(Entity.create("tc_coin_info").set("COIN_NAME", coinName));

			for (Entity en : list) {
				result.add(en.toBeanIgnoreCase(CoinInfo.class));
			}
		} catch (SQLException e) {
			LogUtil.print("queryCoinInfo err", e);
		}

		return result;
	}

	public Map<String, CoinInfo> queryAllMap() {
		List<CoinInfo> list = queryAllList();

		Map<String, CoinInfo> map = new HashMap<>();
		list.forEach(t -> map.put(t.getCoin_name(), t));

		return map;
	}

	public List<CoinInfo> queryAllList() {
		List<CoinInfo> result = new ArrayList<CoinInfo>();

		try {
			List<Entity> list = runner.findAll(Entity.create("tc_coin_info"));

			for (Entity en : list) {
				result.add(en.toBeanIgnoreCase(CoinInfo.class));
			}
		} catch (SQLException e) {
			LogUtil.print("queryCoinInfo err", e);
		}

		return result;
	}

	public void updateAll(List<CoinInfo> list) {

		try {
			session.beginTransaction();
			session.execute("truncate table tc_coin_info", null);

			for (CoinInfo en : list) {
				session.insert(Entity.create("tc_coin_info").set("COIN_NAME", en.getCoin_name())
						.set("MARKET_PRICE", en.getMarket_price()).set("PRICE_UNIT", ConstatnsUtil.Currency.USDT)
						.set("PERCENT", en.getPercent()));
			}
			session.commit();
		} catch (SQLException e) {
			session.quietRollback();
			LogUtil.print("updateAll err", e);
		}

	}
}
