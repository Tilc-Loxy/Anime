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
import com.example.anime.bean.BangumiSubject;

import java.util.List;

/**
 * Bangumi 番剧列表适配器
 * 复用 item_anime 布局展示 BangumiSubject 数据
 */
public class BangumiAdapter extends RecyclerView.Adapter<BangumiAdapter.BangumiViewHolder> {

    private List<BangumiSubject> subjectList;
    private OnBangumiClickListener listener;

    public interface OnBangumiClickListener {
        void onBangumiClick(BangumiSubject subject);
    }

    public BangumiAdapter(List<BangumiSubject> subjectList, OnBangumiClickListener listener) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    public void updateData(List<BangumiSubject> newList) {
        this.subjectList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BangumiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_anime, parent, false);
        return new BangumiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BangumiViewHolder holder, int position) {
        BangumiSubject subject = subjectList.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjectList == null ? 0 : subjectList.size();
    }

    class BangumiViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvRating;
        private TextView tvCountry;

        public BangumiViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvCountry = itemView.findViewById(R.id.tv_country);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onBangumiClick(subjectList.get(position));
                    }
                }
            });
        }

        public void bind(BangumiSubject subject) {
            tvTitle.setText(subject.getDisplayName());

            // 评分
            if (subject.getScore() > 0) {
                tvRating.setText(String.valueOf(subject.getScore()));
            } else {
                tvRating.setText("暂无");
            }

            // 日期
            if (subject.getDate() != null && !subject.getDate().isEmpty()) {
                tvCountry.setText(subject.getDate());
            } else {
                tvCountry.setText(subject.getTypeName());
            }

            // 封面
            Glide.with(itemView.getContext())
                    .load(subject.getCoverUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .transform(new RoundedCorners(16))
                    .into(ivCover);
        }
    }
}
