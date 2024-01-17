package com.romanov.encyclopedia_anime.data.remote;

import com.romanov.encyclopedia_anime.model.AnimeReport;
import com.romanov.encyclopedia_anime.model.Anime;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AnimeApiService {
    @GET("encyclopedia/reports.xml?id=155&type=anime") // Метод для получения списка Аниме
    Call<AnimeReport> getAnimeList(@Query("search") String search);

    @GET("encyclopedia/api.xml?") // Метод для получения детальной информации об Аниме
    Call<Anime> getAnimeDetail(@Query("anime") long anime);
}
