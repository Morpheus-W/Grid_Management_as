package com.cn7782.management.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cn7782.management.R;
import com.cn7782.management.fragment.ChatFragment;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.widget.EaseTitleBar;

public class ChatActivity extends FragmentActivity {

    private EaseTitleBar titleBar;
    private String toChatUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //聊天人或群id
        toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
        titleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(toChatUsername);
        titleBar.setRightImageResource(R.drawable.ease_mm_title_remove);

        //new出EaseChatFragment或其子类的实例
        ChatFragment chatFragment = new ChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }
}
