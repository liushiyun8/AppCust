package foundation.widget.recyclerView;

public interface OnItemDragListener {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}