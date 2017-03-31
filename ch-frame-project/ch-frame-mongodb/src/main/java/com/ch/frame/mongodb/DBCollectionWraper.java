package com.ch.frame.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class DBCollectionWraper {
    protected Log log = LogFactory.getLog(DBCollectionWraper.class);
    private MongoCollection<Document> dc2 = null;

    public DBCollectionWraper(MongoCollection<Document> dc2) {
        this.dc2 = dc2;
    }

    /**
     * 根据ID获取
     */
    public Document getById(String id) {
        return dc2.find(new BasicDBObject("_id", new ObjectId(id))).first();
    }

    private Document convertToDoc(DBObject obj) {
        if (obj == null) return null;
        Document doc = new Document();
        for (String key : obj.keySet()) {
            Object value = obj.get(key);
            if (value instanceof DBObject) {
                value = convertToDoc((DBObject) value);
            }
            doc.put(key, value);
        }
        return doc;
    }

    private BasicDBObject convertToBasicDbObject(Document doc) {
        if (doc == null) return null;
        BasicDBObject rec = new BasicDBObject();
        for (String key : doc.keySet()) {
            Object val = doc.get(key);
            if (val instanceof Document) {
                val = convertToBasicDbObject((Document) val);
            }
            rec.put(key, val);
        }
        return rec;
    }

    /**
     * 获取第一个对象
     *
     * @param ref 请使用findFirstDoc
     */
    @Deprecated
    public DBObject findFirst(Bson ref) {
        Document doc = dc2.find(ref).first();
        return convertToBasicDbObject(doc);
    }

    /**
     * 获取第一个对象
     *
     * @param ref
     * @return
     */
    public Document findFirstDoc(Bson ref) {
        return dc2.find(ref).first();
    }

    /**
     * 总文档数
     *
     * @return
     */
    public long count() {
        return dc2.count();
    }

    /**
     * 统计文档数
     *
     * @param query
     * @return
     */
    public int count(Bson query) {
        return Long.valueOf(dc2.count(query)).intValue();
    }

    /**
     * 创建索引
     *
     * @param keys
     */
    public void createIndex(Bson keys) {
        dc2.createIndex(keys);
    }

    /**
     * 删除集合
     */
    public void drop() {
        dc2.drop();
    }

    /**
     * 创建索引
     *
     * @param name
     */
    public void ensureIndex(String name) {
        createIndex(new Document(name, 1));
    }
    /**
     * 查找记录
     * @param ref
     * @return
     */
    /*public DBCursor find(Bson ref) {
        return dc2.find(ref);
	}
	public DBCursor find() {
		return getDc().find();
	}
	public DBCursor find(DBObject ref, DBObject keys) {
		return getDc().find(ref, keys);
	}*/

    /**
     * 删除符合条件的全部文件
     *
     * @param o
     */
    public void remove(Bson o) {
        dc2.deleteMany(o);
    }


    /**
     * 新加文档
     *
     * @param doc
     */
    public void save(Document doc) {
        dc2.insertOne(doc);
    }

    /**
     * @param q
     * @param o
     * @param upsert 如果未找到时是否插入 默认为false
     * @param multi  是否全部更新，默认为false
     * @return
     */
    @Deprecated
    public void update(Bson q, DBObject o, boolean upsert,
                       boolean multi) {
        if (o != null) {
            boolean find = false;
            for (String key : o.keySet()) {
                if (key.startsWith("$")) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                o = new BasicDBObject("$set", o);
                log.debug("Auto add $set.");
            }
        }
        updateDoc(q, (Bson) o, upsert, multi);
    }

    /**
     * @param q
     * @param o
     * @param upsert 如果未找到时是否插入 默认为false
     * @param multi  是否全部更新，默认为false
     * @return
     */
    public UpdateResult updateDoc(Bson q, Bson o, boolean upsert,
                                  boolean multi) {
        UpdateOptions uo = new UpdateOptions();
        if (upsert) {
            uo.upsert(true);
        }
        UpdateResult r = null;
        if (multi) {
            r = dc2.updateMany(q, o, uo);
        } else {
            r = dc2.updateOne(q, o, uo);
        }
        return r;
    }

    public UpdateResult replaceDoc(Bson q, Bson o, boolean upsert,
                                   boolean multi) {
        UpdateOptions uo = new UpdateOptions();
        if (upsert) {
            uo.upsert(true);
        }
        o = new Document("$set", o);
        UpdateResult r = null;
        if (multi) {
            r = dc2.updateMany(q, o, uo);
        } else {
            r = dc2.updateOne(q, o, uo);
        }
        return r;
    }

    /**
     * 更新一条记录
     *
     * @param q
     * @param o
     * @return
     */
    public UpdateResult update(Bson q, Bson o) {
        return dc2.updateOne(q, o);
    }

    /**
     * 批量更新一个字段的值
     *
     * @param basicDBObject
     * @param string
     * @param enable
     */
    @Deprecated
    public void batchUpdate(Bson q, String field,
                            Object val) {
        update(q, new BasicDBObject("$set", new BasicDBObject(field, val)), false, true);
    }

    /**
     * 查找所有
     *
     * @return
     */
    public FindIterable<Document> find() {
        return dc2.find();
    }

    /**
     * 排序查找所有
     *
     * @return
     */
    public List<Document> sortFind(Bson sort) {
        FindIterable<Document>
            ft = find();

        if (sort != null) {
            ft.sort(sort);
        }
        List<Document> ls = new ArrayList<Document>();
        MongoCursor<Document> it = ft.iterator();
        while (it.hasNext()) {
            ls.add(it.next());
        }
        it.close();
        return ls;
    }

    /**
     * 根据条件查询
     *
     * @param q
     * @return
     */
    public FindIterable<Document> find(Bson q) {
        return dc2.find(q);
    }

    /**
     * 聚合爱函数查询
     *
     * @param qs
     * @return
     */
    public AggregateIterable<Document> aggregate(List<? extends Bson> qs) {
        return dc2.aggregate(qs);
    }

    /**
     * 执行限制查询
     *
     * @param query
     * @param integer
     * @param integer2
     * @return
     */
    public List<Document> findLimit(Bson query, Bson sort, int start, int limit) {
        if (start < 0) start = 0;
        FindIterable<Document>
            ft = find(query);

        if (sort != null) {
            ft.sort(sort);
        }
        List<Document> ls = new ArrayList<Document>();
        MongoCursor<Document> it = ft.skip(start).limit(limit).iterator();
        while (it.hasNext()) {
            ls.add(it.next());
        }
        it.close();
        return ls;
    }

    public List<Document> findAll(Bson query, Bson sort) {
        FindIterable<Document>
            ft = find(query);

        if (sort != null) {
            ft.sort(sort);
        }
        List<Document> ls = new ArrayList<Document>();
        MongoCursor<Document> it = ft.iterator();
        while (it.hasNext()) {
            ls.add(it.next());
        }
        it.close();
        return ls;
    }

    /**
     * 查询Dx的一页数据
     * @return
     */
    /*public PageData findDxPage(Bson query, Bson sort, int page, int pageSize) {
        int limit = pageSize > 0 ? pageSize : 20;
        int start = (page > 0 ? (page - 1) * pageSize : 0);
        FindIterable<Document>
            ft = find(query);
        if (sort != null) {
            ft.sort(sort);
        }
        int total = count(query);
        List<Document> ls = new ArrayList<Document>();
        MongoCursor<Document> it = ft.skip(start).limit(limit).iterator();
        while (it.hasNext()) {
            ls.add(it.next());
        }
        it.close();
        PageData data = new PageData();
        data.setData(ls);
        data.setTotal(total);
        return data;
    }*/

    /**
     * 查询一页数据
     *
     * @return
     */
    public JSONObject findPage(Bson query, Bson sort, int page, int pageSize) {
        int limit = pageSize > 0 ? pageSize : 20;
        int start = (page > 0 ? (page - 1) * pageSize : 0);
        return findLimitForPage(query, sort, start, limit);
    }

    /**
     * 查询范围
     *
     * @param query
     * @param sort
     * @param start
     * @param limit
     * @return
     */
    public JSONObject findLimitForPage(Bson query, Bson sort, int start, int limit) {
        FindIterable<Document>
            ft = find(query);
        if (sort != null) {
            ft.sort(sort);
        }
        int total = count(query);
        List<Document> ls = new ArrayList<Document>();
        FindIterable<Document> fi = ft.skip(start).limit(limit);
        MongoCursor<Document> it = fi.iterator();
        while (it.hasNext()) {
            ls.add(it.next());
        }
        it.close();
        int page = start % limit == 0 ? start / limit : (start / limit + 1);
        if (page <= 0) {
            page = 1;
        }
        JSONObject data = new JSONObject();
        data.put("data", ls);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", limit);
        return data;
    }


    public MongoCollection<Document> getDc() {
        return dc2;
    }
}
