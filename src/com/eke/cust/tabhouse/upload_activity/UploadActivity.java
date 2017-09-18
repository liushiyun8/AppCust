package com.eke.cust.tabhouse.upload_activity;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.http.Callback;
import com.eke.cust.http.DataApi;
import com.eke.cust.http.HttpError;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.DialogUtil;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.StringCheckHelper;
import com.eke.cust.widget.NoScrollGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

import static com.eke.cust.Constants.NOT_SHOW_UPLOAD_BTN;
import static com.eke.cust.Constants.SHOW_LOCAL_IMG;
import static com.eke.cust.Constants.UPLOAD_FAIL;
import static com.eke.cust.Constants.UPLOAD_SUCCESS;
import static com.eke.cust.net.ServerUrl.METHOD_addDataPicBase;

public class UploadActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "UploadActivity";

    private RelativeLayout mRelativeLayout_rl_header;
    private ImageView mImageView_iv_back;
    private TextView mTextView_tv_title;
    private RelativeLayout mRelativeLayout_rl_menu;
    private Button mButton_btn_house_source;
    private View mView_view_line;
    private Button mButton_btn_hetong;
    private LinearLayout mLinearLayout_ll_upload;
    private ImageView mImageView_iv_cloud;
    private TextView mTextView_tv_name;
    private NoScrollGridView mGridView_gridview_house;
    private ImageView mImageView_iv_local;
    private TextView mTextView_tv_name_local;
    private PullToRefreshListView mListView_lv_local;
    private Button mButton_btn_action;

    private LocalPhotoUtil mLocalPhotoUtil;
    private List<String> mAllDateWithPhotos;//存储所有有照片的日期
    private List<String> mAllDateWithPhotosFilter;//存储所有有照片的日期
    private int totalPage = 0;
    private int currentPage = 0;
    private int ITEM_PER_PAGE = 0;

    private int uploadWhat = 0;

    private ImageLoader imageLoader;

