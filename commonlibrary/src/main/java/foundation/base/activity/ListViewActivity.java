package foundation.base.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.common.library.R;

import java.util.ArrayList;

import foundation.util.ThreadUtils;
import foundation.toast.NewDataToast;
import foundation.widget.swiperefresh.SuperSwipeRefreshLayout;


/**
 * Created by wfpb on 15/6/25.
 * <p/>
 * 列表Activity
 */
public abstract class ListViewActivity<T> extends BaseActivity implements
        AdapterView.OnItemClickListener,
        SuperSwipeRefreshLayout.OnRefreshListener {

    private final int kPageSize = 20;

    public enum ListState {

        LS_OK,
        LS_Empty,
        LS_Error
    }

    protected ListView _listView;
    protected SuperSwipeRefreshLayout _refreshLayout;

    protected ArrayList<T> _dataSource = new ArrayList<>();
    private T _beforeItem;
    private ListState _state;
    private ListViewAdapter _adapter;

    protected ViewGroup getListLayout() {
        return _refreshLayout;
    }

    @Override
    protected View onCreateContentView() {
        _refreshLayout = new SuperSwipeRefreshLayout(this);
        _refreshLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        _listView = this.createListView();
        _listView.setCacheColorHint(Color.TRANSPARENT);
        _listView.setBackgroundColor(Color.TRANSPARENT);
        //设置选中背景
        _listView.setSelector(getResources().getDrawable(R.drawable.transparent_bg));
        _listView.setFadingEdgeLength(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            _listView.setScrollBarSize(0);
        }
        _listView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        _refreshLayout.addView(_listView);
        return _refreshLayout;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        loadData();
    }

    protected ListView createListView() {
        return new ListView(this);
    }

    private void initView() {

        _listView.setOnItemClickListener(this);

        if (_refreshLayout != null) {
            _refreshLayout.setOnRefreshListener(this);
            if (isPaged())
                _refreshLayout.setDirection(SuperSwipeRefreshLayout.Direction.BOTH);
            else
                _refreshLayout.setDirection(SuperSwipeRefreshLayout.Direction.TOP);
        }


        _adapter = new ListViewAdapter();

        _listView.setAdapter(_adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _adapter = null;
        _listView = null;
    }

    public void setRefreshMode(SuperSwipeRefreshLayout.Direction mode) {
        if (_refreshLayout != null) {
            _refreshLayout.setDirection(mode);

        }
    }


    protected void setDivider(int divider) {
        _listView.setDivider(getResources().getDrawable(divider));
    }

    protected void setSelector(int color) {
        _listView.setSelector(getResources().getDrawable(color));
    }

    protected int pageSize() {
        return kPageSize;
    }

    protected boolean isPaged() {
        return true;
    }

    private void updateData(final boolean showLoading) {

        if (showLoading) {
            this.showLoading();
        }

        ThreadUtils.runOnWorkThread(new Runnable() {
            @Override
            public void run() {

                int pageSize = pageSize();

                if (!isPaged()) {
                    pageSize = 0;
                }

                final ArrayList<T> ret = loadData(_beforeItem, pageSize);

                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ArrayList<T> result = null;

                        if (ret != null) {

                            result = new ArrayList<>();

                            if (_beforeItem != null) {
                                // 加载更多
                                result.addAll(_dataSource);
                            }
                            result.addAll(ret);

                            _state = result.size() > 0 ? ListState.LS_OK : ListState.LS_Empty;

                        } else {

                            _state = ListState.LS_Error;
                        }

                        reloadData(result, ret != null ? ret.size() : -1, false);
                        hideLoading();
                    }
                });
            }
        });
    }

    private void reloadData(ArrayList<T> dataSource, int moreDataSize, boolean showToast) {



        if (dataSource != null) {
            _dataSource = dataSource;

            if (_beforeItem == null) {
                //下拉刷新
                _adapter = new ListViewAdapter();
                if (_listView != null) {
                    _listView.setAdapter(_adapter);
                }
            } else {
                //上拉加载更多
                _adapter.notifyDataSetChanged();
            }

        }

        _refreshLayout.setRefreshing(false);

    }

    private void showRefreshResult() {
        if (_state == ListState.LS_Error) {

            NewDataToast.makeText(this,
                    getString(R.string.refresh_fail)).show();
            _refreshLayout.setRefreshing(false);
        }
    }

    public void update(ArrayList<T> _dataSource) {
        this._dataSource = _dataSource;
        _adapter.notifyDataSetChanged();
    }





    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // 单击某一项
        if (_dataSource.size() > 0) {
            T item = _dataSource.get(i);
            onClickItem(item, view, i);
        }
    }


    public void onLoad() {
        // 上拉加载更多
        _beforeItem = _dataSource.get(_dataSource.size() - 1);
        this.updateData(false);
    }


    public void onRefresh() {
        // 下拉刷新
        _beforeItem = null;
        this.updateData(false);
    }

    protected void loadData() {
        _beforeItem = null;
        this.updateData(true);
    }

    // region abstract methods

    protected abstract ArrayList<T> loadData(T beforeItem, int pageSize);

    protected abstract View getItemView(int position, T item, View view, ViewGroup viewGroup);

    // endregion

    protected View emptyView() {
        return null;
    }

    protected void onClickItem(T item, View view, int index) {
    }

    protected class ListViewAdapter extends BaseAdapter {

        private View _emptyView = getEmptyView();

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            _emptyView = getEmptyView();
        }

        @Override
        public int getCount() {
            if (_dataSource.size() == 0 && _emptyView != null && _state == ListState.LS_Empty)
                return 1;

            return _dataSource.size();
        }

        @Override
        public Object getItem(int i) {
            if (_dataSource.size() == 0 && _emptyView != null && _state == ListState.LS_Empty)
                return null;
            return _dataSource.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return ListViewActivity.this.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position) {
            return ListViewActivity.this.getItemViewType(position);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (_dataSource.size() == 0 && _emptyView != null && _state == ListState.LS_Empty)
                return _emptyView;

            @SuppressWarnings("unchecked")
            T item = (T) getItem(i);
            return getItemView(i, item, view, viewGroup);
        }
    }

    private View getEmptyView() {
        View v = emptyView();
        if (v != null) {
            v.setMinimumHeight(_listView.getHeight());
            return v;
        }
        return null;
    }

    protected int getViewTypeCount() {
        return 1;
    }

    protected int getItemViewType(int position) {
        return 1;
    }

    protected int getCount() {
        return _dataSource.size();
    }

    protected T getItem(int index) {
        return _dataSource.get(index);
    }

    protected void setRefreshEnabeld(boolean enabled) {
        _refreshLayout.requestDisallowInterceptTouchEvent(enabled);

    }

    protected void addItem(T item, int index) {

        _dataSource.add(index, item);

        if (_dataSource.size() == 1) {
            _adapter = new ListViewAdapter();
            _listView.setAdapter(_adapter);
            _state = ListState.LS_OK;
        } else {
            _adapter.notifyDataSetChanged();
        }
    }

    protected void removeItem(int index) {

        if (_dataSource.size() <= index) {
            return;
        }
        _dataSource.remove(index);

        if (_dataSource.size() == 0) {
            _state = ListState.LS_Empty;
            _adapter = new ListViewAdapter();
            _listView.setAdapter(_adapter);
        } else {
            _adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onRefresh(SuperSwipeRefreshLayout.Direction direction) {
        if (direction == SuperSwipeRefreshLayout.Direction.TOP) {
            onRefresh();
        } else if (direction == SuperSwipeRefreshLayout.Direction.BOTTOM) {
            onLoad();
        }

    }
    protected View inflateItemView(int resId) {
        return getLayoutInflater().inflate(resId, _listView, false);
    }
}
