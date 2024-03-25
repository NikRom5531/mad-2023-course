package com.romanov.encyclopedia_anime.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@Root(name = "anime", strict = false)
@Entity(tableName = "anime_table")
public class Anime {
    @PrimaryKey
    @Attribute(name = "id", required = false)
    public long id;

    @ColumnInfo(name = "name")
    @Attribute(name = "name", required = false)
    private String name;

    @ColumnInfo(name = "type")
    @Attribute(name = "type", required = false)
    private String type;

    @Ignore
    @ElementList(inline = true, entry = "info", required = false)
    private List<Info> info;

    @Ignore
    @ElementList(entry = "episode", inline = true, required = false)
    private List<Episode> episode;

    @ColumnInfo(name = "watchedStatus")
    private boolean watchedStatus = false;

    @ColumnInfo(name = "wishStatus")
    private boolean wishStatus = false;

    public Anime() {
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

    public List<Episode> getEpisode() {
        return episode;
    }

    public void setEpisode(List<Episode> episode) {
        this.episode = episode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Info> getInfo() {
        return info;
    }

    public void setInfo(List<Info> info) {
        this.info = info;
    }

    @NonNull
    @Override
    public String toString() {
        return "Anime{" +
                "id=" + id +
                ", name='" + name + '\'' +
                (type != null ? ", type=" + type : "") +
                (info != null ? ", info=" + info : "") +
                (episode != null ? ", episode=" + episode : "") +
                '}';
    }
    @Root(name = "episode", strict = false)
    public static class Episode {
        @Attribute(name = "num", required = false)
        private double num;
        @ElementList(entry = "title", inline = true, required = false)
        private List<Title> title;

        public Episode() {
        }

        public double getNum() {
            return num;
        }

        public void setNum(double num) {
            this.num = num;
        }

        public List<Title> getTitle() {
            return title;
        }

        public void setTitle(List<Title> title) {
            this.title = title;
        }

        @NonNull
        @Override
        public String toString() {
            return "\nEpisode{" +
                    "num=" + num +
                    ", title=" + title +
                    '}';
        }
    }

    @Root(name = "info", strict = false)
    public static class Info {
        @Attribute(name = "gid", required = false)
        private long gid;
        @Attribute(name = "type", required = false)
        private String type;
        @Attribute(name = "src", required = false)
        private String src;
        @Attribute(name = "width", required = false)
        private int width;
        @Attribute(name = "height", required = false)
        private int height;
        @Attribute(name = "lang", required = false)
        private String lang;
        @Attribute(name = "href", required = false)
        private String href;
        @Text(required = false)
        private String text;

        public Info() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public long getGid() {
            return gid;
        }

        public void setGid(long gid) {
            this.gid = gid;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @NonNull
        @Override
        public String toString() {
            return "\nInfoAnime{" +
                    (gid > 0 ? "gid=" + gid : "") +
                    (type != null ? ", type='" + type + '\'' : "") +
                    (src != null ? ", src='" + src + '\'' : "") +
                    (width > 0 ? ", width=" + width : "") +
                    (height > 0 ? ", height=" + height : "") +
                    (lang != null ? ", lang='" + lang + '\'' : "") +
                    (href != null ? ", href='" + href + '\'' : "") +
                    ", value='" + getText() + '\'' +
                    '}';
        }
    }
    @Root(name = "info", strict = false)
    public static class Title {
        @Attribute(name = "part", required = false)
        private String part;
        @Attribute(name = "gid", required = false)
        private long gid;
        @Attribute(name = "lang", required = false)
        private String lang;
        @Text(required = false)
        private String text;

        public Title() {
        }

        public String getPart() {
            return part;
        }

        public void setPart(String part) {
            this.part = part;
        }

        public long getGid() {
            return gid;
        }

        public void setGid(long gid) {
            this.gid = gid;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @NonNull
        @Override
        public String toString() {
            return "\n* Title{" +
                    (part != null ? "part='" + part + '\'' : "") +
                    " gid=" + gid +
                    " lang='" + lang + '\'' +
                    " title='" + text + '\'' +
                    '}';
        }
    }
}

