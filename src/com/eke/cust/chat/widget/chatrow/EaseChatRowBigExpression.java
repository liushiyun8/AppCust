package com.eke.cust.chat.widget.chatrow;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.eke.cust.R;
import com.eke.cust.chat.EaseConstant;
import com.eke.cust.chat.controller.EaseUI;
import com.eke.cust.chat.domain.EaseEmojicon;
import com.eke.cust.utils.LoadImageUtil;
import com.hyphenate.chat.EMMessage;


/**
 * big emoji icons
 *
 */
public class EaseChatRowBigExpression extends EaseChatRowText{

    private ImageView imageView;


    public EaseChatRowBigExpression(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }
    
    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? 
                R.layout.ease_row_received_bigexpression : R.layout.ease_row_sent_bigexpression, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }


    @Override
    public void onSetUpView() {
        String emojiconId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
        EaseEmojicon emojicon = null;
        if(EaseUI.getInstance().getEmojiconInfoProvider() != null){
            emojicon =  EaseUI.getInstance().getEmojiconInfoProvider().getEmojiconInfo(emojiconId);
        }
        if(emojicon != null){
            if(emojicon.getBigIcon() != 0){
                LoadImageUtil.getInstance().displayFromDrawable(emojicon.getBigIcon(),imageView);
            }else if(emojicon.getBigIconPath() != null){
                LoadImageUtil.getInstance().displayFromSDCard(emojicon.getBigIconPath(),imageView);
            }else{
                imageView.setImageResource(R.drawable.ease_default_expression);
            }
        }
        
        handleTextMessage();
    }
}
