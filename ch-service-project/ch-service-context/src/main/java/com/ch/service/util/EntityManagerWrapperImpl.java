package com.ch.service.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ch.frame.Globals;
import com.ch.frame.redis.CacheResult;
import com.ch.frame.redis.RedisHelper;
import com.ch.model.BaseEntity;
import com.ch.model.annotation.CacheItem;
import com.ch.model.annotation.Model;
import com.ch.model.annotation.ModelCache;
import com.ch.model.annotation.ModelField;
import com.ch.model.vo.Hql;
import com.ch.model.vo.PageData;
import com.ch.model.vo.Where;
import com.ch.service.context.ServiceContext;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 系统JPA管理器
 *
 * @author Administrator
 */
public class EntityManagerWrapperImpl implements EntityManagerWrapper {
    protected Log log = LogFactory.getLog(this.getClass());
    //持久化管理器
    @PersistenceContext
    protected EntityManager em;
    @Resource
    protected JdbcTemplate jdbcTemplate;

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#persist(java.lang.Object)
	 */
    @Override
	public Object persist(Object obj) {
        if (obj == null) {
            return null;
        }
        return merge(obj);
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#get(java.lang.Class, java.lang.Object)
	 */
    @Override
	public <T> T get(Class<T> cla, Object id) {
        if (id == null) {
            return null;
        }
        return (T)em.find(cla, id);
    }
    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#getExists(java.lang.Class, java.lang.Object)
	 */
    @Override
	public Object getExists(Class<?> cla, Object id){
    	Object obj = get(cla, id);
		if(obj == null){
			Model m = cla.getAnnotation(Model.class);
			String text = m==null?cla.getSimpleName():m.name();
			throw new ServiceException(text + "#[" + id + "]不存在");
		}
		return obj;
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#get(java.lang.Class, java.lang.Object, javax.persistence.LockModeType)
	 */
    @Override
	public Object get(Class<?> cla, Object id, LockModeType lt) {
        if (id == null) {
            return null;
        }
        Object obj = em.find(cla, id, lt);
        return obj;
    }
    
    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#refresh(java.lang.Class, javax.persistence.LockModeType)
	 */
    @Override
	public void refresh(Class<?> cla, LockModeType lt) {
        em.refresh(cla, lt);
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#remove(java.lang.Object)
	 */
    @Override
	public void remove(Object obj) {
        if (obj == null) return;
        //记录删除日志
  		ServiceContext ctx = ServiceContext.get();
  		if(ctx != null){
  			ctx.addRemoveLogger(obj);
  		}
        em.remove(obj);
        //同步刷新实体缓存
        refreshCacheByDomainObject(obj, false);

    }
     
    
    

	/* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#toList(java.lang.Class, java.util.List)
	 */
    @Override
	public List<Object> toList(Class<?> cla, List ls){
    	List<Object> rls = new ArrayList<Object>();
		for(Object id : ls){
			Object rec = get(cla, id);
			if(rec != null)
				rls.add(rec);
		}
		return rls;
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#merge(java.lang.Object)
	 */
    @Override
	@SuppressWarnings("unchecked")
    public <T> T merge(Object obj) {
    	if(obj == null)
    		return null;
    	//记录操作日志
    	ServiceContext ctx = ServiceContext.get();	
		String orientData = null;
		boolean newflag = false;
		if(obj instanceof BaseEntity){
			BaseEntity base = (BaseEntity)obj;
			newflag = base.getId() == null;
			//建立人，只有有上下文时才需要记录
			if(base.getCreatedTime() == null){
				base.setCreatedTime(new Timestamp(System.currentTimeMillis()));
				if(ctx != null && ctx.getUserId() != null){
					base.setCreatedById(ctx.getUserId());
				}
			}else{
				base.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
			}
			//最后修改人，只有有上下文时才需要记录
			if(ctx != null && ctx.getUserId() != null){
				base.setUpdatedById(ctx.getUserId());
			}
			//记录修改日志
			if(base.getId() != null && !base.getId().equals(0L)) {
                em.detach(obj);
                Object old = get(obj.getClass(), base.getId());
                if(old != null){
					if(ctx != null){
						orientData = JSON.toJSONString(old);
					}
					//刷新原缓存数据
					refreshCacheByDomainObject(old, true);
				}
				if(old != null) {
					try {
						BeanUtils.copyProperties(obj, old);
					} catch (Exception e) {
						log.error("数据copy属性时失败.", e);
					}
					obj = old;
				}
			}
		}
        obj = (T) em.merge(obj);
  		//记录新加或修改日志
  		if(ctx != null){
  			ctx.addUpdateLogger(orientData, obj);
  		}
        //同步刷新实体缓存
        refreshCacheByDomainObject(obj, false);
  		return (T)obj;
    }
    

	
	private Map<Class, List<Field>> caches = new HashMap<Class, List<Field>>();
    private List<Field> getKeywordFields(Class cla){
    	if(caches.containsKey(cla)){
    		return caches.get(cla);
    	}
    	List<Field> fls = new ArrayList<Field>();
    	Field[] fields = cla.getDeclaredFields();
    	for(Field f : fields){
    		ModelField mf = f.getAnnotation(ModelField.class);
    		if(mf != null && mf.iskeyword()){
    			fls.add(f);
    		}
    	}
    	caches.put(cla, fls);
    	return fls;
    }
    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#removeById(java.lang.Class, java.lang.Object)
	 */
    @Override
	public void removeById(Class<?> cla, Object id){
		Object obj = get(cla, id);
		if(obj != null)
			remove(obj);
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findFirst(java.lang.String, boolean, java.lang.Object)
	 */
    @Override
	@SuppressWarnings("unchecked")
    public <T> T findFirst(String hql, boolean cacheable, Object... params) {
        Query query = em.createQuery(hql);

        if (cacheable) {
            query.setHint("org.hibernate.cacheable", true);
        }
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        List<Object> ls = query.setFirstResult(0).setMaxResults(1).getResultList();
        if (ls.isEmpty()) return null;
        return (T)ls.get(0);
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findDouble(java.lang.String, boolean, java.lang.Object)
	 */
    @Override
	public double findDouble(String hql, boolean cacheable, Object... params) {
        Object obj = findFirst(hql, cacheable, params);
        return obj == null ? 0d : Double.parseDouble(obj + "");
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findInt(java.lang.String, boolean, java.lang.Object)
	 */
    @Override
	public int findInt(String hql, boolean cacheable, Object... params) {
        Object obj = findFirst(hql, cacheable, params);
        return obj == null ? 0 : Integer.parseInt(obj + "");
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findFirst(com.ecity.common.database.vo.Hql)
	 */
    @Override
	@SuppressWarnings("unchecked")
    public <T> T findFirst(Hql hql) {
        return (T) findFirst(hql.getSqlstr(), false, hql.getParamArray());
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#find(com.ecity.common.database.vo.Hql)
	 */
    @Override
	public <T> List<T> find(Hql hql) {
        return find(hql.getSqlstr(), false, hql.getParamArray());
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findFirstWithCache(com.ecity.common.database.vo.Hql)
	 */
    @Override
	@SuppressWarnings("unchecked")
    public <T> T findFirstWithCache(Hql hql) {
        return (T) findFirst(hql.getSqlstr(), true, hql.getParamArray());
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#find(java.lang.String, boolean, java.lang.Object)
	 */
    @Override
	@SuppressWarnings("rawtypes")
    public List find(String hql, boolean cacheable, Object... params) {
        Query query = em.createQuery(hql);
        if (cacheable) {
            query.setHint("org.hibernate.cacheable", true);
        }
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        return query.getResultList();
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findPage(java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.Object)
	 */
    @Override
	public PageData findPage(String hql, boolean cacheable,
                             Integer page, Integer pageSize, Object... params) {
        page = (page == null || page < 1) ? 1 : page;
        pageSize = (pageSize == null || pageSize <= 0) ? 20 : pageSize;
        int start = (page - 1) * pageSize;
        return findPageByStartLimit(hql, cacheable, start, pageSize, params);
    }


    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findPageByStartLimit(java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.Object)
	 */
    @Override
	public PageData findPageByStartLimit(String hql, boolean cacheable,
                                         Integer start, Integer limit, Object... params) {
        if (start < 0) {
            start = 0;
        }
        if (limit <= 0) {
            limit = 20;
        }
        List data = findLimit(hql, cacheable, start, limit, params);
        String thql = hql.trim();
        if (thql.toLowerCase().indexOf("order by") > 0) {
            thql = thql.substring(0, thql.toLowerCase().indexOf("order by"));
        }
        if (thql.toLowerCase().indexOf("from") > 0) {
            thql = thql.substring(thql.toLowerCase().indexOf("from"));
        }
        thql = "select count(*) " + thql;
        int total = findInt(thql, cacheable, params);
        PageData rdata = new PageData(total, data);
        rdata.setPage(start % limit == 0 ? start / limit : (start / limit + 1));
        rdata.setPageSize(limit);
        return rdata;
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findPage(com.ecity.common.database.vo.Hql, boolean, java.lang.Integer, java.lang.Integer)
	 */
    @Override
	public PageData findPage(Hql hql, boolean cacheable, Integer page, Integer pageSize) {
        return findPage(hql.getSqlstr(), cacheable, page, pageSize, hql.getParamArray());
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findPage(com.ecity.common.database.vo.Hql, boolean, com.ecity.common.database.vo.PageData)
	 */
    @Override
	public PageData findPage(Hql hql, boolean cacheable, PageData pageData) {
        return findPage(hql.getSqlstr(), cacheable, pageData.getPage(), pageData.getPageSize(), hql.getParamArray());
    }

    /* (non-Javadoc)
	 * @see com.ecity.common.database.SysEntityManagerWrapperPort#findLimit(java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.Object)
	 */
    @Override
	public List findLimit(String hql, boolean cacheable, Integer start, Integer limit, Object... params) {
        Query query = em.createQuery(hql);
        if (cacheable) {
            query.setHint("org.hibernate.cacheable", true);
        }
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        query.setFirstResult(start == null ? 0 : start);
        if (limit != null && limit > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
    @Override
    public List findByCacheItem(Class domainClass, String cacheItemPrefix,
    		Object... args) {
    	ModelCache mc = (ModelCache)domainClass.getAnnotation(ModelCache.class);
    	if(mc == null){
    		throw new ServiceException("在【" + domainClass.getName() + "】上未找到前缀为【" + cacheItemPrefix + "】的缓存配置");
    	}
    	CacheItem[] items = mc.items();
    	CacheItem item = null;
    	for(CacheItem i : items){
    		if(i.value().startsWith(cacheItemPrefix)){
    			if(item == null){
    				item = i;
    			}else{
    				throw new ServiceException("在【" + domainClass.getName() + "】上存在多个前缀为【" + cacheItemPrefix + "】的缓存配置");
    			}
    		}
    	}
    	if(item == null){
    		throw new ServiceException("在【" + domainClass.getName() + "】上未找到前缀为【" + cacheItemPrefix + "】的缓存配置");
    	}
    	String value = item.value();
    	String selstr = item.listprops();
    	Hql hql = null;
    	List<String> fields = new ArrayList<String>();
    	if(selstr == null || selstr.trim().equals("")){
    		hql = Hql.get("select a.id from " + domainClass.getName() + " a");
    	}else{
    		//前面加上a.
    		for(String s : selstr.split(",")){
    			if(!s.matches("\\w+")){
    				fields.add(s);
    			}else{
    				fields.add("a." + s);
    			}
    		}
    		hql = Hql.get("select " + Globals.join(fields, ",") + " from "
    				+ domainClass.getName() + " a");
    	}
    	Pattern pttern = Pattern.compile("\\{\\w+\\}", Pattern.CASE_INSENSITIVE);
    	Matcher m = pttern.matcher(value);
    	StringBuffer sb = new StringBuffer();
    	int index = 0;
    	Where where = Where.get("where");
    	while(m.find()){
    		String prop = m.group();
    		prop = prop.substring(1, prop.length()-1);
    		if(args == null || args.length <= index){
				throw new RuntimeException("获取实体【" + domainClass.getSimpleName() + "】以【" + cacheItemPrefix + "】开头的缓存键时失败，给定的参数个数不够");
			}
			Object vobj = args[index];
			index++; 
    		m.appendReplacement(sb, vobj==null?"":vobj.toString());
    		//添加参数
    		if(item.nullToIsNull())
    			where.mustEq("a." + prop, vobj);
    		else
    			where.eq("a." + prop, vobj);
    	}
    	m.appendTail(sb);
    	List<Object> ls = null;
    	//先从缓存获取
    	String keygroup = RedisHelper.getDomainCacheGroup(domainClass);
    	String key = sb.toString();
    	CacheResult result = RedisHelper.get(keygroup, key);
    	if(result != null && result.isExists() && result.getData() != null){
    		ls = result.getData();
    	}else{
	    	//再执行数据库查询
	    	hql.add(where);
	    	if(item.orderby() != null && !item.orderby().equals("")){
	    		hql.add("order by " + item.orderby());
	    	}
	    	ls = find(hql);
	    	//缓存最原始的数据
	    	//log.debug("SET CACHEKEY:" + keygroup + key);
			if(ls.size() < 500)
	    		RedisHelper.set(keygroup, key, (Serializable)ls);
    	}
    	//对结果进行转换输出
    	List<Object> data = new ArrayList<Object>();
		for(Object obj1 : ls){
			Object obj = obj1;
			if(obj == null)continue;
			if(fields.isEmpty()){
				obj = get(domainClass, obj);
			}
			if(item.listitemtype() == 1){
				if(fields.isEmpty()){
					data.add(JSONObject.toJSON(obj));
				}else{
					JSONObject rec = new JSONObject();
					Object[] recobjs = null;
					if(obj.getClass().isArray()){
						recobjs = (Object[])obj;
					}else{
						recobjs = new Object[]{obj};
					}
					for(int i=0; i<fields.size(); i++){
						String f = fields.get(i);
						if(f.startsWith("a.")){
							rec.put(f.substring(2), recobjs[i]);
						}else{
							if(f.indexOf("(") > 0){
								f = f.substring(0, f.indexOf("("));    								
							}
							rec.put(f, recobjs[i]);
						}
					}
					data.add(rec);
				}
			}else if(item.listitemtype() == 2){
				if(obj.getClass().isArray()){
					data.add(obj);
				}else{
					if(fields.isEmpty()){
						JSONObject recobj = (JSONObject)JSON.toJSON(obj);
						data.add(recobj.values().toArray());
					}else{
						data.add(new Object[]{obj});
					}
				}
			}else{
				data.add(obj);
			}
		}
		return data;
		
    }
    @Override
    public Long findLongByCacheItem(Class domainClass, String cackeItemPrefix,
    		Object... args) {
    	Object obj = findFirstByCacheItem(domainClass, cackeItemPrefix, args);
    	return obj == null?null:Long.valueOf(obj.toString());
    }
    @Override
    public int findIntByCacheItem(Class domainClass, String cackeItemPrefix,
    		Object... args) {
    	Object obj = findFirstByCacheItem(domainClass, cackeItemPrefix, args);
    	return obj == null?0:Integer.valueOf(obj.toString());
    }
    @Override
    public <T> T findFirstByCacheItem(Class domainClass,
    		String cackeItemPrefix, Object... args) {
    	List<Object> ls = findByCacheItem(domainClass, cackeItemPrefix, args);
    	if(ls.isEmpty()){
    		return null;
    	}
    	return (T)ls.get(0);
    }
    private void refreshCacheByDomainObject(Object obj, boolean onlyNeedRefreshBeforeUpdate){
    	if(obj == null)return;
    	ModelCache model = obj.getClass().getAnnotation(ModelCache.class);
    	if(model == null)return;
    	CacheItem[] caches = model.items();
    	if(caches == null)return;
    	Pattern pttern = Pattern.compile("\\{\\w+\\}", Pattern.CASE_INSENSITIVE);
    	String group = getDomainCacheGroup(obj.getClass());
		for(CacheItem ckey : caches){
			if(onlyNeedRefreshBeforeUpdate && ckey.needRefreshBeforeUpdate()){
				continue;
			}
    		String str = ckey.value();
    		Matcher m = pttern.matcher(str);
    		StringBuffer sb = new StringBuffer();
    		while(m.find()){
    			String prop = m.group();
    			try {
    				prop = prop.substring(1, prop.length()-1);
    				Object vobj = PropertyUtils.getProperty(obj, prop);
        			m.appendReplacement(sb, vobj==null?"":vobj.toString());
				} catch (Exception e) {
					throw new ServiceException("刷新实体【" + obj.getClass().getSimpleName() + "】的缓存【" + str + "】时失败", e);
				}
    		}
    		m.appendTail(sb);
    		String key = sb.toString();
    		RedisHelper.delete(group, key);
    		//log.debug("REMOVE CACHEKEY:" + group + key);
    		
    	}
    }
    private String getDomainCacheGroup(Class entityClass){
    	return "_dm." + entityClass.getSimpleName() + ".";
    }
}
