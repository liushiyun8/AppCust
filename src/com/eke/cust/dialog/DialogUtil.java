package com.eke.cust.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.eke.cust.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wujian on 2016/7/26.
 */

public class DialogUtil {

    public Dialog showHouseMore(Activity activity) {
        final Dialog dlg = new Dialog(activity, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View viewContent = inflater
                .inflate(R.layout.dlg_tab_house_action, null);

        final RelativeLayout rl_page = (RelativeLayout) viewContent
                .findViewById(R.id.rl_page);
        final ListView listview = (ListView) viewContent
                .findViewById(R.id.listview_action);

        rl_page.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
            }
        });

        SimpleAdapter adapter = new SimpleAdapter(activity, getData(),
                R.layout.layout_tab_house_action_list_item,
                new String[]{"action"}, new int[]{R.id.tv_action});
        return dlg;

    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("action", "刷新");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("action", "条件查找");
        list.add(map);

        return list;
    }

}
