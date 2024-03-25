package com.romanov.encyclopedia_anime.ui.viewmodel;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.romanov.encyclopedia_anime.data.remote.AnimeApiClient;
import com.romanov.encyclopedia_anime.model.Anime;
import com.romanov.encyclopedia_anime.model.AnimeDetail;

import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeDetailViewModel extends ViewModel {
    private Anime anime;
    public final String KEY_ANIME_ID = "anime_id";
    public final String KEY_ANIME_NAME = "anime_name";
    public final String KEY_ANIME_IMAGE = "anime_image";
    public final String KEY_ANIME_VINTAGE = "anime_vintage";
    public final String KEY_ANIME_GENRES = "anime_genres";
    public final String KEY_ANIME_SUMMARY = "anime_summary";
    public final String KEY_ANIME_EPISODES = "anime_episodes";
    private final MutableLiveData<Map<String, String>> animeDetailLiveData = new MutableLiveData<>();
    public LiveData<Map<String, String>> getAnimeLiveData() {
        return animeDetailLiveData;
    }

    public AnimeDetailViewModel(){
    }
    public Anime getAnime() {
        return anime;
    }

    public void getAnimeDetail(long animeId) {
        AnimeApiClient animeApiClient = new AnimeApiClient();
        animeApiClient.getAnimeDetail(animeId).enqueue(new Callback<AnimeDetail>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<AnimeDetail> call, @NonNull Response<AnimeDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    anime = response.body().getAnime();
                    animeDetailLiveData.postValue(mapList(response.body().getAnime()));
                } else {
                    Log.w("WARNING", "response: " + response.body());
                    Log.w("WARNING", "call: " + call);
                    animeDetailLiveData.postValue(mapList(null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnimeDetail> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException) Log.e("FAILURE", "Socket Timeout: " + t);
                else Log.e("FAILURE", t.toString());
                animeDetailLiveData.postValue(mapList(null));
            }
        });
    }

    private Map<String, String> mapList(Anime anime) {
        if (anime != null) {
            Map<String, String> mapList = new HashMap<>();
            mapList.put(KEY_ANIME_ID, String.valueOf(anime.getId()));
            mapList.put(KEY_ANIME_NAME, anime.getName());
            List<Anime.Info> infoList = anime.getInfo();
            List<Anime.Episode> episodeList = anime.getEpisode();
            StringBuilder imageSrc = new StringBuilder();
            StringBuilder genres = new StringBuilder();
            StringBuilder plotSummary = new StringBuilder();
            StringBuilder vintage = new StringBuilder();
            StringBuilder episodes = new StringBuilder();
            if (infoList != null) {
                boolean flagPicture = true;
                for (Anime.Info item : infoList) {
                    if (item.getType().equals("Picture") && flagPicture) {
                        imageSrc.append(item.getSrc());
                        flagPicture = false;
                    }
                    if (item.getType().equals("Genres")) {
                        if (genres.length() > 0) genres.append(", ");
                        else genres.append("Genres: ");
                        genres.append(item.getText());
                    }
                    if (item.getType().equals("Plot Summary"))
                        plotSummary.append("Summary:\n").append(item.getText());
                    if (item.getType().equals("Vintage") && vintage.length() == 0) {
                        DateTimeFormatter fullFormatter = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String s_date = "";
                        try {
                            LocalDate date;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (item.getText().matches("\\d{4}-\\d{2}-\\d{2}.*")) {
                                    date = LocalDate.parse(item.getText().split(" ")[0], fullFormatter);
                                    s_date = getDate(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth());
                                } else if (item.getText().matches("\\d{4}-\\d{2}.*")) {
                                    date = LocalDate.parse(item.getText().split(" ")[0] + "-01", fullFormatter);
                                    s_date = getDate(date.getYear(), date.getMonth().getValue(), 0);
                                } else if (item.getText().matches("\\d{4}.*")) {
                                    date = LocalDate.parse(item.getText().split(" ")[0] + "-01-01", fullFormatter);
                                    s_date = getDate(date.getYear(), 0, 0);
                                }
                            } else {
                                continue;
                            }
                        } catch (Exception e) {
                            Log.e("Failed", item.getText());
                        }
                        vintage = new StringBuilder("Vintage: " + s_date + " ");
                    }
                }
                mapList.put(KEY_ANIME_IMAGE, imageSrc.toString());
                mapList.put(KEY_ANIME_GENRES, genres.toString());
                mapList.put(KEY_ANIME_SUMMARY, plotSummary.toString());
                mapList.put(KEY_ANIME_VINTAGE, vintage.toString());
            }
            if (episodeList != null) {
                Map<String, Anime.Episode> uniqueEpisodesMap = new HashMap<>();
                for (Anime.Episode episode : episodeList) {
                    double episodeNumber = episode.getNum();
                    String key = episodeNumber + "-" + "EN";
                    if (episode.getTitle() != null) {
                        for (Anime.Title title : episode.getTitle()) {
                            if ("EN".equalsIgnoreCase(title.getLang())) {
                                key += "-" + title.getPart();
                                break;
                            }
                        }
                    }
                    if (uniqueEpisodesMap.containsKey(key)) {
                        Anime.Episode existingEpisode = uniqueEpisodesMap.get(key);
                        if (existingEpisode != null && existingEpisode.getTitle() != null)
                            for (Anime.Title existingTitle : existingEpisode.getTitle())
                                if ("EN".equalsIgnoreCase(existingTitle.getLang())) continue;
                        uniqueEpisodesMap.put(key, episode);
                    } else uniqueEpisodesMap.put(key, episode);
                }
                List<Anime.Episode> uniqueEpisodesList = new ArrayList<>(uniqueEpisodesMap.values());
                uniqueEpisodesList.sort(Comparator.comparingDouble(Anime.Episode::getNum));
                for (Anime.Episode item : uniqueEpisodesList) {
                    if (episodes.length() > 0) episodes.append("\n* ");
                    else episodes.append("Episodes:\n* ");
                    List<Anime.Title> titles = item.getTitle();
                    String string = titles.get(0).getText();
                    for (Anime.Title title : titles)
                        if (title.getPart() == null) string = title.getText();
                    episodes.append(string);
                }
                mapList.put(KEY_ANIME_EPISODES, episodes.toString());
            }
            return mapList;
        }
        return null;
    }

    private String getDate(int year, int month, int day) {
        String month_s = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && (0 < month && month < 13))
            month_s = String.valueOf(Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault())).toLowerCase();
        return (day != 0 ? day + " " : "") + (!month_s.isEmpty() ? month_s + " " : "") + (year != 0 ? year : "");
    }
}
