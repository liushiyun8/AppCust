package foundation.widget.recyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

public class ItemDragHelperCallback extends ItemTouchHelper.Callback {

    public static final float ALPHA_FULL = 1.0f;

    private OnItemDragListener mOnItemDragListener;

    public ItemDragHelperCallback(OnItemDragListener onItemDragListener) {
        this.mOnItemDragListener = onItemDragListener;
    }

    /**
     * 返回true去支持长按RecyclerView的item时的drag事件
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * 返回true开启触摸视图时的swipe功能
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * 用于设置是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
     * 有以下两种情况：
     * 如果是列表类型的RecyclerView，拖拽只有UP、DOWN两个方向
     * 如果是网格类型的则有UP、DOWN、LEFT、RIGHT四个方向
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager || manager instanceof StaggeredGridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            //支持滑动删除
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            //不支持滑动删除
            //final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // 不同Type之间不可移动
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        //得到拖动ViewHolder的position
        int fromPosition = viewHolder.getAdapterPosition();
        //得到目标ViewHolder的position
        int toPosition = target.getAdapterPosition();
        //通知item的位置改变了.
        mOnItemDragListener.onItemMove(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        //通知有item被清除了
        mOnItemDragListener.onItemDismiss(position);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            Log.d("ItemTouchCallBack", "ACTION_STATE_SWIPE");
            //滑动时改变Item的透明度
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    /**
     * 当长按选中item的时候（拖拽开始的时候）调用
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 当手指松开的时候（拖拽完成的时候）调用
     *
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.d("ItemTouchCallBack", "clearview");
        viewHolder.itemView.setAlpha(ALPHA_FULL);
        viewHolder.itemView.setBackgroundColor(0);
        super.clearView(recyclerView, viewHolder);
    }

}