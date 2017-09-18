package com.eke.cust;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import foundation.base.fragment.BaseFragment;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.tabmine.EntrustActivity;
import com.eke.cust.tabmore.about_activity.AboutActivity;
import com.eke.cust.tabmore.feedback_activity.FeedbackActivity;

import org.droidparts.annotation.inject.InjectView;

public class FragmentMenuMore extends BaseFragment implements OnClickListener {
    @InjectView(id = R.id.listview)
    private ListView listview;
    private String[] mMenuItems = {"意见反馈", "关         于",
            "房源委托"};
    private int[] icons = {R.drawable.icon_feedback,
            R.drawable.icon_about,
            R.drawable.icon_house};


    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.fragment_menu_more);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle("更多");
        MenuListAdapter adapter = new MenuListAdapter(getActivity());
        for (int i = 0; i < mMenuItems.length; i++) {
            adapter.add(new MenuListItem(mMenuItems[i], icons[i]));
        }

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                switch (position) {


                    case 0:// 意见反馈
                    {
                        UIHelper.startActivity(getActivity(), FeedbackActivity.class);
                    }
                    break;

                    case 1:// 关于
                    {
                        UIHelper.startActivity(getActivity(), AboutActivity.class);
                    }
                    break;


                    case 2:// 房源委托
                    {
                        UIHelper.startActivity(getActivity(), EntrustActivity.class);

                    }
                    break;

                    default:
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            default:
                break;
        }
    }

    private class MenuListItem {
        public String item;
        public int icon;

        public MenuListItem(String item, int icon) {
            this.item = item;
            this.icon = icon;
        }
    }

    public class MenuListAdapter extends ArrayAdapter<MenuListItem> {

        public MenuListAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.tab_more_list_item, null);
            }

            TextView title = (TextView) convertView.findViewById(R.id.tv_item);
            title.setText(getItem(position).item);

            ImageView iv = (ImageView) convertView.findViewById(R.id.iv_item);
            iv.setImageResource(getItem(position).icon);

            return convertView;
        }

    }

}
