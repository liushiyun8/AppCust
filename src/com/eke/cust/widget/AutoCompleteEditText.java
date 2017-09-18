package com.eke.cust.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.eke.cust.R;
import com.eke.cust.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wujian on 2017/2/8.
 */

public class AutoCompleteEditText extends AutoCompleteTextView {

    private ArrayAdapter<String> adapter;
    private String startAtSymbol = "#";
    private ArrayList<String> dataList = new ArrayList<>();

    public AutoCompleteEditText(Context context) {
        this(context, null);
    }

    public AutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoCompleteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * @param dataList The data must start with the symbol
     */
    public void setAutoCompleteList(ArrayList<String> dataList) {
        this.setThreshold(1);
        adapter = new ArrayAdapter<String>(getContext(),
                R.layout.listview_item_select_popupwindow, dataList);
        setDropDownWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setDropDownHeight(ScreenUtils.dp2px(getContext(), 200));
        setDropDownVerticalOffset(ScreenUtils.dp2px(getContext(), 5));
        setDropDownBackgroundResource(R.color.transparent);
        setAdapter(adapter);


    }

//    @Override
//    public boolean enoughToFilter() {
//        if (getText() != null) {
//            return getText().length() != 0;
//        }
//        return true;
//    }
//
//    @Override
//    protected void performFiltering(CharSequence text, int keyCode) {
//        String beforeCursor = getText().toString().substring(0, getSelectionStart());
//        Pattern pattern = Pattern.compile(getRegularExpression());
//        Matcher matcher = pattern.matcher(beforeCursor);
//        if (matcher.find()) {
//            text = matcher.group(0);
//            ;
//        }
//        super.performFiltering(text, keyCode);
//    }

//    protected void replaceText(CharSequence text) {
//        String beforeCursor = getText().toString().substring(0, getSelectionStart());
////        String afterCursor = getText().toString().substring(getSelectionStart());
////
////        Pattern pattern = Pattern.compile("\\S*");
////        Matcher matcher = pattern.matcher(beforeCursor);
////        StringBuffer sb = new StringBuffer();
////        int matcherStart = 0;
////        while (matcher.find()) {
////            int curPos = getSelectionStart();
////            if(curPos > matcher.start() &&
////                    curPos <= matcher.end()){
////                matcherStart = matcher.start();
////                matcher.appendReplacement(sb, text.toString()+" ");
////            }
////        }
////        matcher.appendTail(sb);
//        setText(text);
//        if (listener != null) {
//            listener.onClickListener(text.toString());
//        }
//        // setSelection(matcherStart + text.length()+1);
//    }



    OnClickItemListener listener;

    public interface OnClickItemListener {
        void onClickListener(String name);
    }


}
