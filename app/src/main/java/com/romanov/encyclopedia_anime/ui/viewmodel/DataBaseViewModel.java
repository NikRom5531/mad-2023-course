package com.romanov.encyclopedia_anime.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.romanov.encyclopedia_anime.data.local.AnimeDao;
import com.romanov.encyclopedia_anime.data.local.AppDatabase;
import com.romanov.encyclopedia_anime.model.Anime;
import com.romanov.encyclopedia_anime.model.AnimeReport.AnimeItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataBaseViewModel extends ViewModel {
    private Context contextFragment;
    public MediatorLiveData<List<AnimeItem>> allAnime = new MediatorLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MediatorLiveData<List<Anime>> wishListLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<Anime>> watchedListLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> watchedStatusLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> wishStatusLiveData = new MediatorLiveData<>();

    public LiveData<List<Anime>> getWishListLiveData() {
        return wishListLiveData;
    }
    public LiveData<List<Anime>> getWatchedLiveData() {
        return watchedListLiveData;
    }
    public LiveData<Boolean> getWatchedStatusLiveData() {
        return watchedStatusLiveData;
    }
    public LiveData<Boolean> getWishStatusLiveData() {
        return wishStatusLiveData;
    }

    public DataBaseViewModel() {
    }

    public void setContextFragment(Context context) {
        contextFragment = context;
    }

    private AppDatabase getDatabase() {
        return Room.databaseBuilder(contextFragment, AppDatabase.class, "anime_database").fallbackToDestructiveMigration().build();
    }

    // Вызов метода вставки из DAO
    private void insert(Anime anime) {
        executorService.execute(() -> {
            AppDatabase db = getDatabase();
            db.animeDao().insert(anime.getId(), anime.getName(), anime.getType(), anime.isWatchedStatus(), anime.isWishStatus());
            db.close();
        });
    }

    // Вызов метода обновления из DAO
    private void update(Anime anime) {
        executorService.execute(() -> {
            AppDatabase db = getDatabase();
            db.animeDao().update(anime.getId(), anime.getName(), anime.getType(), anime.isWatchedStatus(), anime.isWishStatus());
            db.close();
        });
    }

    // Вызов метода удаления из DAO
    private void delete(long animeId) {
        executorService.execute(() -> {
            AppDatabase db = getDatabase();
            db.animeDao().delete(animeId);
            db.close();
        });
    }

    public LiveData<List<AnimeItem>> getAllAnime() {
        return allAnime;
    }

    public void setAllAnime(List<AnimeItem> animeItemList) {
        allAnime.postValue(animeItemList);
    }

    private LiveData<Boolean> isAnimeWatched(long animeId) {
        AppDatabase db = getDatabase();
        LiveData<Boolean> booleanLiveData = db.animeDao().isAnimeWatched(animeId);
        db.close();
        return booleanLiveData;
    }

    private LiveData<Boolean> isAnimeWish(long animeId) {
        AppDatabase db = getDatabase();
        LiveData<Boolean> booleanLiveData = db.animeDao().isAnimeWish(animeId);
        db.close();
        return booleanLiveData;
    }

    private LiveData<Anime> getAnimeById(long animeId) {
        AppDatabase db = getDatabase();
        LiveData<Anime> anime = db.animeDao().getAnimeById(animeId);
        db.close();
        return anime;
    }

    private LiveData<List<Anime>> getAllListByStatus(LiveData<List<Anime>> sourceLiveData, MutableLiveData<List<Anime>> targetLiveData) {
        MediatorLiveData<List<Anime>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(sourceLiveData, animeList -> {
            if (animeList != null) {
                mediatorLiveData.setValue(animeList);
                targetLiveData.postValue(animeList);
            }
        });
        return mediatorLiveData;
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

    public void setWatchedList() {
        watchedListLiveData.addSource(getAllListByStatus(getAnimeListByWatchedStatus(), watchedListLiveData), watchedList -> {
            if (watchedList != null) watchedListLiveData.postValue(watchedList);
        });
    }

    public void setWishList() {
        wishListLiveData.addSource(getAllListByStatus(getAnimeListByWishStatus(), wishListLiveData), wishList -> {
            if (wishList != null) wishListLiveData.postValue(wishList);
        });
    }

    public void updateStatus(Anime anime, LiveData<Boolean> watchedLiveData, LiveData<Boolean> wishLiveData, MediatorLiveData<Boolean> statusLiveData, String statusType) {
        statusLiveData.addSource(watchedLiveData, isWatched -> {
            statusLiveData.removeSource(watchedLiveData);
            statusLiveData.setValue(isWatched);
            handleUpdateStatus(anime, watchedLiveData, wishLiveData, statusLiveData, statusType);
        });

        statusLiveData.addSource(wishLiveData, isWish -> {
            statusLiveData.removeSource(wishLiveData);
            statusLiveData.setValue(isWish);
            handleUpdateStatus(anime, watchedLiveData, wishLiveData, statusLiveData, statusType);
        });
    }

    private void handleUpdateStatus(Anime anime, LiveData<Boolean> watchedLiveData, LiveData<Boolean> wishLiveData, MutableLiveData<Boolean> statusLiveData, String statusType) {
        Boolean isWish = wishLiveData.getValue();
        Boolean isWatched = watchedLiveData.getValue();

        if (isWish != null && isWatched != null) {
            if ("wish".equals(statusType)) {
                anime.setWishStatus(!isWish);
                statusLiveData.postValue(!isWish);
            } else if ("watched".equals(statusType)) {
                anime.setWatchedStatus(!isWatched);
                statusLiveData.postValue(!isWatched);
            }
            update(anime);
            if (!anime.isWishStatus() && !anime.isWatchedStatus()) delete(anime.getId());
        }
    }

    public void updateWishStatus(Anime anime) {
        LiveData<Boolean> watchedLiveData = isAnimeWatched(anime.getId());
        LiveData<Boolean> wishLiveData = isAnimeWish(anime.getId());
        updateStatus(anime, watchedLiveData, wishLiveData, wishStatusLiveData, "wish");
    }

    public void updateWatchedStatus(Anime anime) {
        LiveData<Boolean> watchedLiveData = isAnimeWatched(anime.getId());
        LiveData<Boolean> wishLiveData = isAnimeWish(anime.getId());
        updateStatus(anime, watchedLiveData, wishLiveData, watchedStatusLiveData, "watched");
    }

    public <T> void checkAnime(T anime) {
        if (anime != null) {
            long id;

            if (anime instanceof Anime) id = ((Anime) anime).getId();
            else if (anime instanceof AnimeItem) id = ((AnimeItem) anime).getId();
            else id = 0;

            if (id > 0) {
                // Наблюдатель за изменениями статуса просмотра (watched)
                LiveData<Boolean> watchedLiveData = Transformations.switchMap(isAnimeWatched(id), result -> {
                    MutableLiveData<Boolean> liveData = new MutableLiveData<>();
                    liveData.setValue(result);
                    return liveData;
                });

                LiveData<Boolean> wishLiveData = Transformations.switchMap(isAnimeWish(id), result -> {
                    MutableLiveData<Boolean> liveData = new MutableLiveData<>();
                    liveData.setValue(result);
                    return liveData;
                });

                // Наблюдатель за изменениями статуса просмотра (watched)
                final Observer<Boolean> watchedObserver = new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isWatched) {
                        watchedStatusLiveData.removeObserver(this); // Удаление наблюдателя
                        watchedStatusLiveData.setValue(isWatched);
                        handleCheckAnime(anime);
                    }
                };

                // Наблюдатель за изменениями статуса желаемого (wish)
                final Observer<Boolean> wishObserver = new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isWish) {
                        wishStatusLiveData.removeObserver(this); // Удаление наблюдателя
                        wishStatusLiveData.setValue(isWish);
                        handleCheckAnime(anime);
                    }
                };
                watchedStatusLiveData.observeForever(watchedObserver);
                wishStatusLiveData.observeForever(wishObserver);
                watchedLiveData.observeForever(watchedObserver);
                wishLiveData.observeForever(wishObserver);
            }
        }
    }

    private <T> void handleCheckAnime(T anime) {
        boolean isWatched = watchedStatusLiveData.getValue() != null ? watchedStatusLiveData.getValue() : false;
        boolean isWish = wishStatusLiveData.getValue() != null ? wishStatusLiveData.getValue() : false;

        if (anime instanceof Anime) {
            ((Anime) anime).setWatchedStatus(isWatched);
            ((Anime) anime).setWishStatus(isWish);
        }
        if (anime instanceof AnimeItem) {
            ((AnimeItem) anime).setWatchedStatus(isWatched);
            ((AnimeItem) anime).setWishStatus(isWish);
        }
    }
}
