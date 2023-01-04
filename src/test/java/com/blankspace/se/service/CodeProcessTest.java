package com.blankspace.se.service;

import java.io.File;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.blankspace.se.service.impl.CodeProcessImpl;

public class CodeProcessTest {

    @Test
    public void renameAndMoveJavaFilesTest() {
        CodeProcessImpl processor = CodeProcessImpl.getSingletonService();
        File rootDictionary = new File("./src/main/resources/codes/ReactiveX/RxJava/blob/ac84182aa2bd866b53e01c8e3fe99683b882c60e/src/main/java/io/reactivex/internal");
        File dictionary     = new File("./src/main/resources/codes/ReactiveX/RxJava/blob/ac84182aa2bd866b53e01c8e3fe99683b882c60e/src/main/java/io/reactivex/internal/util");
        // processor.renameAndMoveJavaFiles(rootDictionary, dictionary);
    }

    @Test
    public void fileMoveTest() {
        File rootDictionary = new File("./src/test/resources");
        File dictionary = new File("./src/test/resources/move_target");
        for (File file : Objects.requireNonNull(rootDictionary.listFiles())) {
            if (file.isFile()) {
                System.out.println(file.getName());
                String newFileName = dictionary + "/" + file.getName();
                file.renameTo(new File(newFileName));
            }
        }
    }

}
