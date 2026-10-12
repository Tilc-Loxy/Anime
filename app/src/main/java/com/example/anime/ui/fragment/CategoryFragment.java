package com.example.anime.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.anime.R;
import com.example.anime.constants.AppConstants;
import com.example.anime.ui.activity.AnimeListActivity;

public class CategoryFragment extends Fragment {

    private LinearLayout llTypeHot;
    private LinearLayout llTypeLove;
    private LinearLayout llTypeComedy;
    private LinearLayout llTypeSciFi;
    private LinearLayout llTypeMystery;
    private LinearLayout llTypeCampus;
    private LinearLayout llTypeAdventure;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();
    }

    private void initViews(View view) {
        llTypeHot = view.findViewById(R.id.ll_type_hot);
        llTypeLove = view.findViewById(R.id.ll_type_love);
        llTypeComedy = view.findViewById(R.id.ll_type_comedy);
        llTypeSciFi = view.findViewById(R.id.ll_type_sci_fi);
        llTypeMystery = view.findViewById(R.id.ll_type_mystery);
        llTypeCampus = view.findViewById(R.id.ll_type_campus);
        llTypeAdventure = view.findViewById(R.id.ll_type_adventure);
    }

    private void setupListeners() {
        llTypeHot.setOnClickListener(v -> navigateToList(AppConstants.TYPE_HOT));
        llTypeLove.setOnClickListener(v -> navigateToList(AppConstants.TYPE_LOVE));
        llTypeComedy.setOnClickListener(v -> navigateToList(AppConstants.TYPE_COMEDY));
        llTypeSciFi.setOnClickListener(v -> navigateToList(AppConstants.TYPE_SCI_FI));
        llTypeMystery.setOnClickListener(v -> navigateToList(AppConstants.TYPE_MYSTERY));
        llTypeCampus.setOnClickListener(v -> navigateToList(AppConstants.TYPE_CAMPUS));
        llTypeAdventure.setOnClickListener(v -> navigateToList(AppConstants.TYPE_ADVENTURE));
    }

    private void navigateToList(String type) {
        Intent intent = new Intent(requireContext(), AnimeListActivity.class);
        intent.putExtra(AppConstants.EXTRA_TYPE, type);
        startActivity(intent);
    }
}
