package com.eke.cust.chat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.chat.ChatHelper;
import com.eke.cust.chat.EaseConstant;
import com.eke.cust.chat.controller.EaseUI;
import com.eke.cust.chat.domain.EaseEmojicon;
import com.eke.cust.chat.domain.EaseUser;
import com.eke.cust.chat.model.EaseAtMessageHelper;
import com.eke.cust.chat.utils.EaseCommonUtils;
import com.eke.cust.chat.utils.EaseUserUtils;
import com.eke.cust.chat.widget.EaseAlertDialog;
import com.eke.cust.chat.widget.EaseChatExtendMenu;
import com.eke.cust.chat.widget.EaseChatInputMenu;
import com.eke.cust.chat.widget.EaseChatMessageList;
import com.eke.cust.chat.widget.EaseVoiceRecorderView;
import com.eke.cust.chat.widget.chatrow.EaseCustomChatRowProvider;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.model.EmpCard;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import foundation.notification.NotificationCenter;
import foundation.notification.NotificationListener;
import foundation.toast.ToastUtil;


/**
 * you can new an EaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
 */
public class EaseChatFragment extends EaseBaseFragment implements EMMessageListener {
    protected static final String TAG = "EaseChatFragment";
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;

    /**
     * params to fragment
     */
    protected Bundle fragmentArgs;
    protected int chatType;
    protected String toChatId = "";
    protected String userName = "";
    protected EaseChatMessageList messageList;
    protected EaseChatInputMenu inputMenu;

    protected EMConversation conversation;

    protected InputMethodManager inputManager;
    protected ClipboardManager clipboard;

    protected Handler handler = new Handler();
    protected File cameraFile;
    protected EaseVoiceRecorderView voiceRecorderView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    protected EMMessage contextMenuMessage;

    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;

    protected int[] itemStrings = {R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location};
    protected int[] itemdrawables = {R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_image_selector,
            R.drawable.ease_chat_location_selector};
    protected int[] itemIds = {ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION};
    private EMChatRoomChangeListener chatRoomChangeListener;
    private boolean isMessageListInited;
    protected MyItemClickListener extendMenuItemClickListener;
    protected EaseUser easeUser;

    private EmpCard empCard;
    private String data_emp = "";
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

