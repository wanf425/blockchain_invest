package com.wt.blockchain.asset.dao;

import java.util.List;

import com.wt.blockchain.asset.dto.CoinDetail;
import com.wt.blockchain.asset.util.Constatns.ConstatnsKey;
import com.wt.blockchain.asset.util.Constatns.MaxDetailID;
import com.wt.blockchain.asset.util.LogUtil;
import com.xiaoleilu.hutool.db.Entity;

public class BackupDao extends BaseDao<CoinDetail> {

	/**
	 * 备份
	 */
	public boolean doBackUp() {
		boolean result = false;
		try {
			session.beginTransaction();

			// 修改配置表回滚ID
			String sql = "select t2.VALUE from tc_constants t2 where t2.TYPE = ? and t2.KEY = ?";
			List<Entity> rollbackId = session.query(sql,
					new Object[] { ConstatnsKey.MAX_DETAIL_ID, MaxDetailID.BACKUP_ID });
			Object rollbackIdValue = rollbackId.get(0).get("VALUE");

			sql = "update tc_constants t set t.VALUE = ? where t.TYPE = ? and t.KEY = ? ";
			session.execute(sql, new Object[] { rollbackIdValue, ConstatnsKey.MAX_DETAIL_ID, MaxDetailID.ROLLBACK_ID });

			// 修改配置表备份ID
			sql = "select max(id) as id from tb_coin_detail";
			List<Entity> maxId = session.query(sql);
			Object maxIdValue = maxId.get(0).get("id");

			sql = "update tc_constants t set t.VALUE = ? where t.TYPE = ? and t.KEY = ? ";
			session.execute(sql, new Object[] { maxIdValue, ConstatnsKey.MAX_DETAIL_ID, MaxDetailID.BACKUP_ID });

			// 备份数据
			sql = "delete from backup_tb_coin_detail ";
			session.execute(sql);
			sql = "insert into backup_tb_coin_detail select * from tb_coin_detail ";
			session.execute(sql);
			sql = "delete from backup_tb_coin_summary ";
			session.execute(sql);
			sql = "insert into backup_tb_coin_summary select * from tb_coin_summary; ";
			session.execute(sql);

			session.commit();
			result = true;
		} catch (Exception e) {
			session.quietRollback();
			LogUtil.print("doBackUp err", e);
		}

		return result;
	}

	/**
	 * 回滚
	 */
	public boolean doRollBack() {
		boolean result = false;
		try {
			session.beginTransaction();

			// 修改配置表备份ID
			String sql = "select t2.VALUE from tc_constants t2 where t2.TYPE = ? and t2.KEY = ?";
			List<Entity> rollbackId = session.query(sql,
					new Object[] { ConstatnsKey.MAX_DETAIL_ID, MaxDetailID.ROLLBACK_ID });
			Object rollbackIdValue = rollbackId.get(0).get("VALUE");

			sql = "update tc_constants t set t.VALUE = ? where t.TYPE = ? and t.KEY = ? ";
			session.execute(sql, new Object[] { rollbackIdValue, ConstatnsKey.MAX_DETAIL_ID, MaxDetailID.BACKUP_ID });

			// 回滚数据
			sql = "delete from tb_coin_detail ";
			session.execute(sql);
			sql = "insert into tb_coin_detail select * from backup_tb_coin_detail ";
			session.execute(sql);
			sql = "delete from tb_coin_summary ";
			session.execute(sql);
			sql = "insert into tb_coin_summary select * from backup_tb_coin_summary; ";
			session.execute(sql);

			session.commit();
			result = true;
		} catch (Exception e) {
			session.quietRollback();
			LogUtil.print("doCancel err", e);
		}

		return result;
	}
}
