package com.romanov.encyclopedia_anime.model;

import androidx.annotation.NonNull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name = "ann", strict = false)
public class AnimeDetail {
    @Element(name = "anime", required = false)
    private Anime anime;

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }

    @NonNull
    @Override
    public String toString() {
        return "AnimeDetail{" +
                "anime=" + anime +
                '}';
    }
}
