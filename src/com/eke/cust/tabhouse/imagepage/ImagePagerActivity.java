package com.eke.cust.tabhouse.imagepage;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.global.GlobalErrorReport;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.model.HouseSource;
import com.eke.cust.model.ImageResource;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.widget.HackyViewPager;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 图片预览
 *
 * @author WJ
 */
public class ImagePagerActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    @InjectView(id = R.id.pager)
    private HackyViewPager mPager;
    @InjectView(id = R.id.txt_point)
    private TextView mTxtPoint;

    @InjectView(id = R.id.ll_bottom)
    private RadioGroup mRadioGroup;
    @InjectBundleExtra(key = "data")
    private HouseSource houseSource;
    private ImagePagerAdapter mAdapter;
    private int mErrorType = GlobalErrorReport.ERROR_TYPE_PROPERTYPIC;

    private ArrayList<ImageResource> RopertyResources = new ArrayList<ImageResource>();
    private ArrayList<ImageResource> PropertyResources = new ArrayList<ImageResource>();
    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {
                    case Constants.NO_NETWORK:
                        break;
                    case Constants.TAG_SUCCESS:
                        Bundle bundle = msg.getData();
                        String request_url = bundle.getString("request_url");
                        String resp = bundle.getString("resp");
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            String result = jsonObject.optString("result", "");
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url.equals(ServerUrl.METHOD_getListPicForeignId)) {
                                    JSONObject obj_data = jsonObject.optJSONObject("data");
                                    if (obj_data != null) {
                                        JSONArray array_data = obj_data.optJSONArray("list");
                                        if (array_data != null) {
                                            ArrayList<ImageResource> imageResources = JSONUtils.getObjectList(array_data, ImageResource.class);
                                            for (int i = 0; i < imageResources.size(); i++) {
                                                ImageResource imageResource = imageResources.get(i);
                                                if (imageResource.pictype.equals("property")) {
                                                    PropertyResources.add(imageResource);
                                                } else {
                                                    RopertyResources.add(imageResource);
                                                }
                                            }
                                            initAdapter(PropertyResources);
                                            mErrorType = GlobalErrorReport.ERROR_TYPE_PROPERTY;

                                        }


                                    }


                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg", "出错!");
                                ToastUtils.show(mContext, errorMsg);

                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            ToastUtils.show(mContext, R.string.http_json_error);
                        }
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
        return inflateContentView(R.layout.image_detail_pager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRadioGroup.setOnCheckedChangeListener(this);
        findViewById(R.id.iv_bar_back).setOnClickListener(this);
        findViewById(R.id.iv_jiucuo).setOnClickListener(this);
        getListPicForeignId();
    }

    private void initAdapter(ArrayList<ImageResource> imageResources) {
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageResources);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(0, false);
        // 更新下标
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int index) {
                int size = mAdapter.getCount();
                String point = String.format("%s/%s", size > 0 ? index + 1 : 0, mAdapter.getCount());
                mTxtPoint.setText(point);
            }

        });
        int size = mAdapter.getCount();
        String point = String.format("%s/%s", size > 0 ? 1 : 0, size);
        mTxtPoint.setText(point);
    }

    private void getListPicForeignId() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("propertyid", houseSource.propertyid);
            ClientHelper clientHelper = new ClientHelper(mContext,
                    ServerUrl.METHOD_getListPicForeignId, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在获取数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bar_back:
                finish();
                break;
            case R.id.iv_jiucuo:
                new GlobalErrorReport(this, ImagePagerActivity.this, mHandler, houseSource.propertyid, mErrorType).showGlobalErrorReportDlg(GlobalErrorReport.ERROR_TYPE_PROPERTYPIC);

                break;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_gonggong) {
            initAdapter(RopertyResources);
        } else if (checkedId == R.id.radio_wuye) {
            initAdapter(PropertyResources);

        }

    }

    private static class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<ImageResource> imageResources;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<ImageResource> imageResources) {
            super(fm);
            this.imageResources = imageResources;
        }

        @Override
        public int getCount() {
            return imageResources == null ? 0 : imageResources.size();
        }

        @Override
        public Fragment getItem(int position) {
            ImageResource imageResource = imageResources.get(position);
            return ImageDetailFragment.newInstance(imageResource);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}