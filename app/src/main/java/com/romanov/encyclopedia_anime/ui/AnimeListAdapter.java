package com.romanov.encyclopedia_anime.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.romanov.encyclopedia_anime.R; // Замените на ваш путь
import com.romanov.encyclopedia_anime.model.AnimeItem;

import java.util.List;

public class AnimeListAdapter extends RecyclerView.Adapter<AnimeListAdapter.AnimeViewHolder> {
    private List<AnimeItem> animeItems;
    public AnimeListAdapter(List<AnimeItem> animeItems) {
        this.animeItems = animeItems;
    }
    public List<AnimeItem> getAnimeList() {
        return animeItems;
    }
    public void setAnimeList(List<AnimeItem> animeReport) {
        this.animeItems = animeReport;
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_anime, parent, false);

        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        AnimeItem anime = animeItems.get(position);

        ImageView buttonWatchList = holder.itemView.findViewById(R.id.buttonWatchList);
        ImageView buttonWishList = holder.itemView.findViewById(R.id.buttonWishList);
        buttonWatchList.setOnClickListener(v -> {
            // Действия при нажатии на кнопку Watch List

            Log.i("WatchList", "anime ID: " + anime.getId());
        });

        buttonWishList.setOnClickListener(v -> {
            // Действия при нажатии на кнопку Wish List

            Log.i("WishList", "anime ID: " + anime.getId());

        });

        holder.bind(anime);
    }

    private void isWatched(int position){
        int animeID = animeItems.get(position).getId();

    }

    private void isWished(int position){
        int animeID = animeItems.get(position).getId();

    }

    @Override
    public int getItemCount() {
        return animeItems.size();
    }

    public class AnimeViewHolder extends RecyclerView.ViewHolder {
        private TextView textNameAnime;
        private TextView textVintage;

        public AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            textNameAnime = itemView.findViewById(R.id.textNameAnime);
            textVintage = itemView.findViewById(R.id.textType);
        }

        public void bind(AnimeItem anime) {
            textNameAnime.setText(anime.getName());
            textVintage.setText(anime.getType());
            // Добавьте здесь другие компоненты вашего элемента списка, если необходимо
        }

    }
}
