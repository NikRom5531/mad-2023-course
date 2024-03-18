package com.romanov.encyclopedia_anime.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.romanov.encyclopedia_anime.R;
import com.romanov.encyclopedia_anime.model.Anime;

import java.util.List;

public class AnimeLowAdapter extends RecyclerView.Adapter<AnimeLowAdapter.AnimeViewHolder> {
    private List<Anime> animeItems;
    public AnimeLowAdapter(List<Anime> animeItems) {
        this.animeItems = animeItems;
    }

    public List<Anime> getAnimeList() {
        return animeItems;
    }

    public void setAnimeList(List<Anime> animeList) {
        this.animeItems = animeList;
    }
    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_db_anime, parent, false);

        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        Anime anime = animeItems.get(position);
        holder.bind(anime);
    }
    @Override
    public int getItemCount() {
        return animeItems.size();
    }

    public static class AnimeViewHolder extends RecyclerView.ViewHolder {
        private final TextView textNameAnime;
        private final TextView textType;

        public AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            textNameAnime = itemView.findViewById(R.id.textNameAnime);
            textType = itemView.findViewById(R.id.textType);
        }

        public void bind(Anime anime) {
            textNameAnime.setText(anime.getName());
            textType.setText(anime.getType());
        }

    }
}
