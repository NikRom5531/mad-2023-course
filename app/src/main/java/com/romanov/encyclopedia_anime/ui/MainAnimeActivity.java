package com.romanov.encyclopedia_anime.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.romanov.encyclopedia_anime.R;
import com.romanov.encyclopedia_anime.databinding.ActivityAnimeListBinding;
import com.romanov.encyclopedia_anime.ui.viewmodel.AnimeListViewModel;

public class MainAnimeActivity extends AppCompatActivity {
    private NavController navController;
    private ActivityAnimeListBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private AnimeListViewModel animeListViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animeListViewModel = new ViewModelProvider(this).get(AnimeListViewModel.class);
        binding = ActivityAnimeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        try {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            navController = navHostFragment != null ? navHostFragment.getNavController() : null;
            appBarConfiguration = new AppBarConfiguration.Builder(navController != null ? navController.getGraph() : null).build();
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (navController != null) {
            if (id == R.id.action_home) {
                navController.navigate(R.id.action_global_animeListFragment);
                return true;
            } else if (id == R.id.action_watched_list) {
                navController.navigate(R.id.action_global_watchListFragment);
                return true;
            } else if (id == R.id.action_wishlist) {
                navController.navigate(R.id.action_global_wishListFragment);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
