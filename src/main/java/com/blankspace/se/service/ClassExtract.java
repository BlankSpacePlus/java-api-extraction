package com.blankspace.se.service;

import java.io.File;
import java.io.IOException;

public interface ClassExtract {

    public void addAllClassInfoIntoMongoDB() throws IOException;

    public void accessAllFiles(File rootFile) throws IOException;

    public void getAllMethods(String fileName, String url) throws IOException;

}
