package com.ch.frame.mongodb;

import com.ch.frame.conf.ConfigHelper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.BsonType;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改多库支持\文件配置化
 *
 * @author huyuangui
 * @time 2016-12-30
 */
public class MongoDbHelper {
    protected Log log = LogFactory.getLog(MongoDbHelper.class);
    //MongoDb数据库
    private MongoClient mongoClient = null;
    private String dbname;
    private String host;
    private int socketTimeout = 60000;
    private int connectTimeout = 6000;
    private int maxWaitTime = 12000;
    private int poolsize = 20;
    private static MongoDbHelper inst;

    public synchronized static MongoDbHelper get() {
        if (inst == null) {
            inst = new MongoDbHelper();
        }
        return inst;
    }

    private MongoDbHelper() {
        host = ConfigHelper.getProp("mongodb").get("host");
        dbname = ConfigHelper.getProp("mongodb").get("dbname");
        poolsize = ConfigHelper.getProp("mongodb").getInt("poolsize");
        connectTimeout = ConfigHelper.getProp("mongodb").getInt("connectTimeout");
        maxWaitTime = ConfigHelper.getProp("mongodb").getInt("maxWaitTime");
        socketTimeout = ConfigHelper.getProp("mongodb").getInt("socketTimeout");
        if (!host.startsWith("mongodb://")) {
            host = "mongodb://" + host;
        }
        List<CodecRegistry> ls = new ArrayList<CodecRegistry>();
        //日期时间 Timestamp
        Map<BsonType, Class<?>> replacements = new HashMap<BsonType, Class<?>>();
        replacements.put(BsonType.DATE_TIME, Timestamp.class);
        BsonTypeClassMap bsonTypeClassMap = new BsonTypeClassMap(replacements);
        DocumentCodecProvider documentCodecProvider =
            new DocumentCodecProvider(bsonTypeClassMap);
        ls.add(CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(new TimestampCodec()),
            CodecRegistries.fromProviders(documentCodecProvider),
            MongoClient.getDefaultCodecRegistry()));
        //java.sql.Date
        replacements = new HashMap<BsonType, Class<?>>();
        replacements.put(BsonType.DATE_TIME, Date.class);
        bsonTypeClassMap = new BsonTypeClassMap(replacements);
        documentCodecProvider =
            new DocumentCodecProvider(bsonTypeClassMap);
        ls.add(CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(new DateCodec()),
            CodecRegistries.fromProviders(documentCodecProvider),
            MongoClient.getDefaultCodecRegistry()));

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(ls);
        mongoClient = new MongoClient(new MongoClientURI(host, MongoClientOptions.builder()
            .connectTimeout(connectTimeout)
            .maxWaitTime(maxWaitTime)
            .writeConcern(WriteConcern.SAFE)
            .connectionsPerHost(poolsize)
            .socketTimeout(socketTimeout)
            .socketKeepAlive(true)
            .codecRegistry(codecRegistry)));
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    /**
     * 获取数据库
     *
     * @param dbname
     * @return
     */
    public MongoDatabase getMongoDataBase(String dbname) {
        if (StringUtils.isBlank(dbname)) {
            dbname = this.dbname;
        }
        return getMongoClient().getDatabase(dbname);
    }

    /**
     * 获取集合 默认采用配置库
     * 只有一个情况默认采用默认数据库，多个默认第一个为数据库dbName，第二个为dcName
     *
     * @param params
     * @return
     */
    public DBCollectionWraper getDc(String... params) {
        if (null != params) {
            if (params.length < 2) {
                return new DBCollectionWraper(getMongoDataBase(dbname).getCollection(params[0]));
            } else {
                return new DBCollectionWraper(getMongoDataBase(params[0]).getCollection(params[1]));
            }
        }
        return null;
    }

    /**
     * 检查dc是否存在  默认采用配置库
     * 只有一个情况默认采用默认数据库，多个默认第一个为数据库dbName，第二个为dcName
     *
     * @param params
     * @return
     */
    public boolean existDc(String... params) {
        if (params == null) {
            return false;
        }
        MongoDatabase mongoDatabase = null;
        String dcName = "";
        if (null != params) {
            if (params.length < 2) {
                mongoDatabase = getMongoDataBase(dbname);
                dcName = params[0];
            } else {
                mongoDatabase = getMongoDataBase(params[0]);
                dcName = params[1];
            }
        }
        if (null != mongoDatabase) {
            MongoCursor<String> it = mongoDatabase.listCollectionNames().iterator();
            try {
                while (it.hasNext()) {
                    if (it.next().equals(dcName)) {
                        return true;
                    }
                }
                return false;
            } finally {
                it.close();
            }
        }
        return false;
    }
}
