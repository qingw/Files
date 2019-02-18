package com.apamatesoft.file;

import java.io.File;

public class Files {

    public static byte DOES_NOT_EXIST = 1;
    public static byte NOT_WRITING = 2;
    public static byte NOT_READING = 3;
    public static byte NOT_VISIBLE = 4;
    public static byte NOT_EXECUTABLE = 5;
    public static byte OTHER = 0;

    public static byte getError(File f) {
        if (!f.exists()) return DOES_NOT_EXIST;
        if (!f.canWrite()) return NOT_WRITING;
        if (!f.canRead()) return NOT_READING;
        if (!f.isHidden()) return  NOT_VISIBLE;
        if (!f.canExecute()) return NOT_EXECUTABLE;
        return OTHER;
    }

}