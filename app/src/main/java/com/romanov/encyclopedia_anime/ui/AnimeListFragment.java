package com.romanov.encyclopedia_anime.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.romanov.encyclopedia_anime.data.remote.AnimeApiClient;
import com.romanov.encyclopedia_anime.databinding.FragmentAnimeListBinding;
import com.romanov.encyclopedia_anime.model.AnimeReport;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeListFragment extends Fragment {
    private FragmentAnimeListBinding binding;
//    private AnimeViewModel animeViewModel;
    private AnimeListAdapter adapter = new AnimeListAdapter(new ArrayList<>());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnimeListBinding.inflate(inflater, container, false);
//        return inflater.inflate(R.layout.fragment_anime_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        animeViewModel = new ViewModelProvider(this).get(AnimeViewModel.class);
        binding.recyclerViewAnimeList.setAdapter(adapter);
        binding.recyclerViewAnimeList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Можете обработать изменения текста поиска при вводе, если необходимо
                return false;
            }
        });
    }

    private void navigateToAnimeDetailFragment() {
        // Вызывайте метод загрузки AnimeDetailFragment из вашей активности
        if (getActivity() instanceof AnimeListActivity) {
            ((AnimeListActivity) getActivity()).loadAnimeDetailFragment();
        }
    }

    // Метод для выполнения поиска
    private void performSearch(String query) {
        Log.i("INFO", query);
        if (!query.isEmpty()) {
            AnimeApiClient animeApiClient = new AnimeApiClient();
            animeApiClient.getAnimeList(query).enqueue(new Callback<AnimeReport>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<AnimeReport> call, @NonNull Response<AnimeReport> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.i("INFO", response.body().toString());
                        AnimeReport animeReport = response.body();
                        adapter.setAnimeList(animeReport.getItems());
                        adapter.notifyDataSetChanged();
                        for (int i = 0; i < animeReport.getItems().size(); i++) {
                            Log.i("ANIME", animeReport.getItems().get(i).toString());
                        }
                    } else {
                        // Обработка ошибок
                        Log.w("WARNING", "response: " + response.body());
                        Log.w("WARNING", "call: " + call);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<AnimeReport> call, @NonNull Throwable t) {
                    // Обработка ошибок
                    Log.e("FAILURE", t.toString());
                }
            });
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}