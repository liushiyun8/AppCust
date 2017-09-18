package com.eke.cust.base;

/**
 * 程序基类
 *
 * @author wujian 2014-3-21 22:43:27
 */
public abstract class BaseListActivity extends BaseActivity {

    protected RefreshState _RefreshState;
    protected final int kPageSize = 20;
    protected int kPage = 0;

    public enum RefreshState {

        LS_INIT,
        LS_Refresh,
        LS_LoadMore
    }


}
