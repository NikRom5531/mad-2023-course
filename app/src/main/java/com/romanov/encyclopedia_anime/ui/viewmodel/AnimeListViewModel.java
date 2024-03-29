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

import com.romanov.encyclopedia_anime.data.local.AppDatabase;
import com.romanov.encyclopedia_anime.data.remote.AnimeApiClient;
import com.romanov.encyclopedia_anime.model.Anime;
import com.romanov.encyclopedia_anime.model.AnimeReport;
import com.romanov.encyclopedia_anime.model.AnimeReport.AnimeItem;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeListViewModel extends ViewModel {
    private Context contextFragment;
    public final String KEY_SAVED_STATE = "savedStateHandle";
    public final String KEY_SEARCH_TEXT = "savedSearchText";
    public final String KEY_ERROR = "errorMessage";
    private String savedQuery = "";
    private final MutableLiveData<List<AnimeItem>> animeListLiveData = new MutableLiveData<>();

    public LiveData<List<AnimeItem>> getAnimeListLiveData() {
        return animeListLiveData;
    }

    private final MutableLiveData<SavedStateHandle> savedStateHandle = new MutableLiveData<>();

    public LiveData<SavedStateHandle> getSavedStateHandle() {
        return savedStateHandle;
    }

    private final MutableLiveData<List<AnimeItem>> responseLiveData = new MutableLiveData<>();
    public void setContextFragment(Context context) {
        contextFragment = context;
    }

    private AppDatabase getDatabase() {
        return Room.databaseBuilder(contextFragment, AppDatabase.class, "anime_database").fallbackToDestructiveMigration().build();
    }

    private LiveData<List<Anime>> getAnimeListByWishStatus() {
        AppDatabase database = getDatabase();
        LiveData<List<Anime>> listLiveData = database.animeDao().getAnimeListByWishStatus();
        database.close();
        return listLiveData;
    }

    private LiveData<List<Anime>> getAnimeListByWatchedStatus() {
        AppDatabase database = getDatabase();
        LiveData<List<Anime>> listLiveData = database.animeDao().getAnimeListByWatchedStatus();
        database.close();
        return listLiveData;
    }
    public void performSearch(String query) {
        savedQuery = query;
        AnimeApiClient animeApiClient = new AnimeApiClient();
        animeApiClient.getAnimeList(query).enqueue(new Callback<AnimeReport>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<AnimeReport> call, @NonNull Response<AnimeReport> response) {
                if (response.isSuccessful() && response.body() != null)
                    responseLiveData.postValue(response.body().getItems());
                else {
                    Log.w("WARNING", "response: " + response.body());
                    Log.w("WARNING", "call: " + call);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnimeReport> call, @NonNull Throwable t) {
                String errorMessage;
                if (t instanceof SocketTimeoutException) {
                    errorMessage = "Превышено время ожидания";
                    Log.e("FAILURE", "Socket Timeout: " + t);
                } else if (t instanceof UnknownHostException) {
                    errorMessage = "Не удалось установить соединение";
                    Log.e("FAILURE", "Unknown Host: " + t);
                } else {
                    errorMessage = t.toString();
                    Log.e("FAILURE", t.toString());
                }
                setSavedStateHandle(KEY_ERROR, errorMessage);
            }
        });
    }

    private final MediatorLiveData<List<AnimeItem>> mediatorLiveData = new MediatorLiveData<>();

    public void init() {
        LiveData<List<Anime>> watchedLiveData = getAnimeListByWatchedStatus();
        LiveData<List<Anime>> wishLiveData = getAnimeListByWishStatus();
        mediatorLiveData.removeSource(responseLiveData);
        mediatorLiveData.addSource(responseLiveData, animeItems -> initAnimeList(animeItems, watchedLiveData.getValue(), wishLiveData.getValue()));
        mediatorLiveData.addSource(watchedLiveData, watchedList -> initAnimeList(responseLiveData.getValue(), watchedList, wishLiveData.getValue()));
        mediatorLiveData.addSource(wishLiveData, wishList -> initAnimeList(responseLiveData.getValue(), watchedLiveData.getValue(), wishList));
        mediatorLiveData.observeForever(animeListLiveData::setValue);
    }

    private void initAnimeList(List<AnimeItem> animeItemList, List<Anime> watchedList, List<Anime> wishList){
        if (animeItemList != null && watchedList != null && wishList != null) {
            Map<Long, Anime> watchedMap = new HashMap<>();
            Map<Long, Anime> wishMap = new HashMap<>();
            for (Anime anime : watchedList) watchedMap.put(anime.getId(), anime);
            for (Anime anime : wishList) wishMap.put(anime.getId(), anime);
            for (AnimeItem animeItem : animeItemList) {
                animeItem.setWishStatus(wishMap.containsKey(animeItem.getId()));
                animeItem.setWatchedStatus(watchedMap.containsKey(animeItem.getId()));
            }
            mediatorLiveData.setValue(animeItemList);
            setSavedStateHandle(KEY_SAVED_STATE, animeItemList);
        }
    }

    private <T> void setSavedStateHandle(String key, T obj) {
        SavedStateHandle savedStateHandleTemp = new SavedStateHandle();
        savedStateHandleTemp.set(KEY_SEARCH_TEXT, savedQuery);
        savedStateHandleTemp.set(key, obj);
        savedStateHandle.postValue(savedStateHandleTemp);
    }
}
