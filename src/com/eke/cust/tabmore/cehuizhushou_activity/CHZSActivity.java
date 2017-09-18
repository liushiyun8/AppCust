package com.eke.cust.tabmore.cehuizhushou_activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.global.nodeinfo.DistrictNodeInfo;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.GlobalSPA;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.TransformUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CHZSActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "CHZSActivity";
	private static final int DRAW_MODE_NORMAL = 0;
	private static final int DRAW_MODE_FOREWORD = 1;
	private static final int DRAW_MODE_BACKWORD = 2;
	public static final int MSG_UPDATE_ZONG_HUSHU = 10;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private ImageView mImageView_iv_back;
	private ImageView mImageView_iv_history;
//	private ImageView mImageView_iv_left;
//	private ImageView mImageView_iv_top;
//	private ImageView mImageView_iv_down;
//	private ImageView mImageView_iv_right;
//	private Button mButton_btn_luodian;
	private Button mButton_btn_xiangqianchexiao;
	private Button mButton_btn_xianghouchexiao;
	private Button mButton_btn_quanbuqingchu;
	private Button mButton_btn_kaishihuatu;
	private Button mButton_btn_weihe;
	
	private LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	public MyLocationListener mMyLocationListener;
	private BitmapDescriptor MLocationIndicator = null;
	private MarkerOptions mMarkerOptions = null;
	private Marker mPenMarker;
	
	private double latitude = 0.0;
	private double longitude = 0.0;
	private boolean isLocated = false;
	private double moveMargin = 0.00005;
	private List<LatLng> mPointList = new ArrayList<LatLng>();
	private List<LatLng> mPointListBackup = new ArrayList<LatLng>();
	private int redrawCount = 0;//撤销次数
	private boolean mIsStartDraw = false;
	
	private String cityid = "";
	private String districtid = "";
	private TextView mTextView_tv_shi_distinct_complete;
	private List<DistrictNodeInfo> mListDistrict = new ArrayList<DistrictNodeInfo>();
	
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
							if (request_url.equals(ServerUrl.METHOD_insertEkeempmaprecord)) {
								Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
							}
							else if (request_url.equals(ServerUrl.METHOD_queryDistrictByCity)) {
								mListDistrict.clear();
								
								JSONArray array_data = jsonObject.optJSONArray("data");
								if (array_data != null) {
									for (int i = 0; i < array_data.length(); i++) {
										JSONObject object = array_data.getJSONObject(i);
										if (object != null) {
											try {
												DistrictNodeInfo districtNodeInfo = TransformUtil
																				.getEntityFromJson(
																					object,
																					DistrictNodeInfo.class);
												if (districtNodeInfo != null) {
													mListDistrict.add(districtNodeInfo);
												}
												
											} catch (InstantiationException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (IllegalAccessException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
									
									if (mListDistrict.size() > 0 && mTextView_tv_shi_distinct_complete != null) {
										mTextView_tv_shi_distinct_complete.setText(mListDistrict.get(0).getDistrictname());
										districtid = mListDistrict.get(0).getDistrictno();
									}
								}
								
							}
						}
						else if (result.equals(Constants.RESULT_ERROR)) {
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_more_chzs);
		
		initActivity();
		
		mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        initLocation();
        
        mLocationClient.start();//定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
        mLocationClient.requestLocation();
        
        mMarkerOptions = new MarkerOptions()
			.position(new LatLng(latitude, longitude))
			.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.chzs_pen1));	
        
	}
	
	private void initActivity() {
		mMapView = (MapView)findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();	
		mImageView_iv_back = (ImageView)findViewById(R.id.iv_back);
		mImageView_iv_history = (ImageView)findViewById(R.id.iv_history);
//		mImageView_iv_left = (ImageView)findViewById(R.id.iv_left);
//		mImageView_iv_top = (ImageView)findViewById(R.id.iv_top);
//		mImageView_iv_down = (ImageView)findViewById(R.id.iv_down);
//		mImageView_iv_right = (ImageView)findViewById(R.id.iv_right);
//		mButton_btn_luodian = (Button)findViewById(R.id.btn_luodian);
		mButton_btn_xiangqianchexiao = (Button)findViewById(R.id.btn_xiangqianchexiao);
		mButton_btn_xianghouchexiao = (Button)findViewById(R.id.btn_xianghouchexiao);
		mButton_btn_quanbuqingchu = (Button)findViewById(R.id.btn_quanbuqingchu);
		mButton_btn_kaishihuatu = (Button)findViewById(R.id.btn_kaishihuatu);
		mButton_btn_weihe = (Button)findViewById(R.id.btn_weihe);
		
		mImageView_iv_history.setOnClickListener(this);
//		mImageView_iv_left.setOnClickListener(this);
//		mImageView_iv_top.setOnClickListener(this);
//		mImageView_iv_down.setOnClickListener(this);
//		mImageView_iv_right.setOnClickListener(this);
//		mButton_btn_luodian.setOnClickListener(this);
		mButton_btn_xiangqianchexiao.setOnClickListener(this);
		mButton_btn_xianghouchexiao.setOnClickListener(this);
		mButton_btn_quanbuqingchu.setOnClickListener(this);
		mButton_btn_kaishihuatu.setOnClickListener(this);
		mButton_btn_weihe.setOnClickListener(this);
		
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChange(MapStatus arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				// TODO Auto-generated method stub
				//获取地图中心点坐标
				LatLng ll = arg0.target;
				latitude = ll.latitude;
				longitude = ll.longitude;
				
				if(BuildConfig.DEBUG) {
					MyLog.d(TAG, "lat = " + latitude + ", lng = " + longitude);
				}
			}

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onMapClick(LatLng latLng) {
				// TODO Auto-generated method stub
				MyLog.d(TAG, "clicked...");
				if (!mIsStartDraw) {
					return;
				}
				
				double latitude1 = latLng.latitude; 
                double longitude1 = latLng.longitude; 
                
                mBaiduMap.clear();
    			LatLng latlng = new LatLng(latitude1, longitude1);
    			mPointList.add(latlng);
    			drawLines(DRAW_MODE_NORMAL);
			}
		});
	}

	
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，
        int span=1000;
        
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(false);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
       
        mLocationClient.setLocOption(option);
    }

	/**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                
                isLocated = true;
                

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                isLocated = true;
                
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                isLocated = true;
                
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
           
            logMsg(sb.toString());
            MyLog.i(TAG, sb.toString());
           // mLocationClient.setEnableGpsRealTimeTransfer(true);
            
            if (isLocated) {
            	latitude = location.getLatitude();
                longitude = location.getLongitude();
                mLocationClient.stop();
                
//                MyLocationData locData = new MyLocationData.Builder()
//					.accuracy(location.getRadius())
//					// 此处设置开发者获取到的方向信息，顺时针0-360
//					.direction(100).latitude(location.getLatitude())
//					.longitude(location.getLongitude()).build();
//                mBaiduMap.setMyLocationData(locData);	//设置定位数据
                
                LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, mBaiduMap.getMaxZoomLevel());	//设置地图中心点以及缩放级别
				mBaiduMap.animateMapStatus(u);
			}
        }


    }


    /**
     * 显示请求字符串
     * @param str
     */
    public void logMsg(String str) {
        
    }
    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        mLocationClient.stop();
        super.onStop();
    }
    
    private void showIsCompleteImfoDlg() {
		final Dialog dlg = new Dialog(CHZSActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(CHZSActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_more_chzs_is_complete, null);
				
		final Button btn_later = (Button)viewContent.findViewById(R.id.btn_later);
		final Button btn_confirm = (Button)viewContent.findViewById(R.id.btn_confirm);
		
		btn_later.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
			}
		});
		
		btn_confirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				showCompleteImfoDlg();
			}
		});
		
		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK){ 
					
				}
				return false;
			}
		});
		
		DisplayMetrics  dm = new DisplayMetrics();
		CHZSActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(CHZSActivity.this, 100);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}
    
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < nameWayList.size(); i++) {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("nameway", nameWayList.get(i));
            list.add(map);
		}
        
        return list;
	}
    
    private List<Map<String, Object>> getQuXianData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mListDistrict.size(); i++) {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("action", mListDistrict.get(i).getDistrictname());
            list.add(map);
		}
        
        return list;
	}
    
    private void showSelectDlg() {
		final Dialog dlg = new Dialog(CHZSActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(CHZSActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_house_source_clicked, null);
		
		final ListView listview = (ListView)viewContent.findViewById(R.id.listview_action);
		
		SimpleAdapter adapter = new SimpleAdapter(CHZSActivity.this, getQuXianData(), R.layout.layout_dailichongzhi_list_item,
                new String[]{"action"},
                new int[]{R.id.tv_action});
		
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				if ( mTextView_tv_shi_distinct_complete != null) {
					mTextView_tv_shi_distinct_complete.setText(mListDistrict.get(position).getDistrictname());
					districtid = mListDistrict.get(position).getDistrictno();
				}
				
				dlg.dismiss();
			}
		});
		
		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK){ 
					
				}
				return false;
			}
		});
		
		DisplayMetrics  dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