                            if (!TextUtils.isEmpty( result ))
                                if (result.equals(Constants.RESULT_SUCCESS)) {

                                    switch (request_url){
                                        case ServerUrl.METHOD_getNewsById:
                                            JSONArray array_data = jsonObject.optJSONArray("data");
                                            if (array_data != null) {
                                                for (int i = 0; i < array_data.length(); i++) {
                                                    JSONObject object = array_data.getJSONObject(i);
                                                    String empid = JSONUtils.getString(object, "empid");
                                                    String ekenickname = JSONUtils.getString(object, "ekenickname", "");
                                                    String ekeicon = JSONUtils.getString(object, "ekeicon", "");
                                                    easeUser = new EaseUser(empid);
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
                                            break;
                                        case ServerUrl.METHOD_getEmpCard://查看助理名片
                                            JSONObject data = JSONUtils.getJSONObject(jsonObject, "data", null);
                                            if (data != null) {
                                                String empid = JSONUtils.getString(data, "empid");
                                                String empname = JSONUtils.getString(data, "empname", "");
                                                String telcard = JSONUtils.getString(data, "telcard", "");
                                                String address = JSONUtils.getString(data, "address", "");
                                                String email = JSONUtils.getString(data, "email", "");
                                                String empno = JSONUtils.getString(data, "empno", "");
                                                String signer = JSONUtils.getString(data, "signer", "");
                                                String wxno = JSONUtils.getString(data, "wxno", "");
                                                empCard = new EmpCard();

                                                empCard.empid = empid;
                                                if (StringCheckHelper.isEmpty(empname)) {
                                                    empname = "";
                                                }
                                                empCard.empname = empname;
                                                if (StringCheckHelper.isEmpty(telcard)) {
                                                    telcard = "";
                                                }
                                                empCard.telcard = telcard;
                                                if (StringCheckHelper.isEmpty(address)) {
                                                    address = "";
                                                }
                                                empCard.address = address;
                                                if (StringCheckHelper.isEmpty(email)) {
                                                    email = "";
                                                }
                                                empCard.email = email;

                                                if (StringCheckHelper.isEmpty(empno)) {
                                                    empno = "";
                                                }
                                                empCard.empno = empno;
                                                if (StringCheckHelper.isEmpty(signer)) {
                                                    signer = "";
                                                }
                                                empCard.signer = signer;
                                                if (StringCheckHelper.isEmpty(wxno)) {
                                                    wxno = "";
                                                }
                                                empCard.wxno = wxno;

                                                //查询助理是否在线
                                                if (!TextUtils.isEmpty(toChatId  ))
                                                if (!TextUtils.equals(toChatId,EaseConstant.EXTRA_KEFU )) {
                                                    getHXUserSttus(empid);
                                                }


                                            }
                                            break;
                                        case ServerUrl.METHOD_getHXUserSttus://查询助理是否在线
                                             data_emp = jsonObject.optString("data", "");
                                            break;
                                    }
                                } else if (result.equals(Constants.RESULT_ERROR)) {
                                    String errorMsg = jsonObject.optString("errorMsg",
                                            "出错!");
                                    Toast.makeText(mContext.getApplicationContext(),
                                            errorMsg, Toast.LENGTH_SHORT).show();
                                }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(mContext.getApplicationContext(), "出错!",
                                    Toast.LENGTH_SHORT).show();
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

    // 查询助理是否在线
    private void getHXUserSttus(String empid) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("empid", empid);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_getHXUserSttus, obj.toString(), mHandler,
                    false);
            clientHelper.isShowProgress(false);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.ease_fragment_chat);
    }

    private void getNickNameByIds(String ids) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("empid", ids);
            ClientHelper clientHelper = new ClientHelper(getActivity(),
                    ServerUrl.METHOD_findEmployeeByIds, obj.toString(),
                    mHandler, false);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        fragmentArgs = getArguments();
        // check if single chat or group chat
        chatType = fragmentArgs.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        // userId you are chat with or group id
        toChatId = fragmentArgs.getString(EaseConstant.EXTRA_USER_ID);
        userName = fragmentArgs.getString(EaseConstant.EXTRA_USER_NAME);
        if (!ChatHelper.getInstance().getContactList().containsKey(toChatId.toUpperCase())) {
            {
                if (!toChatId.equals(EaseConstant.EXTRA_KEFU)) {
                    //根据助理id获取昵称等基本情况
                    getNickNameByIds(toChatId);
                }

            }
        }
        super.onActivityCreated(savedInstanceState);
        //查看助理名片
        getEmpCard();
        NotificationCenter.defaultCenter.addListener(NotificationKey.click_chat_add, this);
    }

    /**
     * init view
     */

    protected void initView() {
        // hold to record voice
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(R.id.voice_recorder);

        // message list layout
        messageList = (EaseChatMessageList) getView().findViewById(R.id.message_list);
        if (chatType != EaseConstant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
        listView = messageList.getListView();

        extendMenuItemClickListener = new MyItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(R.id.input_menu);
        registerExtendMenuItem();
        // init input menu
        inputMenu.init(null);
        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);

                //助理不在线时发送消息个微信
                if (!TextUtils.isEmpty( data_emp)){
                    switch (data_emp){
                        case "offline":
                            sendWXTempToEmp(content);//
                            break;
                        case "online":
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {

                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //环信转微信
    private void sendWXTempToEmp(String content) {
        JSONObject obj = new JSONObject();
        try {
            CurrentUser currentUser = AppContext.getInstance().getAppPref().getUser();
            String custname = currentUser.custtel;
            obj.put("custname", custname);//
            obj.put("empid", empCard.empid);
            obj.put("content", content);
            ClientHelper clientHelper = new ClientHelper(mContext, ServerUrl.METHOD_sendWXTempToEmp,
                    obj.toString(), mHandler,false);
            clientHelper.sendPost(true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void setUpView() {

        registerLeftImageView(R.drawable.arrow_back);
        if (chatType == EaseConstant.CHATTYPE_SERVICE) {
            setTitle("客服坐席");
        } else if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            // set title
            setTitle();
        } else {
            onChatRoomViewCreation();

        }
        if (chatType != EaseConstant.CHATTYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }

        setRefreshLayoutListener();

        // show forward message if the message is not null
        String forward_msg_id = getArguments().getString("forward_msg_id");
        if (forward_msg_id != null) {
            forwardMessage(forward_msg_id);
        }
    }

    private void setTitle() {
        String username = "";
        EaseUser easeUser = ChatHelper.getInstance().getUserInfo(toChatId);
        if (easeUser != null) {
            username = easeUser.getNikename();
        }
        setTitle(!StringCheckHelper.isEmpty(username) ? username : userName);


    }

    /**
     * register extend menu, item id need > 3 if you override this method and keep exist item
     */
    protected void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
    }


    protected void onConversationInit() {
        conversation = EMClient.getInstance().chatManager().getConversation(toChatId, EaseCommonUtils.getConversationType(chatType), true);
        conversation.markAllMessagesAsRead();
// the number of messages loaded into conversation is getChatOptions().getNumberOfMessagesLoaded
// you can change this number
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }

    }

    protected void onMessageListInit() {
        messageList.init(toChatId, chatType, chatFragmentHelper != null ?
                chatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();

        messageList.getListView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.hideExtendMenuContainer();
                return false;
            }
        });

        isMessageListInited = true;
    }

    protected void setListItemClickListener() {
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarClick(username);
                }
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onResendClick(final EMMessage message) {
                new EaseAlertDialog(getActivity(), R.string.resend, R.string.confirm_resend, null, new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }

                        resendMessage(message);
                    }
                }, true).show();
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if (chatFragmentHelper != null) {
                    return chatFragmentHelper.onMessageBubbleClick(message);
                }
                return false;
            }

        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                } else {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_more_messages),
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists())
                    sendImageMessage(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // location
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress);
                } else {
                    Toast.makeText(getActivity(), R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isMessageListInited)
            messageList.refresh();
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);

        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EaseAtMessageHelper.get().removeAtMeGroup(toChatId);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(this);

        // remove activity from foreground activity list
        EaseUI.getInstance().popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(toChatId);
        }

        if (chatRoomChangeListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomChangeListener(chatRoomChangeListener);
        }
        NotificationCenter.defaultCenter.removeListener(NotificationKey.click_chat_add, this);

    }

    @Override
    protected void goBack() {
        closeConversation();
    }

    private void back() {
        if (inputMenu.onBackPressed()) {
            getActivity().finish();
            if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(toChatId);
            }
        }
    }

    public void onBackPressed() {
        closeConversation();
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Joining......");
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatId, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity().isFinishing() || !toChatId.equals(value.getId()))
                            return;
                        pd.dismiss();
                        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(toChatId);
                        if (room != null) {
                            setTitle(room.getName());
                            EMLog.d(TAG, "join room success : " + room.getName());
                        } else {
                            setTitle();
//                            titleBar.setTitle(toChatId);
                        }
                        addChatRoomChangeListenr();
                        onConversationInit();
                        onMessageListInit();
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                getActivity().finish();
            }
        });
    }


    protected void addChatRoomChangeListenr() {
        chatRoomChangeListener = new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(toChatId)) {
                    showChatroomToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                    getActivity().finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showChatroomToast("member : " + participant + " join the room : " + roomId);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                showChatroomToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
            }

            @Override
            public void onMemberKicked(String roomId, String roomName, String participant) {
                if (roomId.equals(toChatId)) {
                    String curUser = EMClient.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMClient.getInstance().chatroomManager().leaveChatRoom(toChatId);
                        getActivity().finish();
                    } else {
                        showChatroomToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                    }
                }
            }

        };

        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);
    }

    protected void showChatroomToast(final String toastContent) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), toastContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // implement methods in EMMessageListener
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            String username = null;
            // group message
            if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }

            // if the message is for current conversation
            if (username.equals(toChatId)) {
                messageList.refreshSelectLast();
                EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
            } else {
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {

    }

    @Override
    public void onMessageReadAckReceived(List<EMMessage> messages) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> messages) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }


    /**
     * handle the click event for extend menu
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentHelper != null) {
                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE:
                    selectPicFromCamera();
                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal();
                    break;
                case ITEM_LOCATION:
                    startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    break;

                default:
                    break;
            }
        }

    }

    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol) {
        if (EMClient.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        if (EaseUserUtils.getUserInfo(username) != null) {
            if (autoAddAtSymbol)
                inputMenu.insertText("@" + EaseUserUtils.getUserInfo(username) + " ");
            else
                inputMenu.insertText(EaseUserUtils.getUserInfo(username) + " ");
        } else {
            if (autoAddAtSymbol)
                inputMenu.insertText("@" + username + " ");
            else
                inputMenu.insertText(username + " ");
        }
    }


    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username) {
        inputAtUsername(username, true);
    }


    //send message
    protected void sendTextMessage(String content) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content, toChatId);
            sendMessage(message);
        }
    }

    /**
     * send @ message, only support group chat message
     *
     * @param content
     */
    private void sendAtMessage(String content) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            EMLog.e(TAG, "only support group chat message");
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatId);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatId);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        } else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessage(message);

    }


    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatId, name, identityCode);
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatId);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatId);
        sendMessage(message);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatId);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatId);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatId);
        sendMessage(message);
    }


    protected void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }
        if (chatFragmentHelper != null) {
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(ChatType.GroupChat);
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.setChatType(ChatType.ChatRoom);
        }
        //send message
        EMClient.getInstance().chatManager().sendMessage(message);
        //refresh ui
        if (isMessageListInited) {
            messageList.refreshSelectLast();
        }
    }


    public void resendMessage(EMMessage message) {
        message.setStatus(EMMessage.Status.CREATE);
        EMClient.getInstance().chatManager().sendMessage(message);
        messageList.refresh();
    }

    //===================================================================================


    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    /**
     * send file
     *
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            Toast.makeText(getActivity(), R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        //limit the size < 10M
        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(getActivity(), R.string.The_file_is_not_greater_than_10_m, Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }


    /**
     * clear the conversation history
     */
    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(), null, msg, null, new EaseAlertDialog.AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    EMClient.getInstance().chatManager().deleteConversation(toChatId, true);
                    messageList.refresh();
                }
            }
        }, true).show();
        ;
    }

    /**
     * open group detail
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatId);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    /**
     * hide
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * forward message
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                } else {
                    // get the content and send it
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            default:
                break;
        }

        if (forward_msg.getChatType() == EMMessage.ChatType.ChatRoom) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }


    protected EaseChatFragmentHelper chatFragmentHelper;

    public void setChatFragmentListener(EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper {
        /**
         * set message attribute
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * enter to chat detail
         */
        void onEnterToChatDetails();

        /**
         * on avatar clicked
         *
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * on avatar long pressed
         *
         * @param username
         */
        void onAvatarLongClick(String username);

        /**
         * on message bubble clicked
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * on message bubble long pressed
         */
        void onMessageBubbleLongClick(EMMessage message);

        /**
         * on extend menu item clicked, return true if you want to override
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         *
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();

    }


    //region 结束会话
    private void closeConversation() {
        final Dialog dlg = new Dialog(getActivity(), R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater.inflate(R.layout.close_conversation, null);
        Button btn_left = (Button) viewContent.findViewById(R.id.btn_left);
        Button btn_right = (Button) viewContent.findViewById(R.id.btn_right);

        btn_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.cancel();
                back();


            }
        });

        btn_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.cancel();
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(false);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels * 2 / 3;
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        dlg.show();
    }

    //endregion


    @Override
    public boolean onNotification(NotificationListener.Notification notification) {
        if (notification.key.equals(NotificationKey.click_chat_add)) {
            View view = (View) notification.object;
            if (!TextUtils.isEmpty(toChatId) && !toChatId.equals(EaseConstant.EXTRA_KEFU)) {
                initPopWindow(view);

            }
            return true;

        }
        return super.onNotification(notification);
    }

    public void getEmpCard() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("empid", toChatId.toUpperCase());
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_getEmpCard, obj.toString(), mHandler,
                    false);
            clientHelper.isShowProgress(false);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initPopWindow(View v) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bottom, null, false);
        LinearLayout layout_mannger = (LinearLayout) view.findViewById(R.id.layout_mannger);
        LinearLayout layout_mannger_phone = (LinearLayout) view.findViewById(R.id.layout_mannger_phone);

        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        TextView mTxtContent = (TextView) view.findViewById(R.id.txt_content);
        if (empCard != null) {
            mTxtContent.setText(String.format("您好，我在关注%s的房子,想问下%s房源是否还在", empCard.address, empCard.empno));
            layout_mannger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("高级助理:" + empCard.empname + "\n");
                    stringBuilder.append("编    号 :" + empCard.empno + "\n");
                    stringBuilder.append("手机号码:" + empCard.telcard + "\n");
                    stringBuilder.append("电子邮箱:" + empCard.email + "\n");
                    stringBuilder.append("微 信 号:" + empCard.wxno + "\n");
                    stringBuilder.append("办公地址:" + empCard.address + "\n");
                    stringBuilder.append("服务宣言:" + empCard.signer);

                    EMMessage message = EMMessage.createTxtSendMessage(stringBuilder.toString(), AppContext.getInstance().getAppPref().getUser().custid);
                    sendMessage(message);

                    popWindow.dismiss();
                }
            });

            layout_mannger_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EMMessage message = EMMessage.createTxtSendMessage(empCard.telcard, toChatId);
                    sendMessage(message);
                    popWindow.dismiss();

                }
            });

        }
        popWindow.setAnimationStyle(R.anim.show_from_bottom);  //设置加载动画


        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, -10, 30);


    }
}
