package com.blankspace.se.service;

import java.io.File;
import java.io.IOException;

public interface ClassExtract {

    void addAllClassInfoIntoMongoDB() throws IOException;

    void accessAllFiles(File rootFile) throws IOException;

    void getAllMethods(String fileName, String url) throws IOException;

}
