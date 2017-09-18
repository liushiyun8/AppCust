package com.eke.cust.tabhouse;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.SearchHouse;
import com.eke.cust.widget.rangeseekbar.RangeSeekBar;

import org.droidparts.annotation.inject.InjectResource;
import org.droidparts.annotation.inject.InjectView;

import java.util.ArrayList;

/**
 * 条件查找
 */
public class FindConditionsActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    //租售房
    @InjectView(id = R.id.radio_group)
    private RadioGroup mRadioGroup;
    @InjectView(id = R.id.second_hand)
    private RadioButton mRadioSenondHand;
    @InjectView(id = R.id.resent)
    private RadioButton mRadioResent;
    @InjectView(id = R.id.txt_price)
    private TextView mTxtPrice;
    @InjectView(id = R.id.range_price)
    private RangeSeekBar mRangePrice;
    @InjectView(id = R.id.range_area)
    private RangeSeekBar mRangeArea;
    @InjectView(id = R.id.bt_ok, click = true)
    private Button mBtOk;
    @InjectView(id = R.id.iv_minus, click = true)
    private ImageView mIvMinus;
    @InjectView(id = R.id.iv_add, click = true)
    private ImageView mIvAdd;
    @InjectView(id = R.id.txt_number)
    private TextView mTxtNumber;

    @InjectResource(value = R.array.item_house_room)
    private String[] item_house;
    private int current = 0;
    //房屋搜索条件
    private SearchHouse searchHouse;
    //查找条件
    private ArrayList<String> arrayList = new ArrayList<String>();


    //户型
    public static final int CHANGE_ROOM = 0x05;//

    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {
                    case CHANGE_ROOM:
                        if (current != 0) {
                            mTxtNumber.setTextColor(Color.parseColor("#ff0000"));
                        } else {
                            mTxtNumber.setTextColor(Color.parseColor("#CCCCCC"));
                        }
                        mTxtNumber.setText(item_house[current]);
                        arrayList.set(1, item_house[current]);
                        break;
                    case Constants.NO_NETWORK:
                        break;
                    case Constants.TAG_SUCCESS:
                        break;
                    case Constants.TAG_FAIL:
                        ToastUtils.show(mContext, R.string.http_error);
                        break;
                    case Constants.TAG_EXCEPTION:
                        ToastUtils.show(mContext, R.string.http_error);
                        break;
                }

            }
        }
    };

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_find_conditions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("条件查找");
        registerLeftImageView(R.drawable.arrow_back);
        registerRightTextView("复位");
        mRadioGroup.setOnCheckedChangeListener(this);


        initData();

    }

    private void initData() {
        arrayList.add("出售");
        arrayList.add("全部");
        arrayList.add("0-1200万");
        arrayList.add("0-300平方");
        mRadioGroup.check(R.id.second_hand);
        mRangePrice.setSelectedMaxValue(1010);
        mRangePrice.setSelectedMinValue(0);
        mRangeArea.setSelectedMaxValue(210);
        mRangeArea.setSelectedMinValue(0);

        mTxtNumber.setTextColor(Color.parseColor("#CCCCCC"));
        current = 0;
        mTxtNumber.setText(item_house[current]);
        initSearch();
    }

    @Override
    protected void goNext() {
        super.goNext();
        initData();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.second_hand) {
            mTxtPrice.setText("售价(万元)");
            arrayList.set(0, "出售");
        } else if (checkedId == R.id.resent) {
            mTxtPrice.setText("租金(元)");
            arrayList.set(0, "出租");

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_minus:
                if (current > 0) {
                    current--;
                    mHandler.sendEmptyMessage(CHANGE_ROOM);
                }
                break;
            case R.id.iv_add:
                if (current < item_house.length - 1) {
                    current++;
                    mHandler.sendEmptyMessage(CHANGE_ROOM);
                }
                break;
            case R.id.bt_ok:
                UIHelper.startActivity(this, FindHouseResultActivity.class, buildCondition(), searchHouse);
                break;
        }

    }

    private String buildCondition() {
        if (arrayList.get(0).equals("出租")) {
            arrayList.set(2, String.format("(%s)元/月", mRangePrice.getSelectedMinValue() + "-" + mRangePrice.getSelectedMaxValue()));

        }else{
            arrayList.set(2, String.format("(%s)万", mRangePrice.getSelectedMinValue() + "-" + mRangePrice.getSelectedMaxValue()));

        }
        arrayList.set(3, String.format("(%s)平方", mRangeArea.getSelectedMinValue() + "-" + mRangeArea.getSelectedMaxValue()));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("条件:");
        for (String s : arrayList) {
            stringBuilder.append(s + "+");
        }
        String title = stringBuilder.toString();
        initSearch();
        return title.substring(0, title.lastIndexOf("+"));

    }

    private void initSearch() {
        searchHouse = new SearchHouse();
        searchHouse.trade = arrayList.get(0);
        if (searchHouse.trade.equals("出租")) {
            searchHouse.minrentprice = mRangePrice.getSelectedMinValue().toString();
            searchHouse.maxrentprice = mRangePrice.getSelectedMaxValue().toString();
        } else {
            searchHouse.minprice = mRangePrice.getSelectedMinValue().toString();
            searchHouse.maxprice = mRangePrice.getSelectedMaxValue().toString();
        }
        searchHouse.countf =item_house[current];
        searchHouse.maxsquare = mRangeArea.getSelectedMaxValue().toString();
        searchHouse.minsquare = mRangeArea.getSelectedMinValue().toString();
    }
}
