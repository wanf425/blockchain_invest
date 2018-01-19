package com.wt.blockchain.asset.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.wt.blockchain.asset.dao.CoinInfoDao;
import com.wt.blockchain.asset.dto.CoinInfo;

public class CommonUtil {

	/**
	 * 初始化下拉框
	 * 
	 * @param list
	 * @param cb
	 * @param f
	 */
	public static <T> void initialComboBox(List<T> list, JComboBox<T> cb, final Function<T, String> f) {
		list.stream().forEach(t -> cb.addItem(t));
		cb.setRenderer((l, value, index, isSelected, cellHasFocus) -> new JLabel(f.apply(value)));
	}

	/**
	 * 人币汇率
	 * 
	 * @return
	 */
	public static double getExchangeRate() {
		CoinInfoDao coinInfoDao = new CoinInfoDao();
		List<CoinInfo> list = coinInfoDao.queryCoinInfo(Constatns.Currency.RMB);

		return list.get(0).getMarket_price();
	}

	public static Double formateNumDouble(Double num, String... formate) {
		String numStr = formateNum(num, formate);
		Double numDouble = 0.0;
		try {
			numDouble = Double.valueOf(numStr);
		} catch (Exception e) {
			LogUtil.print("formateNumDouble err" + e.getMessage());
		}

		return numDouble;
	}

	/**
	 * 格式化数字
	 * 
	 * @param num
	 * @param formate
	 * @return
	 */
	public static String formateNum(Double num, String... formate) {
		String result = "";

		try {
			if (num != null) {
				String defalutFormate = "#0.00";

				if (formate != null && formate.length > 0) {
					defalutFormate = formate[0];
				}

				result = new DecimalFormat(defalutFormate).format(num);
			}
		} catch (Exception e) {
			LogUtil.print("formateNum err", e);
		}

		return result;
	}

	/**
	 * 格式化
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static String formatJson(String jsonStr) {
		if (null == jsonStr || "".equals(jsonStr))
			return "";
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		int indent = 0;
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			// 遇到{ [换行，且下一行缩进
			switch (current) {
			case '{':
			case '[':
				sb.append(current);
				sb.append('\n');
				indent++;
				addIndentBlank(sb, indent);
				break;
			// 遇到} ]换行，当前行缩进
			case '}':
			case ']':
				sb.append('\n');
				indent--;
				addIndentBlank(sb, indent);
				sb.append(current);
				break;
			// 遇到,换行
			// case ',':
			// sb.append(current);
			// if (last != '\\') {
			// sb.append('\n');
			// addIndentBlank(sb, indent);
			// }
			// break;
			case '\n':
				break;
			default:
				sb.append(current);
			}
		}

		return sb.toString();
	}

	/**
	 * 添加space
	 * 
	 * @param sb
	 * @param indent
	 */
	private static void addIndentBlank(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++) {
			sb.append("    ");
		}
	}

	/**
	 * 计算收益率
	 * 
	 * @param cs
	 * @param marketPrice
	 * @return
	 */
	public static double getRateNum(Double avarange_price, Double coinNum, Double marketPrice) {
		return formateNumDouble(marketPrice - avarange_price) * coinNum;
	}

	/**
	 * 计算收益数
	 * 
	 * @param cs
	 * @param marketPrice
	 * @return
	 */
	public static Double getRatePercent(Double avarange_price, Double marketPrice) {
		return formateNumDouble(marketPrice / avarange_price * 100);
	}

	public static String formateDate(Date date) {
		return new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(date);
	}

}
