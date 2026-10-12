package com.example.anime.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.anime.R;
import com.example.anime.bean.Anime;

import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {

    private List<Anime> animeList;
    private OnAnimeClickListener listener;

    public interface OnAnimeClickListener {
        void onAnimeClick(Anime anime);
    }

    public AnimeAdapter(List<Anime> animeList, OnAnimeClickListener listener) {
        this.animeList = animeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_anime, parent, false);
        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        Anime anime = animeList.get(position);
        holder.bind(anime);
    }

    @Override
    public int getItemCount() {
        return animeList == null ? 0 : animeList.size();
    }

    class AnimeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvRating;

        public AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvRating = itemView.findViewById(R.id.tv_rating);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAnimeClick(animeList.get(position));
                    }
                }
            });
        }

        public void bind(Anime anime) {
            tvTitle.setText(anime.getTitle());

            // 在线列表数据没有评分，隐藏评分标签
            if (anime.getRating() > 0) {
                tvRating.setVisibility(View.VISIBLE);
                tvRating.setText(String.valueOf(anime.getRating()));
            } else {
                tvRating.setVisibility(View.GONE);
            }

            Glide.with(itemView.getContext())
                    .load(anime.getCover())
                    .placeholder(R.drawable.ic_placeholder)
                    .transform(new RoundedCorners(16))
                    .into(ivCover);
        }
    }
}
