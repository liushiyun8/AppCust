package foundation.widget.recyclerView;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by wujian on 2016/10/5.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private boolean includeEdge;
    public SpaceItemDecoration(int spacing, boolean includeEdge) {
        this.space = spacing;
        this.includeEdge = includeEdge;
    }
    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position=parent.getChildAdapterPosition(view);
        if(position!= 0){
            outRect.top = space;
            if(position==parent.getChildCount()&&includeEdge){
                outRect.bottom = space;
            }

        }
    }
}
