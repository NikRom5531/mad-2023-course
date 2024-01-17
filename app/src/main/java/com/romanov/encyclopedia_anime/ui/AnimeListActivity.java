package com.romanov.encyclopedia_anime.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.romanov.encyclopedia_anime.R;

public class AnimeListActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_list);

        try {
            // Находите NavController и настраивайте его
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);

            // Установите NavController для ActionBar
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            NavigationUI.setupActionBarWithNavController(this, navController);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для загрузки AnimeListFragment
    private void loadAnimeListFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new AnimeListFragment())
                .commit();
    }

    // Метод для загрузки AnimeDetailFragment
    public void loadAnimeDetailFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new AnimeDetailFragment())
                .addToBackStack(null) // Добавление транзакции в стек возврата
                .commit();
    }
    @Override
    public boolean  onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (AppBarConfiguration) null) || super.onSupportNavigateUp();
    }
}
