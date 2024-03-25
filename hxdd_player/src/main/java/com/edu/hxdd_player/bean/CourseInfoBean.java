package com.edu.hxdd_player.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class CourseInfoBean implements Serializable {

    public ArrayList<UploadedFile> uploadedFiles;
    public ArrayList<Teacher> teacherList;
    public ArrayList<Book> textbookList;

    public class UploadedFile implements Serializable {
        public String filename;
        public String url;
        /**
         * -1 代表已下载
         * 0-99  下载中
         * 100 下载完成
         */
        public int progress;

    }

    public class Teacher implements Serializable {
        public String name;
        public String photo;
        public String description;
        public String title;
    }

    public class Book implements Serializable {
        public String author;
        public String textbookName;
        /**
         * 出版社
         */
        public String press;
        /**
         * 版本
         */
        public String version;
    }
}
