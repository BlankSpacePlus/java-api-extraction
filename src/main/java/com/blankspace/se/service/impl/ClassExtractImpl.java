package com.blankspace.se.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.blankspace.se.dao.ApiDao;
import com.blankspace.se.pojo.JavaClass;
import com.blankspace.se.pojo.JavaMethod;
import com.blankspace.se.service.ClassExtract;

public class ClassExtractImpl implements ClassExtract {

    private static final List<JavaClass> javaClassList = new ArrayList<>();

    public void addAllClassInfoIntoMongoDB() throws IOException {
        accessAllFiles(new File("./src/main/resources/docs/api"));
        ApiDao.getSingletonDao().insertAllJavaAPIs(javaClassList);
    }

    public void accessAllFiles(File rootFile) throws IOException {
        if (rootFile != null) {
            String rootDictFile = rootFile.getName();
            for (File file : Objects.requireNonNull(rootFile.listFiles())) {
                String fileName = file.getName();
                if (file.isDirectory() && !"index-files".equals(fileName) && !"class-use".equals(fileName)) {
                    accessAllFiles(file);
                } else if (file.isFile() && !"api".equals(rootDictFile) && fileName.endsWith(".html")
                        && !"package-summary.html".equals(fileName) && !"package-tree.html".equals(fileName)
                        && !"package-use.html".equals(fileName) && !"module-summary.html".equals(fileName)) {
                    System.out.println(fileName.replaceAll(".html", ".class"));
                    StringBuilder urlLink = new StringBuilder();
                    // https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Integer.html
                    urlLink.append("https://docs.oracle.com/en/java/javase/19");
                    // 如果Windows系统路径不符合Web上URL格式，则replaceAll()处理，正则表达式反斜线要加四个才行
                    String baseURL = rootFile.toString()
                            .replaceAll(".\\\\src\\\\main\\\\resources", "")
                            .replaceAll("\\\\", "/");
                    urlLink.append(baseURL).append('/').append(fileName);
                    System.out.println(urlLink);
                    getAllMethods(file.toString(), urlLink.toString());
                }
            }
        }
    }

    public void getAllMethods(String fileName, String url) throws IOException {
        StringBuilder htmlContent = new StringBuilder();
        JavaClass newClassObj = new JavaClass();
        System.out.println(javaClassList.size() + 1);
        newClassObj.setId(javaClassList.size() + 1);
        newClassObj.setClassURL(url);
        // File、Path的根目录为工程根目录
        try (Scanner scanner = new Scanner(Files.newInputStream(Paths.get(fileName)))) {
            while (scanner.hasNextLine()) {
                htmlContent.append(scanner.nextLine()).append("\n");
            }
        }
        Document doc = Jsoup.parse(htmlContent.toString());
        Elements headerDiv = doc.getElementsByClass("header");
        for (Element headerElement : headerDiv) {
            Elements aList = headerElement.getElementsByTag("a");
            Elements h1List = headerElement.getElementsByTag("h1");
            StringBuilder className = new StringBuilder();
            if (!aList.isEmpty() && !h1List.isEmpty()) {
                className.append(aList.get(1).text()).append('.');
                className.append(h1List.get(0).text().substring(6));
            }
            newClassObj.setClassName(className.toString().trim());
            System.out.println(className);
        }
        List<JavaMethod> javaMethodList = new ArrayList<>();
        int counter = 1;
        Element methodDetailSection = doc.getElementById("method-detail");
        if (methodDetailSection != null) {
            // 实际上只有一个
            Elements memberUlList = methodDetailSection.getElementsByTag("ul");
            for (Element memberUl : memberUlList) {
                Elements detailLiList = memberUl.getElementsByTag("li");
                for (Element detailLi : detailLiList) {
                    // 实际上只有一个
                    Elements detailSectionList = detailLi.getElementsByClass("detail");
                    for (Element detailSection : detailSectionList) {
                        JavaMethod newMethodObj = new JavaMethod();
                        newMethodObj.setId(counter++);
                        String methodName = detailSection.attr("id");
                        Elements memberSignature = detailSection.getElementsByClass("member-signature");
                        StringBuilder methodTitle = new StringBuilder();
                        // class: modifiers + return-type + element-name + parameters + exceptions
                        for (Element methodSpan : memberSignature) {
                            methodTitle.append(methodSpan.text().trim()).append(' ');
                        }
                        System.out.println(methodTitle);
                        // 把.替换成*，不然MongoDB不支持写入
                        newMethodObj.setMethodTitle(methodTitle.toString().trim().replaceAll("[.]", "*"));
                        Elements blockDiv = detailSection.getElementsByClass("block");
                        StringBuilder methodContent = new StringBuilder();
                        for (Element content : blockDiv) {
                            methodContent.append(content.text().trim());
                        }
                        newMethodObj.setMethodContent(methodContent.toString());
                        System.out.println(methodContent);
                        Elements notesDiv = detailSection.getElementsByClass("notes");
                        StringBuilder methodNotes = new StringBuilder();
                        for (Element note : notesDiv) {
                            methodNotes.append(note.text().trim());
                        }
                        newMethodObj.setMethodNotes(methodNotes.toString());
                        javaMethodList.add(newMethodObj);
                        System.out.println(methodNotes);
                    }
                }
            }
        }
        newClassObj.setMethodList(javaMethodList);
        javaClassList.add(newClassObj);
        System.out.println("---------------------------------------------------------");
    }

}
