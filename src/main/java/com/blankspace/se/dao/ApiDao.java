package com.blankspace.se.dao;

import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.blankspace.se.pojo.JavaClass;
import com.blankspace.se.pojo.JavaMethod;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ApiDao {

    private static volatile ApiDao singletonDao;

    private static final String MONGO_HOST = "127.0.0.1";

    private static final Integer MONGO_PORT = 27017;

    private static final String MONGO_DB = "java_api";

    private ApiDao() {
    }

    // 双重校验锁单例模式
    public static ApiDao getSingletonDao() {
        if (singletonDao == null) {
            synchronized (ApiDao.class) {
                if (singletonDao == null) {
                    singletonDao = new ApiDao();
                }
            }
        }
        return singletonDao;
    }

    public void insertAllJavaAPIs(List<JavaClass> javaClassList) {
        String url = "mongodb://" + MONGO_HOST + ":" + MONGO_PORT;
        // 连接到 mongodb 服务
        // https://stackoverflow.com/questions/54426018/mongoclient-is-abstract-cannot-be-instantiated
        // https://mongodb.github.io/mongo-java-driver/3.9/javadoc/com/mongodb/ConnectionString.html
        // https://www.runoob.com/mongodb/mongodb-tutorial.html
        // https://pdai.tech/md/db/nosql-mongo/mongo-x-usage-4.html
        try (MongoClient mongoClient = MongoClients.create(url)) {
            // 连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB);
            System.out.println("Connect to database successfully");
            // 创建Collection
            mongoDatabase.createCollection(MONGO_DB);
            System.out.println("create collection");
            // 获取collection
            MongoCollection<Document> collection = mongoDatabase.getCollection(MONGO_DB);
            int tempId = 1;
            // 插入document
            for (JavaClass classObj : javaClassList) {
                Document methodDocument = new Document();
                for (JavaMethod method : classObj.getMethodList()) {
                    Map<String, Object> methodObjMap = new ObjectMapper().convertValue(method, new TypeReference<Map<String, Object>>() {
                    });
                    methodDocument.append(method.getMethodTitle(), methodObjMap);
                    System.out.println(method);
                }
                Document classDocument = new Document("id", tempId)
                        .append("className", classObj.getClassName())
                        .append("classURL", classObj.getClassURL())
                        .append("method", methodDocument);
                System.out.println(classObj);
                collection.insertOne(classDocument);
            }
            // 统计Document条数
            System.out.println(collection.countDocuments());
        } catch (Exception e) {
            e.printStackTrace();
            //System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

}
