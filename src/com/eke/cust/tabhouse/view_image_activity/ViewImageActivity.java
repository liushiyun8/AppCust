package com.eke.cust.tabhouse.view_image_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.chat.widget.photoview.EasePhotoView;
import com.eke.cust.global.GlobalErrorReport;
import com.eke.cust.model.HouseSource;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DialogUtil;
import com.eke.cust.utils.MyLog;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.eke.cust.global.GlobalErrorReport.ERROR_TYPE_PROPERTYPIC;

public class ViewImageActivity extends BaseActivity implements OnClickListener {
    @InjectBundleExtra(key = "data")
    private HouseSource houseSource;
    private static final int UPLOAD_SUCCESS = 10;
    private static final int UPLOAD_FAIL = 11;
    public static final int MSG_SHOW_SET_BTN = 12;

    private TextView mTextView_tv_gonggong;
    private TextView mTextView_tv_wuye;
    private ViewPager mViewPager_vPager_gonggong;
    private ViewPager mViewPager_vPager_wuye;
    private ImageView mImageView_tv_jiucuo;
    private TextView mTextView_tv_count;
    private EasePhotoView mPhotoView_iv_photo_gongogn_one_img;
    private TextView mTextView_tv_set;

    private EdgeEffectCompat rightEdgeGonggong;
    private EdgeEffectCompat leftEdgeWuye;
    private int gongongSelectIndex = 1;
    private int wuyeSelectIndex = 1;

    private List<ViewImageNode> mListGonggongNodes = new ArrayList<ViewImageNode>();
    private List<ViewImageNode> mListWuyeNodes = new ArrayList<ViewImageNode>();

    private List<View> mListGonggong = new ArrayList<View>();
    private List<View> mListWuye = new ArrayList<View>();
    private ViewPagerAdapter mViewPagerAdapter_gonggong = null;
    private ViewPagerAdapter mViewPagerAdapter_wuye = null;

    private int selectedImageSet = 0;
    private int mErrorType = ERROR_TYPE_PROPERTYPIC;

