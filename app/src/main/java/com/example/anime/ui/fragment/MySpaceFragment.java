package com.example.anime.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime.R;
import com.example.anime.adapter.AnimeAdapter;
import com.example.anime.bean.Anime;
import com.example.anime.constants.AppConstants;
import com.example.anime.ui.activity.AnimeDetailActivity;
import com.example.anime.ui.activity.ChatActivity;
import com.example.anime.ui.activity.CollectListActivity;
import com.example.anime.ui.activity.LoginActivity;
import com.example.anime.ui.activity.HistoryListActivity;
import com.example.anime.utils.FileUtils;
import com.example.anime.utils.SPUtils;

import java.util.List;

public class MySpaceFragment extends Fragment {

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    private ImageView ivAvatar;
    private LinearLayout llAvatar;
    private TextView tvNickname;
    private TextView tvUsername;
    private LinearLayout llCollect;
    private LinearLayout llHistory;
    private LinearLayout llChat;
    private LinearLayout llLogout;
    private RecyclerView rvCollect;

    private String userId;
    private String nickname;
    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_space, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserInfo();
        initViews(view);
        setupListeners();
        loadCollectData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
        loadCollectData();
    }

    private void loadUserInfo() {
        userId = SPUtils.getUserId(requireContext());
        nickname = SPUtils.getNickname(requireContext());
        username = SPUtils.getUsername(requireContext());
    }

    private void initViews(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        llAvatar = view.findViewById(R.id.ll_avatar);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvUsername = view.findViewById(R.id.tv_username);
        llCollect = view.findViewById(R.id.ll_collect);
        llHistory = view.findViewById(R.id.ll_history);
        llChat = view.findViewById(R.id.ll_chat);
        llLogout = view.findViewById(R.id.ll_logout);
        rvCollect = view.findViewById(R.id.rv_collect);

        tvNickname.setText(nickname);
        tvUsername.setText("@" + username);
        
        loadAvatar();
    }

    private void setupListeners() {
        llAvatar.setOnClickListener(v -> {
            pickImageFromGallery();
        });

        llCollect.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), CollectListActivity.class));
        });

        llHistory.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), HistoryListActivity.class));
        });

        llChat.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ChatActivity.class));
        });

        llLogout.setOnClickListener(v -> {
            SPUtils.clearUser(requireContext());
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == requireActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                saveAvatar(bitmap);
                ivAvatar.setImageBitmap(bitmap);
                Toast.makeText(requireContext(), "头像更换成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "头像更换失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadAvatar() {
        String avatarPath = FileUtils.getAvatarPath(requireContext(), userId);
        if (avatarPath != null && !avatarPath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
            if (bitmap != null) {
                ivAvatar.setImageBitmap(bitmap);
            }
        }
    }

    private void saveAvatar(Bitmap bitmap) {
        FileUtils.saveAvatar(requireContext(), userId, bitmap);
    }

    private void loadCollectData() {
        List<Anime> collectedAnimes = FileUtils.getCollectedAnimes(requireContext(), userId);
        if (collectedAnimes == null) {
            collectedAnimes = java.util.Collections.emptyList();
        }

        rvCollect.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        AnimeAdapter adapter = new AnimeAdapter(collectedAnimes, new AnimeAdapter.OnAnimeClickListener() {
            @Override
            public void onAnimeClick(Anime anime) {
                Intent intent = new Intent(requireContext(), AnimeDetailActivity.class);
                intent.putExtra(AppConstants.EXTRA_ANIME_ID, anime.getId());
                startActivity(intent);
            }
        });
        rvCollect.setAdapter(adapter);
    }
}
