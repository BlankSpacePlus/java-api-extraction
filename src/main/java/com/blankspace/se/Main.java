package com.blankspace.se;

import java.io.File;
import java.io.IOException;

import com.blankspace.se.service.ClassExtract;
import com.blankspace.se.service.impl.ClassExtractImpl;

public class Main {

    public static void main(String[] args) throws IOException {
        ClassExtract extractor = new ClassExtractImpl();
        extractor.addAllClassInfoIntoMongoDB();
    }

}
