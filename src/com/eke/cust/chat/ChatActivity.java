package com.eke.cust.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.chat.controller.EaseUI;
import com.eke.cust.chat.ui.EaseChatFragment;

/**
 * 发送消息
 */
public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    private  String toChatUserId;
    private  String toChatUserName;

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_chat);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        activityInstance = this;
        //聊天人或群id
        toChatUserId = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
        toChatUserName = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_NAME);
        chatFragment = new EaseChatFragment();
        //传入参数ents(getIntent().getExtras());
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

    }

    protected void onResume() {
        super.onResume();
        // cancel the notification
        EaseUI.getInstance().getNotifier().reset();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra(EaseConstant.EXTRA_USER_ID);
        if (toChatUserId.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }

    public String getToChatUserId() {
        return toChatUserId;
    }
}
