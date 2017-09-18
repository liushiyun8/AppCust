package foundation.widget.recyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


/**
 * Created by wujian on 2017/2/17.
 */

public class OffestDecoration extends RecyclerView.ItemDecoration {
    private String TAG="OffestDecoration";


    private int mSpace;
    private int mEdgeSpace;
    /**
     * @param mSpace item间的间距 默认没有边距
     */
    public OffestDecoration(int mSpace, Context ctx) {
        this(mSpace, 0, ctx);
    }
    /**
     * @param mSpace     item间的间距
     * @param mEdgeSpace 边距(padding)
     */
    public OffestDecoration(int mSpace, int mEdgeSpace, Context ctx) {
        this.mSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mSpace, ctx.getResources().getDisplayMetrics()) + 0.5f);
        this.mEdgeSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mEdgeSpace, ctx.getResources().getDisplayMetrics()) + 0.5f);
    }
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        Log.i(TAG, "getItemOffsets");
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        int childPosition = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();
        if (manager != null) {
            if (manager instanceof GridLayoutManager) {
                // 待会再处理
            } else if (manager instanceof LinearLayoutManager) {
                setLinearOffset(((LinearLayoutManager) manager).getOrientation(), outRect, childPosition, itemCount);
            }
        }
    }


    private void setLinearOffset(int orientation, Rect outRect, int childPosition, int itemCount) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            if (childPosition == 0) {
                // 第一个要设置PaddingLeft
                outRect.set(mEdgeSpace, 0, mSpace, 0);
            } else if (childPosition == itemCount - 1) {
                // 最后一个设置PaddingRight
                outRect.set(0, 0, mEdgeSpace, 0);
            } else {
                outRect.set(0, 0, mSpace, 0);
            }
        } else {

            if (childPosition == 0) {
                outRect.set(0, 0, 0, mSpace);
            } else
                if (childPosition == itemCount - 1) {
                // 最后一个要设置PaddingBottom
                outRect.set(0, 0, 0, mEdgeSpace);
            } else {
                outRect.set(0, 0, 0, mSpace);
            }
        }
    }
}
