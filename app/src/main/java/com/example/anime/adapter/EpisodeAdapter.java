package com.example.anime.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime.R;
import com.example.anime.bean.Episode;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private List<Episode> episodes;
    private OnEpisodeClickListener listener;

    public interface OnEpisodeClickListener {
        void onEpisodeClick(Episode episode);
    }

    public EpisodeAdapter(List<Episode> episodes, OnEpisodeClickListener listener) {
        this.episodes = episodes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.bind(episode);
    }

    @Override
    public int getItemCount() {
        return episodes == null ? 0 : episodes.size();
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEpisodeNumber;

        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEpisodeNumber = itemView.findViewById(R.id.tv_episode_number);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEpisodeClick(episodes.get(position));
                    }
                }
            });
        }

        public void bind(Episode episode) {
            tvEpisodeNumber.setText(String.valueOf(episode.getEpisodeNumber()));
        }
    }
}
