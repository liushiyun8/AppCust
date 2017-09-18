package foundation.widget.recyclerView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hiwhitley on 2016/4/28.
 */
public class HomeRecyclerView extends RecyclerView {


    public HomeRecyclerView(Context context) {
        super(context);
    }

    public HomeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean onTouchEvent(MotionEvent e) {
        stopNestedScroll();
        return super.onTouchEvent(e);
    }


}