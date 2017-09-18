package com.eke.cust.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.chat.ChatHelper;
import com.eke.cust.chat.EaseConstant;
import com.eke.cust.chat.domain.EaseUser;
import com.eke.cust.chat.utils.EaseCommonUtils;
import com.eke.cust.chat.utils.EaseSmileUtils;
import com.eke.cust.chat.widget.MineConversationList;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.StringCheckHelper;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * conversation list adapter
 */
public class MineConversationAdapater extends ArrayAdapter<EMConversation> {
    private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;

    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;

    public MineConversationAdapater(Context context, int resource,
                                    List<EMConversation> objects) {
        super(context, resource, objects);
        conversationList = objects;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);
    }

    @Override
    public int getCount() {
        return conversationList.size() < 5 ? 5 : conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mine_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            convertView.setTag(holder);
        }
        EMConversation conversation = getItem(position);
        if (conversation != null) {
            EMMessage lastMessage = conversation.getLastMessage();
            String time = DateUtil.getScheduledateToString(lastMessage.localTime());
            String username = conversation.getUserName();
            if (username.equals(EaseConstant.EXTRA_KEFU)) {
                username = "移动客服";
            } else {
                EaseUser easeUser = ChatHelper.getInstance().getUserInfo(lastMessage.getFrom());
                if (easeUser != null) {
                    username = easeUser.getNikename();
                }
            }

            if (StringCheckHelper.isEmpty(username)) {
                username = "暂无";
            }
            holder.name.setText(time + " " + username + ":");
            String content = null;
            if (cvsListHelper != null) {
                content = cvsListHelper.onSetItemSecondaryText(lastMessage);
            }
            holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))),
                    TextView.BufferType.SPANNABLE);
            if (content != null) {
                holder.message.setText(content);
            }


        }

        return convertView;
    }


    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }


    private MineConversationList.EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(MineConversationList.EaseConversationListHelper cvsListHelper) {
        this.cvsListHelper = cvsListHelper;
    }

    private static class ViewHolder {

        TextView name;

        TextView message;

    }
}

