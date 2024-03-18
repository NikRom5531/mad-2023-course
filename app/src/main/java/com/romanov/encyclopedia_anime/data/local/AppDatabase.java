package com.romanov.encyclopedia_anime.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.romanov.encyclopedia_anime.model.Anime;

@Database(entities = {Anime.class}, version = 1)
//@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AnimeDao animeDao();
}