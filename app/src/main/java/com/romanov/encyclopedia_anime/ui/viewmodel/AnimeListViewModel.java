package com.romanov.encyclopedia_anime.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.romanov.encyclopedia_anime.data.local.AnimeDao;
import com.romanov.encyclopedia_anime.data.local.AppDatabase;
import com.romanov.encyclopedia_anime.data.remote.AnimeApiClient;
import com.romanov.encyclopedia_anime.model.Anime;
import com.romanov.encyclopedia_anime.model.AnimeReport.AnimeItem;
import com.romanov.encyclopedia_anime.model.AnimeReport;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeListViewModel extends ViewModel {
    private AnimeDao animeDao;
    public final String KEY_SAVED_STATE = "savedStateHandle";
    private final MutableLiveData<List<AnimeItem>> animeListLiveData = new MutableLiveData<>();

    public LiveData<List<AnimeItem>> getAnimeListLiveData() {
        return animeListLiveData;
    }

    private final MutableLiveData<SavedStateHandle> savedStateHandle = new MutableLiveData<>();

    public LiveData<SavedStateHandle> getSavedStateHandle() {
        return savedStateHandle;
    }

    private final MutableLiveData<List<AnimeItem>> responseLiveData = new MutableLiveData<>();

    public void setDatabase(Context context) {
        setAnimeDao(Room.databaseBuilder(context, AppDatabase.class, "anime_database").fallbackToDestructiveMigration().build().animeDao());
    }

    private void setAnimeDao(AnimeDao animeDao) {
        this.animeDao = animeDao;
    }

    private LiveData<List<Anime>> getAnimeListByWishStatus() {
        return animeDao.getAnimeListByWishStatus();
    }

    private LiveData<List<Anime>> getAnimeListByWatchedStatus() {
        return animeDao.getAnimeListByWatchedStatus();
    }

    public void performSearch(String query) {
        AnimeApiClient animeApiClient = new AnimeApiClient();
        animeApiClient.getAnimeList(query).enqueue(new Callback<AnimeReport>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<AnimeReport> call, @NonNull Response<AnimeReport> response) {
                if (response.isSuccessful() && response.body() != null) responseLiveData.postValue(response.body().getItems());
                else {
                    Log.w("WARNING", "response: " + response.body());
                    Log.w("WARNING", "call: " + call);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnimeReport> call, @NonNull Throwable t) {
                SavedStateHandle savedStateHandleError = new SavedStateHandle();
                if (t instanceof SocketTimeoutException) {
                    Log.e("FAILURE", "Socket Timeout: " + t);
                    savedStateHandleError.set("errorMessage", "Превышено время ожидания");
                } else if (t instanceof UnknownHostException) {
                    Log.e("FAILURE", "Unknown Host: " + t);
                    savedStateHandleError.set("errorMessage", "Не удалось установить соединение");
                } else {
                    Log.e("FAILURE", t.toString());
                    savedStateHandleError.set("errorMessage", t.toString());
                }
                savedStateHandle.postValue(savedStateHandleError);
            }
        });
    }

    public void init() {
        MediatorLiveData<List<AnimeItem>> mediatorLiveData = new MediatorLiveData<>();
        LiveData<List<Anime>> watchedLiveData = getAnimeListByWatchedStatus();
        LiveData<List<Anime>> wishLiveData = getAnimeListByWishStatus();
        mediatorLiveData.addSource(responseLiveData, animeItems -> {
            if (animeItems != null && watchedLiveData.getValue() != null && wishLiveData.getValue() != null) {
                List<Anime> watchedList = watchedLiveData.getValue();
                List<Anime> wishList = wishLiveData.getValue();
                Map<Long, Anime> watchedMap = new HashMap<>();
                Map<Long, Anime> wishMap = new HashMap<>();
                for (Anime anime : watchedList) watchedMap.put(anime.getId(), anime);
                for (Anime anime : wishList) wishMap.put(anime.getId(), anime);
                for (AnimeItem animeItem : animeItems) {
                    animeItem.setWishStatus(wishMap.containsKey(animeItem.getId()));
                    animeItem.setWatchedStatus(watchedMap.containsKey(animeItem.getId()));
                }
                mediatorLiveData.setValue(animeItems);
                SavedStateHandle savedStateHandle1 = new SavedStateHandle();
                savedStateHandle1.set(KEY_SAVED_STATE, animeItems);
                savedStateHandle.setValue(savedStateHandle1);
            }
        });
        mediatorLiveData.addSource(watchedLiveData, watchedList -> {
            if (responseLiveData.getValue() != null && watchedList != null && wishLiveData.getValue() != null) mediatorLiveData.setValue(responseLiveData.getValue());
        });
        mediatorLiveData.addSource(wishLiveData, wishList -> {
            if (responseLiveData.getValue() != null && watchedLiveData.getValue() != null && wishList != null) mediatorLiveData.setValue(responseLiveData.getValue());
        });
        mediatorLiveData.observeForever(animeListLiveData::setValue);
    }
}