//		WindowManager.LayoutParams lp =window.getAttributes();
//		lp.width = dm.widthPixels*2/3;
//		window.setAttributes(lp);
		
		dlg.show();
	}
    
    private PopupWindow pw;
	private int selectedNameWayIndex = 0;
	private int currentSetDongInxe = 0;
	private List<String> nameWayList = new ArrayList<String>();
    private void showCompleteImfoDlg() {
    	selectedNameWayIndex = 0;
    	currentSetDongInxe = 0;
    	
		final Dialog dlg = new Dialog(CHZSActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(CHZSActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_tab_more_chzs_complete, null);
		
		final ChzsViewPagerAdapter adapter;
		final List<ChzsNode> listNode = new ArrayList<ChzsNode>();
		final List<View> list = new ArrayList<View>();
		
		final TextView mTextView_tv_shi;
		final Button mButton_btn_distinct_left;
		final TextView mTextView_tv_shi_distinct;
		final Button mButton_btn_distinct_right;
		final EditText mEditText_et_loupan_name;
		final TextView mTextView_tv_zonghushu;
		final EditText mEditText_et_zonghushu;
		final ImageView mImageView_iv_zongdongshu_down;
		final EditText mEditText_et_zongdongshu_count;
		final ImageView mImageView_iv_zongdongshu_up;
		final TextView mTextView_tv_fanghaomingming;
		final TextView mTextView_tv_fanghaomingming_select;
		final ViewPager mViewPager_vPager_gonggong;
		final ImageView mImageView_iv_dong_set_left;
		final TextView mTextView_tv_current_dong;
		final ImageView mImageView_iv_dong_set_right;
		final Button mButton_btn_submit;
		
		mTextView_tv_shi = (TextView)viewContent.findViewById(R.id.tv_shi);
		mButton_btn_distinct_left = (Button)viewContent.findViewById(R.id.btn_distinct_left);
		mTextView_tv_shi_distinct = (TextView)viewContent.findViewById(R.id.tv_shi_distinct);
		mButton_btn_distinct_right = (Button)viewContent.findViewById(R.id.btn_distinct_right);
		mEditText_et_loupan_name = (EditText)viewContent.findViewById(R.id.et_loupan_name);
		mTextView_tv_zonghushu = (TextView)viewContent.findViewById(R.id.tv_zonghushu);
		mEditText_et_zonghushu = (EditText)viewContent.findViewById(R.id.et_zonghushu);
		mImageView_iv_zongdongshu_down = (ImageView)viewContent.findViewById(R.id.iv_zongdongshu_down);
		mEditText_et_zongdongshu_count = (EditText)viewContent.findViewById(R.id.et_zongdongshu_count);
		mImageView_iv_zongdongshu_up = (ImageView)viewContent.findViewById(R.id.iv_zongdongshu_up);
		mTextView_tv_fanghaomingming = (TextView)viewContent.findViewById(R.id.tv_fanghaomingming);
		mTextView_tv_fanghaomingming_select = (TextView)viewContent.findViewById(R.id.tv_fanghaomingming_select);
		mViewPager_vPager_gonggong = (android.support.v4.view.ViewPager)viewContent.findViewById(R.id.vPager_gonggong);
		mImageView_iv_dong_set_left = (ImageView)viewContent.findViewById(R.id.iv_dong_set_left);
		mTextView_tv_current_dong = (TextView)viewContent.findViewById(R.id.tv_current_dong);
		mImageView_iv_dong_set_right = (ImageView)viewContent.findViewById(R.id.iv_dong_set_right);
		mButton_btn_submit = (Button)viewContent.findViewById(R.id.btn_submit);	
		
		String city = GlobalSPA.getInstance(CHZSActivity.this).getStringValueForKey(GlobalSPA.KEY_CITY);
		mTextView_tv_shi.setText(city);
		mTextView_tv_shi_distinct_complete = mTextView_tv_shi_distinct;
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("txt", city);
			
			ClientHelper clientHelper = new ClientHelper(CHZSActivity.this,
					ServerUrl.METHOD_queryDistrictByCity, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在获取数据, 请稍候...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(false);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mTextView_tv_shi_distinct.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSelectDlg();
			}
		});
		
		final int ZongDongShu_max = 8;
		final int ZongDongShu_min = 1;
		
		final Handler handler = new Handler() {
			public void dispatchMessage(android.os.Message msg) {
			super.dispatchMessage(msg);
			
				if (msg != null) {
					switch (msg.what) {
					case MSG_UPDATE_ZONG_HUSHU:
					{
						int count = 0;
						for (int i = 0; i < listNode.size(); i++) {
							count += listNode.get(i).getZongHushuCount();
						}
						mEditText_et_zonghushu.setText(count + "");
					}
					break;
					}
				}
			}
		};
		
		for (int i = 0; i < 8; i++) {
			ChzsNode node = new ChzsNode(CHZSActivity.this, handler);
			listNode.add(node);
			list.add(node.genView());
		}
		
		mEditText_et_zonghushu.setText(6*8*2*8 + "");
		
		mTextView_tv_current_dong.setText((currentSetDongInxe+1) + "/" + list.size());
		
		nameWayList.clear();
		nameWayList.add("1.【字母、数字任意组合】 例：601.18D");
		nameWayList.add("2.【字母、数字、字符任意组合】 例：601.18-D");
		nameWayList.add("3.【楼层(2位)+房间号(数字)】 例：0601.1804");
		nameWayList.add("4.【楼层(2位)+房间号(字母)】 例：06A.18D");
		nameWayList.add("5.【楼层(2位)+房间号(数字或字母)】 例：0601.18D");
		
		mTextView_tv_fanghaomingming_select.setText(nameWayList.get(0));
		
		adapter = new ChzsViewPagerAdapter(list, mViewPager_vPager_gonggong, this);
		mViewPager_vPager_gonggong.setAdapter(adapter);
		
		mButton_btn_submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String loupan_name = mEditText_et_loupan_name.getText().toString().trim();
				if (loupan_name.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入楼盘名!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String loupan_zonghushu = mEditText_et_zongdongshu_count.getText().toString().trim();
				if (loupan_zonghushu.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入楼盘总户数!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				dlg.dismiss();
				
				JSONObject obj = new JSONObject();
				try {
					obj.put("val", mTextView_tv_shi.getText().toString());/////////////////////////////
					obj.put("districtid", districtid);
					obj.put("estatename", loupan_name);
					obj.put("total", loupan_zonghushu);
					obj.put("type", "");/////////////////
					obj.put("mes", mTextView_tv_fanghaomingming_select.getText().toString());
					
					StringBuilder ekebaidumap = new StringBuilder();
					for (int i = 0; i < mPointList.size(); i++) {
						LatLng pos = mPointList.get(i);
						ekebaidumap.append(pos.latitude);
						ekebaidumap.append(",");
						ekebaidumap.append(pos.longitude);
						if (i != mPointList.size()-1) {
							ekebaidumap.append(";");
						}
					}
					obj.put("main", ekebaidumap.toString());
					
					JSONArray array = new JSONArray();
					for (int i = 0; i < listNode.size(); i++) {
						ChzsNode node = listNode.get(i);
						JSONObject object = new JSONObject();
						object.put("buildingname", node.getBuildingName());
						object.put("floorall", node.getFloorAll());
						object.put("counth", node.getCounth());
						object.put("cell", node.getCell());
						
						array.put(object);
					}
					obj.put("buildcell", array);
					
					ClientHelper clientHelper = new ClientHelper(CHZSActivity.this,
							ServerUrl.METHOD_insertEkeempmaprecord, obj.toString(), mHandler);
					clientHelper.setShowProgressMessage("正在提交数据...");
					clientHelper.isShowProgress(true);
					clientHelper.sendPost(true);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		mImageView_iv_zongdongshu_down.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int zongdongshu = Integer.valueOf(mEditText_et_zongdongshu_count.getText().toString().trim());
				if (zongdongshu == ZongDongShu_min) {
					return;
				}
				mEditText_et_zongdongshu_count.setText((--zongdongshu)+ "");
				
				listNode.clear();
				list.clear();
				for (int i = 0; i < zongdongshu; i++) {
					ChzsNode node = new ChzsNode(CHZSActivity.this, handler);
					listNode.add(node);
					list.add(node.genView());
				}
				adapter.notifyDataSetChanged();
				mViewPager_vPager_gonggong.setCurrentItem(0);
				currentSetDongInxe = 0;
				mTextView_tv_current_dong.setText((currentSetDongInxe+1) + "/" + list.size());
				mEditText_et_zonghushu.setText(6*8*2*list.size() + "");
			}
		});
		
		mImageView_iv_zongdongshu_up.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int zongdongshu = Integer.valueOf(mEditText_et_zongdongshu_count.getText().toString().trim());
				if (zongdongshu == ZongDongShu_max) {
					return;
				}
				mEditText_et_zongdongshu_count.setText((++zongdongshu)+ "");
				
				listNode.clear();
				list.clear();
				for (int i = 0; i < zongdongshu; i++) {
					ChzsNode node = new ChzsNode(CHZSActivity.this, handler);
					listNode.add(node);
					list.add(node.genView());
				}
				adapter.notifyDataSetChanged();
				mViewPager_vPager_gonggong.setCurrentItem(0);
				currentSetDongInxe = 0;
				mTextView_tv_current_dong.setText((currentSetDongInxe+1) + "/" + list.size());
				mEditText_et_zonghushu.setText(6*8*2*list.size() + "");
			}
		});
		
		mTextView_tv_fanghaomingming_select.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View myView = getLayoutInflater().inflate(R.layout.pop_fanghao_name_way, null);
                pw = new PopupWindow(myView, mTextView_tv_fanghaomingming_select.getMeasuredWidth()+DensityUtil.dip2px(getApplicationContext(), 30), LayoutParams.WRAP_CONTENT, true);
                pw.setFocusable(true);
                //将window视图显示在tv_bank下面
                pw.showAsDropDown(mTextView_tv_fanghaomingming_select);
                final ListView lv = (ListView) myView.findViewById(R.id.lv_pop_nameway);
                SimpleAdapter adapter = new SimpleAdapter(CHZSActivity.this, getData(), R.layout.layout_fanghao_nameway_list_item,
		                new String[]{"nameway"},
		                new int[]{R.id.tv_nameway});
                
                lv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						lv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						for (int i = 0; i < lv.getChildCount(); i++) {
							if (i == selectedNameWayIndex) {
								lv.getChildAt(i).findViewById(R.id.tv_checked).setVisibility(View.VISIBLE);
							}
							else {
								lv.getChildAt(i).findViewById(R.id.tv_checked).setVisibility(View.INVISIBLE);
							}
						}
					}
                	
                });
				
				lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
 
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                    	selectedNameWayIndex = position;
                    	mTextView_tv_fanghaomingming_select.setText(nameWayList.get(position));
                        pw.dismiss();
                    }
                });
			}
		});
		
		mImageView_iv_dong_set_left.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentSetDongInxe == 0) {
					return;
				}
				
				currentSetDongInxe--;
				mViewPager_vPager_gonggong.setCurrentItem(currentSetDongInxe);
				mTextView_tv_current_dong.setText((currentSetDongInxe+1) + "/" + list.size());
			}
		});
		
		mImageView_iv_dong_set_right.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentSetDongInxe == list.size()-1) {
					return;
				}
				
				currentSetDongInxe++;
				mViewPager_vPager_gonggong.setCurrentItem(currentSetDongInxe);
				mTextView_tv_current_dong.setText((currentSetDongInxe+1) + "/" + list.size());
			}
		});
		
		mViewPager_vPager_gonggong.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				currentSetDongInxe = arg0;
				mTextView_tv_current_dong.setText((currentSetDongInxe+1) + "/" + list.size());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK){ 
					
				}
				return false;
			}
		});
		
		DisplayMetrics  dm = new DisplayMetrics();
		CHZSActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(CHZSActivity.this, 40);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		dlg.show();
	}
    
    private void drawLines(int draw_mode) {
    	List<LatLng> list = null;
    	if (draw_mode == DRAW_MODE_BACKWORD) {
			if (mPointList.size() > 1) {
//				redrawCount ++;
//				mPointListBackup.clear();
//				for (int i = 0; i < mPointList.size() - redrawCount; i++) {
//					mPointListBackup.add(mPointList.get(i));
//				}
//				
//				list = mPointListBackup;
				
				mPointListBackup.add(mPointList.get(mPointList.size()-1));
				mPointList.remove(mPointList.size()-1);
				list = mPointList;
			}
		}
    	else if(draw_mode == DRAW_MODE_FOREWORD) {
    		if (redrawCount == 0) {
    			list = mPointList;
			}
    		else {
    			redrawCount --;
    			mPointListBackup.clear();
				for (int i = 0; i < mPointList.size() - redrawCount; i++) {
					mPointListBackup.add(mPointList.get(i));
				}
				
				list = mPointListBackup;
    		}
    		
    		if (mPointListBackup.size() > 0) {
    			mPointList.add(mPointListBackup.get(mPointListBackup.size()-1));
    			mPointListBackup.remove(mPointListBackup.size()-1);
				list = mPointList;
			}
    	}
    	else if (draw_mode == DRAW_MODE_NORMAL) {
//    		if (redrawCount > 0) {
//    			mPointList.clear();
//				for (int i = 0; i < mPointListBackup.size() - redrawCount; i++) {
//					mPointList.add(mPointList.get(i));
//				}
//			}
    		list = mPointList;
    	}
    	
    	if (list == null) {
			return;
		}
    	
    	for (int i = 0; i < list.size(); i++) {
			MarkerOptions markOptions = new MarkerOptions()
				.position(list.get(i))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.chzs_red_rect));
			if (i == 0) {
				markOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.chzs_red_dot));
			}
			Marker marker = (Marker)mBaiduMap.addOverlay(markOptions);
			marker.setAnchor(0.5f, 0.5f);
			
			if (i > 0) {
				List<LatLng> points = new ArrayList<LatLng>();
				points.add(list.get(i-1));
				points.add(list.get(i));
				
				boolean isDotLine = false;
				if (i == list.size()-1) {
					isDotLine = true;
				}
				
				OverlayOptions ooPolyline = new PolylineOptions().width(5)
		                .color(0xAAFF0000).points(points).dottedLine(isDotLine);					
				
		        mBaiduMap.addOverlay(ooPolyline);
			}
			
			if (i == list.size()-1) {
				mMarkerOptions.position(list.get(list.size()-1));
				mPenMarker = (Marker)mBaiduMap.addOverlay(mMarkerOptions);
			}
		}
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_history:
		{
			Intent intent = new Intent(CHZSActivity.this, CHHistoryActivity.class);
			startActivity(intent);
		}
			break;
			
