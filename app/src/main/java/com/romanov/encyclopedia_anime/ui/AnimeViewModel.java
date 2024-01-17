package com.romanov.encyclopedia_anime.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.romanov.encyclopedia_anime.data.AnimeRepository;
import com.romanov.encyclopedia_anime.model.AnimeItem;

import java.util.List;

public class AnimeViewModel extends AndroidViewModel {
    private AnimeRepository repository;
    private LiveData<List<AnimeItem>> animeList;

    public AnimeViewModel(Application application) {
        super(application);
        repository = new AnimeRepository(application);
        animeList = repository.getAllAnime();
    }

    public LiveData<List<AnimeItem>> getAnimeList() {
        return animeList;
    }

//    public LiveData<List<AnimeItem>> searchAnime(String title) {
//        return repository.searchAnime(title);
//    }

    public LiveData<AnimeItem> getAnimeDetails(int animeId) {
        return repository.getAnimeDetails(animeId);
    }

    public void updateWatchedStatus(int animeId, boolean isWatched) {
        repository.updateWatchedStatus(animeId, isWatched);
    }

    public void updateWishListStatus(int animeId, boolean isWishListed) {
        repository.updateWishListStatus(animeId, isWishListed);
    }

//    public void insertAnime(AnimeItem anime) {
//        repository.insertAnime(anime);
//    }
}
