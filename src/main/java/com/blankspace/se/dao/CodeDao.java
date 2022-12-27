package com.blankspace.se.dao;

public class CodeDao {

    private static volatile CodeDao singletonDao;

    private CodeDao() {
    }

    // 双重校验锁单例模式
    public static CodeDao getSingletonDao() {
        if (singletonDao == null) {
            synchronized (CodeDao.class) {
                if (singletonDao == null) {
                    singletonDao = new CodeDao();
                }
            }
        }
        return singletonDao;
    }

    // TODO 实现数据存取

}
