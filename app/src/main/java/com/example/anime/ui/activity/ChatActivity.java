package com.example.anime.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageView ivSend;
    private LinearLayout llInput;

    private List<ChatMessage> messages;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        initMessages();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvChat = findViewById(R.id.rv_chat);
        etMessage = findViewById(R.id.et_message);
        ivSend = findViewById(R.id.iv_send);
        llInput = findViewById(R.id.ll_input);

        tvTitle.setText("动漫交流群");
    }

    private void initMessages() {
        messages = new ArrayList<>();
        messages.add(new ChatMessage("系统", "欢迎来到动漫交流群！", false, "10:00"));
        messages.add(new ChatMessage("小明", "大家好！最近有什么好看的动漫推荐吗？", false, "10:05"));
        messages.add(new ChatMessage("小红", "我推荐《咒术回战》，超级好看！", false, "10:06"));
        messages.add(new ChatMessage("你", "我觉得《鬼灭之刃》也很棒！", true, "10:10"));
        messages.add(new ChatMessage("小李", "同意！还有《进击的巨人》最终季也快出了", false, "10:12"));
        messages.add(new ChatMessage("管理员", "请大家遵守群规，文明交流哦~", false, "10:15"));
    }

    private void setupRecyclerView() {
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(messages);
        rvChat.setAdapter(adapter);
        rvChat.scrollToPosition(messages.size() - 1);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                messages.add(new ChatMessage("你", text, true, "刚刚"));
                adapter.notifyItemInserted(messages.size() - 1);
                rvChat.scrollToPosition(messages.size() - 1);
                etMessage.setText("");

                simulateReply();
            }
        });
    }

    private void simulateReply() {
        String[] replies = {
                "哈哈，我也这么觉得！",
                "是的是的，特别好看！",
                "同意你的观点~",
                "我也想看这个！",
                "期待更新！",
                "太棒了！"
        };
        
        new Thread(() -> {
            try {
                Thread.sleep(1000 + (long)(Math.random() * 2000));
                
                String[] names = {"小明", "小红", "小李", "管理员"};
                String name = names[(int)(Math.random() * names.length)];
                String reply = replies[(int)(Math.random() * replies.length)];
                
                runOnUiThread(() -> {
                    messages.add(new ChatMessage(name, reply, false, "刚刚"));
                    adapter.notifyItemInserted(messages.size() - 1);
                    rvChat.scrollToPosition(messages.size() - 1);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static class ChatMessage {
        String sender;
        String content;
        boolean isSelf;
        String time;

        ChatMessage(String sender, String content, boolean isSelf, String time) {
            this.sender = sender;
            this.content = content;
            this.isSelf = isSelf;
            this.time = time;
        }
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<ChatMessage> messages;

        ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1) {
                view = View.inflate(parent.getContext(), R.layout.item_chat_self, null);
            } else {
                view = View.inflate(parent.getContext(), R.layout.item_chat_other, null);
            }
            return new ChatViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).isSelf ? 1 : 0;
        }

        @Override
        public void onBindViewHolder(ChatViewHolder holder, int position) {
            ChatMessage msg = messages.get(position);
            holder.tvSender.setText(msg.sender);
            holder.tvContent.setText(msg.content);
            holder.tvTime.setText(msg.time);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView tvSender;
            TextView tvContent;
            TextView tvTime;

            ChatViewHolder(View itemView) {
                super(itemView);
                tvSender = itemView.findViewById(R.id.tv_sender);
                tvContent = itemView.findViewById(R.id.tv_content);
                tvTime = itemView.findViewById(R.id.tv_time);
            }
        }
    }
}