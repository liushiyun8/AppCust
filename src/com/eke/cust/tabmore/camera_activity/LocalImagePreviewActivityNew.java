package com.eke.cust.tabmore.camera_activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabhouse.image_viewer_activity.ImageViewerActivity;
import com.eke.cust.tabhouse.upload_activity.LocalPhotoUtil;
import com.eke.cust.tabhouse.upload_activity.PhoteBean;
import com.eke.cust.tabhouse.upload_activity.PhotoDateAdapter;
import com.eke.cust.tabhouse.upload_activity.PhotoDateAdapterNew;
import com.eke.cust.tabmine.profile_activity.ImgClipZoomActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.droidparts.annotation.inject.InjectView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import foundation.permissions.EasyPermissions;

public class LocalImagePreviewActivityNew extends BaseActivity {
    public static final int FROM_PROFILE_HEAD_SETUP = 1;
    public static final int FROM_SELECT_SHENFENZHENG_ZHENG = 2;
    public static final int FROM_SELECT_SHENFENZHENG_FAN = 3;
    public static final int FROM_ADD_HOUSE_FNAGYUANTU = 4;
    public static final int MSG_IMG_SELECTED = 10;
    public static final int MSG_IMG_CLICKED = 11;
    private static final int SHOW_LOCAL_IMG = 2;
    @InjectView(id = R.id.lv_local)
    private PullToRefreshListView mListView_lv_local;
    private int mFromWhere = 0;
    private LocalPhotoUtil mLocalPhotoUtil;
    private List<String> mAllDateWithPhotos;//存储所有有照片的日期
    private List<String> mAllDateWithPhotosFilter;//存储所有有照片的日期
    private int totalPage = 0;
    private int currentPage = 0;
    private int ITEM_PER_PAGE = 0;
    private List<PhoteBean> list;

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0x01;
    private PhotoDateAdapterNew adapter;





    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case SHOW_LOCAL_IMG:
                        adapter = new PhotoDateAdapterNew(getApplicationContext(), list, mAllDateWithPhotosFilter, mHandler, mFromWhere);
                        mListView_lv_local.setAdapter(adapter);
                        break;
                    case MSG_IMG_SELECTED: {
                        Bundle bundle = msg.getData();
                        String img_path = bundle.getString("img_path");
                        Intent intent = new Intent(LocalImagePreviewActivityNew.this, ImgClipZoomActivity.class);
                        intent.putExtra("img_url", img_path);
                        startActivity(intent);
                        finish();
                    }
                    break;
                    case Constants.FROM_CANERA: {
                        Bundle bundle = msg.getData();
                        String img_path = bundle.getString("img_path");
                        UIHelper.startActivity(LocalImagePreviewActivityNew.this, ImageViewerActivity.class, img_path);

//                        Intent intent = new Intent(getActivity(), ImgClipZoomActivity.class);
//                        intent.putExtra("img_url", img_path);
//                        startActivity(intent);
//                        getActivity().finish();
                    }
                    case MSG_IMG_CLICKED: {
                        Bundle bundle = msg.getData();
                        String img_path = bundle.getString("img_path");
                        Intent intent = new Intent();
                        intent.putExtra("img_url", img_path);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    break;

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
                                if (request_url.equals(ServerUrl.METHOD_addFollow)) {

                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg", "出错!");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };



    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_tab_more_camera_preview_new);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLeftImageView(R.drawable.arrow_back);
        mFromWhere = getIntent().getIntExtra("from_where", 0);
//        Bundle bundle = new Bundle();
//        bundle.putInt("from_where", mFromWhere);
//        LocalImagePreviewFragment localImagePreviewFragment = new LocalImagePreviewFragment();
//        localImagePreviewFragment.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.content, localImagePreviewFragment).commit();
//        setTitle("本地图片预览");
//        if (mFromWhere == Constants.FROM_PROFILE_HEAD_SETUP) {//from profile head setup
//            setTitle("选择头像");
//
//        } else if (mFromWhere == Constants.FROM_SELECT_SHENFENZHENG_ZHENG) {
//            setTitle("选择身份证正面照片");
//        } else if (mFromWhere == Constants.FROM_SELECT_SHENFENZHENG_FAN) {
//            setTitle("选择身份证反面照片");
//        } else if (mFromWhere == Constants.FROM_ADD_HOUSE_FNAGYUANTU) {
//            setTitle("选择房源图");
//        }
        setTitle("预览");
        registerLeftImageView(R.drawable.arrow_back);

        mLocalPhotoUtil = new LocalPhotoUtil(this);
        requestReadPermissions();
        initActivity();

        mFromWhere = getIntent().getIntExtra("from_where", 0);


        if (mFromWhere == FROM_SELECT_SHENFENZHENG_ZHENG) {
            setTitle("选择身份证正面照片");
        } else if (mFromWhere == FROM_SELECT_SHENFENZHENG_FAN) {
            setTitle("选择身份证反面照片");
        } else if (mFromWhere == FROM_ADD_HOUSE_FNAGYUANTU) {
            setTitle("选择房源图");
        }
    }

    private void requestReadPermissions() {
        if (!EasyPermissions.hasPermissions(this, needPermissions)) {
            EasyPermissions.requestPermissions(this, "需要文件读取权限", REQUEST_PERMISSION_STORAGE, needPermissions);
        } else {
            mHandler.postDelayed(runnable_local_img, 100);
        }
    }


    private Runnable runnable_local_img = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            list = mLocalPhotoUtil.getPhotos();
            totalPage = mLocalPhotoUtil.getTotalPages();
            currentPage = mLocalPhotoUtil.getCurrentPage();
            ITEM_PER_PAGE = mLocalPhotoUtil.getItemsPerPage();
            mAllDateWithPhotos = mLocalPhotoUtil.getImageDateList();
            mAllDateWithPhotosFilter = mLocalPhotoUtil.getImageDateListFilter();
            mHandler.sendEmptyMessage(SHOW_LOCAL_IMG);

        }
    };

    private void initActivity() {

        mListView_lv_local.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mListView_lv_local.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
        mListView_lv_local.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
        mListView_lv_local.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");

        mListView_lv_local.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
                if (currentPage < totalPage) {
                    int addCount = 0;
                    if ((mAllDateWithPhotos.size() - currentPage * ITEM_PER_PAGE) / ITEM_PER_PAGE > 0) {
                        addCount = ITEM_PER_PAGE;
                    } else {
                        addCount = (mAllDateWithPhotos.size() - currentPage * ITEM_PER_PAGE) % ITEM_PER_PAGE;
                    }

                    if (addCount > 0) {
                        for (int i = currentPage * ITEM_PER_PAGE; i < currentPage * ITEM_PER_PAGE + addCount; i++) {
                            mAllDateWithPhotosFilter.add(mAllDateWithPhotos.get(i));
                        }
                        adapter.notifyDataSetChanged();

                        //如果加载数据的时间太短，直接调用mListView_lv_local.onRefreshComplete();则不起作用
                        mListView_lv_local.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mListView_lv_local.onRefreshComplete();
                            }
                        }, 500);

                        currentPage++;
                        mLocalPhotoUtil.setCurrentPage(currentPage);
                    } else {
                        mListView_lv_local.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mListView_lv_local.onRefreshComplete();
                            }
                        }, 500);
                    }
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mHandler.postDelayed(runnable_local_img, 100);
            } else {
                ToastUtils.show(mContext, "权限被禁止，无法选择本地图片");
            }
        }
    }
}
