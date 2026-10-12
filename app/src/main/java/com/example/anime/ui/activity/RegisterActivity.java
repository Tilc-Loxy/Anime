package com.example.anime.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anime.R;
import com.example.anime.bean.User;
import com.example.anime.utils.FileUtils;
import com.example.anime.utils.SPUtils;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etNickname;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etNickname = findViewById(R.id.et_nickname);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
    }

    private void setListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String nickname = etNickname.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(nickname)) {
                    Toast.makeText(RegisterActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
                    return;
                }

                User existingUser = FileUtils.findUserByUsername(RegisterActivity.this, username);
                if (existingUser != null) {
                    Toast.makeText(RegisterActivity.this, "账号已存在", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User(UUID.randomUUID().toString(), username, password, nickname);
                FileUtils.addUser(RegisterActivity.this, newUser);

                SPUtils.setLogin(RegisterActivity.this, true);
                SPUtils.saveUserId(RegisterActivity.this, newUser.getId());
                SPUtils.saveUsername(RegisterActivity.this, newUser.getUsername());
                SPUtils.saveNickname(RegisterActivity.this, newUser.getNickname());

                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
