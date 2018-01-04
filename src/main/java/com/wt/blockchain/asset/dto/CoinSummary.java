package com.wt.blockchain.asset.dto;

import java.util.Date;

/**
 * 交易汇总
 * 
 * @author wangtao
 */
public class CoinSummary extends BaseDto {

	private Integer id;
	private String coin_name; 
	private Double coin_num = 0.0;
	private Double total_cost = 0.0;
	private Double service_charge = 0.0;
	private String monetary_unit;
	private Double avarange_price = 0.0; // 购买均价
	private Double settlement_price = 0.0;
	private Date settlement_date;
	private String settlement_version;
	private String op_type;
	private Double market_price;
	private Double rate_percent; // 收益率
	private Double rate_num; // 收益数
	private Double asset_percent; // 资产占比
	private Double pre_percent; // 预分配比例
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCoin_name() {
		return coin_name;
	}

	public void setCoin_name(String coin_name) {
		this.coin_name = coin_name;
	}

	public Double getCoin_num() {
		return coin_num;
	}

	public void setCoin_num(Double coin_num) {
		this.coin_num = coin_num;
	}

	public Double getTotal_cost() {
		return total_cost;
	}

	public void setTotal_cost(Double total_cost) {
		this.total_cost = total_cost;
	}

	public Double getService_charge() {
		return service_charge;
	}

	public void setService_charge(Double service_charge) {
		this.service_charge = service_charge;
	}

	public String getMonetary_unit() {
		return monetary_unit;
	}

	public void setMonetary_unit(String monetary_unit) {
		this.monetary_unit = monetary_unit;
	}

	public Double getAvarange_price() {
		return avarange_price;
	}

	public void setAvarange_price(Double avarange_price) {
		this.avarange_price = avarange_price;
	}

	public Double getSettlement_price() {
		return settlement_price;
	}

	public void setSettlement_price(Double settlement_price) {
		this.settlement_price = settlement_price;
	}

	public Date getSettlement_date() {
		return settlement_date;
	}

	public void setSettlement_date(Date settlement_date) {
		this.settlement_date = settlement_date;
	}

	public String getSettlement_version() {
		return settlement_version;
	}

	public void setSettlement_version(String settlement_version) {
		this.settlement_version = settlement_version;
	}

	public String getOp_type() {
		return op_type;
	}

	public void setOp_type(String op_type) {
		this.op_type = op_type;
	}

	public Double getMarket_price() {
		return market_price;
	}

	public void setMarket_price(Double market_price) {
		this.market_price = market_price;
	}

	public Double getRate_percent() {
		return rate_percent;
	}

	public void setRate_percent(Double rate_percent) {
		this.rate_percent = rate_percent;
	}

	public Double getRate_num() {
		return rate_num;
	}

	public void setRate_num(Double rate_num) {
		this.rate_num = rate_num;
	}

	public Double getAsset_percent() {
		return asset_percent;
	}

	public void setAsset_percent(Double asset_percent) {
		this.asset_percent = asset_percent;
	}

	public Double getPre_percent() {
		return pre_percent;
	}

	public void setPre_percent(Double pre_percent) {
		this.pre_percent = pre_percent;
	}
}
