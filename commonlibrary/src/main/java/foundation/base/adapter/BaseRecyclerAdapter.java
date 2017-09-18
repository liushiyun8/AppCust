package foundation.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater inflater;
    private ClickListener listener;
    private SparseBooleanArray selectedItems;
    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mDatas = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selectedItems = new SparseBooleanArray();

    }

    public BaseRecyclerAdapter(Context context, List<T> datas) {
        if (datas == null) datas = new ArrayList<>();
        this.mContext = context;
        this.mDatas = datas;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selectedItems = new SparseBooleanArray();
    }


    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }
    public void clearSelectedState() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }
    public ArrayList<Integer> getSelectedItems() {
        ArrayList<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
    public void switchSelectedState(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public BaseRecyclerAdapter(Context context, T[] datas) {
        this.mContext = context;
        this.mDatas = new ArrayList<T>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Collections.addAll(mDatas, datas);
        this.selectedItems = new SparseBooleanArray();

    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /** 更新数据，替换原有数据 */
    public void updateItems(List<T> items) {
        mDatas = items;
        notifyDataSetChanged();
    }

    public    List<T>  getData(){
        return  mDatas;
    }
    /** 插入一条数据 */
    public void addItem(T item) {
        mDatas.add(0, item);
        notifyItemInserted(0);
    }

    /** 插入一条数据 */
    public void addItem(T item, int position) {
        position = Math.min(position, mDatas.size());
        mDatas.add(position, item);
        notifyItemInserted(position);
    }

    /** 在列表尾添加一串数据 */
    public void addItems(List<T> items) {
        int start = mDatas.size();
        mDatas.addAll(items);
        notifyItemRangeChanged(start, items.size());
    }

    /** 移除一条数据 */
    public void removeItem(int position) {
        if (position > mDatas.size() - 1) {
            return;
        }
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    /** 移除一条数据 */
    public void removeItem(T item) {
        int position = 0;
        ListIterator<T> iterator = mDatas.listIterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (next == item) {
                iterator.remove();
                notifyItemRemoved(position);
            }
            position++;
        }
    }

    /** 清除所有数据 */
    public void removeAllItems() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public  void setClickListener(ClickListener clickListener){
        this.listener=clickListener;

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public interface ClickListener {
        void onItemClicked(int position);
        boolean onItemLongClicked(int position);
    }
}