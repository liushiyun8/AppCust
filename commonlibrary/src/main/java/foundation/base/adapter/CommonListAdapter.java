package foundation.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wu_jian on 2015/7/30.
 */
public abstract class CommonListAdapter<T> extends android.widget.BaseAdapter {
    protected List<T> list;
    protected int layoutId;
    protected Context context;
    protected LayoutInflater inflater;
    private View _emptyView = getEmptyView();
    private ListView  listView;


    public CommonListAdapter(Context context, int layoutId, List<T> list) {
        this.list = list;
        this.layoutId = layoutId;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    public CommonListAdapter(ListView  listView,Context context, int layoutId, List<T> list) {
        this.list = list;
        this.layoutId = layoutId;
        this.context = context;
        this.listView=listView;
        inflater = LayoutInflater.from(context);
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        _emptyView = getEmptyView();
    }
    public void addList(ArrayList<T> list) {
        if (list == null) {
            return;
        }
        if (this.list != null) {
            this.list.addAll(list);
        } else {
            this.list = list;
        }
    }
    public void removeAll( ){
        if (this.list != null) {
            this.list.clear();
        }
    }
    @Override
    public int getCount() {
        if (list.size() == 0 && _emptyView != null ){
            return 1;

        }else if(list.size() == 0 || list == null){
            return 0;
        }else{
            return list.size();
        }
    }

    private View getEmptyView() {
        View v = emptyView();
        if (v != null) {
            v.setMinimumHeight(listView.getHeight());
            return v;
        }
        return null;
    }
    protected View emptyView() {
        return null;
    }


    @Override
    public T getItem(int arg0) {
        return list.get(arg0);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (list.size() == 0 && _emptyView != null)
            return _emptyView;
        ViewHolder viewHolder = ViewHolder.getViewHolder(context, position, layoutId, convertView, parent);
        convert(viewHolder, getItem(position),position);
        return viewHolder.getConvertView();
    }
    public abstract void convert(ViewHolder holder, T t,int position);
}
