package com.romanov.encyclopedia_anime.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.romanov.encyclopedia_anime.R;
import com.romanov.encyclopedia_anime.data.local.AppDatabase;
import com.romanov.encyclopedia_anime.databinding.FragmentAnimeListBinding;
import com.romanov.encyclopedia_anime.model.AnimeReport.AnimeItem;
import com.romanov.encyclopedia_anime.ui.adapter.AnimeListAdapter;
import com.romanov.encyclopedia_anime.ui.viewmodel.AnimeListViewModel;
import com.romanov.encyclopedia_anime.ui.viewmodel.DataBaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class AnimeListFragment extends Fragment {
    private FragmentAnimeListBinding binding;
    private DataBaseViewModel dataBaseViewModel;
    private AnimeListAdapter adapter;
    private AnimeListViewModel animeListViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animeListViewModel = new ViewModelProvider(requireActivity()).get(AnimeListViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnimeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Подписываемся на изменения LiveData
        dataBaseViewModel = new ViewModelProvider(this).get(DataBaseViewModel.class);
        dataBaseViewModel.setDatabase(Room.databaseBuilder(requireContext(), AppDatabase.class, "anime_database").fallbackToDestructiveMigration().build());
        adapter = new AnimeListAdapter(new ArrayList<>());

        dataBaseViewModel.getAllAnime().observe(getViewLifecycleOwner(), this::updateAnimeList);
        animeListViewModel.getAnimeListLiveData().observe(getViewLifecycleOwner(), animeList -> dataBaseViewModel.checkList(animeList));
        adapter.setOnItemClickListener(position -> {
            Bundle bundle = new Bundle();
            Log.i("INFO", "anime_id: " + adapter.getAnimeList().get(position).getId());
            bundle.putLong("anime_id", adapter.getAnimeList().get(position).getId());

            NavController navController = Navigation.findNavController(AnimeListFragment.this.requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_animeListFragment_to_animeDetailFragment, bundle);
        });

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
                return false;
            }
        });

        // Восстановление данных
        animeListViewModel.getSavedStateHandle().observe(getViewLifecycleOwner(), savedStateHandle -> {
            if (savedStateHandle != null) {
                List<AnimeItem> animeList = savedStateHandle.get(animeListViewModel.KEY_SAVED_STATE);
                updateAnimeList(animeList);
            }
        });
    }

    // Метод для выполнения поиска
    private void performSearch(String query) {
        progressBar(View.VISIBLE);
        hideKeyboard();
        animeListViewModel.performSearch(query);

    }

    private void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    private void updateAnimeList(List<AnimeItem> animeList) {
        if (animeList != null) adapter.setAnimeList(animeList);
        progressBar(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Сохранение состояния в SavedStateHandle
        if (adapter != null) if (adapter.getAnimeList() != null)
            if (animeListViewModel.getSavedStateHandle().getValue() != null)
                animeListViewModel.getSavedStateHandle().getValue().set(animeListViewModel.KEY_SAVED_STATE, adapter.getAnimeList());
    }
}