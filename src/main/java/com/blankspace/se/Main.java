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
            for (File file : Objects.requireNonNull(rootFile.listFiles())) {
                if (file.isDirectory()) {
                    accessAllFiles(file);
                }
                if (file.isFile()) {
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
                        StringBuilder methodContent = new StringBuilder();
                        // class: modifiers + return-type + element-name + parameters
                        for (Element methodSpan : memberSignature) {
                            methodContent.append(methodSpan.text()).append(' ');
                        }
                        System.out.println(methodContent);
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
