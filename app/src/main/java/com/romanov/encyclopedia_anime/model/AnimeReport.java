package com.romanov.encyclopedia_anime.model;

import androidx.annotation.NonNull;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "report", strict = false)
public class AnimeReport {
    @ElementList(inline = true, entry = "item")
    private List<AnimeItem> items;

    public List<AnimeItem> getItems() {
        return items;
    }

    @NonNull
    @Override
    public String toString() {
        return "AnimeReport{" +
                "items=" + items +
                '}';
    }
}
