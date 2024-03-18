package com.romanov.encyclopedia_anime.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.romanov.encyclopedia_anime.data.local.AnimeDao;
import com.romanov.encyclopedia_anime.data.local.AppDatabase;
import com.romanov.encyclopedia_anime.model.Anime;
import com.romanov.encyclopedia_anime.model.AnimeReport.AnimeItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataBaseViewModel extends ViewModel {
    private AnimeDao animeDao;
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

    public void setDatabase(AppDatabase database) {
        setAnimeDao(database.animeDao());
    }

    private void setAnimeDao(AnimeDao animeDao) {
        this.animeDao = animeDao;
    }

    // Вызов метода вставки из DAO
    private void insert(Anime anime) {
        executorService.execute(() -> animeDao.insert(anime.getId(), anime.getName(), anime.getType(), anime.isWatchedStatus(), anime.isWishStatus()));
    }

    // Вызов метода обновления из DAO
    private void update(Anime anime) {
        executorService.execute(() -> animeDao.update(anime.getId(), anime.getName(), anime.getType(), anime.isWatchedStatus(), anime.isWishStatus()));
    }

    // Вызов метода удаления из DAO
    private void delete(long animeId) {
        executorService.execute(() -> animeDao.delete(animeId));
    }

    public LiveData<List<AnimeItem>> getAllAnime() {
        return allAnime;
    }

    public void setAllAnime(List<AnimeItem> animeItemList) {
        allAnime.postValue(animeItemList);
    }

    private LiveData<Boolean> isAnimeWatched(long animeId) {
        return animeDao.isAnimeWatched(animeId);
    }

    private LiveData<Boolean> isAnimeWish(long animeId) {
        return animeDao.isAnimeWish(animeId);
    }

    private LiveData<Anime> getAnimeById(long animeId) {
        return animeDao.getAnimeById(animeId);
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

    public void setWatchedList() {
        watchedListLiveData.addSource(getAllListByStatus(animeDao.getAnimeListByWatchedStatus(), watchedListLiveData), watchedList -> {
            if (watchedList != null) watchedListLiveData.postValue(watchedList);
        });
    }

    public void setWishList() {
        wishListLiveData.addSource(getAllListByStatus(animeDao.getAnimeListByWishStatus(), wishListLiveData), wishList -> {
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
        LiveData<Boolean> watchedLiveData = animeDao.isAnimeWatched(anime.getId());
        LiveData<Boolean> wishLiveData = animeDao.isAnimeWish(anime.getId());
        updateStatus(anime, watchedLiveData, wishLiveData, wishStatusLiveData, "wish");
    }

    public void updateWatchedStatus(Anime anime) {
        LiveData<Boolean> watchedLiveData = animeDao.isAnimeWatched(anime.getId());
        LiveData<Boolean> wishLiveData = animeDao.isAnimeWish(anime.getId());
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

    public void checkList(List<AnimeItem> animeItemList) {
        for (AnimeItem animeItem : animeItemList) checkAnime(animeItem);
        setAllAnime(animeItemList);
    }
}
