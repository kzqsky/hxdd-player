package com.edu.hxdd_player.bean.parameters;

import com.google.gson.GsonBuilder;

import java.io.Serializable;

public class BaseParameters implements Serializable {
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
