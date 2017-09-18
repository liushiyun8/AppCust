package foundation.widget.recyclerView.listener;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by wujian on 2017/2/24.
 */

public class SwipeScrollListener extends RecyclerView.OnScrollListener {
    private RecyclerView recyclerView;
    private ViewGroup refreshLayout;

    public SwipeScrollListener(ViewGroup refreshLayout, RecyclerView recyclerView) {
        this.refreshLayout = refreshLayout;
        this.recyclerView = recyclerView;
        disptachScrollRefresh();
    }


    private void disptachScrollRefresh() {
        if (this.recyclerView != null && refreshLayout != null) {
            this.recyclerView.addOnScrollListener(this);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        dispatchScroll();
    }


    private void dispatchScroll() {
        if (this.recyclerView != null && this.refreshLayout != null) {
            //不可滚动
            if (!(ViewCompat.canScrollVertically(recyclerView, -1) || ViewCompat.canScrollVertically(recyclerView, 1))) {
                refreshLayout.setEnabled(true);
            } else//可以滚动
            {
                if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
                    refreshLayout.setEnabled(true);
                } else if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                    refreshLayout.setEnabled(true);
                } else {
                    refreshLayout.setEnabled(false);
                }

            }
        }
    }
}