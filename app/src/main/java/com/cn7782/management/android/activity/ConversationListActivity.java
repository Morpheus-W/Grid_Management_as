package com.cn7782.management.android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cn7782.management.R;
import com.cn7782.management.fragment.ConversationListFragment;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

public class ConversationListActivity extends FragmentActivity implements EMEventListener {

    private ConversationListFragment conversationListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        conversationListFragment = new ConversationListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment).commit();
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) event.getData();

                refreshUIWithMessage();
                break;
            }

            case EventOfflineMessage: {
                break;
            }

            case EventConversationListChanged: {
                break;
            }

            default:
                break;
        }
    }

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                conversationListFragment.refresh();
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMChatManager.getInstance().unregisterEventListener(this);
    }
}
