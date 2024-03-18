package com.romanov.encyclopedia_anime.ui.viewmodel;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.romanov.encyclopedia_anime.data.local.AnimeDao;
import com.romanov.encyclopedia_anime.data.local.AppDatabase;
import com.romanov.encyclopedia_anime.data.remote.AnimeApiClient;
import com.romanov.encyclopedia_anime.model.Anime;
import com.romanov.encyclopedia_anime.model.AnimeReport.AnimeItem;
import com.romanov.encyclopedia_anime.model.AnimeReport;
import com.romanov.encyclopedia_anime.ui.fragment.AnimeListFragment;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeListViewModel extends ViewModel {
    public final String KEY_SAVED_STATE = "savedStateHandle";
    private final MutableLiveData<List<AnimeItem>> animeListLiveData = new MutableLiveData<>();
    public LiveData<List<AnimeItem>> getAnimeListLiveData() {
        return animeListLiveData;
    }
    private final MutableLiveData<SavedStateHandle> savedStateHandle = new MutableLiveData<>();
    public LiveData<SavedStateHandle> getSavedStateHandle() {
        return savedStateHandle;
    }

    public void performSearch(String query) {
        AnimeApiClient animeApiClient = new AnimeApiClient();
        animeApiClient.getAnimeList(query).enqueue(new Callback<AnimeReport>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<AnimeReport> call, @NonNull Response<AnimeReport> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AnimeReport animeReport = response.body();
                    //////////////////////////
                    List<AnimeItem> animeItemList = animeReport.getItems();
                    checkList(animeItemList);
                    //////////////////////////
                    animeListLiveData.postValue(animeItemList);
                    SavedStateHandle savedStateHandle1 = new SavedStateHandle();
                    savedStateHandle1.set(KEY_SAVED_STATE, animeItemList);
                    savedStateHandle.postValue(savedStateHandle1);
                } else {
                    // Обработка ошибок
                    Log.w("WARNING", "response: " + response.body());
                    Log.w("WARNING", "call: " + call);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnimeReport> call, @NonNull Throwable t) {
                SavedStateHandle savedStateHandleError = new SavedStateHandle();
                if (t instanceof SocketTimeoutException) {
                    Log.e("FAILURE", "Socket Timeout: " + t);
//                    Toast.makeText("Превышено время ожидания", Toast.LENGTH_SHORT).show();
                    savedStateHandleError.set("errorMessage", "Превышено время ожидания");
                } else if (t instanceof UnknownHostException){
                    Log.e("FAILURE", "Unknown Host: " + t);
                    savedStateHandleError.set("errorMessage", "Не удалось установить соединение");
                } else {
                    // Обработка других ошибок
                    Log.e("FAILURE", t.toString());
                    savedStateHandleError.set("errorMessage", t.toString());
                }
                savedStateHandle.postValue(savedStateHandleError);
            }
        });
    }

    ////////////////////////////////////
    private final Object databaseLock = new Object();

    private AnimeDao animeDao;

    public void setDatabase(AppDatabase database) {
        synchronized (databaseLock) {
            setAnimeDao(database.animeDao());
        }
    }

    private void setAnimeDao(AnimeDao animeDao) {
        synchronized (databaseLock) {
            this.animeDao = animeDao;
        }
    }

    private LiveData<List<Anime>> getAnimeListByWishStatus() {
        synchronized (databaseLock) {
            return animeDao.getAnimeListByWishStatus();
        }
    }

    private LiveData<List<Anime>> getAnimeListByWatchedStatus() {
        synchronized (databaseLock) {
            return animeDao.getAnimeListByWatchedStatus();
        }
    }

    public void checkList(List<AnimeItem> animeItemList) {
//        List<AnimeItem> newList = new ArrayList<>();
        synchronized (databaseLock) {
//            LiveData<List<Anime>> watchedList = getAnimeListByWatchedStatus();
//            LiveData<List<Anime>> wishList = getAnimeListByWishStatus();
            Map<Long, Anime> watchedMap = new HashMap<>();
            Map<Long, Anime> wishMap = new HashMap<>();

            final Observer<List<Anime>> watchedListObserver = watchedList -> {
                if (watchedList != null) for (Anime anime : watchedList) watchedMap.put(anime.getId(), anime);
            };

            final Observer<List<Anime>> wishListObserver = wishList -> {
                if (wishList != null) for (Anime anime : wishList) wishMap.put(anime.getId(), anime);
            };
//            watchedList.observe( ,(Observer<List<Anime>>) value -> {
//                if (watchedList.getValue() != null) for (Anime anime : watchedList.getValue()) watchedMap.put(anime.getId(), anime);
//            });
//            if (watchedList.getValue() != null) for (Anime anime : watchedList.getValue()) watchedMap.put(anime.getId(), anime);
//            if (wishList.getValue() != null) for (Anime anime : wishList.getValue()) wishMap.put(anime.getId(), anime);

            for (AnimeItem animeItem : animeItemList) {
                if (watchedMap.containsKey(animeItem.getId())) animeItem.setWatchedStatus(true);
                if (wishMap.containsKey(animeItem.getId())) animeItem.setWishStatus(true);
//                newList.add(animeItem);
            }
        }
//        animeListLiveData.postValue(newList);
//        SavedStateHandle savedStateHandle1 = new SavedStateHandle();
//        savedStateHandle1.set(KEY_SAVED_STATE, newList);
//        savedStateHandle.postValue(savedStateHandle1);
    }
    ////////////////////////////
}
