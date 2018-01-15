package com.wt.blockchain.asset.util;

import java.math.BigDecimal;

public class NumberUtil {

	/**
	 * 减法运算
	 * 
	 * @param base
	 * @param subtrahend
	 * @param nullValue
	 * @return
	 */
	public static Double sub(Object base, Object subtrahend, Double nullValue) {
		Double result = nullValue;

		try {
			BigDecimal baseBD = new BigDecimal(base.toString());
			BigDecimal subtrahendBD = new BigDecimal(subtrahend.toString());
			result = baseBD.subtract(subtrahendBD).doubleValue();
		} catch (Exception e) {
			LogUtil.print("sub err " + e.getMessage());
		}

		return result;
	}

	/**
	 * 加法运算
	 * 
	 * @param base
	 * @param augend
	 * @param nullValue
	 * @return
	 */
	public static Double add(Object base, Object augend, Double nullValue) {
		Double result = nullValue;

		try {
			BigDecimal baseBD = new BigDecimal(base.toString());
			BigDecimal augendBD = new BigDecimal(augend.toString());
			result = baseBD.add(augendBD).doubleValue();
		} catch (Exception e) {
			LogUtil.print("add err " + e.getMessage());
		}

		return result;
	}
	
	/**
	 * 除法运算
	 * 
	 * @param base
	 * @param divisor
	 * @param nullValue
	 * @return
	 */
	public static Double divide(Object base, Object divisor, Double nullValue) {
		Double result = nullValue;

		try {
			BigDecimal baseBD = new BigDecimal(base.toString());
			BigDecimal divisorBD = new BigDecimal(divisor.toString());
			result = baseBD.divide(divisorBD).doubleValue();
		} catch (Exception e) {
			LogUtil.print("divide err " + e.getMessage());
		}

		return result;
	}
	
	/**
	 * 乘法运算
	 * 
	 * @param base
	 * @param multiplicand
	 * @param nullValue
	 * @return
	 */
	public static Double multiply(Object base, Object multiplicand, Double nullValue) {
		Double result = nullValue;

		try {
			BigDecimal baseBD = new BigDecimal(base.toString());
			BigDecimal multiplicandBD = new BigDecimal(multiplicand.toString());
			result = baseBD.multiply(multiplicandBD).doubleValue();
		} catch (Exception e) {
			LogUtil.print("multiply err " + e.getMessage());
		}

		return result;
	}
}