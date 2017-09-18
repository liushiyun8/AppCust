package com.eke.cust.chat.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.eke.cust.R;
import com.eke.cust.notification.NotificationKey;

import foundation.notification.NotificationCenter;


/**
 * primary menu
 */
public class EaseChatPrimaryMenu extends EaseChatPrimaryMenuBase implements OnClickListener {
    private EditText editText;
    private View buttonSetModeKeyboard;
    //    private RelativeLayout edittext_layout;
    private ImageView add;
    private View buttonSend;
    private View buttonPressToSpeak;
    private ImageView faceNormal;
    private ImageView faceChecked;
    private Button buttonMore;
    private RelativeLayout faceLayout;
    private LinearLayout rl_bottom;
    private Context context;
    private EaseVoiceRecorderView voiceRecorderView;

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatPrimaryMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(final Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this);
        editText = (EditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
//        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        add = (ImageView) findViewById(R.id.btn_add);
        rl_bottom = (LinearLayout) findViewById(R.id.rl_bottom);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        faceNormal = (ImageView) findViewById(R.id.iv_face_normal);
        faceChecked = (ImageView) findViewById(R.id.iv_face_checked);
        faceLayout = (RelativeLayout) findViewById(R.id.rl_face);
        buttonMore = (Button) findViewById(R.id.btn_more);
        //  edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);

        buttonSend.setOnClickListener(this);
        buttonSetModeKeyboard.setOnClickListener(this);
        add.setOnClickListener(this);
        buttonMore.setOnClickListener(this);
//        faceLayout.setOnClickListener(this);
        editText.setOnClickListener(this);
//        editText.requestFocus();

//        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
//                } else {
//                    edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
//                }
//
//            }
//        });
        // listen the text change
//        editText.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(s)) {
//                    buttonMore.setVisibility(View.GONE);
//                    buttonSend.setVisibility(View.VISIBLE);
//                } else {
//                    buttonMore.setVisibility(View.VISIBLE);
//                    buttonSend.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


//        buttonPressToSpeak.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(listener != null){
//                    return listener.onPressToSpeakBtnTouch(v, event);
//                }
//                return false;
//            }
//        });
    }

    /**
     * set recorder view when speak icon is touched
     *
     * @param voiceRecorderView
     */
    public void setPressToSpeakRecorderView(EaseVoiceRecorderView voiceRecorderView) {
        this.voiceRecorderView = voiceRecorderView;
    }

    /**
     * append emoji icon to editText
     *
     * @param emojiContent
     */
    public void onEmojiconInputEvent(CharSequence emojiContent) {
        editText.append(emojiContent);
    }

    /**
     * delete emojicon
     */
    public void onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }
    }

    /**
     * on clicke event
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_send) {
            if (listener != null) {
                String s = editText.getText().toString();
                editText.setText("");
                listener.onSendBtnClicked(s);
            }
        } else if (id == R.id.btn_add) {
            //FIXME  点击
            initPopWindow(add);
            NotificationCenter.defaultCenter.postNotification(NotificationKey.click_chat_add,add);
        } else if (id == R.id.btn_set_mode_keyboard)

        {
            setModeKeyboard();
            showNormalFaceImage();
            if (listener != null)
                listener.onToggleVoiceBtnClicked();
        } else if (id == R.id.btn_more)

        {
            //点击更新
//            add.setVisibility(View.VISIBLE);
//            buttonSetModeKeyboard.setVisibility(View.GONE);
////            edittext_layout.setVisibility(View.VISIBLE);
//            buttonPressToSpeak.setVisibility(View.GONE);
//            showNormalFaceImage();
//            if(listener != null)
//                listener.onToggleExtendClicked();
        } else if (id == R.id.et_sendmessage)

        {
            faceNormal.setVisibility(View.VISIBLE);
            faceChecked.setVisibility(View.INVISIBLE);
            if (listener != null)
                listener.onEditTextClicked();
        } else if (id == R.id.rl_face)

        {
            toggleFaceImage();
            if (listener != null) {
                listener.onToggleEmojiconClicked();
            }
        } else

        {
        }

    }


    /**
     * show voice icon when speak bar is touched
     */
    protected void setModeVoice() {
        hideKeyboard();
//        edittext_layout.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        buttonMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);

    }

    /**
     * show keyboard
     */
    protected void setModeKeyboard() {
        // edittext_layout.setVisibility(View.VISIBLE);
        buttonSetModeKeyboard.setVisibility(View.GONE);
        add.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        editText.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(editText.getText())) {
            buttonMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            buttonMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }


    protected void toggleFaceImage() {
        if (faceNormal.getVisibility() == View.VISIBLE) {
            showSelectedFaceImage();
        } else {
            showNormalFaceImage();
        }
    }

    private void showNormalFaceImage() {
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);
    }

    private void showSelectedFaceImage() {
        faceNormal.setVisibility(View.INVISIBLE);
        faceChecked.setVisibility(View.VISIBLE);
    }


    @Override
    public void onExtendMenuContainerHide() {
        showNormalFaceImage();
    }

    @Override
    public void onTextInsert(CharSequence text) {
        int start = editText.getSelectionStart();
        Editable editable = editText.getEditableText();
        editable.insert(start, text);
        setModeKeyboard();
    }

    @Override
    public EditText getEditText() {
        return editText;
    }

    private void initPopWindow(View v) {
//        您好，我是助理，工号1234，联系手机是137您可以点右上角参看本网点（小区）房源。
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bottom, null, false);

        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        TextView mTxtContent = (TextView) view.findViewById(R.id.txt_content);
//        mTxtContent.setText(String.format("您好，我是助理,工号%s联系手机是137您可以点右上角参看本网点（小区）房源", chatName));

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画


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

        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
//        popWindow.showAtLocation(v, 50, 0);



    }


}
