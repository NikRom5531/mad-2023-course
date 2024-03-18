package com.romanov.encyclopedia_anime.data.local;

import androidx.room.TypeConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romanov.encyclopedia_anime.model.Anime.*;

public class Converters {
    @TypeConverter
    public String fromList(List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : stringList) stringBuilder.append(value).append(",");
        return stringBuilder.toString();
    }

    @TypeConverter
    public List<String> toList(String data) {
        String[] values = data.split(",");
        return new ArrayList<>(Arrays.asList(values));
    }
    private static final Gson gson = new Gson();

    // Конвертирование объекта в JSON
    public static <T> String toJson(T object) {
        Type type = new TypeToken<T>() {}.getType();
        return gson.toJson(object, type);
    }

    // Конвертирование JSON в объект указанного типа
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    // Конвертирование списка объектов в JSON
    public static <T> String listToJson(List<T> list) {
        Type type = new TypeToken<List<T>>() {}.getType();
        return gson.toJson(list, type);
    }

    // Конвертирование JSON в список объектов указанного типа
    public static <T> List<T> jsonToList(String json, Class<T> classOfT) {
        Type type = new TypeToken<List<T>>() {}.getType();
        return gson.fromJson(json, type);
    }
    @TypeConverter
    public static List<Info> fromInfoList(String value) {
        Type listType = new TypeToken<List<Info>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String toInfoList(List<Info> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Episode> fromEpisodeList(String value) {
        Type listType = new TypeToken<List<Episode>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String toEpisodeList(List<Episode> list) {
        return gson.toJson(list);
    }
}

