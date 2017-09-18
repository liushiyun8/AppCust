package com.eke.cust.tabmine.profile_activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.UserLocationHelper;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangjie on 2016/5/15.
 */
public class ChongzhiNotFindActivity extends BaseActivity implements
		View.OnClickListener, BDLocationListener,
		BaiduMap.OnMapStatusChangeListener {
	private Button commitBtn;
	private TextView addressTextView;
	private EditText loupanNameEditText;
	private MapView mapView;
	private BaiduMap mBaiduMap;

	private double latitude = 0.0;
	private double longitude = 0.0;
	/**
	 * 定位模式
	 */
	private MyLocationConfiguration.LocationMode mCurrentMode;
	/**
	 * 定位端
	 */
	private LocationClient mLocClient;
	/**
	 * 是否是第一次定位
	 */
	private boolean isFirstLoc = true;
	/**
	 * 定位坐标
	 */
	private LatLng locationLatLng;
	/**
	 * 定位城市
	 */
	private String city;

	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			super.dispatchMessage(msg);

			if (msg != null) {
				switch (msg.what) {
				/*
				 * case GlobalLocation.MSG_GLOBAL_LOCATION: { Bundle bundle =
				 * msg.getData(); latitude = bundle.getDouble("latitude");
				 * longitude = bundle.getDouble("longitude"); String addr =
				 * bundle.getString("addr");
				 *//*
					 * mCityName = bundle.getString("city"); mDistrict =
					 * bundle.getString("district"); mRoad =
					 * bundle.getString("road");
					 *//*
						 * addressTextView.setText(addr); } break;
						 */

				case UserLocationHelper.MSG_GLOBAL_LOCATION_FAIL: {
					/*
					 * mDialogUtil.dissmissProgressDialog();
					 * showLocationFailDlg();
					 */
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
							if (request_url
									.equals(ServerUrl.METHOD_addnewloupan)) {
								setResult(RESULT_OK);
								finish();
							}
						} else if (result.equals(Constants.RESULT_ERROR)) {
							String errorMsg = jsonObject.optString("errorMsg",
									"出错!");
							Toast.makeText(getApplicationContext(), errorMsg,
									Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "请求出错!",
								Toast.LENGTH_SHORT).show();
					}

					break;

				case Constants.TAG_FAIL:
					Toast.makeText(getApplicationContext(), "请求出错!",
							Toast.LENGTH_SHORT).show();
					break;
				case Constants.TAG_EXCEPTION:
					Toast.makeText(getApplicationContext(), "请求出错!",
							Toast.LENGTH_SHORT).show();
					break;
				}

			}
		}
	};
	private String address = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.chongzhi_notfind_layout);
		initViews();
		initDatas();
	}

	private void initViews() {
		commitBtn = (Button) findViewById(R.id.btn_submit);
		addressTextView = (TextView) findViewById(R.id.tv_current_address);
		loupanNameEditText = (EditText) findViewById(R.id.et_content_loupan_name);
		mapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mapView.getMap();
		View child = mapView.getChildAt(1);
		if (child != null
				&& (child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}
		mapView.showZoomControls(false);
		commitBtn.setOnClickListener(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
	}

	private void initDatas() {
		mapView.invalidate();// 刷新地图
		mBaiduMap = mapView.getMap();
		// 获取坐标，待会用于POI信息点与定位的距离
		currentLatLng = new LatLng(latitude, longitude);
		// drawRealTimePoint(currentLatLng);
		// 定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().zoom(18).build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);

		// 地图状态改变相关监听
		mBaiduMap.setOnMapStatusChangeListener(this);

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		// 定位图层显示方式
		mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

		/**
		 * 设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效 customMarker用户自定义定位图标
		 * enableDirection是否允许显示方向信息 locationMode定位图层显示方式
		 */
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.location_pin_small);

		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));

		// 初始化定位
		mLocClient = new LocationClient(this);
		// 注册定位监听
		mLocClient.registerLocationListener(this);

		// 定位选项
		LocationClientOption option = new LocationClientOption();
		/**
		 * coorType - 取值有3个： 返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09 返回百度经纬度坐标系
		 * ：bd09ll
		 */
		option.setCoorType("bd09ll");
		// 设置是否需要地址信息，默认为无地址
		option.setIsNeedAddress(true);
		/*
		 * //设置是否需要返回位置POI信息，可以在BDLocation.getPoiList()中得到数据
		 * option.setIsNeedLocationPoiList(true);
		 */
		/**
		 * 设置定位模式 Battery_Saving 低功耗模式 Device_Sensors 仅设备(Gps)模式 Hight_Accuracy
		 * 高精度模式
		 */
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		// 设置是否打开gps进行定位
		option.setOpenGps(true);
		// 设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
		option.setScanSpan(1000);

		// 设置 LocationClientOption
		mLocClient.setLocOption(option);

		// 开始定位
		mLocClient.start();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 退出时停止定位
		mLocClient.stop();
		// 退出时关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);

		// activity 销毁时同时销毁地图控件
		mapView.onDestroy();

		mapView = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_submit:
			if (TextUtils.isEmpty(loupanNameEditText.getText().toString())) {
				Toast.makeText(this, "请输入楼盘信息", Toast.LENGTH_SHORT).show();
				break;
			}
			if (!isLocationSuccess) {
				Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
				break;
			}
			JSONObject obj = new JSONObject();
			try {
				obj.put("longitude", longitude);
				obj.put("latitude", latitude);
				obj.put("address", address);
				obj.put("estatename", loupanNameEditText.getText().toString()
						.trim());
				ClientHelper clientHelper = new ClientHelper(this,
						ServerUrl.METHOD_addnewloupan, obj.toString(), mHandler);
				clientHelper.isShowProgress(true);
				clientHelper.sendPost(true);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * Intent intent = new Intent(); intent.putExtra("la",latitude);
			 * intent.putExtra("lo",longitude);
			 * intent.putExtra("address",address);
			 * intent.putExtra("loupan",loupanNameEditText
			 * .getText().toString().trim());
			 * 
			 * setResult(RESULT_OK,intent);
			 */
			break;
		case R.id.iv_back:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
	}

	private boolean isLocationSuccess = false;

	@Override
	public void onReceiveLocation(BDLocation bdLocation) {
		// 如果bdLocation为空或mapView销毁后不再处理新数据接收的位置
		if (bdLocation == null || mBaiduMap == null) {
			return;
		}
		isLocationSuccess = true;
		// 定位数据
		MyLocationData data = new MyLocationData.Builder()
		// 定位精度bdLocation.getRadius()
		// .accuracy(bdLocation.getRadius())
				.accuracy(0)
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(bdLocation.getDirection())
				// 经度
				.latitude(bdLocation.getLatitude())
				// 纬度
				.longitude(bdLocation.getLongitude())
				// 构建
				.build();
		// 是否是第一次定位
		if (isFirstLoc) {
			latitude = bdLocation.getLatitude();
			longitude = bdLocation.getLongitude();
			// address = bdLocation.getAddrStr();
			// addressTextView.setText(address);
			// 设置定位数据
			mBaiduMap.setMyLocationData(data);
			isFirstLoc = false;
			LatLng ll = new LatLng(bdLocation.getLatitude(),
					bdLocation.getLongitude());
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(ll, 18);
			mBaiduMap.animateMapStatus(msu);
			// 获取坐标，待会用于POI信息点与定位的距离
			locationLatLng = new LatLng(bdLocation.getLatitude(),
					bdLocation.getLongitude());
			// drawRealTimePoint(locationLatLng);
			// 获取城市，待会用于POISearch
			city = bdLocation.getCity();

		}

	}

	@Override
	public void onMapStatusChangeStart(MapStatus mapStatus) {

	}

	@Override
	public void onMapStatusChange(MapStatus mapStatus) {

	}

	@Override
	public void onMapStatusChangeFinish(MapStatus mapStatus) {
		// 地图操作的中心点
		LatLng cenpt = mapStatus.target;
		latitude = cenpt.latitude;
		longitude = cenpt.longitude;
	}

	private LatLng currentLatLng;
	private Marker currentMarker;

	private void drawRealTimePoint(LatLng point) {
		currentLatLng = point;
		if (currentMarker != null) {
			currentMarker.remove();
		}

		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.location_pin_small);
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		currentMarker = (Marker) mapView.getMap().addOverlay(option);
	}
}
