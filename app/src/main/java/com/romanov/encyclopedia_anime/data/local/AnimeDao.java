package com.romanov.encyclopedia_anime.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.romanov.encyclopedia_anime.model.Anime;

import java.util.List;

@Dao
public interface AnimeDao {
    @Query("INSERT INTO anime_table (id, name, type, watchedStatus, wishStatus) VALUES (:animeId, :name, :type, :watchedStatus, :wishStatus)")
    void insert(long animeId, String name, String type, boolean watchedStatus, boolean wishStatus);

    @Query("INSERT OR REPLACE INTO anime_table (id, name, type, watchedStatus, wishStatus) VALUES (:animeId, :name, :type, :watchedStatus, :wishStatus);")
    void update(long animeId, String name, String type, boolean watchedStatus, boolean wishStatus);

    @Query("DELETE FROM anime_table WHERE id = :animeId")
    void delete(long animeId);

    @Query("SELECT * FROM anime_table WHERE watchedStatus = 1")
    LiveData<List<Anime>> getAnimeListByWatchedStatus();

    @Query("SELECT * FROM anime_table WHERE wishStatus = 1")
    LiveData<List<Anime>> getAnimeListByWishStatus();

    @Query("SELECT * FROM anime_table WHERE id = :animeId")
    LiveData<Anime> getAnimeById(long animeId);

    @Query("SELECT EXISTS (SELECT 1 FROM anime_table WHERE id = :animeId AND watchedStatus = 1)")
    LiveData<Boolean> isAnimeWatched(long animeId);

    @Query("SELECT EXISTS (SELECT 1 FROM anime_table WHERE id = :animeId AND wishStatus = 1)")
    LiveData<Boolean> isAnimeWish(long animeId);

    @Query("SELECT EXISTS (SELECT 1 FROM anime_table WHERE id = :animeId AND wishStatus = 0 AND watchedStatus = 0)")
    LiveData<Boolean> isAnimeEmpty(long animeId);

}
