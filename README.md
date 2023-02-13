# Java抽取Java-API文档数据

## 开发工具和数据源

- MongoDB
    - [MongoDB](https://www.mongodb.com)
    - [Install MongoDB Community Edition on Windows](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-windows)
    - [MongoDB安装教程](https://www.cnblogs.com/TM0831/p/10606624.html)
    - [BSON](https://www.mongodb.com/docs/manual/reference/bson-types)
    - 踩坑：MongoDB的Key一定不要有`.`和`$`
- Maven
    - [Maven Central Repository](https://mvnrepository.com)
    - lombok
        - [lombok](https://projectlombok.org)
        - [GitHub](https://github.com/projectlombok/lombok)
    - jsoup
        - [jsoup](https://jsoup.org)
        - [GitHub](https://github.com/jhy/jsoup)
    - tree-sitter
        - [tree-sitter](https://tree-sitter.github.io)
        - [GitHub](https://github.com/tree-sitter/tree-sitter)
        - [Documentation](https://tree-sitter.github.io/tree-sitter)
- Data Source
    - Java API
        - [JDK 19.0.1 API Doc](https://docs.oracle.com/en/java/javase/19/docs/api/index.html)
        - [JDK 19.0.1 API Doc Download](https://www.oracle.com/java/technologies/javase-jdk19-doc-downloads.html)
    - CodeSearchNet
        - [GitHub](https://github.com/github/CodeSearchNet)
        - [Paper](https://arxiv.org/abs/1909.09436)
        - [Java代码数据下载](https://s3.amazonaws.com/code-search-net/CodeSearchNet/v2/java.zip)

## 开发记录

- 关于绝对路径和相对路径：原先一直喜欢用相对路径，但后来由于文件量太大导致编译拷贝过慢，所以暂时改成绝对路径。
