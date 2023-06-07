package com.blankspace.se.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.SSLHandshakeException;

import com.blankspace.se.pojo.JavaCodeStorage;
import com.blankspace.se.pojo.JavaMethodCode;
import com.blankspace.se.service.CodeProcess;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CodeProcessImpl implements CodeProcess {

    private static final String ROOT_PATH = "./src/main/resources/codes/"; // 最好是写绝对路径

    private static final String PROXY_ADDRESS = "127.0.0.1";

    private static final int PROXY_PORT = 7079;

    private static final Set<String> urlNotFoundSet = new HashSet<>();

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
    public void downloadSourceCodeFiles() {
        File rootDictionary = new File(ROOT_PATH);
        // CodeSearchNet的.jsonl文件
        for (File file : Objects.requireNonNull(rootDictionary.listFiles())) {
            String fileName = file.getName();
            if (fileName.startsWith("java") && fileName.endsWith(".jsonl")) {
                String fileFullName = ROOT_PATH + file.getName();
                System.out.println("正在扫描" + fileFullName);
                saveSourceCodeFiles(fileFullName);
            }
        }
    }

    private void saveSourceCodeFiles(String fileName) {
        // File、Path的根目录为工程根目录
        try (Scanner scanner = new Scanner(Files.newInputStream(Paths.get(fileName)))) {
            while (scanner.hasNextLine()) {
                String json = scanner.nextLine();
                ObjectMapper mapper = new ObjectMapper();
                JavaMethodCode code = mapper.readValue(json, JavaMethodCode.class);
                String methodCodeURL = code.getUrl();
                // 通过扫描所有URL确认CodeSearchNet所有数据均来自GitHub，同时确认所有URL中/blob/只出现一次
                // checkGitHubURL(methodCodeURL);
                // String methodCode = code.getCode();
                // 扫描含有.的非`实例.方法`调用的方法，检测出292.74MB，数据规模非常大
                // checkMethodCodeHasPoint(methodCode);
                downloadFromGitHub(methodCodeURL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkGitHubURL(String url) {
        // 确认URL是否属于GitHub，且/blob/只出现一次
        if (!url.startsWith("https://github.com") && url.replace("/blob/", "/").contains("/blob/")) {
            System.out.println(url);
        }
    }

    private void checkMethodCodeHasPoint(String code) {
        String[] codeTokens = code.split("\\s+");
        for (String codeToken : codeTokens) {
            if (codeToken.contains(".") && !codeToken.matches("[a-z](.+)")) {
                String fileName = ROOT_PATH + "point.txt";
                appendCodeToFile(fileName, code);
                break;
            }
        }
    }

    /**
     * 在文件末追加代码
     * @param fileName 文件全名
     * @param code 代码段
     */
    private void appendCodeToFile(String fileName, String code) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true))) {
            bufferedWriter.write(code);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFromGitHub(String methodCodeURL) {
        // 获取从URL解析出的文件路径信息和下载链接
        JavaCodeStorage storageInfo = getFilePathFromURL(methodCodeURL);
        String classCodeURL = storageInfo.getClassCodeURL();
        String classFilePath = storageInfo.getClassFilePath();
        String classFileName = storageInfo.getClassFileName();
        // 下载文件
        downloadJavaFiles(classCodeURL, classFilePath, classFileName);
    }

    /**
     * 从URL中解析所需的文件路径信息和下载链接
     * @param methodCodeURL CodeSearchNet代码对应的URL
     * @return 封装好的文件路径信息和下载链接
     */
    private JavaCodeStorage getFilePathFromURL(String methodCodeURL) {
        String[] methodCodeURLInfo = methodCodeURL.split("#");
        String classCodeURL = methodCodeURLInfo[0];
        String[] methodLineRange = methodCodeURLInfo[1].split("-");
        // 代码行号
        int lineBegin = Integer.parseInt(methodLineRange[0].substring(1));
        int lineEnd = Integer.parseInt(methodLineRange[1].substring(1));
        // 删除前置https://github.com/
        String classCodePath = classCodeURL.replaceAll("https://github.com/", "");
        String[] classCodePathNodes = classCodePath.split("/");
        int classCodePathLength = classCodePathNodes.length;
        // 创建文件夹
        checkAndMkdirs(classCodePathNodes, classCodePathLength - 1);
        // 具体文件名
        String classFileName = classCodePathNodes[classCodePathLength - 1];
        // 获取文件待存储目录
        String classFilePath = ROOT_PATH + classCodePath.replaceAll("/" + classFileName, "");
        // 带上路径的完整文件名
        classFileName = classFilePath + "/" + classFileName;
        // 拼接可访问下载文件的URL
        classCodeURL = classCodeURL.replaceAll("https://github.com/", "https://raw.githubusercontent.com/");
        classCodeURL = classCodeURL.replaceAll("/blob/", "/");
        return new JavaCodeStorage(classCodeURL, classFilePath, classFileName);
    }

    private void checkAndMkdirs(String[] pathNodes, int length) {
        String path = ROOT_PATH;
        for (int i = 0; i < length; i++) {
            String node = pathNodes[i];
            path += node;
            path += "/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    private String getBaseURL(String url) {
        String result = "https://raw.githubusercontent.com/";
        url = url.replaceAll(result, "");
        String[] urlNodes = url.split("/");
        result = result + urlNodes[0] + urlNodes[1];
        return result;
    }

    private void downloadJavaFiles(String url, String filePath, String fileName) {
        String baseURL = getBaseURL(url);
        if (!urlNotFoundSet.contains(baseURL) && !new File(fileName).exists()) {
            // 创建不同的文件夹目录
            File file = new File(filePath);
            // 判断文件夹是否存在，多余的检查罢了
            if (!file.exists()) {
                // 如果文件夹不存在，则创建新的的文件夹
                file.mkdirs();
            }
            HttpURLConnection connection = null;
            BufferedInputStream bis = null;
            try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
                // 建立链接
                URL httpUrl = new URL(url);
                // 不加代理无法访问GitHub，报错：java.net.UnknownHostException: raw.githubusercontent.com
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_ADDRESS, PROXY_PORT));
                connection = (HttpURLConnection) httpUrl.openConnection(proxy);
                // 以POST方式提交表单，默认GET方式
                // conn.setRequestMethod(method);
                // 设置超时间为3秒
                connection.setConnectTimeout(3 * 1000);
                // 防止屏蔽程序抓取而返回403错误
                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                // POST方式不能使用缓存
                connection.setUseCaches(false);
                // 连接指定的资源
                connection.connect();
                // 获取网络输入流
                bis = new BufferedInputStream(connection.getInputStream());
                byte[] buffer = new byte[4096];
                int length = bis.read(buffer);
                // 保存文件
                while (length != -1) {
                    bos.write(buffer, 0, length);
                    length = bis.read(buffer);
                }
            } catch (FileNotFoundException e) {
                // 不可避免会出现一些404的情况，比如仓库被删除、用户改名称等，需要把URL存入Set，以防多次重复访问
                urlNotFoundSet.add(baseURL);
                e.printStackTrace();
            } catch (SSLHandshakeException e) {
                // javax.net.ssl.SSLHandshakeException: Remote host terminated the handshake
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            } catch (EOFException e) {
                // java.io.EOFException: SSL peer shut down incorrectly
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void renameAndMoveJavaFiles(File rootDictionary, File dictionary) {
        // 下载文件后出现了路径配置BUG，需要修复
        // 例如：.\src\main\resources\codes\1337joe\cubesensors-for-java\blob\f3ba432d36b744e68b916682d7ef24afaff447f8\src\main\java\com\w3asel\cubesensors\api\v1\jsonStateParser.java
        // 正解：.\src\main\resources\codes\1337joe\cubesensors-for-java\blob\f3ba432d36b744e68b916682d7ef24afaff447f8\src\main\java\com\w3asel\cubesensors\api\v1\json\StateParser.java
        assert rootDictionary != null;
        assert dictionary != null;
        String dictName = dictionary.getName();
        for (File file : Objects.requireNonNull(rootDictionary.listFiles())) {
            String fileName = file.toString();
            if (file.isFile() && fileName.endsWith(".java")) {
                String[] filePathNodes = fileName.split("\\\\");
                int filePathDepth = filePathNodes.length;
                String originFileName = filePathNodes[filePathDepth-1];
                if (originFileName.startsWith(dictName)) {
                    String newFileName = dictName + "\\\\" + originFileName.replaceAll(dictName, "");
                    fileName = fileName.replaceAll(originFileName, newFileName);
                    file.renameTo(new File(fileName));
                }
            }
        }
    }

    public void modifyMethodCodeImports() {
        File rootDictionary = new File(ROOT_PATH);
        // 扫描CodeSearchNet的.jsonl文件
        for (File file : Objects.requireNonNull(rootDictionary.listFiles())) {
            String fileName = file.getName();
            if (fileName.startsWith("java") && fileName.endsWith(".jsonl")) {
                String fileFullName = ROOT_PATH + file.getName();
                System.out.println("正在扫描" + fileFullName);
                loadAndModifyMethodCode(fileFullName);
            }
        }
    }

    private void loadAndModifyMethodCode(String fileName) {
        // File、Path的根目录为工程根目录
        try (Scanner scanner = new Scanner(Files.newInputStream(Paths.get(fileName)))) {
            while (scanner.hasNextLine()) {
                String json = scanner.nextLine();
                ObjectMapper mapper = new ObjectMapper();
                JavaMethodCode codeObject = mapper.readValue(json, JavaMethodCode.class);
                String methodCode = codeObject.getCode();
                String methodCodeURL = codeObject.getUrl();
                // 获取从URL解析出的文件路径全名信息
                JavaCodeStorage storageInfo = getFilePathFromURL(methodCodeURL);
                String classFileName = storageInfo.getClassFileName();
                // TODO import替换
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getJavaFileImports() {
        accessJavaFiles(new File(ROOT_PATH));
    }

    private void accessJavaFiles(File rootFile) {
        if (rootFile != null) {
            for (File file : Objects.requireNonNull(rootFile.listFiles())) {
                String fileName = file.toString();
                if (file.isDirectory()) {
                    accessJavaFiles(file);
                    // 修复下载文件后的文件路径BUG
                    // renameAndMoveJavaFiles(rootFile, file);
                } else if (file.isFile() && fileName.endsWith(".java")) {
                    System.out.println(fileName);
                    loadAndProcessJavaFile(fileName);
                }
            }
        }
    }

    private void loadAndProcessJavaFile(String fileName) {
        // File、Path的根目录为工程根目录
        try (Scanner scanner = new Scanner(Files.newInputStream(Paths.get(fileName)))) {
            while (scanner.hasNextLine()) {
                String lineCode = scanner.nextLine();
                if (lineCode.contains("class ")) {
                    break;
                } else if (lineCode.contains("import")) {
                    System.out.println(lineCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
