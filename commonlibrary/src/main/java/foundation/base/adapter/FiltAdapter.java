package foundation.base.adapter;

import android.content.Context;


import java.util.ArrayList;
import java.util.List;

import foundation.callback.ICallback1;

/**
 * Created by Administrator on 2015-8-12.
 */
public abstract class FiltAdapter<T> extends CommonListAdapter<T> {
    protected  List<T> allItem;
    public FiltAdapter(Context context, int layoutId, List list) {
        super(context, layoutId, list);
    }
    public void filter(ICallback1<List<T>> callback1){
        allItem = new ArrayList<T>();
        allItem.addAll(list);
        callback1.callback(list);
        notifyDataSetChanged();
    }
    public void removeFilter(){
        if(allItem != null && allItem.size()>list.size()){
            list = allItem;
        }
        allItem = null;
        notifyDataSetChanged();
    }
}
