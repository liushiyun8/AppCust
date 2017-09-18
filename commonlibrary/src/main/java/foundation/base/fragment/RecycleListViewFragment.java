package foundation.base.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.common.library.R;

import java.util.ArrayList;
import java.util.List;

import foundation.base.adapter.BaseRecyclerAdapter;
import foundation.enums.ListState;
import foundation.enums.RefreshState;
import foundation.util.ThreadUtils;
import foundation.widget.recyclerView.EmptyRecyclerView;
import foundation.widget.recyclerView.GridSpaceItemDecoration;
import foundation.widget.recyclerView.SpaceItemDecoration;
import foundation.widget.swiperefresh.SuperSwipeRefreshLayout;

/**
 * Created by wfpb on 15/6/25.
 * <p/>
 * 列表Fragment
 */
public abstract class RecycleListViewFragment<T> extends BaseFragment implements SuperSwipeRefreshLayout.OnRefreshListener {
	protected final int kPageSize = 2;
	protected int kPage = 0;
	//网络列
	protected int spanCount = 2;
	//网格方向
	protected int orientation = 1;


	protected EmptyRecyclerView mRecyclerView;
	protected SuperSwipeRefreshLayout _refreshLayout;

	protected ArrayList<T> _dataSource = new ArrayList<>();
	private ListState _state;
	private RefreshState _RefreshState;
	protected BaseRecyclerAdapter _adapter;

	protected ViewGroup getListLayout() {
		return _refreshLayout;
	}

	@Override
	protected View onCreateContentView() {
		_refreshLayout = new SuperSwipeRefreshLayout(getActivity());
		_refreshLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

		mRecyclerView = this.createListView();
		mRecyclerView.setLayoutParams(new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		_refreshLayout.addView(mRecyclerView);

		return _refreshLayout;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				int topRowVerticalPosition =
						(recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
				//  _refreshLayout.setEnabled(topRowVerticalPosition >= 0);

			}

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);


			}
		});
		mRecyclerView.setEmptyView(emptyView());
		loadData();
	}


	protected void setRefreshEnabeld(boolean enabled) {
		_refreshLayout.setEnabled(enabled);
	}


	protected EmptyRecyclerView createListView() {
		return new EmptyRecyclerView(getActivity());
	}

	@SuppressLint("ResourceAsColor")
	private void initView() {

		_refreshLayout.setOnRefreshListener(this);

		_refreshLayout.setDirection(SuperSwipeRefreshLayout.Direction.BOTH);
		_adapter = new ListViewAdapter(getActivity(), _dataSource);
		int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.dimen_div_5);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		if (getSortType()) {
			mRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
			mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		} else {
			mRecyclerView.addItemDecoration(new GridSpaceItemDecoration(spanCount, spacingInPixels, false));
			mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount, orientation, false));
		}
		mRecyclerView.setAdapter(_adapter);
	}

	public void onPause() {
		super.onPause();
		if (_refreshLayout.isRefreshing()) {
			_refreshLayout.setRefreshing(false);
		}
	}

	protected int pageSize() {
		return kPageSize;
	}

	protected boolean isPaged() {
		return true;
	}

	protected boolean getSortType() {
		return true;
	}

	private void updateData(final boolean showLoading) {

		if (showLoading) {
			this.showLoading();
		}

		ThreadUtils.runOnWorkThread(new Runnable() {
			@Override
			public void run() {

				Log.d("time", "start_time=" + System.currentTimeMillis());
				final ArrayList<T> ret = loadListData();
				ThreadUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {

						ArrayList<T> result = null;

						if (ret != null) {
							result = new ArrayList<>();
							result.addAll(ret);
							_state = result.size() > 0 ? ListState.LS_OK : ListState.LS_Empty;
						} else {

							_state = ListState.LS_Error;
						}

						reloadData(result);
						hideLoading();
						Log.d("time", "end_time=" + System.currentTimeMillis());

					}
				});
			}
		});
	}


	public void initData() {

	}


	private void reloadData(ArrayList<T> dataSource) {


		if (dataSource != null) {
			if (_RefreshState == RefreshState.LS_Refresh || _RefreshState == RefreshState.LS_INIT) {
				_dataSource.clear();
				_dataSource = dataSource;
				//下拉刷新
				_adapter = new ListViewAdapter(getActivity(), dataSource);
				if (mRecyclerView != null)
					mRecyclerView.setAdapter(_adapter);
				initData();
			} else {
				//上拉加载更多
				_dataSource.addAll(dataSource);

				_adapter.addItems(dataSource);
			}

		}
		setRefreshReset();


	}


	private void setRefreshReset() {
		if (_refreshLayout != null) {
			_refreshLayout.setRefreshing(false);
		}
	}


	public void onLoad() {
		//        // 上拉加载更多
		_RefreshState = RefreshState.LS_LoadMore;
		kPage += 1;
		this.updateData(false);
	}

	public void onRefresh() {
		// 下拉刷新
		_RefreshState = RefreshState.LS_Refresh;
		kPage = 0;
		this.updateData(false);
	}


	protected void loadData() {
		_RefreshState = RefreshState.LS_INIT;
		kPage = 0;
		this.updateData(true);
	}

	// region abstract methods

	public abstract ArrayList<T> loadListData();


	// endregion

	protected View emptyView() {
		return null;
	}

	protected int getItemCount() {
		return pageSize();
	}


	protected class ListViewAdapter extends BaseRecyclerAdapter {

		public ListViewAdapter(Context context, List datas) {
			super(context, datas);
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return getViewHolder(viewType);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			BindViewHolder(holder, position);
		}

		@Override
		public int getItemViewType(int position) {
			return ItemViewType(position);
		}
	}


	public abstract void BindViewHolder(RecyclerView.ViewHolder holder, int position);

	protected View getEmptyView() {
		View v = emptyView();
		if (v != null && mRecyclerView != null) {
			v.setMinimumHeight(mRecyclerView.getHeight());
			return v;
		}
		return null;
	}


	protected int getCount() {
		return _dataSource.size();
	}

	protected T getItem(int index) {
		return _dataSource.get(index);
	}

	public int ItemViewType(int position) {
		return 0;
	}

	public abstract RecyclerView.ViewHolder getViewHolder(int viewType);

	protected View inflateItemView(int resId) {
		return getLayoutInflater().inflate(resId, mRecyclerView, false);
	}

	@Override
	public void onRefresh(SuperSwipeRefreshLayout.Direction direction) {
		if (direction == SuperSwipeRefreshLayout.Direction.TOP) {
			onRefresh();
		} else if (direction == SuperSwipeRefreshLayout.Direction.BOTTOM) {
			onLoad();
		}

	}
}
