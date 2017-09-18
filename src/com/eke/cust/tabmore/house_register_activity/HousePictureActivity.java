package com.eke.cust.tabmore.house_register_activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.model.ImageResource;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabmore.camera_activity.LocalImagePreviewFragment;
import com.eke.cust.tabmore.house_register_activity.house_add.HouseHistory;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.StringCheckHelper;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HousePictureActivity extends BaseActivity {
    @InjectBundleExtra(key = "data")
    private HouseHistory houseHistory;
    @InjectView(id = R.id.txt_house_name)
    private TextView mTxtName;
    @InjectView(id = R.id.gridview)
    private GridView mGridview;
    private ArrayList<ImageResource> imageResources;

    private CommonListAdapter<ImageResource> adapter;


    //region 接口调用
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
                                if (request_url
                                        .equals(ServerUrl.METHOD_initPropertyWTImg)) {
                                    StringBuilder stringBuilder = new StringBuilder();

                                    JSONObject obj_data = jsonObject.getJSONObject("data");
                                    JSONArray jsonArray = JSONUtils.getJSONArray(obj_data, "listekepic", null);

                                    String estatename = JSONUtils.getString(obj_data, "estatename", "");
                                    String buildno = JSONUtils.getString(obj_data, "buildno", "");
                                    String roomno = JSONUtils.getString(obj_data, "roomno", "");

                                    if (!StringCheckHelper.isEmpty(estatename)) {

                                        stringBuilder.append(String.format("[%s]", estatename));
                                    }
                                    if (!StringCheckHelper.isEmpty(buildno)) {
                                        stringBuilder.append(buildno + "栋");
                                    }
                                    if (!StringCheckHelper.isEmpty(roomno)) {
                                        stringBuilder.append(roomno);
                                    }
                                    mTxtName.setText(stringBuilder.toString());
                                    if (jsonArray != null) {
                                        imageResources = JSONUtils.getObjectList(jsonArray, ImageResource.class);
                                    }
                                    initPicture(imageResources);


                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg",
                                        "出错!");
                                Toast.makeText(
                                        HousePictureActivity.this
                                                .getApplicationContext(),
                                        errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(
                                    HousePictureActivity.this
                                            .getApplicationContext(),
                                    "请求出错!", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(
                                HousePictureActivity.this.getApplicationContext(),
                                "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(
                                HousePictureActivity.this.getApplicationContext(),
                                "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };

    private void initPicture(ArrayList<ImageResource> imageResources) {
        adapter = new CommonListAdapter<ImageResource>(this, R.layout.item_house_picture, imageResources) {
            @Override
            public void convert(ViewHolder holder, ImageResource imageResource, int position) {
                holder.getImageView(R.id.iv_house_image);

            }
        };

        mGridview.setAdapter(adapter);
    }
    //endregion

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_house_picture);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("房间图管理");

        String name = houseHistory.estatename;
        if (!StringCheckHelper.isEmpty(name)) {
            mTxtName.setText(String.format("[%s]", name));
        }
        registerLeftImageView(R.drawable.arrow_back);
        Bundle bundle = new Bundle();
        bundle.putInt("from_where", Constants.SHOW_HOUSE_IMG);
        bundle.putSerializable("data", houseHistory);
        LocalImagePreviewFragment localImagePreviewFragment = new LocalImagePreviewFragment();
        localImagePreviewFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.local_content, localImagePreviewFragment).commit();
        getImage();
    }

    private void getImage() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("propertyid", houseHistory.propertyid);

            ClientHelper clientHelper = new ClientHelper(this,
                    ServerUrl.METHOD_initPropertyWTImg, obj.toString(),
                    mHandler);
            clientHelper.setShowProgressMessage("正在获取数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
