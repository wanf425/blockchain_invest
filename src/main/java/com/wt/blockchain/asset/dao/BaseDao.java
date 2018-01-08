package com.wt.blockchain.asset.dao;

import javax.sql.DataSource;

import com.google.gson.Gson;
import com.wt.blockchain.asset.dto.BaseDto;
import com.xiaoleilu.hutool.db.Session;
import com.xiaoleilu.hutool.db.SqlRunner;
import com.xiaoleilu.hutool.db.ds.DSFactory;

public class BaseDao<T extends BaseDto> {
	protected static SqlRunner runner = SqlRunner.create();
	protected static Session session = Session.create();
	Gson gson = new Gson();

	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
		System.out.println(System.getProperty("user.dir"));
		DataSource ds = DSFactory.get();
		System.out.println("ok");
	}
}
