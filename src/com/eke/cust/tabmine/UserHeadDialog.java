package com.eke.cust.tabmine;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.ScreenUtils;
import com.eke.cust.utils.StringCheckHelper;

import org.droidparts.fragment.DialogFragment;
import org.droidparts.util.ResourceUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserHeadDialog extends DialogFragment {
    private ImageView mIvHead;

    public View onCreateView(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View view = mInflater.inflate(R.layout.fragment_user_head, null);
        mIvHead = (ImageView) view.findViewById(R.id.iv_user_head);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        window.setLayout(ScreenUtils.getScreenWidth(getActivity()) - ResourceUtils.dpToPx(getActivity(), 30), ViewGroup.LayoutParams.WRAP_CONTENT);
        CurrentUser currentUser = AppContext.getInstance().getAppPref().getUser();
        if (currentUser != null) {
            if (!StringCheckHelper.isEmpty(currentUser.ekeicon)) {
                mIvHead.setImageBitmap(BitmapUtils.stringtoBitmap(currentUser.ekeicon));
            } else {
                mIvHead.setImageResource(R.drawable.head_gray);
            }
        }

    }
}
