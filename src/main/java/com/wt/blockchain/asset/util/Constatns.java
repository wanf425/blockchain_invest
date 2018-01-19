package com.wt.blockchain.asset.util;

import com.wt.blockchain.asset.dto.CoinInfo;

public class Constatns {

	public interface SETTLEMENT_STATE {
		public static int IS_SETTLEMENT = 1; // 是
		public static int NOT_SETTLEMENT = 1; // 不是
	}

	public interface ConstatnsKey {
		public static String OP_TYPE = "opType"; // 操作类型
		public static String COIN_NAME = "coinName"; // 币种
		public static String CURRENCY_TYPE = "currencyType"; // 货币类型
		public static String MARKET = "market"; // 交易平台
		public static String MAX_DETAIL_ID = "maxDetailID"; // 已备份的明细交易最大ID
	}

	public interface MaxDetailID {
		public static String BACKUP_ID = "backupID"; // 备份时明细表最大ID
		public static String ROLLBACK_ID = "rollbackID"; // 回滚ID
	}
	
	public interface OpType {
		public static String buy = "1";
		public static String sell = "2";
	}

	public interface Market {
		public static String OKOEX = "OKex";
		public static String HUOBI = "HUOBI";
		public static String SYSTEM = "system"; // 系统默认
	}

	public interface Currency {
		public static String USDT = "USDT";
		public static String RMB = "RMB";
		public static String BTC = "BTC";
		public static String ETH = "ETH";
		public static double rate = 6.64;
	}

	public static String reverseOpType(String opType) {
		if (OpType.buy.equals(opType)) {
			return OpType.sell;
		} else if (OpType.sell.equals(opType)) {
			return OpType.buy;
		} else {
			return opType;
		}
	}

	public static Double getCost(String currency, double num, CoinInfo currencyCoinInfo) {
		Double result = 0.0;

		if (currencyCoinInfo != null) {
			result = num * currencyCoinInfo.getMarket_price();
		}

		return result;
	}
}
