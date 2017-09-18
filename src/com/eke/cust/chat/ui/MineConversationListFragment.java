package com.eke.cust.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.chat.ChatActivity;
import com.eke.cust.chat.ChatHelper;
import com.eke.cust.chat.EaseConstant;
import com.eke.cust.chat.domain.EaseUser;
import com.eke.cust.chat.widget.MineConversationList;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.StringCheckHelper;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import foundation.notification.NotificationCenter;

/**
 * conversation list fragment
 */
public class MineConversationListFragment extends EaseBaseFragment implements EMMessageListener {
    private final static int MSG_REFRESH = 2;
    protected boolean hidden;
    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
    protected MineConversationList conversationListView;
    protected boolean isConflict;

    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {
                    case Constants.NO_NETWORK:
                        break;

                    case Constants.TAG_SUCCESS:
                        Bundle bundle = msg.getData();
                        String request_url = bundle.getString("request_url");
                        String resp = bundle.getString("resp");
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            String result = jsonObject.optString("result", "");
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url
                                        .equals(ServerUrl.METHOD_findEmployeeByIds)) {
                                    JSONArray array_data = jsonObject
                                            .optJSONArray("data");
                                    if (array_data != null) {
                                        for (int i = 0; i < array_data.length(); i++) {

                                            JSONObject object = array_data
                                                    .getJSONObject(i);
                                            if (object != null) {
                                                String empid = JSONUtils.getString(object, "empid");
                                                String ekenickname = JSONUtils.getString(object, "ekenickname", "");
                                                String ekeicon = JSONUtils.getString(object, "ekeicon", "");
                                                EaseUser easeUser = new EaseUser(empid);
                                                if (StringCheckHelper.isEmpty(ekeicon)) {
                                                    ekeicon = BitmapUtils.bitmapToBase64(BitmapUtils.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.default_avatar)));
                                                }
                                                if (StringCheckHelper.isEmpty(ekenickname)) {
                                                    ekenickname = "暂无";
                                                }
                                                easeUser.setAvatar(ekeicon);
                                                easeUser.setNikeName(ekenickname);
                                                ChatHelper.getInstance().saveContact(easeUser);
                                            }
                                        }

                                        conversationListView.refresh();

                                    }
                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg",
                                        "请求!");
                                Toast.makeText(mContext.getApplicationContext(),
                                        errorMsg, Toast.LENGTH_SHORT).show();
                                MyLog.d(TAG, "会话信息请求出错");
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(mContext.getApplicationContext(), "出错!",
                                    Toast.LENGTH_SHORT).show();
                            MyLog.d(TAG, "会话信息请求出错" + e.getMessage());

                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(mContext.getApplicationContext(), "请求出错!",
                                Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };

    protected EMConversationListener convListener = new EMConversationListener() {

        @Override
        public void onCoversationUpdate() {
            refresh();
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mine_concersation_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
        NotificationCenter.defaultCenter.addListener(NotificationKey.update_conversation,this);
    }

    @Override
    protected void initView() {
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        conversationListView = (MineConversationList) getView().findViewById(R.id.list_mymsg);
    }

    @Override
    protected void setUpView() {

        conversationList.addAll(loadConversationList());
        conversationListView.init(conversationList);

        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                if (conversation != null) {
                    String username = conversation.getUserName();
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ChatActivity.class);
                    if (username.equals(EaseConstant.EXTRA_KEFU)) {
                        intent.putExtra(EaseConstant.EXTRA_USER_ID, EaseConstant.EXTRA_KEFU);
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SERVICE);
                    } else {
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                        intent.putExtra(EaseConstant.EXTRA_USER_ID, username);
                    }
                    startActivity(intent);

                }
            }
        });


        EMClient.getInstance().addConnectionListener(connectionListener);

        EMClient.getInstance().chatManager().addMessageListener(this);

        conversationListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
    }

    private void getNickNameByIds(String ids) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("empid", ids);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_findEmployeeByIds, obj.toString(), mHandler);
            clientHelper.isShowProgress(false);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                isConflict = true;
            } else {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };
    private MineConversationListItemClickListener listItemClickListener;

    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case MSG_REFRESH: {
                    conversationList.clear();
                    conversationList.addAll(loadConversationList());
                    conversationListView.refresh();
                    break;
                }
                default:
                    break;
            }
        }
    };


    /**
     * refresh ui
     */
    public void refresh() {
        if (!handler.hasMessages(MSG_REFRESH)) {
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    /**
     * load conversation list
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        Iterator<String> iter = conversations.keySet().iterator();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
            if (list.size() == 5) {
                break;
            }
        }

        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  EMClient.getInstance().removeConnectionListener(connectionListener);
        NotificationCenter.defaultCenter.removeListener(NotificationKey.update_conversation,this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isConflict) {
            outState.putBoolean("isConflict", true);
        }
    }

    public interface MineConversationListItemClickListener {
        /**
         * click event for conversation list
         *
         * @param conversation -- clicked item
         */
        void onListItemClicked(EMConversation conversation);
    }

    /**
     * set conversation list item click listener
     *
     * @param listItemClickListener
     */
    public void setConversationListItemClickListener(MineConversationListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        Log.d("onMessage", "onMessageReceived");
        refresh();

    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        Log.d("onMessage", "onCmdMessageReceived");

    }

    @Override
    public void onMessageReadAckReceived(List<EMMessage> list) {
        Log.d("onMessage", "onMessageReadAckReceived");
    }

    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> list) {
        Log.d("onMessage", "onMessageDeliveryAckReceived");

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {
        Log.d("onMessage", "onMessageChanged");

    }

    @Override
    public boolean onNotification(Notification notification) {
        if (notification.key.equals(NotificationKey.update_conversation)) {
            refresh();
            return true;
        }
        return super.onNotification(notification);
    }
}
