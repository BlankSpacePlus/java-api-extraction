package com.blankspace.se.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.blankspace.se.pojo.JavaClass;
import com.blankspace.se.pojo.JavaMethod;
import com.blankspace.se.pojo.JavaMethodApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
            System.out.println("Creating collection");
            // 获取Collection
            MongoCollection<Document> collection = mongoDatabase.getCollection(MONGO_DB);
            int tempId = 1;
            // 插入Document
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
        }
    }

    public List<JavaMethodApi> getAllJavaAPIs() {
        String url = "mongodb://" + MONGO_HOST + ":" + MONGO_PORT;
        int tempId = 1;
        List<JavaMethodApi> apiList = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(url)) {
            // 连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB);
            System.out.println("Connect to database successfully");
            // 获取Collection
            MongoCollection<Document> collection = mongoDatabase.getCollection(MONGO_DB);
            System.out.println("Getting collection");
            // 遍历Collection
            MongoCursor<Document> cursor = collection.find().iterator();
            try {
                while (cursor.hasNext()) {
                    Document document = cursor.next();
                    int classId = document.getInteger("id");
                    String className = document.getString("className");
                    String classURL = document.getString("classURL");
                    Document methodDocuments = (Document) document.get("method");
                    for (Map.Entry<String, Object> methodEntry : methodDocuments.entrySet()) {
                        Document methodDocument = (Document) methodEntry.getValue();
                        int methodId = methodDocument.getInteger("id");
                        String methodTitle = methodDocument.getString("methodTitle");
                        String methodContent = methodDocument.getString("methodContent");
                        String methodNotes = methodDocument.getString("methodNotes");
                        String methodName = className + ".";
                        String[] methodTitleTokens = methodTitle.trim().split("\\s+");
                        boolean flag = false;
                        for (String methodTitleToken : methodTitleTokens) {
                            if (methodTitleToken.contains("(")) {
                                flag = true;
                            }
                            if (flag) {
                                methodName += methodTitleToken;
                                if (methodTitleToken.contains(")")) {
                                    break;
                                }
                                methodName += " ";
                            }
                        }
                        methodName = methodName.trim();
                        JavaMethodApi api = new JavaMethodApi(tempId++, methodName, methodContent, methodNotes);
                        System.out.println(api);
                        apiList.add(api);
                    }
                }
            } finally {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiList;
    }

    public String getAllJavaAPIJson() {
        List<JavaMethodApi> apiList = getAllJavaAPIs();
        String result = "";
        try {
            result = new ObjectMapper().writeValueAsString(apiList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        File file = new File("src/main/resources/json/api.json");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {
            if (!file.exists()) {
                file.createNewFile();
            }
            bw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
