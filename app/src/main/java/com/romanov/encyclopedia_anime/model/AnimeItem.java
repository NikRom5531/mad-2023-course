package com.romanov.encyclopedia_anime.model;

import androidx.annotation.NonNull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class AnimeItem {
    @Element(required = false)
    private int id;

    @Element(required = false)
    private long gid;

    @Element(required = false)
    private String type;

    @Element(required = false)
    private String name;

    @Element(required = false)
    private String precision;

    @Element(required = false)
    private String vintage;

    public int getId() {
        return id;
    }

    public long getGid() {
        return gid;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPrecision() {
        return precision;
    }

    public String getVintage() {
        return vintage;
    }

    @NonNull
    @Override
    public String toString() {
        return "AnimeItem{" +
                "id=" + id +
                ", gid=" + gid +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", precision='" + precision + '\'' +
                ", vintage='" + vintage + '\'' +
                '}';
    }
}