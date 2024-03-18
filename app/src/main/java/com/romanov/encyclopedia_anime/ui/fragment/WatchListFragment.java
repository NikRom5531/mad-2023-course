package com.romanov.encyclopedia_anime.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romanov.encyclopedia_anime.data.local.AppDatabase;
import com.romanov.encyclopedia_anime.databinding.FragmentWatchListBinding;
import com.romanov.encyclopedia_anime.model.Anime;
import com.romanov.encyclopedia_anime.ui.adapter.AnimeLowAdapter;
import com.romanov.encyclopedia_anime.ui.viewmodel.DataBaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class WatchListFragment extends Fragment {
    private FragmentWatchListBinding binding;
    private DataBaseViewModel dataBaseViewModel;
    private final AnimeLowAdapter adapter = new AnimeLowAdapter(new ArrayList<>());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWatchListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar(View.VISIBLE);
        dataBaseViewModel = new ViewModelProvider(this).get(DataBaseViewModel.class);
        dataBaseViewModel.setDatabase(Room.databaseBuilder(requireContext(), AppDatabase.class, "anime_database").fallbackToDestructiveMigration().build());
        dataBaseViewModel.getWatchedLiveData().observe(getViewLifecycleOwner(), this::updateAnimeList);
        dataBaseViewModel.setWatchedList();

        binding.recyclerViewWatchList.setAdapter(adapter);
        binding.recyclerViewWatchList.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateAnimeList(List<Anime> animeList) {
        if (animeList != null) {
            adapter.setAnimeList(animeList);
            adapter.notifyDataSetChanged();
        }
        progressBar(View.GONE);
    }

    private void progressBar(int view) {
        binding.progressBar.setVisibility(view);
        binding.progressBarText.setVisibility(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}