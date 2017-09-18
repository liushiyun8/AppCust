package foundation.base.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
	protected Context ctx;
	protected LayoutInflater layoutInflater;
	public ArrayList<T> list;

	protected Handler handler;

	public MyBaseAdapter(Context ctx, ArrayList<T> list) {
		super();

		this.list = list;
		this.ctx = ctx.getApplicationContext();
		layoutInflater = LayoutInflater.from(ctx);
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

	@Override
	public int getCount() {
		if (list.size() == 0 || list == null) {
			return 0;
		} else {
			return list.size();
		}

	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		return initView(position, convertView);
	}

	/**
	 * getView方法需重写
	 * 
	 * @param position
	 * @param convertView
	 * @return
	 */
	public abstract View initView(int position, View convertView);
}
