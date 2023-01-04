package com.blankspace.se;

import com.blankspace.se.service.ClassProcess;
import com.blankspace.se.service.CodeProcess;
import com.blankspace.se.service.impl.ClassProcessImpl;
import com.blankspace.se.service.impl.CodeProcessImpl;

public class Main {

    private static void initJavaClassData() {
        ClassProcess processor = ClassProcessImpl.getSingletonService();
        processor.addAllClassInfoIntoMongoDB();
    }

    private static void initCodeSearchNetData() {
        CodeProcess processor = CodeProcessImpl.getSingletonService();
        processor.downloadSourceCodeFiles();
    }

    private static void loadClassFileImport() {
        CodeProcess processor = CodeProcessImpl.getSingletonService();
        processor.getJavaFileImports();
    }

    public static void main(String[] args) {
        // initJavaClassData();
        // initCodeSearchNetData();
        loadClassFileImport();
    }

}
