package foundation.base.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import static foundation.widget.swiperefresh.SuperSwipeRefreshLayout.Direction.BOTH;
import static foundation.widget.swiperefresh.SuperSwipeRefreshLayout.Direction.TOP;

/**

 * <p/>
 * Fragment
 */
public abstract class BaseListViewFragment<T> extends BaseFragment implements
        AdapterView.OnItemClickListener, SuperSwipeRefreshLayout.OnRefreshListener {

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
        _refreshLayout = new SuperSwipeRefreshLayout(getActivity());
        _refreshLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        _listView = this.createListView();
        //设置分割线
        //  mRecyclerView.setDivider(getResources().getDrawable(R.drawable.line));
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        loadData();
    }

    protected ListView createListView() {
        return new ListView(getActivity());
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {

        _listView.setOnItemClickListener(this);

        if (_refreshLayout != null) {
            _refreshLayout.setOnRefreshListener(this);
            if (isPaged())
                _refreshLayout.setDirection(BOTH);
            else
                _refreshLayout.setDirection(TOP);
        }


        _adapter = new ListViewAdapter();

        _listView.setAdapter(_adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _adapter = null;
        _listView = null;
    }


    public void setRefreshMode(SuperSwipeRefreshLayout.Direction mode) {
        if (_refreshLayout != null) {
            _refreshLayout.setDirection(mode);

        }

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

                        reloadData(result );
                        hideLoading();
                    }
                });
            }
        });
    }

    private void reloadData(ArrayList<T> dataSource) {

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

        setRefreshReset();


    }


    public void update(ArrayList<T> _dataSource) {
        this._dataSource = _dataSource;
        _adapter.notifyDataSetChanged();
    }

    private void setRefreshReset() {
        if (_refreshLayout != null) {
            _refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // 单击某一项
        if (_dataSource.size() > 0) {
            T item = _dataSource.get(i);
            onClickItem(item, view, i);
        }
    }


    protected void loadData() {
        _beforeItem = null;
        this.updateData(true);
    }

    public void onRefresh(SuperSwipeRefreshLayout.Direction direction) {
        if (direction == SuperSwipeRefreshLayout.Direction.TOP) {
            kPage = 0;
        } else if (direction == SuperSwipeRefreshLayout.Direction.BOTTOM) {
            kPage++;
        }
        this.updateData(false);

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
            return BaseListViewFragment.this.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position) {
            return BaseListViewFragment.this.getItemViewType(position);
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
            if (_listView != null) {
                v.setMinimumHeight(_listView.getHeight());
            }
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

    protected View inflateItemView(int resId) {
        return getLayoutInflater().inflate(resId, _listView, false);
    }
}
