package com.example.anime.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime.R;

import java.util.List;

public class StaticBannerAdapter extends RecyclerView.Adapter<StaticBannerAdapter.BannerViewHolder> {

    private List<Integer> bannerResIds;

    public StaticBannerAdapter(List<Integer> bannerResIds) {
        this.bannerResIds = bannerResIds;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bind(bannerResIds.get(position));
    }

    @Override
    public int getItemCount() {
        return bannerResIds == null ? 0 : bannerResIds.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner);
        }

        public void bind(int resId) {
            ivBanner.setImageResource(resId);
        }
    }
}