//	private static final int ITEM_PER_PAGE = 7;
//	
//	private static final String[]STORE_IMAGES={
//		MediaStore.Images.Media.DISPLAY_NAME,
//		MediaStore.Images.Media.LATITUDE,
//		MediaStore.Images.Media.LONGITUDE,
//		MediaStore.Images.Media._ID,
//		MediaStore.Images.Media.BUCKET_ID,
//		MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//		MediaStore.Images.Media.DATE_TAKEN,
//		MediaStore.Images.Media.DATA,
//	};

    private List<PhoteBean> list;
    private List<PhoteBean> listServer = new ArrayList<PhoteBean>();
    private PhotoDateAdapter adapter;
    private ServerImageItemAdapter adapter_server;

    private String mPropertyid = "0206031025320225BC71C2B754E67659";
    private DialogUtil mDialogUtil;

    private String estateid = "";
    private String foreignId = "";
    private String type = "";

    private String del_picid;
    private String upload_imgpath;
    private boolean mIsUpload = false;//false-delete, ture-upload

    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {
                    case SHOW_LOCAL_IMG: {
                        adapter = new PhotoDateAdapter(getApplicationContext(), list, mAllDateWithPhotosFilter, mHandler, 0);
                        mListView_lv_local.setAdapter(adapter);
                        findViewById(R.id.btn_house_source).performClick();
                    }
                    break;

                    case Constants.SHOW_UPLOAD_BTN: {
                        Bundle bundle = msg.getData();
                        upload_imgpath = bundle.getString("img_path");
                        final String img_name = bundle.getString("img_name");
                        if (BuildConfig.DEBUG) {
                            MyLog.d(TAG, "img: " + upload_imgpath + ", " + img_name);
                        }
                        mButton_btn_action.setVisibility(View.VISIBLE);
                        mButton_btn_action.setText("确认上传");
                        mIsUpload = true;


                        for (int i = 0; i < listServer.size(); i++) {
                            listServer.get(i).setSelect(false);
                        }
                        adapter_server.notifyDataSetChanged();
                    }
                    break;

                    case NOT_SHOW_UPLOAD_BTN: {
                        Bundle bundle = msg.getData();
                        String img_path = bundle.getString("img_path");
                        mButton_btn_action.setVisibility(View.GONE);
                    }
                    break;

                    case Constants.SHOW_BIG_IMG: {
                        Bundle bundle = msg.getData();
                        String img_path = bundle.getString("img_path");
                        showBigImgDlg(img_path);
                    }
                    break;

                    case UPLOAD_SUCCESS: {
                        mDialogUtil.dissmissProgressDialog();
                        Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();

                        getServerImgs();
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
                                if (request_url.equals(ServerUrl.METHOD_initPropertyWTImg)) {
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
                                    mTextView_tv_name.setText(stringBuilder.toString());
                                    listServer.clear();
                                    if (jsonArray != null) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject obj = jsonArray.getJSONObject(i);
                                            if (obj != null) {
                                                String filePath = obj.optString("filePath");
                                                PhoteBean photo = new PhoteBean();
                                                photo.setImagePath(filePath);
                                                photo.pictype=obj.optString("pictype");
                                                photo.setImageid(obj.optString("picid"));
                                                listServer.add(photo);
                                            }
                                        }
                                        adapter_server.notifyDataSetChanged();
                                    }

                                } else if (request_url.equals(ServerUrl.METHOD_deletePicByForgin)) {
                                    mButton_btn_action.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "删除成功!", Toast.LENGTH_SHORT).show();
                                    getServerImgs();
                                } else if (request_url.equals(ServerUrl.METHOD_upload_daili_hetong)) {
                                    mDialogUtil.dissmissProgressDialog();
                                    Toast.makeText(getApplicationContext(), "上传合同成功!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else if(request_url.equals(ServerUrl.METHOD_addDataPicBase)){
                                    mDialogUtil.dissmissProgressDialog();
                                    getServerImgs();
                                    Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();

                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg", "出错!");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                                mDialogUtil.dissmissProgressDialog();
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
        return inflateContentView(R.layout.activity_tab_house_upload);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("图片管理");
        registerLeftImageView(R.drawable.arrow_back);
        mDialogUtil = new DialogUtil(UploadActivity.this);

        mLocalPhotoUtil = new LocalPhotoUtil(UploadActivity.this);

        estateid = getIntent().getStringExtra("estateid");
        foreignId = getIntent().getStringExtra("foreignId");
        type = getIntent().getStringExtra("type");
        initActivity();

        imageLoader = ImageLoader.getInstance();

        mPropertyid = getIntent().getStringExtra("propertyid");
        if (type.equals("deallist")) {
            uploadWhat = 1;
            mRelativeLayout_rl_menu.setVisibility(View.GONE);
            mLinearLayout_ll_upload.setVisibility(View.VISIBLE);
            list = mLocalPhotoUtil.getPhotos();
            totalPage = mLocalPhotoUtil.getTotalPages();
            currentPage = mLocalPhotoUtil.getCurrentPage();
            ITEM_PER_PAGE = mLocalPhotoUtil.getItemsPerPage();
            mAllDateWithPhotos = mLocalPhotoUtil.getImageDateList();
            mAllDateWithPhotosFilter = mLocalPhotoUtil.getImageDateListFilter();
            adapter = new PhotoDateAdapter(getApplicationContext(), list, mAllDateWithPhotosFilter, mHandler, 0);
            mListView_lv_local.setAdapter(adapter);
        } else {
            setTitle("房间图管理");
            if (mPropertyid != null && !mPropertyid.equals("")) {
                getServerImgs();
                mHandler.postDelayed(runnable_local_img, 500);

            } else {

                Toast.makeText(getApplicationContext(), "出错!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private void initActivity() {
        mRelativeLayout_rl_header = (RelativeLayout) findViewById(R.id.rl_header);
        mImageView_iv_back = (ImageView) findViewById(R.id.iv_back);
        mTextView_tv_title = (TextView) findViewById(R.id.tv_title);
        mRelativeLayout_rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
        mButton_btn_house_source = (Button) findViewById(R.id.btn_house_source);
        mView_view_line = (View) findViewById(R.id.view_line);
        mButton_btn_hetong = (Button) findViewById(R.id.btn_hetong);
        mLinearLayout_ll_upload = (LinearLayout) findViewById(R.id.ll_upload);
        mImageView_iv_cloud = (ImageView) findViewById(R.id.iv_cloud);
        mTextView_tv_name = (TextView) findViewById(R.id.tv_name);
        mGridView_gridview_house = (NoScrollGridView) findViewById(R.id.gridview_house);
        mImageView_iv_local = (ImageView) findViewById(R.id.iv_local);
        mTextView_tv_name_local = (TextView) findViewById(R.id.tv_name_local);
        mListView_lv_local = (PullToRefreshListView) findViewById(R.id.lv_local);
        mButton_btn_action = (Button) findViewById(R.id.btn_action);


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

        mButton_btn_action.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mIsUpload && upload_imgpath != null) {
                    mDialogUtil.setProgressMessage("正在上传...");
                    mDialogUtil.showProgressDialog();
                    if (type.equals("deallist")) {
                        uploadDeallistHetong(upload_imgpath);
                    } else if (type.equals("house_history")) {
                        addDataPic(upload_imgpath);
                    } else {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                String upload_path = ServerUrl.METHOD_image_upload +
                                        "foreignId=" + foreignId + "&type=" + type + "&token=" + AppContext
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
                } else {
                    if (del_picid != null) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("picid", del_picid);

                            ClientHelper clientHelper = new ClientHelper(UploadActivity.this,
                                    ServerUrl.METHOD_deletePicByForgin, obj.toString(), mHandler);
                            clientHelper.setShowProgressMessage("正在删除...");
                            clientHelper.isShowProgress(true);
                            clientHelper.sendPost(true);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        mButton_btn_house_source.setOnClickListener(this);
        mButton_btn_hetong.setOnClickListener(this);

        listServer.clear();
//		PhoteBean photo1 = new PhoteBean();
//		photo1.setImageId(R.drawable.server_image);
//		listServer.add(photo1);
//		
//		PhoteBean photo2 = new PhoteBean();
//		photo2.setImageId(R.drawable.server_image);
//		listServer.add(photo2);

        adapter_server = new ServerImageItemAdapter(listServer, getApplicationContext(),mGridView_gridview_house);
        mGridView_gridview_house.setAdapter(adapter_server);
        mGridView_gridview_house.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // TODO Auto-generated method stub
                for (int i = 0; i < listServer.size(); i++) {
                    if (!listServer.get(i).getImagePath().equals(listServer.get(position).getImagePath())) {
                        listServer.get(i).setSelect(false);
                    }
                }

                if (listServer.get(position).isSelect()) {
                    listServer.get(position).setSelect(false);
                    mButton_btn_action.setVisibility(View.GONE);
                    del_picid = null;
                } else {
                    listServer.get(position).setSelect(true);
                    mButton_btn_action.setVisibility(View.VISIBLE);
                    mButton_btn_action.setText("确认删除");

                    mIsUpload = false;
                    del_picid = listServer.get(position).getImageid();

                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setSelect(false);
                    }
                    adapter_server.notifyDataSetChanged();
                }

                adapter_server.refreshItem(position);

                return false;
            }
        });
    }

    private void uploadDeallistHetong(String filePath) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("filePath", Base64.encodeToString(BitmapUtils.getBitmapByte(filePath), Base64.DEFAULT));
            obj.put("uploadertype", "助理");
            obj.put("foreignId", foreignId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ClientHelper clientHelper = new ClientHelper(this,
                ServerUrl.METHOD_upload_daili_hetong, obj.toString(), mHandler);
        clientHelper.setShowProgressMessage("正在提交数据...");
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }

    //上传房间图
    private void addDataPic(String filePath) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("filePath", Base64.encodeToString(BitmapUtils.getBitmapByte(filePath), Base64.DEFAULT));
            obj.put("foreignId", foreignId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ClientHelper clientHelper = new ClientHelper(this,
                METHOD_addDataPicBase, obj.toString(), mHandler);
        clientHelper.setShowProgressMessage("正在上传...");
        clientHelper.isShowProgress(true);
        clientHelper.sendPost(true);
    }

    private void getServerImgs() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("propertyid", mPropertyid);
            ClientHelper clientHelper = new ClientHelper(UploadActivity.this,
                    ServerUrl.METHOD_initPropertyWTImg, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在获取数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    private void showBigImgDlg(String img_path) {
        final Dialog dlg = new Dialog(this, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewContent = inflater.inflate(R.layout.dlg_show_big_img, null);

        final ImageView iv_img = (ImageView) viewContent.findViewById(R.id.iv_img);
        imageLoader.displayImage("file://" + img_path, iv_img, AppContext.mDisplayImageOptions_no_round_corner,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // TODO Auto-generated method stub

                        if (loadedImage != null) {
                            int bmp_width = loadedImage.getWidth();
                            int bmp_height = loadedImage.getHeight();

                            DisplayMetrics dm = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(dm);
                            int screen_width = dm.widthPixels;

                            Matrix matrix = new Matrix();
                            float scale = (float) screen_width / (float) bmp_width;
                            matrix.postScale(scale, scale);
                            MyLog.d(TAG, "width: " + bmp_width + ", height: " + bmp_height + ", scale: " + scale);
                            Bitmap resizeBmp = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, true);
                            iv_img.setImageBitmap(resizeBmp);
                            resizeBmp = null;
                        }

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        // TODO Auto-generated method stub

                    }
                });

        iv_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
            }
        });

        final RelativeLayout rl_main = (RelativeLayout) viewContent.findViewById(R.id.rl_main);
        rl_main.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(UploadActivity.this, 20);
        lp.height = dm.heightPixels - DensityUtil.dip2px(UploadActivity.this, 20);
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
        dlg.show();
    }

