package com.blankspace.se.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JavaMethodCode {

    private String repo;

    private String path;

    private String func_name;

    private String original_string;

    private String language;

    private String code;

    private List<String> code_tokens;

    private String docstring;

    private List<String> docstring_tokens;

    private String sha;

    private String url;

    private String partition;

}
