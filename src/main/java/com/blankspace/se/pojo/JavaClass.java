package com.blankspace.se.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JavaClass {

    int id;

    String className;

    String classURL;

    List<JavaMethod> methodList;

}
