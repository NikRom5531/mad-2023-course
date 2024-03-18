package com.romanov.encyclopedia_anime.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.romanov.encyclopedia_anime.R;
import com.romanov.encyclopedia_anime.model.AnimeReport.AnimeItem;

import java.util.List;

public class AnimeListAdapter extends RecyclerView.Adapter<AnimeListAdapter.AnimeViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

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

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) onItemClickListener.onItemClick(position);
        });

        holder.bind(anime);
    }
    @Override
    public int getItemCount() {
        return animeItems.size();
    }

    public static class AnimeViewHolder extends RecyclerView.ViewHolder {
        private final TextView textNameAnime;
        private final TextView textType;
        private final ImageView buttonWatchList;
        private final ImageView buttonWishList;

        public AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            textNameAnime = itemView.findViewById(R.id.textNameAnime);
            textType = itemView.findViewById(R.id.textType);
            buttonWatchList = itemView.findViewById(R.id.buttonWatchList);
            buttonWishList = itemView.findViewById(R.id.buttonWishList);
        }

        public void bind(AnimeItem anime) {
            textNameAnime.setText(anime.getName());
            textType.setText(anime.getType());
            buttonWatchList.setImageResource(checkWatched(anime.isWatchedStatus()));
            buttonWishList.setImageResource(checkWish(anime.isWishStatus()));
            // Добавьте здесь другие компоненты вашего элемента списка, если необходимо
        }

        private int checkWatched(boolean status){
//            if (status) buttonWatchList.setColorFilter(Color.argb(255, 66,170,255));
//            else buttonWatchList.clearColorFilter();
            if (status) return R.drawable.ic_view_on;
            else return R.drawable.ic_view;
        }

        private int checkWish(boolean status){
//            if (status) buttonWishList.setColorFilter(Color.RED);
//            else buttonWishList.clearColorFilter();
            if (status) return R.drawable.ic_wish_on;
            else return R.drawable.ic_wish;
        }
    }

}
