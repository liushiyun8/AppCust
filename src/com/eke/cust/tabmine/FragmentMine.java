package com.eke.cust.tabmine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.eke.cust.AppContext;
import com.eke.cust.FragmentMenuMine;
import com.eke.cust.R;
import com.eke.cust.notification.NotificationKey;

import foundation.base.fragment.BaseFragment;
import foundation.notification.NotificationCenter;

public class FragmentMine extends BaseFragment {

    //endregion
    @Override
    protected View onCreateContentView() {
        View view = inflateContentView(R.layout.activity_entrust);
        return view;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (AppContext.getInstance().isLogin()) {
            getChildFragmentManager().beginTransaction().replace(R.id.content, new FragmentMenuMine()).commit();
        } else {
            getChildFragmentManager().beginTransaction().replace(R.id.content, new EntrustFragment()).commit();
        }
        NotificationCenter.defaultCenter.addListener(NotificationKey.update_login, this);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter.removeListener(NotificationKey.update_login, this);

    }

    public boolean onNotification(Notification notification) {
        if (notification.key.equals(NotificationKey.update_login)) {
            if (AppContext.getInstance().isLogin()) {
                getChildFragmentManager().beginTransaction().replace(R.id.content, new FragmentMenuMine()).commitAllowingStateLoss();
                return true;
            } else {
                getChildFragmentManager().beginTransaction().replace(R.id.content, new EntrustFragment()).commitAllowingStateLoss();
                return true;
            }
        }
        return super.onNotification(notification);
    }
}