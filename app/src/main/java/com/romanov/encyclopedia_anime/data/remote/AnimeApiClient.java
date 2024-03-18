package com.romanov.encyclopedia_anime.data.remote;

import com.romanov.encyclopedia_anime.model.AnimeDetail;
import com.romanov.encyclopedia_anime.model.AnimeReport;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class AnimeApiClient {
    private final AnimeApiService apiService;
    private static final String BASE_URL = "https://animenewsnetwork.com/";

    public AnimeApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        apiService = retrofit.create(AnimeApiService.class);
    }

    public Call<AnimeReport> getAnimeList(String search) {
        return apiService.getAnimeList(search);
    }

    public Call<AnimeDetail> getAnimeDetail(long animeID) {
        return apiService.getAnimeDetail(animeID);
    }
}

