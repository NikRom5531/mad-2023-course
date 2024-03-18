package com.romanov.encyclopedia_anime.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Root(name = "report", strict = false)
public class AnimeReport {
    @ElementList(inline = true, entry = "item", required = false)
    private List<AnimeItem> items;

    public List<AnimeItem> getItems() {
        return items != null ? items : new ArrayList<>();
    }

    @NonNull
    @Override
    public String toString() {
        return "AnimeReport{" +
                "items=" + items +
                '}';
    }

    @Root(name = "item", strict = false)
    public static class AnimeItem implements Serializable {
        @Element(required = false)
        private long id;

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

        private boolean watchedStatus = false;
        private boolean wishStatus = false;

        public long getId() {
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

        public void setId(long id) {
            this.id = id;
        }

        public void setGid(long gid) {
            this.gid = gid;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPrecision(String precision) {
            this.precision = precision;
        }

        public void setVintage(String vintage) {
            this.vintage = vintage;
        }

        public boolean isWatchedStatus() {
            return watchedStatus;
        }

        public void setWatchedStatus(boolean watchedStatus) {
            this.watchedStatus = watchedStatus;
        }

        public boolean isWishStatus() {
            return wishStatus;
        }

        public void setWishStatus(boolean wishStatus) {
            this.wishStatus = wishStatus;
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
                    ", watchedStatus='" + watchedStatus + '\'' +
                    ", wishStatus='" + wishStatus + '\'' +
                    '}' + "\n";
        }
    }
}
