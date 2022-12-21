package com.blankspace.se;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

    private static void accessAllFiles(File rootFile) throws IOException {
        if (rootFile != null) {
            String rootDictFile = rootFile.getName();
            for (File file : Objects.requireNonNull(rootFile.listFiles())) {
                String fileName = file.getName();
                if (file.isDirectory() && !"index-files".equals(fileName)) {
                    accessAllFiles(file);
                } else if (file.isFile() && !"api".equals(rootDictFile) && fileName.endsWith(".html")
                        && !"package-summary.html".equals(fileName) && !"package-tree.html".equals(fileName)
                        && !"package-use.html".equals(fileName) && !"module-summary.html".equals(fileName)) {
                    System.out.println(file.getName());
                    getAllMethods(file.toString());
                }
            }
        }
    }

    private static void getAllMethods(String fileName) throws IOException {
        StringBuilder htmlContent = new StringBuilder();
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
            System.out.println(className);
        }
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
                        String methodName = detailSection.attr("id");
                        Elements memberSignature = detailSection.getElementsByClass("member-signature");
                        StringBuilder methodTitle = new StringBuilder();
                        // class: modifiers + return-type + element-name + parameters + exceptions
                        for (Element methodSpan : memberSignature) {
                            methodTitle.append(methodSpan.text()).append(' ');
                        }
                        System.out.println(methodTitle);
                        Elements blockDiv = detailSection.getElementsByClass("block");
                        StringBuilder methodContent = new StringBuilder();
                        for (Element content : blockDiv) {
                            methodContent.append(content.text()).append(' ');
                        }
                        System.out.println(methodContent);
                        Elements notesDiv = detailSection.getElementsByClass("notes");
                        StringBuilder methodNotes = new StringBuilder();
                        for (Element note : notesDiv) {
                            methodNotes.append(note.text()).append(' ');
                        }
                        System.out.println(methodNotes);
                    }
                }
            }
        }
        System.out.println("---------------------------------------------------------");
    }

    public static void main(String[] args) throws IOException {
        accessAllFiles(new File("./src/main/resources/docs/api"));
    }

}
