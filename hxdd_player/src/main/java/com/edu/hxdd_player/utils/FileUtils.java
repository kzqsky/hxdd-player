package com.edu.hxdd_player.utils;

import android.os.Environment;

import java.io.File;

public class FileUtils {
    public static final  String DIRECTORY_DOWNLOADS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    /**
     * 通过url获取本地文件路径
     * DIRECTORY_DOWNLOADS 目录下
     * @param url
     * @return
     */
    public static String urlToFilePath(String url) {
        return DIRECTORY_DOWNLOADS + "/" + getUrlFileName(url);
    }

    /**
     * url转为本地路径 并判断是否存在
     * @param url
     * @return
     */
    public static boolean urlFileExist(String url) {
        String filename = urlToFilePath(url);
        return new File( filename).exists();
    }

    /**
     * 通过url获取文件名
     * @param url
     * @return
     */
    public static String getUrlFileName(String url) {
        String filename = null;
        String[] strings = url.split("/");
        for (String string : strings) {
            if (string.contains("?")) {
                int endIndex = string.indexOf("?");
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex);
                    return filename;
                }
            }
        }
        if (strings.length > 0) {
            filename = strings[strings.length - 1];
        }
        return filename;
    }
}
