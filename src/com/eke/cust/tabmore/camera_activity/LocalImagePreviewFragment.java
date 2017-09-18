package com.eke.cust.tabmore.camera_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.http.Callback;
import com.eke.cust.http.DataApi;
import com.eke.cust.http.HttpError;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabhouse.image_viewer_activity.ImageViewerActivity;
import com.eke.cust.tabhouse.upload_activity.LocalPhotoUtil;
import com.eke.cust.tabhouse.upload_activity.PhoteBean;
import com.eke.cust.tabhouse.upload_activity.PhotoDateAdapter;
import com.eke.cust.tabmine.profile_activity.ImgClipZoomActivity;
import com.eke.cust.tabmore.house_register_activity.house_add.HouseHistory;
import com.eke.cust.utils.DialogUtil;
import com.eke.cust.utils.MyLog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mylhyl.pickpicture.PickPictureCallback;
import com.mylhyl.pickpicture.PickPictureHelper;
import com.mylhyl.pickpicture.PictureTotal;

import org.droidparts.annotation.inject.InjectView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import foundation.base.fragment.BaseFragment;
import foundation.permissions.EasyPermissions;
import okhttp3.Response;

import static android.R.attr.type;
import static android.app.Activity.RESULT_OK;
import static com.eke.cust.Constants.NOT_SHOW_UPLOAD_BTN;
import static com.eke.cust.Constants.SHOW_HOUSE_IMG;
import static com.eke.cust.Constants.SHOW_LOCAL_IMG;
import static com.eke.cust.Constants.UPLOAD_FAIL;
import static com.eke.cust.Constants.UPLOAD_SUCCESS;
import static org.droidparts.Injector.getApplicationContext;

public class LocalImagePreviewFragment extends BaseFragment implements View.OnClickListener {
    @InjectView(id = R.id.lv_local)
    private PullToRefreshListView mListView_lv_local;

    @InjectView(id = R.id.btn_action, click = true)
    private Button mBtAction;

    private LocalPhotoUtil mLocalPhotoUtil;
    private List<String> mAllDateWithPhotos;//存储所有有照片的日期
    private List<String> mAllDateWithPhotosFilter;//存储所有有照片的日期
    private int totalPage = 0;
    private int currentPage = 0;
    private int ITEM_PER_PAGE = 0;

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;

    private List<PhoteBean> list;
    private PhotoDateAdapter adapter;

    private int mFromWhere = 0;
    private HouseHistory houseHistory;
    private String upload_imgpath;
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private DialogUtil mDialogUtil;

    public static final int REQUEST_CODE_SELECT_PICTURE = 102;
    public static final int REQUEST_CODE_SELECT_ALBUM = 104;
    public static final String EXTRA_PICTURE_PATH = "picture_path";