    private float downPos = 0.0f;
    private boolean mIsOnlyOneGonggong = false;
    private boolean mIsOnlyOneWuye = false;
    private boolean mIsWuyeSelected = false;
    private boolean mIsFromWuye = false;
    private DialogUtil mDialogUtil;

    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {
                    case UPLOAD_SUCCESS: {
                        mDialogUtil.dissmissProgressDialog();
                        Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
                        mTextView_tv_set.setVisibility(View.GONE);
                    }
                    break;

                    case UPLOAD_FAIL: {
                        mDialogUtil.dissmissProgressDialog();
                        Toast.makeText(getApplicationContext(), "上传失败!", Toast.LENGTH_SHORT).show();
                        mTextView_tv_set.setVisibility(View.GONE);
                    }
                    break;


                    case Constants.NO_NETWORK:
                        break;

                    case Constants.TAG_SUCCESS:
                        mDialogUtil.showProgressDialog();
                        mListGonggongNodes.clear();
                        mListWuyeNodes.clear();
                        mListGonggong.clear();
                        mListWuye.clear();

                        Bundle bundle = msg.getData();
                        String request_url = bundle.getString("request_url");
                        String resp = bundle.getString("resp");
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            String result = jsonObject.optString("result", "");
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url.equals(ServerUrl.METHOD_getListPicForeignId)) {
                                    JSONArray array_data = jsonObject.optJSONArray("data");
                                    if (array_data != null) {
                                        for (int i = 0; i < array_data.length(); i++) {
                                            JSONObject obj = array_data.getJSONObject(i);
                                            if (obj != null) {
                                                String filePath = obj.optString("filePath");
                                                String pictype = obj.optString("pictype");
                                                String picid = obj.optString("picid");

                                                ViewImageNode node = new ViewImageNode(ViewImageActivity.this, mHandler, filePath, picid);
                                                if (pictype.equals("property")) {//房源物业
                                                    mListWuyeNodes.add(node);
//												mListWuye.add(node.genView(imageLoader, filePath));

                                                } else if (pictype.equals("estate")) {//小区公共
                                                    mListGonggongNodes.add(node);
//												mListGonggong.add(node.genView(imageLoader, filePath));
                                                }
                                            }
                                        }
                                    }

                                    if (mListGonggong.size() == 1) {
                                        mListGonggongNodes.add(new ViewImageNode(ViewImageActivity.this, mHandler, "assets://dummy.png", "mydummy"));
                                        mIsOnlyOneGonggong = true;
                                    }

                                    if (mListWuye.size() == 1) {
                                        mListWuyeNodes.add(new ViewImageNode(ViewImageActivity.this, mHandler, "assets://dummy.png", "mydummy"));
                                        mIsOnlyOneWuye = true;
                                    }

                                    mViewPagerAdapter_gonggong = new ViewPagerAdapter(mListGonggongNodes, mViewPager_vPager_gonggong, ViewImageActivity.this, mHandler);
                                    mViewPager_vPager_gonggong.setAdapter(mViewPagerAdapter_gonggong);
                                    mViewPager_vPager_gonggong.setOnPageChangeListener(new OnPageChangeListener() {

                                        @Override
                                        public void onPageSelected(int arg0) {
                                            // TODO Auto-generated method stub
                                            MyLog.d("--", "onPageSelected--> ");
                                            mTextView_tv_set.setVisibility(View.GONE);

                                            if (selectedImageSet == 0) {
                                                if (mIsOnlyOneGonggong) {
                                                    gongongSelectIndex = 1;
                                                    mTextView_tv_count.setText("1/1");
                                                } else {
                                                    gongongSelectIndex = arg0 + 1;
                                                    mTextView_tv_count.setText((gongongSelectIndex) + "/" + mListGonggong.size());
                                                }

                                            }

                                        }

                                        @Override
                                        public void onPageScrolled(int arg0, float arg1, int arg2) {
                                            // TODO Auto-generated method stub
                                            MyLog.d("--", "--> " + arg0 + ", " + arg1 + ", " + arg2);
                                            if (selectedImageSet == 0) {
                                                if (mIsOnlyOneGonggong) {
                                                    if (arg2 > 140 && !mIsWuyeSelected && !mIsFromWuye) {
                                                        MyLog.d("--", "--> selectWuYe");
                                                        mIsWuyeSelected = true;
                                                        selectWuYe();
                                                    }

                                                    if (mIsFromWuye) {
                                                        mHandler.postDelayed(new Runnable() {

                                                            @Override
                                                            public void run() {
                                                                // TODO Auto-generated method stub
                                                                mIsFromWuye = false;
                                                            }
                                                        }, 400);
                                                    }

                                                }
                                            }

                                        }

                                        @Override
                                        public void onPageScrollStateChanged(int arg0) {
                                            // TODO Auto-generated method stub
                                            if (selectedImageSet == 0) {
                                                if (rightEdgeGonggong != null && !rightEdgeGonggong.isFinished()) {
                                                    selectWuYe();
                                                }
                                            }

                                        }
                                    });

                                    mViewPagerAdapter_wuye = new ViewPagerAdapter(mListWuyeNodes, mViewPager_vPager_wuye, ViewImageActivity.this, mHandler);
                                    mViewPager_vPager_wuye.setAdapter(mViewPagerAdapter_wuye);
                                    mViewPager_vPager_wuye.setOnPageChangeListener(new OnPageChangeListener() {

                                        @Override
                                        public void onPageSelected(int arg0) {
                                            // TODO Auto-generated method stub
                                            mTextView_tv_set.setVisibility(View.GONE);

                                            if (selectedImageSet == 1) {
                                                if (mIsOnlyOneWuye) {
                                                    wuyeSelectIndex = 1;
                                                    mTextView_tv_count.setText("1/1");

                                                    if (arg0 == 1) {
                                                        mViewPager_vPager_wuye.setCurrentItem(0);
                                                    }
                                                } else {
                                                    MyLog.d("--", "onPageSelected--> selectWuYe");
                                                    wuyeSelectIndex = arg0 + 1;
                                                    mTextView_tv_count.setText((wuyeSelectIndex) + "/" + mListWuye.size());
                                                }
                                            }

                                        }

                                        @Override
                                        public void onPageScrolled(int arg0, float arg1, int arg2) {
                                            // TODO Auto-generated method stub
                                            if (selectedImageSet == 1) {

                                            }
                                        }

                                        @Override
                                        public void onPageScrollStateChanged(int arg0) {
                                            // TODO Auto-generated method stub
                                            if (selectedImageSet == 1) {
                                                if (leftEdgeWuye != null && !leftEdgeWuye.isFinished()) {
                                                    mIsFromWuye = true;
                                                    selectGonggong();
                                                }
                                            }

                                        }
                                    });

                                    selectGonggong();

                                    try {
                                        Field leftEdgeField = mViewPager_vPager_wuye.getClass().getDeclaredField("mLeftEdge");
                                        Field rightEdgeField = mViewPager_vPager_gonggong.getClass().getDeclaredField("mRightEdge");
                                        if (leftEdgeField != null && rightEdgeField != null) {
                                            leftEdgeField.setAccessible(true);
                                            rightEdgeField.setAccessible(true);
                                            leftEdgeWuye = (EdgeEffectCompat) leftEdgeField.get(mViewPager_vPager_wuye);
                                            rightEdgeGonggong = (EdgeEffectCompat) rightEdgeField.get(mViewPager_vPager_gonggong);
                                        }
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

//								if (mListGonggong.size() == 1) {
//									mPhotoView_iv_photo_gongogn_one_img.setVisibility(View.VISIBLE);
//									mViewPager_vPager_gonggong.setVisibility(View.GONE);
//									imageLoader.displayImage(mListGonggongNodes.get(0).getUrl(), mPhotoView_iv_photo_gongogn_one_img, EkeApplication.mDisplayImageOptions_no_round_corner);
//								}
//								
//								if (mListWuye.size() == 1) {
//									imageLoader.displayImage(mListWuyeNodes.get(0).getUrl(), mPhotoView_iv_photo_wuye_one_img, EkeApplication.mDisplayImageOptions_no_round_corner);
//								}
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
                        mDialogUtil.dissmissProgressDialog();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_house_view_image);
        mDialogUtil = new DialogUtil(ViewImageActivity.this);

        initActivity();

        JSONObject obj = new JSONObject();
        try {
            obj.put("foreignId", houseSource.propertyid);

            ClientHelper clientHelper = new ClientHelper(ViewImageActivity.this,
                    ServerUrl.METHOD_getListPicForeignId, obj.toString(), mHandler);
            clientHelper.setShowProgressMessage("正在获取数据...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initActivity() {
        mTextView_tv_gonggong = (TextView) findViewById(R.id.tv_gonggong);
        mTextView_tv_wuye = (TextView) findViewById(R.id.tv_wuye);
        mViewPager_vPager_gonggong = (ViewPager) findViewById(R.id.vPager_gonggong);
        mViewPager_vPager_wuye = (ViewPager) findViewById(R.id.vPager_wuye);
        mImageView_tv_jiucuo = (ImageView) findViewById(R.id.iv_jiucuo);
        mTextView_tv_count = (TextView) findViewById(R.id.tv_count);
        mPhotoView_iv_photo_gongogn_one_img = (EasePhotoView) findViewById(R.id.iv_photo_gongogn_one_img);
        mTextView_tv_set = (TextView) findViewById(R.id.tv_set);
        mListGonggong.clear();


        mTextView_tv_gonggong.setOnClickListener(this);
        mTextView_tv_wuye.setOnClickListener(this);
        mImageView_tv_jiucuo.setOnClickListener(this);

        mTextView_tv_set.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ViewImageNode view = null;
                if (selectedImageSet == 0) {
                    if (mIsOnlyOneGonggong) {
                        view = mListGonggongNodes.get(0);
                    } else {
                        view = mListGonggongNodes.get(gongongSelectIndex - 1);
                    }
                } else {
                    if (mIsOnlyOneWuye) {
                        view = mListWuyeNodes.get(0);
                    } else {
                        view = mListWuyeNodes.get(wuyeSelectIndex - 1);
                    }
                }
                MyLog.e("TAG", selectedImageSet + ", " + gongongSelectIndex + ", " + wuyeSelectIndex);
                if (view == null || view.getLocalImgPath() == null) {
                    return;
                }

                final String path = view.getLocalImgPath();

                mTextView_tv_set.setVisibility(View.GONE);

                Intent intent = new Intent(ViewImageActivity.this, ImgClipZoomFengMianActivity.class);
                intent.putExtra("img_url", path);
                intent.putExtra("propertyid", houseSource.propertyid);
                startActivity(intent);

//				mDialogUtil.setProgressMessage("正在上传...");
//				mDialogUtil.showProgressDialog();
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						String upload_path = ServerUrl.METHOD_PropertyHeadPic + 
//								"&token=" + GlobalSPA.getInstance(getApplicationContext()).getStringValueForKey(GlobalSPA.KEY_TOKEN)+
//								"&propertyid=" + mId;
//															
//						DataApi.uploadFile(new File(path), upload_path, new com.eke.http.Callback<String>() {
//		                    @Override
//		                    public void success(String s, Response response) {
//		                    	MyLog.d("f", "----res = " + s );
//		                        if (null != s) {
//		                        	mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
//		                        }else{
//		                        	mHandler.sendEmptyMessage(UPLOAD_FAIL);
//		                        }
//		                    }
//
//		                    @Override
//		                    public void failure(HttpError error) {
//		                    	mHandler.sendEmptyMessage(UPLOAD_FAIL);
//		                    }
//		                });
//					}
//				}).start();	
            }
        });

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_jiucuo: {
                new GlobalErrorReport(this, ViewImageActivity.this, mHandler, houseSource.propertyid, mErrorType).showGlobalErrorReportDlg(ERROR_TYPE_PROPERTYPIC);
            }
            break;

