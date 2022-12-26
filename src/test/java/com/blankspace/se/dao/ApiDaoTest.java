package com.blankspace.se.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.blankspace.se.pojo.JavaClass;
import com.blankspace.se.pojo.JavaMethod;

public class ApiDaoTest {

    @Test
    public void insertOneJavaClass() {
        List<JavaMethod> javaMethodList = new ArrayList<>();
        javaMethodList.add(new JavaMethod(1, "public int read() throws IOException", "See the general contract of the read method of InputStream.", "Overrides: read in class FilterInputStream Returns: the next byte of data, or -1 if the end of the stream is reached. Throws: IOException - if this input stream has been closed by invoking its close() method, or an I/O error occurs. See Also: FilterInputStream.in "));
        javaMethodList.add(new JavaMethod(2, "public int read(byte[] b, int off, int len) throws IOException", "Reads bytes from this byte-input stream into the specified byte array, starting at the given offset. This method implements the general contract of the corresponding read method of the InputStream class. As an additional convenience, it attempts to read as many bytes as possible by repeatedly invoking the read method of the underlying stream. This iterated read continues until one of the following conditions becomes true: The specified number of bytes have been read, The read method of the underlying stream returns -1, indicating end-of-file, or The available method of the underlying stream returns zero, indicating that further input requests would block. If the first read on the underlying stream returns -1 to indicate end-of-file then this method returns -1. Otherwise this method returns the number of bytes actually read. Subclasses of this class are encouraged, but not required, to attempt to read as many bytes as possible in the same fashion.", "Overrides: read in class FilterInputStream Parameters: b - destination buffer. off - offset at which to start storing bytes. len - maximum number of bytes to read. Returns: the number of bytes read, or -1 if the end of the stream has been reached. Throws: IOException - if this input stream has been closed by invoking its close() method, or an I/O error occurs. See Also: FilterInputStream.in"));
        JavaClass javaClass = new JavaClass(1, "java.io.BufferedInputStream", "https://docs.oracle.com/en/java/javase/19//docs/api/java.base/java/io/BufferedInputStream.html", javaMethodList);
        List<JavaClass> javaClassList = new ArrayList<>();
        javaClassList.add(javaClass);
        ApiDao.getSingletonDao().insertAllJavaAPIs(javaClassList);
    }

    public static void main(String[] args) {
        List<JavaMethod> javaMethodList = new ArrayList<>();
        javaMethodList.add(new JavaMethod(1, "public int read() throws IOException", "See the general contract of the read method of InputStream.", "Overrides: read in class FilterInputStream Returns: the next byte of data, or -1 if the end of the stream is reached. Throws: IOException - if this input stream has been closed by invoking its close() method, or an I/O error occurs. See Also: FilterInputStream.in "));
        javaMethodList.add(new JavaMethod(2, "public int read(byte[] b, int off, int len) throws IOException", "Reads bytes from this byte-input stream into the specified byte array, starting at the given offset. This method implements the general contract of the corresponding read method of the InputStream class. As an additional convenience, it attempts to read as many bytes as possible by repeatedly invoking the read method of the underlying stream. This iterated read continues until one of the following conditions becomes true: The specified number of bytes have been read, The read method of the underlying stream returns -1, indicating end-of-file, or The available method of the underlying stream returns zero, indicating that further input requests would block. If the first read on the underlying stream returns -1 to indicate end-of-file then this method returns -1. Otherwise this method returns the number of bytes actually read. Subclasses of this class are encouraged, but not required, to attempt to read as many bytes as possible in the same fashion.", "Overrides: read in class FilterInputStream Parameters: b - destination buffer. off - offset at which to start storing bytes. len - maximum number of bytes to read. Returns: the number of bytes read, or -1 if the end of the stream has been reached. Throws: IOException - if this input stream has been closed by invoking its close() method, or an I/O error occurs. See Also: FilterInputStream.in"));
        JavaClass javaClass = new JavaClass(1, "java.io.BufferedInputStream", "https://docs.oracle.com/en/java/javase/19//docs/api/java.base/java/io/BufferedInputStream.html", javaMethodList);
        List<JavaClass> javaClassList = new ArrayList<>();
        javaClassList.add(javaClass);
        ApiDao.getSingletonDao().insertAllJavaAPIs(javaClassList);
    }

}
