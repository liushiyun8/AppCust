package com.eke.cust.tabmore.help_activity;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.utils.MyLog;

public class HelpListAdapter extends BaseExpandableListAdapter {

	private LinkedList<String> mGroupArray;
	private List<LinkedList<HelpNodeInfo>> mChildArray;
	private Context context;
	private LayoutInflater inflater;
	private Handler mHandler;
	
	public HelpListAdapter(Context context,
			Handler handler,
			LinkedList<String> groupList,
			List<LinkedList<HelpNodeInfo>> childArray) {
		inflater = ((Activity) context).getLayoutInflater();
		this.mHandler = handler;
		this.mGroupArray = groupList;
		this.mChildArray = childArray;
	}

	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mGroupArray.size();
	}

	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return mChildArray.get(groupPosition).size();
	}

	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mGroupArray.get(groupPosition);
	}

	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return mChildArray.get(groupPosition).get(childPosition);
	}

	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int group = groupPosition;
		final int child = childPosition;
		MyLog.d("TAG", "--<>group = " + group + ", child = " + child + ", " + mChildArray.get(groupPosition).get(childPosition).isExpanded());
		if(convertView == null){
            convertView = inflater.inflate(R.layout.layout_help_sub_list_item, parent, false);
        }
		
		final HelpNodeInfo node = mChildArray.get(groupPosition).get(childPosition);
		LinearLayout ll_title = (LinearLayout)convertView.findViewById(R.id.ll_title);
		ImageView iv = (ImageView) convertView.findViewById(R.id.iv_item);
        TextView item = (TextView) convertView.findViewById(R.id.tv_item);
        TextView content = (TextView) convertView.findViewById(R.id.tv_content);
        item.setText(node.getTitle());
        content.setText(node.getContent());
        
        ll_title.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				if (mHandler != null) {
					Message message = mHandler.obtainMessage();
					message.what = HelpActivity.MSG_UPDATE_CHILD;
					message.arg1 = group;
					message.arg2 = child;
					if (node.isExpanded()) {
						message.obj = "0";
					}
					else {
						message.obj = "1";
					}
					mHandler.sendMessage(message);
				}
			}
		});
        
        if (mChildArray.get(groupPosition).get(childPosition).isExpanded()) {
			iv.setImageResource(R.drawable.icon_down);
			content.setVisibility(View.VISIBLE);
		}
        else {
        	iv.setImageResource(R.drawable.icon_right);
        	content.setVisibility(View.GONE);
        }
        
        return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int index = groupPosition;
		final boolean expanded = isExpanded;
		if(convertView == null){
            convertView = inflater.inflate(R.layout.layout_help_main_list_item, parent, false);
        }
		
		LinearLayout ll_main = (LinearLayout)convertView.findViewById(R.id.ll_main);
        TextView group = (TextView) convertView.findViewById(R.id.tv_item);
        ImageView arrow = (ImageView) convertView.findViewById(R.id.iv_item);
        group.setText(mGroupArray.get(groupPosition));
        
        ll_main.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mHandler != null) {
					Message message = mHandler.obtainMessage();
					message.what = HelpActivity.MSG_UPDATE_GROUP;
					message.arg1 = index;
					if (expanded) {
						message.arg2 = 0;
					}
					else {
						message.arg2 = 1;
					}
					mHandler.sendMessage(message);
				}
			}
		});
        
        if (isExpanded) {
        	arrow.setImageResource(R.drawable.icon_down);     	
        }
        else {
        	arrow.setImageResource(R.drawable.icon_right);
		}
        
        return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}