            case R.id.tv_gonggong: {
                if (selectedImageSet != 0) {
                    mIsFromWuye = true;
                    selectGonggong();
                    if (mListGonggong.size() > 0) {
                        mViewPager_vPager_gonggong.setCurrentItem(0);
                        gongongSelectIndex = 1;
                    }
                }

            }
            break;

            case R.id.tv_wuye: {
                if (selectedImageSet != 1) {
                    selectWuYe();
                }

            }
            break;
            default:
                break;
        }
    }

    private void selectGonggong() {
        selectedImageSet = 0;
        mTextView_tv_gonggong.setTextColor(0xffffffff);
        mTextView_tv_wuye.setTextColor(0xff8e8e8e);
        mViewPager_vPager_gonggong.setVisibility(View.VISIBLE);
        mViewPager_vPager_wuye.setVisibility(View.GONE);

        if (mListGonggong.size() == 0) {
            mTextView_tv_count.setText("0/0");
        } else {
            if (mIsOnlyOneGonggong) {
                mTextView_tv_count.setText(gongongSelectIndex + "/1");
                mViewPager_vPager_gonggong.setCurrentItem(0);
                gongongSelectIndex = 1;
            } else {
                MyLog.d("--", "**-->" + gongongSelectIndex + "/" + mListGonggong.size());
                mTextView_tv_count.setText(gongongSelectIndex + "/" + mListGonggong.size());
            }
        }

        mErrorType = ERROR_TYPE_PROPERTYPIC;
        mIsWuyeSelected = false;
    }

    private void selectWuYe() {
        selectedImageSet = 1;
        mErrorType = GlobalErrorReport.ERROR_TYPE_ESTATEPIC;
        mTextView_tv_gonggong.setTextColor(0xff8e8e8e);
        mTextView_tv_wuye.setTextColor(0xffffffff);
        mViewPager_vPager_gonggong.setVisibility(View.GONE);
        mViewPager_vPager_wuye.setVisibility(View.VISIBLE);

        if (mListWuye.size() == 0) {
            mTextView_tv_count.setText("0/0");
        } else {
            mViewPager_vPager_wuye.setCurrentItem(0);
            wuyeSelectIndex = 1;
            if (mIsOnlyOneWuye) {
                mTextView_tv_count.setText(wuyeSelectIndex + "/1");
            } else {
                MyLog.d("--", "@@-->" + wuyeSelectIndex + "/" + mListWuye.size());
                mTextView_tv_count.setText(wuyeSelectIndex + "/" + mListWuye.size());
            }
        }
    }

}
