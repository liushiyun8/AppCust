package foundation.base.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import org.droidparts.util.L;


/*
class DemoActivity extends Activity implements BaseAdapter.DataSource, BaseAdapter.Delegate
{
	private ListView mRecyclerView;
	private ListView _listView2;
	private BaseAdapter _adapter;
	private BaseAdapter _adapter2;
	private ArrayList<String> _dataList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//mRecyclerView && _dataList
		_adapter = new BaseAdapter(this, this);
		mRecyclerView.setAdapter(_adapter);
	}

	@Override
	public void didSelectItem(int position, View view) {
		
	}

	@Override
	public int getCount() {
		return _dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return _dataList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.picture_file_list_item,parent, false);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		
		return convertView;
	}
	
	
	
	public static class Holder
	{
		public AsyncImageView fileLogo;
		public TextView fileName;
		public TextView fileCount;
		public Button button;

		public Holder(View convertView) {
			fileLogo = (AsyncImageView) convertView
					.findViewById(R.id.fileLogo);
			fileName = (TextView) convertView.findViewById(R.id.fileName);
			fileCount = (TextView) convertView.findViewById(R.id.fileCount);
			button = (Button) convertView.findViewById(R.id.fileCount);
		}
	}


}


*/
public class BaseAdapter extends android.widget.BaseAdapter {
	
	public static abstract class DataSource
	{
		public DataSource() {
			L.d("life cycle", "DataSource constructor");
		}
		
		protected void finalize() throws Throwable {
			super.finalize();
			L.d("life cycle", "DataSource finalize");
		}
		
		public abstract int getCount();
		
		public abstract Object getItem(int position);
		
		public abstract View getView(int position, View convertView, ViewGroup parent);
		
		public int getItemViewType(int position) {
			return 0;
		}
		public int getViewTypeCount() {
			return 1;
		}
		
	}
	
	public interface Delegate
	{
		void didSelectItem(int position, View view);
	}

	private DataSource _dataSource;
	private Delegate _delegate;

	public BaseAdapter(DataSource dataSource, Delegate delegate)
	{
		_dataSource = dataSource;
		_delegate = delegate;
	}
	
	@Override
	public int getCount() {
		
		int count = 0;
		
		try {
			count = _dataSource.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (count == 0) {
			count = 0;
		}
		
		return count;
	}
	
	
	@Override
	public Object getItem(int position) {
		try {
			return _dataSource.getItem(position);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int getItemViewType(int position) {
        return _dataSource.getItemViewType(position);
    }

	@Override
    public int getViewTypeCount() {
        return _dataSource.getViewTypeCount();
    }
    
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = null;
		
		try {
			view = _dataSource.getView(position, convertView, parent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (view != null && _delegate != null) {
			
			final int index = position;
			final View f_view = view;
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						_delegate.didSelectItem(index, f_view);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	
		if (view == null) {
			view = null;
		}
		
		return view;
	}
}
