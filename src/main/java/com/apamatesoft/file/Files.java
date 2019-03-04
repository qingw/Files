package com.apamatesoft.file;

import java.io.File;
import java.util.regex.Pattern;

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

    public static String getName(String file) {
        String[] s = file.split(Pattern.quote("."));
        if (s.length<=2) return s[0];
        String o = "";
        for (int i=0; i<s.length-1; i++) o += s+".";
        return o.substring(0, o.length()-1);
    }

    public static String getName(File file) {
        return getName(file.getName());
    }

    public static String getExtension(String file) {
        String[] t = file.split(Pattern.quote("."));
        if (t.length<=1) return "";
        return t[t.length-1];
    }

    public static String getExtension(File file) {
        if (file.isDirectory()) return "";
        return getExtension(file.getName());
    }



}