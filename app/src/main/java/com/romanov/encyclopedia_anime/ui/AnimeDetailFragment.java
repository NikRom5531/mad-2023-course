package com.romanov.encyclopedia_anime.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.romanov.encyclopedia_anime.R;

public class AnimeDetailFragment extends Fragment {

    private AnimeViewModel animeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anime_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animeViewModel = new ViewModelProvider(this).get(AnimeViewModel.class);

        // Получение аргументов, переданных из AnimeListFragment
        int animeId = getArguments().getInt("animeId", -1);

        // Загрузка детальной информации об аниме с использованием animeViewModel.getAnimeDetails(animeId)
        // Обновление UI с полученными данными
        // ...

        // Пример обработки нажатия на кнопку "Добавить в желаемое"
        view.findViewById(R.id.buttonAddToWishlist).setOnClickListener(v -> {
            animeViewModel.updateWishListStatus(animeId, true);
            Toast.makeText(requireContext(), "Added to Wish List", Toast.LENGTH_SHORT).show();
            // В AnimeListFragment, например, при нажатии на элемент списка
            Navigation.findNavController(view).navigate(R.id.action_animeListFragment_to_animeDetailFragment);
        });

    }
}
