package foundation.base.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.library.R;
import com.umeng.analytics.MobclickAgent;

import org.droidparts.Injector;
import org.droidparts.util.Strings;

import foundation.LoadingHandler;
import foundation.notification.NotificationListener;
import foundation.widget.NavigationBar;

public class BaseFragment extends Fragment implements NotificationListener {
    public int kPage = 0;
    /**
     * 是否可见状态
     */
    private boolean isVisible;
    /**
     * 标志位，View已经初始化完成。
     */
    private boolean isPrepared;
    /**
     * 是否第一次加载
     */
    private boolean isFirstLoad = true;

    public String TAG = getClass().getSimpleName();

    protected LinearLayout _rootView;
    protected NavigationBar navigationBar;
    protected FrameLayout _containerLayout;
    protected View _contentView;
    protected LayoutInflater _layoutInflater;
    protected int _contaninerViewId = 100;
    protected int _navBarViewId = 101;
    protected Activity mContext;

    protected View onCreateContentView() {
        return null;
    }

    @SuppressWarnings("ResourceType")
    protected View createView() {
        _rootView = new LinearLayout(this.getActivity());
        _rootView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        _rootView.setOrientation(LinearLayout.VERTICAL);

        //头部
        navigationBar = onCreateNavbar();
        if (navigationBar != null) {
            navigationBar.setLayoutParams(new LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            //noinspection ResourceType
            navigationBar.setId(_navBarViewId);
            navigationBar.setBackgroundResource(R.drawable.nav_bar_bg);

            _rootView.addView(navigationBar);
        }

        //内容区
        _containerLayout = new FrameLayout(this.getActivity());
        _containerLayout.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0, 1));
        _containerLayout.setId(_contaninerViewId);
        _rootView.addView(_containerLayout);

        //嵌入内容区
        _contentView = onCreateContentView();
        if (_contentView != null) {
            _containerLayout.addView(_contentView);
        }

        return _rootView;
    }

    public void setDrawableLeft(TextView textView, int resid) {

        if (textView != null) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), resid);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView.setCompoundDrawables(drawable, null, null, null);
            }
        }
    }

    public void setDrawableTop(TextView textView, int resid) {

        if (textView != null) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), resid);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView.setCompoundDrawables(null, drawable, null, null);
            }
        }
    }

    // region Life cycle


    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        _layoutInflater = inflater;
        isFirstLoad = true;
        View view = createView();
        Injector.inject(view, this);
        // ButterKnife.bind(this, view);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        isPrepared = true;
        return view;
    }


    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    protected void registerRightImageView(final int resid) {
        if (navigationBar == null)
            return;
        showNavigationBar(true);
        navigationBar.setRightImage(resid, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });

    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void onInvisible() {
    }

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirstLoad) {
            return;
        }
        isFirstLoad = false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _loadingHandler = new LoadingHandler(getActivity());
        mContext = getActivity();
        lazyLoad();
        showNavigationBar(false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // endregion

    // region Loading dialog

    private LoadingHandler _loadingHandler;

    public void showLoading() {
        _loadingHandler.showLoading();
    }

    public void showLoading(String message) {
        _loadingHandler.showLoading(message);
    }

    public void updateLoading(String message) {
        _loadingHandler.updateLoading(message);
    }

    public void hideLoading() {
        _loadingHandler.hideLoading();
    }

    // endregion

    protected void registerLeftImageView(int resid) {
        if (navigationBar == null)
            return;
        showNavigationBar(true);
        navigationBar.registerBack(resid, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();

            }
        });
    }

    protected void showNavigationBar(boolean show) {

        if (navigationBar == null)
            return;

        if (show) {
            navigationBar.setVisibility(View.VISIBLE);
        } else {
            navigationBar.setVisibility(View.GONE);
        }
    }

    protected ViewGroup getContainer() {
        return _containerLayout;
    }

    protected NavigationBar onCreateNavbar() {
        return new NavigationBar(this.getActivity());
    }

    protected void goBack() {
    }

    protected void goNext() {
    }

    protected void setTitle(String title) {
        if (navigationBar == null)
            return;

        if (Strings.isNotEmpty(title)) {
            showNavigationBar(true);
        }
        navigationBar.setTitle(title);
    }

    protected void setTitle(int title) {
        setTitle(getResourceString(title));
    }


    protected void setRightTitle(String title) {
        if (navigationBar == null)
            return;
        navigationBar.setRightText(title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });

    }


    protected void setRightTitle(int resId) {
        this.setRightTitle(getResourceString(resId));
    }


    protected String getResourceString(int resId) {
        String result = null;
        try {
            result = this.getResources().getString(resId);
        } catch (Exception e) {
        }
        return result;
    }

    protected View inflateContentView(int resId) {
        return getLayoutInflater().inflate(resId, _containerLayout, false);
    }

    protected LayoutInflater getLayoutInflater() {
        return _layoutInflater;
    }

    public boolean onNotification(NotificationListener.Notification notification) {

        return true;
    }

}