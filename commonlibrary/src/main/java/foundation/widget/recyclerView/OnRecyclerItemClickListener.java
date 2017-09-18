package foundation.widget.recyclerView;


import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import static android.support.v7.widget.RecyclerView.*;


/**
 * 为RecyclerView添加onItemClickListener
 */
public class OnRecyclerItemClickListener implements OnItemTouchListener {

    private GestureDetectorCompat mGestureDetector;
    private RecyclerView mRecyclerView;

    public OnRecyclerItemClickListener(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                ViewHolder vh = mRecyclerView.getChildViewHolder(child);
                if (vh != null) {
                    onItemClick(vh);

                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                ViewHolder vh = mRecyclerView.getChildViewHolder(child);
                if (vh != null) {
                    onItemLongClick(vh);
                }
            }

        }
    }

    public void onItemLongClick(ViewHolder vh) {
    }

    public void onItemClick(ViewHolder vh) {
    }
}