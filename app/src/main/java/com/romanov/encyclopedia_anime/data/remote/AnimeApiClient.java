package com.romanov.encyclopedia_anime.data.remote;

import com.romanov.encyclopedia_anime.model.AnimeReport;
import com.romanov.encyclopedia_anime.model.Anime;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class AnimeApiClient {
    private AnimeApiService apiService;
    private static final String BASE_URL = "https://animenewsnetwork.com/";

    public AnimeApiClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor()) // Добавляем Interceptor
                .build();
        // инициализация Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        apiService = retrofit.create(AnimeApiService.class);
    }

    public Call<AnimeReport> getAnimeList(String search) {
        return apiService.getAnimeList(search);
    }

    public Call<Anime> getAnimeDetail(long animeID) {
        return apiService.getAnimeDetail(animeID);
    }
}