//	private List<PhoteBean> getPhotos(){
//		List<PhoteBean>list = new ArrayList<PhoteBean>();
//		Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
//		//Map<String, PhoteBean>countMap = new HashMap<>();
//		PhoteBean photo =null;
//		while (cursor.moveToNext()) {
//			String id = cursor.getString(3);
//			String displayname = cursor.getString(0);
//			//String imgid =cursor.getString(6);
//			photo = new PhoteBean();
//			photo.setDisplayname(displayname);
//			photo.setId(id);
//			//photo.setImageid(imgid);
//			
//			photo.setDate(DateUtil.getDateToString1(cursor.getLong(6)));
//			photo.setDateLong(cursor.getLong(6));
//			photo.setImagePath(cursor.getString(7));
//			
//			list.add(photo);
//		}
//		cursor.close();
//		
//		Collections.sort(list, new PhotoComparator());
//		
//		mAllDateWithPhotos.clear();
//		for (int i = 0; i < list.size(); i++) {
//			boolean isDateExist = false;
//			for (int j = 0; j < mAllDateWithPhotos.size(); j++) {
//				if (list.get(i).getDate().equals(mAllDateWithPhotos.get(j))) {
//					isDateExist = true;
//					break;
//				}
//			}
//			
//			if (!isDateExist) {
//				mAllDateWithPhotos.add(list.get(i).getDate());
//			}
//		}
//		
//		for (int i = 0; i < mAllDateWithPhotos.size(); i++) {
//			Log.d(TAG, i + ": " + mAllDateWithPhotos.get(i));
//		}
//		
//		totalPage = mAllDateWithPhotos.size()/ITEM_PER_PAGE + ((mAllDateWithPhotos.size()%ITEM_PER_PAGE > 0)?1:0);
//		if (totalPage > 0) {
//			currentPage = 1;
//		}
//		
//		mAllDateWithPhotosFilter.clear();
//		if (totalPage > 0) {
//			int addCount = 0;
//			if (mAllDateWithPhotos.size() >= ITEM_PER_PAGE) {
//				addCount = ITEM_PER_PAGE;
//			}
//			else {
//				addCount = mAllDateWithPhotos.size()%ITEM_PER_PAGE;
//			}
//			
//			for (int i = 0; i < addCount; i++) {
//				mAllDateWithPhotosFilter.add(mAllDateWithPhotos.get(i));
//			}
//		}
//		
//		return list;
//	}
//
//	private class PhotoComparator implements Comparator<PhoteBean> {
//
//		@Override
//		public int compare(PhoteBean lhs, PhoteBean rhs) {
//			// TODO Auto-generated method stub
//			long l = lhs.getDateLong();
//			long r = rhs.getDateLong();
//			if (l > r) {
//				return -1;
//			}
//			
//			if (l < r) {
//				return 1;
//			}
//			
//			return 0;
//		}
//		
//	}

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_house_source: {
                uploadWhat = 0;
                mRelativeLayout_rl_menu.setVisibility(View.GONE);
                mLinearLayout_ll_upload.setVisibility(View.VISIBLE);
            }
            break;

            case R.id.btn_hetong: {
                uploadWhat = 1;
                mRelativeLayout_rl_menu.setVisibility(View.GONE);
                mLinearLayout_ll_upload.setVisibility(View.VISIBLE);
            }
            break;

            default:
                break;
        }
    }
}