    private ProgressDialog mProgressDialog;
    private ListView mListView;
    private PickPictureHelper pickPictureHelper;

    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {
                    case SHOW_LOCAL_IMG:
                        adapter = new PhotoDateAdapter(getActivity(), list, mAllDateWithPhotosFilter, mHandler, mFromWhere);
                        mListView_lv_local.setAdapter(adapter);
                        break;

                    case Constants.MSG_IMG_SELECTED: {
                        Bundle bundle = msg.getData();
                        String img_path = bundle.getString("img_path");
                        Intent intent = new Intent(getActivity(), ImgClipZoomActivity.class);
                        intent.putExtra("img_url", img_path);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    case Constants.FROM_CANERA: {
                        Bundle bundle = msg.getData();
                        String img_path = bundle.getString("img_path");
                        UIHelper.startActivity(getActivity(), ImageViewerActivity.class, img_path);

//                        Intent intent = new Intent(getActivity(), ImgClipZoomActivity.class);
//                        intent.putExtra("img_url", img_path);
//                        startActivity(intent);
//                        getActivity().finish();
                    }
                    break;

                    case Constants.MSG_IMG_CLICKED: {
                        Bundle bundle = msg.getData();
                        String img_path = bundle.getString("img_path");
                        Intent intent = new Intent();
                        intent.putExtra("img_url", img_path);
                        getActivity().setResult(RESULT_OK, intent);
                        getActivity().finish();
                    }
                    break;
                    case Constants.SHOW_UPLOAD_BTN: {
                        Bundle bundle = msg.getData();
                        upload_imgpath = bundle.getString("img_path");
                        final String img_name = bundle.getString("img_name");

                        MyLog.d(TAG, "img: " + upload_imgpath + ", " + img_name);
                        mBtAction.setVisibility(View.VISIBLE);

                    }
                    break;
                    case NOT_SHOW_UPLOAD_BTN: {
                        Bundle bundle = msg.getData();
                        String img_path = bundle.getString("img_path");
                        mBtAction.setVisibility(View.GONE);
                    }
                    break;
                    case UPLOAD_SUCCESS: {
                        mDialogUtil.dissmissProgressDialog();
                        Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();

                    }
                    break;

                    case UPLOAD_FAIL: {
                        mDialogUtil.dissmissProgressDialog();
                        Toast.makeText(getApplicationContext(), "上传失败!", Toast.LENGTH_SHORT).show();
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
        return inflateContentView(R.layout.fragment_localimage);
    }

    private void requestReadPermissions() {
        if (!EasyPermissions.hasPermissions(getActivity(), needPermissions)) {
            EasyPermissions.requestPermissions(this, "需要文件读取权限", REQUEST_PERMISSION_STORAGE, needPermissions);
        } else {
           // getImage();
            mHandler.postDelayed(runnable_local_img, 100);
        }
    }



    private void  getImage(){
        pickPictureHelper = PickPictureHelper.readPicture(getActivity(), new PickPictureCallback() {
            @Override
            public void onStart() {
                MyLog.d(TAG,"开始");
                //显示进度条
                showLoading();
            }

            @Override
            public void onSuccess(List<PictureTotal> list) {
                for (int i = 0; i <list.size() ; i++) {

                }
                MyLog.d(TAG,"加载完成");

                ToastUtils.show(mContext,"加载完成");
                hideLoading();
            }

            @Override
            public void onError() {
                MyLog.d(TAG,"报错");

                ToastUtils.show(mContext,"报错");
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDialogUtil = new DialogUtil(getActivity());

        initActivity();
        if (getArguments() != null) {
            mFromWhere = getArguments().getInt("from_where", 0);
            if (mFromWhere == Constants.SHOW_HOUSE_IMG) {
                houseHistory = (HouseHistory) getArguments().getSerializable("data");
            }
            mLocalPhotoUtil = new LocalPhotoUtil(getActivity());
            requestReadPermissions();
        }

    }


    private void initActivity() {

        mListView_lv_local.setMode(Mode.PULL_FROM_END);
        mListView_lv_local.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载");
        mListView_lv_local.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
        mListView_lv_local.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");

        mListView_lv_local.setOnRefreshListener(new OnRefreshListener2<ListView>() {

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
        mListView_lv_local.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mFromWhere == SHOW_HOUSE_IMG) {
                    ToastUtils.show(getActivity(), "长按");

                }
                return false;
            }
        });

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


    @Override
    public void onClick(View view) {
        if (view == mBtAction) {
            upload();

        }
    }

    private void upload() {
        mDialogUtil.setProgressMessage("正在上传...");
        mDialogUtil.showProgressDialog();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String upload_path = ServerUrl.METHOD_image_upload +
                        "foreignId=" + houseHistory.propertyid + "&type=" + type + "&token=" + AppContext
                        .getInstance().getAppPref().userToken();
                MyLog.d(TAG, "upload: " + upload_path);

                DataApi.uploadFile(new File(upload_imgpath), upload_path, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        if (null != s) {
                            mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
                        } else {
                            mHandler.sendEmptyMessage(UPLOAD_FAIL);
                        }
                    }

                    @Override
                    public void failure(HttpError error) {
                        mHandler.sendEmptyMessage(UPLOAD_FAIL);
                    }
                });
            }
        }).start();
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mHandler.postDelayed(runnable_local_img, 100);
            } else {
                ToastUtils.show(mContext, "权限被禁止，无法选择本地图片");
            }
        }
    }

}
