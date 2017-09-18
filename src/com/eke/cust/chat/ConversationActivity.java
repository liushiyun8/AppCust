package com.eke.cust.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.chat.ui.EaseConversationListFragment;
import com.eke.cust.notification.NotificationKey;
import com.hyphenate.chat.EMConversation;

import foundation.notification.NotificationCenter;
import foundation.notification.NotificationListener;


/**
 * 联系人列表
 */
public class ConversationActivity extends BaseActivity implements NotificationListener {
    private EaseConversationListFragment conversationListFragment;

    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_conversation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("消息");
        registerLeftImageView(R.drawable.arrow_back);

        NotificationCenter.defaultCenter.addListener(NotificationKey.service_kf, this);
        conversationListFragment = new EaseConversationListFragment();
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(ConversationActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment).commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
