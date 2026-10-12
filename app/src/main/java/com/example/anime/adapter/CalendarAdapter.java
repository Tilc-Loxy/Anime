package com.example.anime.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime.R;
import com.example.anime.bean.BangumiSubject;
import com.example.anime.ui.activity.BangumiDetailActivity;
import com.example.anime.utils.BangumiApiService;

import java.util.List;

/**
 * 每日放送适配器 - 按天分组展示番剧
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {

    private List<BangumiApiService.DaySchedule> scheduleList;

    public CalendarAdapter(List<BangumiApiService.DaySchedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public void updateData(List<BangumiApiService.DaySchedule> newList) {
        this.scheduleList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        BangumiApiService.DaySchedule schedule = scheduleList.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return scheduleList == null ? 0 : scheduleList.size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWeekday;
        private RecyclerView rvDayAnime;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeekday = itemView.findViewById(R.id.tv_weekday);
            rvDayAnime = itemView.findViewById(R.id.rv_day_anime);
        }

        public void bind(BangumiApiService.DaySchedule schedule) {
            tvWeekday.setText(schedule.weekdayCn);

            // 横向滚动展示当天的番剧
            rvDayAnime.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext(),
                            LinearLayoutManager.HORIZONTAL, false));

            BangumiAdapter adapter = new BangumiAdapter(schedule.subjects, subject -> {
                Intent intent = new Intent(itemView.getContext(), BangumiDetailActivity.class);
                intent.putExtra("subject_id", subject.getId());
                itemView.getContext().startActivity(intent);
            });
            rvDayAnime.setAdapter(adapter);
        }
    }
}
