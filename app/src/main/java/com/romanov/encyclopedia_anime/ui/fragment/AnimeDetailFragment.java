package com.romanov.encyclopedia_anime.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.romanov.encyclopedia_anime.R;
import com.romanov.encyclopedia_anime.databinding.FragmentAnimeDetailBinding;

import com.romanov.encyclopedia_anime.ui.viewmodel.AnimeDetailViewModel;
import com.romanov.encyclopedia_anime.ui.viewmodel.DataBaseViewModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AnimeDetailFragment extends Fragment {
    private final String KEY_MAP_SAVE = "mapListBundle";
    private FragmentAnimeDetailBinding binding;
    private Map<String, String> saveMapList = null;
    private long animeId;
    private DataBaseViewModel dataBaseViewModel;
    private AnimeDetailViewModel animeDetailViewModel;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState); // Сохранение данных
        if (saveMapList != null) {
            Bundle mapBundle = new Bundle();
            for (Map.Entry<String, String> entry : saveMapList.entrySet())
                mapBundle.putString(entry.getKey(), entry.getValue());
            outState.putBundle(KEY_MAP_SAVE, mapBundle);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnimeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Получение аргументов, переданных из AnimeListFragment
        animeDetailViewModel = new ViewModelProvider(requireActivity()).get(AnimeDetailViewModel.class);
        animeDetailViewModel.getAnimeLiveData().observe(getViewLifecycleOwner(), this::setData);

        Bundle args = getArguments();
        animeId = args != null ? args.getLong(animeDetailViewModel.KEY_ANIME_ID) : 0;

        dataBaseViewModel = new ViewModelProvider(this).get(DataBaseViewModel.class);
        dataBaseViewModel.setContextFragment(requireContext());
        dataBaseViewModel.getWatchedStatusLiveData().observe(getViewLifecycleOwner(), this::checkWatched);
        dataBaseViewModel.getWishStatusLiveData().observe(getViewLifecycleOwner(), this::checkWish);

        // Восстановление данных
        if (savedInstanceState != null) {
            Bundle mapBundle = savedInstanceState.getBundle(KEY_MAP_SAVE);
            if (mapBundle != null) {
                saveMapList = new HashMap<>();
                for (String key : mapBundle.keySet()) {
                    String value = mapBundle.getString(key);
                    saveMapList.put(key, value);
                }
                setData(saveMapList);
            }
        } else animeDetailViewModel.getAnimeDetail(animeId);

        // Обработка нажатия на кнопку "Добавить в желаемое" и "Добавить в просмотренное"
        binding.buttonWishList.setOnClickListener(v -> dataBaseViewModel.updateWishStatus(animeDetailViewModel.getAnime()));
        binding.buttonWatchList.setOnClickListener(v -> dataBaseViewModel.updateWatchedStatus(animeDetailViewModel.getAnime()));
    }

    private void setText(TextView textView, String text) {
        if (!text.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            if (text.equals("null")) text = getString(R.string.textUnknown);
            textView.setText(text);
        } else textView.setVisibility(View.GONE);
    }

    private void setData(Map<String, String> mapList) {
        if (binding != null && animeId > 0 && mapList != null) {
            if (String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_ID)).equals(String.valueOf(animeId))) {
                binding.imageViewAnime.setImageResource(R.drawable.ic_anime);
                if (mapList.get(animeDetailViewModel.KEY_ANIME_IMAGE) != null && !String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_IMAGE)).trim().isEmpty())
                    Picasso.get().load(mapList.get(animeDetailViewModel.KEY_ANIME_IMAGE)).into(binding.imageViewAnime);
                if (mapList.get(animeDetailViewModel.KEY_ANIME_NAME) != null)
                    setText(binding.textViewTitle, String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_NAME)));
                if (mapList.get(animeDetailViewModel.KEY_ANIME_GENRES) != null && !String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_GENRES)).trim().isEmpty())
                    setText(binding.textViewGenres, String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_GENRES)));
                else setText(binding.textViewGenres, "");
                if (mapList.get(animeDetailViewModel.KEY_ANIME_SUMMARY) != null && !String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_SUMMARY)).trim().isEmpty())
                    setText(binding.textPlotSummary, String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_SUMMARY)));
                else setText(binding.textPlotSummary, "");
                if (mapList.get(animeDetailViewModel.KEY_ANIME_VINTAGE) != null && !String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_VINTAGE)).trim().isEmpty())
                    setText(binding.textViewYear, String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_VINTAGE)));
                else setText(binding.textViewYear, "");
                if (mapList.get(animeDetailViewModel.KEY_ANIME_EPISODES) != null && !String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_EPISODES)).trim().isEmpty())
                    setText(binding.textViewEpisodes, String.valueOf(mapList.get(animeDetailViewModel.KEY_ANIME_EPISODES)));
                else setText(binding.textViewEpisodes, "");

                saveMapList = mapList;

                dataBaseViewModel.checkAnime(animeDetailViewModel.getAnime());

                binding.scrollView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void checkWatched(boolean status) {
        if (status) binding.buttonWatchList.setImageResource(R.drawable.ic_view_on);
        else binding.buttonWatchList.setImageResource(R.drawable.ic_view);
    }

    private void checkWish(boolean status) {
        if (status) binding.buttonWishList.setImageResource(R.drawable.ic_wish_on);
        else binding.buttonWishList.setImageResource(R.drawable.ic_wish);
    }
}
