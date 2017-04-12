package com.dilapp.radar.ui.topic;

import com.dilapp.radar.textbuilder.BBSDescribeItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by husj1 on 2015/7/28.
 */
public class PresetPostModel implements Serializable {

    private String title;
    private List<BBSDescribeItem> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<BBSDescribeItem> getList() {
        return list;
    }

    public void setList(List<BBSDescribeItem> list) {
        this.list = list;
    }
}
