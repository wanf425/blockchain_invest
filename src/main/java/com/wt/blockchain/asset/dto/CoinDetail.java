package com.wt.blockchain.asset.dto;

import java.util.Date;

/**
 * 交易明细
 * 
 * @author wangtao
 */
public class CoinDetail extends BaseDto {

	private Integer id;
	private String coin_name;
	private Double coin_num;
	private Double total_cost;
	private String total_cost_currency; // 货币单位
	private Double service_charge; // 手续费金额
	private String servcieChargeCurrency; // 手续费单位
	private String monetary_unit;
	private Double avarange_price;
	private String op_type;
	private Date op_time;
	private String op_market;
	private String remark; // 备注
	private Date settlement_date; // 结算时间
	private String settlement_version; // 结算版本号
	private Double settlement_price; // 结算价格
	private Integer is_settlement; // 是否结算生成数据
	private Date update_Date; // 修改时间
	private Date create_Date; // 创建时间
	
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

	public String getOp_type() {
		return op_type;
	}

	public void setOp_type(String op_type) {
		this.op_type = op_type;
	}

	public Date getOp_time() {
		return op_time;
	}

	public void setOp_time(Date op_time) {
		this.op_time = op_time;
	}

	public String getOp_market() {
		return op_market;
	}

	public void setOp_market(String op_market) {
		this.op_market = op_market;
	}

	public String getTotal_cost_currency() {
		return total_cost_currency;
	}

	public void setTotal_cost_currency(String total_cost_currency) {
		this.total_cost_currency = total_cost_currency;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Double getSettlement_price() {
		return settlement_price;
	}

	public void setSettlement_price(Double settlement_price) {
		this.settlement_price = settlement_price;
	}

	public Integer getIs_settlement() {
		return is_settlement;
	}

	public void setIs_settlement(Integer is_settlement) {
		this.is_settlement = is_settlement;
	}

	public Date getUpdate_Date() {
		return update_Date;
	}

	public void setUpdate_Date(Date update_Date) {
		this.update_Date = update_Date;
	}

	public Date getCreate_Date() {
		return create_Date;
	}

	public void setCreate_Date(Date create_Date) {
		this.create_Date = create_Date;
	}

	public String getServcieChargeCurrency() {
		return servcieChargeCurrency;
	}

	public void setServcieChargeCurrency(String servcieChargeCurrency) {
		this.servcieChargeCurrency = servcieChargeCurrency;
	}

}
