package com.wt.blockchain.asset.util;

public class ConstatnsUtil {

	public interface MARKET {
		public static String SYSTEM = "system"; // 系统默认
	}
	
	public interface SETTLEMENT_STATE {
		public static int IS_SETTLEMENT = 1; // 是
		public static int NOT_SETTLEMENT = 1; // 不是
	}
	
	public interface ConstatnsKey {
		public static String OP_TYPE = "opType"; // 操作类型
		public static String COIN_NAME = "coinName"; // 币种
		public static String CURRENCY_TYPE = "currencyType"; // 货币类型
		public static String MARKET = "market"; // 交易平台
	}

	public interface OpType {
		public static String buy = "1";
		public static String sell = "2";
	}

	public interface Market {
		public static String OKOEX = "OKex";
		public static String HUOBI = "HUOBI";
	}
	
	public interface Currency {
		public static String USDT = "USDT";
		public static String RMB = "RMB";
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

	public static Double getCost(String currency, double num) {
		if (Currency.RMB.equals(currency)) {
			return num / Currency.rate;
		} else {
			return num;
		}
	}
}
