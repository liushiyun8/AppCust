package foundation.base.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class TheBaseAdapter<T,VH extends TheBaseAdapter.ViewHolder> extends BaseAdapter {
	protected Context mContext;
	protected LayoutInflater mLayoutInflater;
	public ArrayList<T> list;

	protected Handler handler;

	public TheBaseAdapter(Context ctx, ArrayList<T> list) {
		super();

		this.list = list;
		this.mContext = ctx;
		mLayoutInflater = LayoutInflater.from(ctx);
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

		VH holder;
		if (convertView == null) {
			holder = onCreateViewHolder(parent);
			convertView = holder.itemView;

			convertView.setTag(holder);
		} else {
			holder = (VH) convertView.getTag();
		}
		onBindViewHolder(holder,position);
		return convertView;
	}

	protected abstract void onBindViewHolder(VH viewHolder, int position);

	public abstract VH onCreateViewHolder(ViewGroup parent);

	public  static class ViewHolder{
		public View itemView;

		public ViewHolder(View itemView) {
			this.itemView = itemView;
		}
	}
}
