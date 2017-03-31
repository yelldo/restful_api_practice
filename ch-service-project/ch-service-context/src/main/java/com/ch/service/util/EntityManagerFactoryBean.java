package com.ch.service.util;

import com.alibaba.fastjson.JSON;
import com.ch.frame.conf.ConfigHelper;
import com.ch.frame.util.ClassUtils;
import com.ch.model.BaseEntity;
import com.ch.model.annotation.CodeGroup;
import com.ch.model.annotation.CreateIndex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.Column;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Table;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EntityManagerFactoryBean extends
		LocalContainerEntityManagerFactoryBean {
	protected Log log = LogFactory.getLog(this.getClass());

	class SqlKey {
		public SqlKey(String key, int ver) {
			this.key = key;
			this.version = ver;
		}

		public SqlKey(String key, int ver, String sql) {
			this.key = key;
			this.sql = sql;
			this.version = ver;
		}

		String key;
		int version;
		String sql;

		@Override
		public boolean equals(Object obj) {
			SqlKey sk = (SqlKey) obj;
			return sk.key.equals(key) && sk.version == version;
		}

		@Override
		public String toString() {
			return sql;
		}
	}

	private String[] packages = null;

	@Override
	public void setPackagesToScan(String... packagesToScan) {
		super.setPackagesToScan(packagesToScan);
		packages = packagesToScan;
	}

	/**
	 * 初始化数据： 1.扫描所有的实体类，检查表名是否重复
	 * 2.扫描所有实体类的@GeneralIndex,@ForeignKey,@Sql注解，自动生成相应的sql执行
	 * 3.扫描@CodeDefine,生成字典缓存
	 */
	@Override
	protected void postProcessEntityManagerFactory(EntityManagerFactory emf,
			PersistenceUnitInfo pui) {
		log.debug("Before1 postProcessEntityManagerFactory...");
		super.postProcessEntityManagerFactory(emf, pui);
		log.debug("Before2 postProcessEntityManagerFactory...");
		List<Class> entitys = new ArrayList<Class>();
		if (packages != null) {
			for (String s : packages) {
				entitys.addAll(ClassUtils.getClasses(s));
			}
		}
		log.debug("Before3 postProcessEntityManagerFactory...");
		// 处理自定义注解
		List<SqlKey> cache = new ArrayList<SqlKey>();
		for (Class c : entitys) {
			if (!org.apache.commons.lang3.ClassUtils.isAssignable(c, BaseEntity.class)
					&& !Modifier.isAbstract(c.getModifiers()))
				continue; // TODO:
			Field[] fields = c.getDeclaredFields();
			for (Field f : fields) {
				CodeGroup cg = f.getAnnotation(CodeGroup.class);
				if (cg != null) {
					// 添加字典
					DictionaryCodeBuffer.addCodeGroup(cg);
				}
				CreateIndex ci = f.getAnnotation(CreateIndex.class);
				if (ci != null) {
					if (Modifier.isAbstract(c.getModifiers())) { // 抽象类直接过掉
						continue;
					}
					// 生成数据库索引
					SqlKey sqlKey = generatorIndexSql(ci, c, f);
					if (sqlKey != null) {
						cache.add(sqlKey);
					}

				}
			}
		}
		log.debug("Before4 postProcessEntityManagerFactory...");
		DataSource ds = getDataSource();
		Connection c = null;
		try {
			c = ds.getConnection();
			log.debug("Before40 postProcessEntityManagerFactory...");
			// 初始化表
			initTable(c);
			log.debug("Before41 postProcessEntityManagerFactory...");
			for (SqlKey sk : cache) {
				c.setAutoCommit(false);
				try {
					// 执行
					PreparedStatement ps = c
							.prepareStatement("select a.id from tinit a where a.ckey = ? and a.ver = ?");
					ps.setString(1, sk.key);
					ps.setInt(2, sk.version);
					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						rs.close();
						continue;
					}
					rs.close();
					// 执行
					exec(c, sk.sql);
					exec(c, "insert into tinit(ckey, ver) values(?,?)", sk.key,
							sk.version);
					c.commit();
					log.info("Executed:" + sk.sql);
				} catch (Exception e) {
					c.rollback();
					log.warn("Execute sql:" + sk.sql + " error, cause:"
							+ e.getMessage());
				}
			}
			log.debug("End postProcessEntityManagerFactory");
		} catch (Exception e) {
			log.info("Execute sql error", e);
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	private void initTable(Connection c) {
		try {
			PreparedStatement ps = c
					.prepareStatement("select * from tinit where id is null");
			ps.executeQuery();
		} catch (Exception e2) {
			try {
				String csql = "create table tinit(id bigint primary key auto_increment, ckey varchar(512), ver int)";
				exec(c, csql);
			} catch (Exception e) {
				log.error("create tinit table error", e);
			}
		}
	}

	private void exec(Connection c, String sql, Object... params)
			throws Exception {
		PreparedStatement ps = c.prepareStatement(sql);
		try {
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			ps.execute();
		} finally {
			ps.close();
		}
	}

	@Override
	public void setJpaProperties(Properties jpaProperties) {
		if (jpaProperties != null) {
			if (ConfigHelper.getProp("mysql").getBool("updateschema", true))
				jpaProperties.put("hibernate.hbm2ddl.auto", "update");
			if (log.isDebugEnabled()) {
				jpaProperties.put("hibernate.show_sql", "true");
				jpaProperties.put("hibernate.format_sql", "true");
			}
			log.debug("jpaProperties:" + JSON.toJSONString(jpaProperties));
		}
		super.setJpaProperties(jpaProperties);
	}

	private SqlKey generatorIndexSql(CreateIndex index, Class<?> entity,
			Field field) {
		String indexName = index.value();
		Table table = (Table) entity.getAnnotation(Table.class);
		if (table == null)
			return null;
		Column c = (Column) field.getAnnotation(Column.class);
		String cname = null;
		if (c != null) {
			// throw new FrameException("Not found column annotation on field:"
			// + entity.getName() + "." + field.getName());
			cname = c.name();
		}
		if (cname == null || cname.trim().equals(""))
			cname = field.getName();
		if (indexName == null || indexName.trim().equals("")) {
			indexName = table.name() + "_" + cname;
		}
		String sql = "create index " + indexName + " on " + table.name() + "("
				+ cname + ")";
		return new SqlKey("index_" + indexName, 1, sql);
	}

}