//		case R.id.iv_left:
//		{
//			mPenMarker.remove();
//			longitude -= moveMargin;
//			mMarkerOptions.position(new LatLng(latitude, longitude));
//			mPenMarker = (Marker)mBaiduMap.addOverlay(mMarkerOptions);
//		}
//			break;
//			
//		case R.id.iv_top:
//		{
//			mPenMarker.remove();
//			latitude += moveMargin;
//			mMarkerOptions.position(new LatLng(latitude, longitude));
//			mPenMarker = (Marker)mBaiduMap.addOverlay(mMarkerOptions);
//		}
//			break;
//			
//		case R.id.iv_right:
//		{
//			mPenMarker.remove();
//			longitude += moveMargin;
//			mMarkerOptions.position(new LatLng(latitude, longitude));
//			mPenMarker = (Marker)mBaiduMap.addOverlay(mMarkerOptions);
//		}
//			break;
//			
//		case R.id.iv_down:
//		{
//			mPenMarker.remove();
//			latitude -= moveMargin;
//			mMarkerOptions.position(new LatLng(latitude, longitude));
//			mPenMarker = (Marker)mBaiduMap.addOverlay(mMarkerOptions);
//		}
//			break;
//			
//		case R.id.btn_luodian:
//		{
//			mBaiduMap.clear();
//			LatLng latlng = new LatLng(latitude, longitude);
//			mPointList.add(latlng);
//			drawLines(DRAW_MODE_NORMAL);
//		}
//			break;
			
		case R.id.btn_xiangqianchexiao:
		{
			if (!mIsStartDraw) {
				return;
			}
			
			mBaiduMap.clear();
			drawLines(DRAW_MODE_FOREWORD);
		}
			break;
			
		case R.id.btn_xianghouchexiao:
		{
			if (!mIsStartDraw) {
				return;
			}
			mBaiduMap.clear();
			drawLines(DRAW_MODE_BACKWORD);
		}
			break;
			
		case R.id.btn_quanbuqingchu:
		{
			mIsStartDraw = false;
			mBaiduMap.clear();
			mPointList.clear();
			mPointListBackup.clear();
			redrawCount = 0;
		}
			break;
			
		case R.id.btn_kaishihuatu:
		{
			if (mIsStartDraw) {
				return;
			}
			
			mIsStartDraw = true;
			
			mMarkerOptions.position(new LatLng(latitude, longitude));
			mPenMarker = (Marker)mBaiduMap.addOverlay(mMarkerOptions);
			
//			mBaiduMap.clear();
			LatLng latlng = new LatLng(latitude, longitude);
			mPointList.add(latlng);
			drawLines(DRAW_MODE_NORMAL);
		}
			break;
			
		case R.id.btn_weihe:
		{
			if (!mIsStartDraw) {
				return;
			}
			
			if (mPointList.size() < 3) {
				Toast.makeText(getApplicationContext(), "最少选择3个点!", Toast.LENGTH_SHORT).show();
				break;
			}
			
			showIsCompleteImfoDlg();
		}
			break;
			

		default:
			break;
		}
	}
}
