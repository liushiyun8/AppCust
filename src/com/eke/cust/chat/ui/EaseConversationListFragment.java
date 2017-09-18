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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.chat.ChatActivity;
import com.eke.cust.chat.EaseConstant;
import com.eke.cust.chat.widget.EaseConversationList;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * conversation list fragment
 *
 */
public class EaseConversationListFragment extends EaseBaseFragment implements EMMessageListener {
	private final static int MSG_REFRESH = 2;
    protected boolean hidden;
    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
    protected EaseConversationList conversationListView;
    protected FrameLayout errorItemContainer;
    private String kefuID;
    private TextView unread_msg_number;
    private TextView all_msg_number;
    protected boolean isConflict;
    
    protected EMConversationListener convListener = new EMConversationListener(){

		@Override
		public void onCoversationUpdate() {
			refresh();
		}
    	
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_conversation_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        conversationListView = (EaseConversationList) getView().findViewById(R.id.list);
        errorItemContainer = (FrameLayout) getView().findViewById(R.id.fl_error_item);
    }
    
    @Override
    protected void setUpView() {
        unread_msg_number = (TextView) getActivity().findViewById(R.id.unread_msg_number);
        all_msg_number = (TextView) getActivity().findViewById(R.id.all_msg_number);
        conversationList.addAll(loadConversationList());
        conversationListView.init(conversationList);
        getActivity().findViewById(R.id.list_kf_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 联系客服，跳转到与客服聊天界面
                Intent intent = new Intent();
                intent.setClass(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, EaseConstant.EXTRA_KEFU);
                intent.putExtra(EaseConstant.EXTRA_USER_NAME, "移动客服");
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SERVICE);
                startActivity(intent);

            }
        });
        if(listItemClickListener != null){
            conversationListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EMConversation conversation = conversationListView.getItem(position);
                    listItemClickListener.onListItemClicked(conversation);
                }
            });
        }
        
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
    private EaseConversationListItemClickListener listItemClickListener;
    
    protected Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 0:
                onConnectionDisconnected();
                break;
            case 1:
                onConnectionConnected();
                break;
            
            case MSG_REFRESH:
	            {
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
     * connected to server
     */
    protected void onConnectionConnected(){
        errorItemContainer.setVisibility(View.GONE);
    }
    
    /**
     * disconnected with server
     */
    protected void onConnectionDisconnected(){
        errorItemContainer.setVisibility(View.VISIBLE);
    }
    

    /**
     * refresh ui
     */
    public void refresh() {
    	if(!handler.hasMessages(MSG_REFRESH)){
    		handler.sendEmptyMessage(MSG_REFRESH);
    	}
    }
    
    /**
     * load conversation list
     * 
     * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +    */
    protected List<EMConversation> loadConversationList(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        Iterator<String> iter = conversations.keySet().iterator();
        String key = "";
        while (iter.hasNext()) {
            key = iter.next();
            if (key.equals(EaseConstant.EXTRA_KEFU)) {
                //更新客服
                EMConversation conversation = conversations.get(key);
                conversations.remove(key);
                if (conversation.getUnreadMsgCount() > 0) {
                    // show unread message count
                    unread_msg_number.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    unread_msg_number.setText(String.valueOf(conversation.getUnreadMsgCount()));
                } else {
                    unread_msg_number.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    unread_msg_number.setText("" + 0);

                }
                all_msg_number.setText("/" + String.valueOf(conversation.getAllMsgCount()));
                break;
            }

        }
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
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isConflict){
            outState.putBoolean("isConflict", true);
        }
    }
    
    public interface EaseConversationListItemClickListener {
        /**
         * click event for conversation list
         * @param conversation -- clicked item
         */
        void onListItemClicked(EMConversation conversation);
    }
    
    /**
     * set conversation list item click listener
     * @param listItemClickListener
     */
    public void setConversationListItemClickListener(EaseConversationListItemClickListener listItemClickListener){
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
}
