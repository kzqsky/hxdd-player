package com.edu.hxdd_player.bean;

import java.io.Serializable;

public class BaseBean<T> implements Serializable {
    public int code;
    public boolean success;
    public String message;
    public T data;
}
