package com.blankspace.se.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

import com.blankspace.se.pojo.JavaMethodCode;
import com.blankspace.se.service.CodeProcess;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CodeProcessImpl implements CodeProcess {

    private static volatile CodeProcessImpl singletonService;

    private CodeProcessImpl() {
    }

    // 双重校验锁单例模式
    public static CodeProcessImpl getSingletonService() {
        if (singletonService == null) {
            synchronized (CodeProcessImpl.class) {
                if (singletonService == null) {
                    singletonService = new CodeProcessImpl();
                }
            }
        }
        return singletonService;
    }

    @Override
    public void accessAllFiles() {
        String rootPath = "./src/main/resources/codes/";
        File rootDictionary = new File(rootPath);
        for (File file : Objects.requireNonNull(rootDictionary.listFiles())) {
            String fileName = rootPath + file.getName();
            process(fileName);
        }
    }

    private void process(String fileName) {
        // File、Path的根目录为工程根目录
        try (Scanner scanner = new Scanner(Files.newInputStream(Paths.get(fileName)))) {
            while (scanner.hasNextLine()) {
                String json = scanner.nextLine();
                ObjectMapper mapper = new ObjectMapper();
                JavaMethodCode code = mapper.readValue(json, JavaMethodCode.class);
                System.out.println(code);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
