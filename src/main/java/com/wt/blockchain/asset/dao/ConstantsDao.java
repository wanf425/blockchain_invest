package com.wt.blockchain.asset.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.wt.blockchain.asset.dto.Constants;
import com.wt.blockchain.asset.util.LogUtil;
import com.xiaoleilu.hutool.db.Entity;

public class ConstantsDao extends BaseDao<Constants> {

	public List<Constants> queryByType(String type) {
		List<Constants> result = new ArrayList<Constants>();
		
		try {
			List<Entity> list = runner.findAll(Entity.create("tc_constants").set("type", type));
			
			for(Entity en : list) {
				result.add(en.toBeanIgnoreCase(Constants.class));
			}
		} catch (SQLException e) {
			LogUtil.print("queryByType err", e);
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		ConstantsDao d = new ConstantsDao();
		d.queryByType("opType");
	}
}
