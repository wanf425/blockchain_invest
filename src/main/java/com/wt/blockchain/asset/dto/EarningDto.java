package com.wt.blockchain.asset.dto;

import java.util.Date;

public class EarningDto extends BaseDto {

	private int id;
	private Date settlement_date; // 结算日期
	private Double total_invest; // 总投入
	private Double current_invest; // 当期投入
	private Double increase_rate; // 增长率(去当期)
	private Double total_value; // 总市值
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getSettlement_date() {
		return settlement_date;
	}
	public void setSettlement_date(Date settlement_date) {
		this.settlement_date = settlement_date;
	}
	public Double getTotal_invest() {
		return total_invest;
	}
	public void setTotal_invest(Double total_invest) {
		this.total_invest = total_invest;
	}
	public Double getCurrent_invest() {
		return current_invest;
	}
	public void setCurrent_invest(Double current_invest) {
		this.current_invest = current_invest;
	}
	public Double getIncrease_rate() {
		return increase_rate;
	}
	public void setIncrease_rate(Double increase_rate) {
		this.increase_rate = increase_rate;
	}
	public Double getTotal_value() {
		return total_value;
	}
	public void setTotal_value(Double total_value) {
		this.total_value = total_value;
	}
	
}
