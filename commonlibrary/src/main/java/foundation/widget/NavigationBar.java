package foundation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.library.R;


/**
 * Created by  on 15/7/7.
 * 导航栏
 */
public class NavigationBar extends RelativeLayout {
    public TextView mTxtTitle;
    public TextView mTxtRight;
    public ImageView mIvBack;
    public ImageView mIvRight;


    public NavigationBar(Context context) {
        super(context);
        initWidthContext(context);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWidthContext(context);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWidthContext(context);
    }

    protected void initWidthContext(Context context) {

		LayoutInflater.from(context).inflate(R.layout.common_navigationbar, this, true);

        mIvBack = (ImageView) findViewById(R.id.nav_bar_back);
        mTxtTitle = (TextView) findViewById(R.id.nav_bar_title);
        mTxtRight = (TextView) findViewById(R.id.nav_bar_right_txt);
        mIvRight = (ImageView) findViewById(R.id.nav_bar_right_image);

    }

    public void setTitle(String title) {
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText(title);
    }
    public void setTitle(String title,int textSize) {
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setTextSize(textSize);
        mTxtTitle.setText(title);
    }

    public CharSequence getTitle() {
        return mTxtTitle.getText();
    }

    public void setTitle(int resid) {
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText(resid);
    }

    public void setRightText(String title, OnClickListener listener) {
        if (listener == null) {
            mTxtRight.setVisibility(View.INVISIBLE);
        } else {
            mTxtRight.setText(title);
            mTxtRight.setVisibility(View.VISIBLE);
            mTxtRight.setOnClickListener(listener);
        }
    }
    public void setRightText(int title, OnClickListener listener) {
        if (listener == null) {
            mTxtRight.setVisibility(View.INVISIBLE);
        } else {
            mTxtRight.setText(title);
            mTxtRight.setVisibility(View.VISIBLE);
            mTxtRight.setOnClickListener(listener);
        }
    }
    public void setRightImage(int resid, OnClickListener listener) {
        if (listener == null) {
            mIvRight.setVisibility(View.INVISIBLE);
        } else {
            mIvRight.setImageResource(resid);
            mTxtRight.setVisibility(View.VISIBLE);
            mTxtRight.setOnClickListener(listener);
        }
    }

    public void registerBack(int resid,OnClickListener listener) {
        if (listener == null) {
            mIvBack.setVisibility(View.INVISIBLE);
        } else {
            mIvBack.setImageResource(resid);
            mIvBack.setVisibility(View.VISIBLE);
            mIvBack.setOnClickListener(listener);
        }
    }


    public View getmTxtBack() {
        return mIvBack;
    }


}
