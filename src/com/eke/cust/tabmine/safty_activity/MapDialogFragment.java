package com.eke.cust.tabmine.safty_activity;


import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.eke.cust.AppContext;
import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.global.nodeinfo.PageNodeInfo;
import com.eke.cust.location.LocationHelper;
import com.eke.cust.model.House;
import com.eke.cust.model.HouseCover;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabmine.profile_activity.EstateNodeInfo;
import com.eke.cust.utils.ScreenUtils;
import com.eke.cust.utils.TransformUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import foundation.log.LogUtils;
import foundation.notification.NotificationCenter;
import foundation.permissions.AfterPermissionGranted;
import foundation.permissions.EasyPermissions;
import foundation.toast.ToastManager;
import foundation.toast.ToastUtil;
import foundation.util.JSONUtils;
import foundation.util.StringUtil;
import foundation.widget.SoftKeyboardStateHelper;

import static com.hyphenate.chat.EMGCMListenerService.TAG;
import static org.droidparts.Injector.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapDialogFragment extends DialogFragment implements
        BaiduMap.OnMapStatusChangeListener, EasyPermissions.PermissionCallbacks, SoftKeyboardStateHelper.SoftKeyboardStateListener,OnGetGeoCoderResultListener {
    private TextureMapView mapView;
    private TextView mTxtAddress;
    private AutoCompleteTextView mEdtHouseName;
    private RelativeLayout mLayoutRoot;

    private BaiduMap mBaiduMap;

    public double latitude = 0.0;
    public double longitude = 0.0;
    private LocationHelper locateHelper;
    private LocationHelper.LocationListener mLocationListener;
    private static final int REQUEST_LOCATION_PERMISSIONS = 1;
    private String mCityName = "深圳";
    private PageNodeInfo mEstatePageNodeInfo = new PageNodeInfo();
    private ArrayList<EstateNodeInfo> mSelectedEstates = new ArrayList<EstateNodeInfo>();
    private ArrayList<EstateNodeInfo> mAllEstates = new ArrayList<EstateNodeInfo>();
    private EstateNodeInfo estateNodeInfo;
    private House house;
    /**
     * 反地理编码
     */
    private GeoCoder mSearch;
    // 覆盖物
    private ArrayList<HouseCover> houseCovers = new ArrayList<HouseCover>();
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
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "result: " + resp);
                            }
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url.equals(ServerUrl.METHOD_queryListPage)) {
                                    JSONObject obj_data = jsonObject.getJSONObject("data");
                                    try {
                                        mEstatePageNodeInfo = TransformUtil.getEntityFromJson(obj_data, PageNodeInfo.class);
                                        JSONArray array_data_allEstatePage = obj_data.optJSONArray("data");
                                        if (array_data_allEstatePage != null) {
                                            for (int i = 0; i < array_data_allEstatePage.length(); i++) {
                                                JSONObject object = array_data_allEstatePage.getJSONObject(i);
                                                if (object != null) {
                                                    EstateNodeInfo node = TransformUtil.getEntityFromJson(object, EstateNodeInfo.class);
                                                    if (node != null) {
                                                        for (int j = 0; j < mSelectedEstates.size(); j++) {
                                                            if (node.getEstateid().equals(mSelectedEstates.get(j).getEstateid())) {
                                                                node.setSelected(true);
                                                                break;
                                                            }
                                                        }
                                                        mAllEstates.add(node);
                                                    }
                                                }
                                            }

                                            if (mAllEstates.size() == 0) {
                                                ToastUtils.show(getActivity(), "没有相关楼盘信息");
                                            }
                                            setAdapter(getList(mAllEstates));

                                        }
                                    } catch (InstantiationException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }


                                } else if (request_url
                                        .equals(ServerUrl.METHOD_insertEkeempestatemodi)) {
                                    Toast.makeText(getActivity(), "提交成功!",
                                            Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                } else if (request_url
                                        .equals(ServerUrl.METHOD_getListEstateMap)) {
                                    JSONArray jsonArray = JSONUtils.getJSONArray(
                                            jsonObject, "data", null);
                                    if (jsonArray != null) {
                                        houseCovers = JSONUtils.getObjectList(
                                                jsonArray, HouseCover.class);
                                    }
                                    initMapCover(houseCovers);
                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                String errorMsg = jsonObject.optString("errorMsg",
                                        "出错!");
                                Toast.makeText(getActivity(), errorMsg,
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "请求出错!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(getActivity(), "请求出错!", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(getActivity(), "请求出错!", Toast.LENGTH_SHORT)
                                .show();
                        break;
                }

            }
        }
    };
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private int estateidposition = 0;

    private void setAdapter(ArrayList<String> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }

        mEdtHouseName.setThreshold(1);
        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                R.layout.listview_item_select_popupwindow, dataList);
        mEdtHouseName.setDropDownWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mEdtHouseName.setDropDownHeight(ScreenUtils.dp2px(getContext(), 200));
        mEdtHouseName.setDropDownVerticalOffset(ScreenUtils.dp2px(getContext(), 5));
        mEdtHouseName.setDropDownBackgroundResource(R.color.transparent);
        mEdtHouseName.setAdapter(adapter);
        mEdtHouseName.showDropDown();
        mEdtHouseName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                estateidposition =  position ;
//                ToastUtil.showToast(position+"");
//                ToastUtil.showToast(mSelectedEstates.get(position).getEstateid());
//                estateNodeInfo = (EstateNodeInfo) parent.getItemAtPosition(position);
//                seachAddress(new LatLng(estateNodeInfo.lat,estateNodeInfo.lon));
//                clickLocation(new LatLng(estateNodeInfo.lat,estateNodeInfo.lon),getZoomlevel());

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        window.setLayout(ScreenUtils.getScreenWidth(getActivity()), ViewGroup.LayoutParams.WRAP_CONTENT);

        initViews();
    }

    public void onStart() {
        super.onStart();
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = dm.widthPixels;
        layoutParams.gravity = Gravity.TOP;
        layoutParams.y = ScreenUtils.dp2px(getContext(), 45);//设置gravity为TOP或BOTTOM时,相对的offset
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getDialog().getWindow().setAttributes(layoutParams);

    }

    private void initViews() {
        mapView = (TextureMapView) getView().findViewById(R.id.bmapView);
        mTxtAddress = (TextView) getView().findViewById(R.id.tv_current_address);
        mEdtHouseName = (AutoCompleteTextView) getView().findViewById(R.id.search_house);
        mLayoutRoot = (RelativeLayout) getView().findViewById(R.id.layout_root);
        RelativeLayout root = (RelativeLayout) getView().findViewById(R.id.dialog_root);
        SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(root);
        softKeyboardStateHelper.addSoftKeyboardStateListener(this);
        getView().findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(getHouseName())) {
                    ToastManager.manager.show("请选择楼盘");
                    return;
                }
                if (house == null) {
                    house = new House();
                    house.houseName = mEdtHouseName.getText().toString();
                    house.opestatelat = latitude;
                    house.opestatelon = longitude;
                    ;
//                    ToastUtil.showToast(mAllEstates.get(1).getEstateid());
                    house.estateid = mAllEstates.get(estateidposition).getEstateid();
                    if (estateNodeInfo != null) {
                        house.estateid = estateNodeInfo.getEstateid();
                        house.opestatedistrict = estateNodeInfo.getDistrictname();
                    }
                }
                MapDialogFragment.this.dismiss();
                NotificationCenter.defaultCenter.postNotification(NotificationKey.selectLoupan, house);

            }
        });
        mBaiduMap = mapView.getMap();
        View child = mapView.getChildAt(1);
        if (child != null
                && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        mapView.showZoomControls(false);

        mapView.invalidate();// 刷新地图
        // drawRealTimePoint(currentLatLng);
        // 定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        // 改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        // 地图状态改变相关监听
        mBaiduMap.setOnMapStatusChangeListener(this);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));

        mBaiduMap
                .setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int position = marker.getZIndex();
                        HouseCover houseCover = houseCovers.get(position);
                        if (getZoomlevel() >= 15) {
                            house = new House();
                            mEdtHouseName.setText(houseCover.name);
                            house.houseName = mEdtHouseName.getText().toString();
                            house.opestatelat = houseCover.lat;
                            house.opestatelon = houseCover.lon;
                            house.estateid = houseCover.id;
                            house.opestatedistrict = houseCover.estatename;
                        } else {
                            clickLocation(new LatLng(houseCover.lat, houseCover.lon), getZoomlevel() + 3);
                        }
//
                        return false;
                    }
                });

        locateHelper = new LocationHelper.Builder(getApplicationContext())
                .setScanSpan(0)
                .setIsNeedLocationDescribe(true).build();

        mLocationListener = new LocationHelper.LocationListener() {
            @Override
            public void onReceiveLocation(LocationHelper.LocationEntity location) {
                System.out.println(location);
                //  mCityName=location.getCity();
                mTxtAddress.setText(location.getAddrStr());
                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                updateMapState(latLng);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(" throwable " + e);
            }
        };
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 单击地图
             */
            public void onMapClick(LatLng point) {
                updateMapState(point);
            }

            /**
             * 单击地图中的POI点
             */
            public boolean onMapPoiClick(MapPoi poi) {
                return false;
            }
        });
        requestLocationPermissions();

        mEdtHouseName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String name = mEdtHouseName.getText().toString();
//                    ToastUtil.showToast(name);
                    getLoupanData(name);
                    return true;
                }
                return false;
            }
        });


        mEdtHouseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEdtHouseName.setFocusableInTouchMode(true);
                mEdtHouseName.requestFocus();
                mLayoutRoot.setVisibility(View.GONE);
                mLayoutRoot.setFocusable(false);
                mLayoutRoot.setFocusableInTouchMode(false);

            }
        });

        root.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > 0)) {
                    //软键盘弹出
                    LogUtils.d(TAG, "软键盘弹出");
                    SoftKeyboardStateHelper.showSoftKeyboard(mEdtHouseName);
                    String name = mEdtHouseName.getText().toString();
                    getLoupanData(name);
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > 0)) {
                    //软键盘关闭
                    LogUtils.d(TAG, "软键盘关闭");
                    mEdtHouseName.setFocusableInTouchMode(false);
                    mLayoutRoot.setVisibility(View.VISIBLE);
                    mLayoutRoot.setFocusable(true);
                    mLayoutRoot.setFocusableInTouchMode(true);
                    mLayoutRoot.requestFocus();

                }
            }
        });
    }

    private String getHouseName() {
        return mEdtHouseName.getText().toString();
    }

    private void location() {
        locateHelper.registerLocationListener(mLocationListener);
        locateHelper.start();
    }

    private void getLoupanData(String estatename) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("estatename", estatename);
            obj.put("cityid", AppContext.getInstance().getAppPref().cityId());
            ClientHelper clientHelper = new ClientHelper(
                    getContext(),
                    ServerUrl.METHOD_queryListPage, obj.toString(),
                    mHandler);
            clientHelper.setShowProgressMessage("正在获取楼盘数据, 请稍候...");
            clientHelper.isShowProgress(true);
            clientHelper.sendPost(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> getList(ArrayList<EstateNodeInfo> house) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < house.size(); i++) {
            list.add(house.get(i).getEstatename());
        }
        return list;
    }


    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        locateHelper.unRegisterLocationListener(mLocationListener);
        locateHelper.stop();
        // 退出时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);

        // activity 销毁时同时销毁地图控件
        mapView.onDestroy();

        mapView = null;
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    //region 定位相关
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSIONS)
    private void requestLocationPermissions() {
        if (!EasyPermissions.hasPermissions(this.getContext(), needPermissions)) {
            EasyPermissions.requestPermissions(this, "需要定位权限的权限", REQUEST_LOCATION_PERMISSIONS, needPermissions);
        } else {
            location();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        location();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private int getZoomlevel() {
        int maplevel = (int) mapView.getMap().getMapStatus().zoom;
        return maplevel;
    }
    // 点击定位
    public void clickLocation(LatLng currentLatLng, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(currentLatLng).zoom(zoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder
                .build()));
    }
    private void updateMapState(LatLng location) {
        mBaiduMap.clear();
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.location_pin_small);
        MarkerOptions ooA = new MarkerOptions().position(location).icon(bitmapDescriptor)
                .zIndex(9).draggable(true);
        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
        mBaiduMap.addOverlay(ooA);
        seachAddress(location);

    }
    private void seachAddress(final LatLng location) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(location));
            }
        }, 500);

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        // 地图操作的中心点
        LatLng cenpt = mapStatus.target;
        latitude = cenpt.latitude;
        longitude = cenpt.longitude;
        getHouseAddressByLocation(buildMain(), getZoomlevel());


    }

    private String buildMain() {
        StringBuilder stringBuilder = new StringBuilder();
        LatLng leftLatlng = getLeftTopLatLng();
        LatLng rightLatlng = getRightBottomLatLng();
        stringBuilder.append(leftLatlng.longitude).append(",")
                .append(leftLatlng.latitude).append(";")
                .append(rightLatlng.longitude).append(",")
                .append(rightLatlng.latitude).append(";");
        return stringBuilder.toString();
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        LogUtils.d("键盘打开");

    }

    // 获取左上角地理位置
    public LatLng getLeftTopLatLng() {
        Point pt = new Point();
        pt.x = 0;
        pt.y = 0;
        LatLng latLng = mapView.getMap().getProjection().fromScreenLocation(pt);
        return latLng;
    }

    // 获取右下角位置
    public LatLng getRightBottomLatLng() {
        Point pty = new Point();
        pty.x = ScreenUtils.getScreenWidth(getContext());
        pty.y = ScreenUtils.getScreenHeight(getContext());
        LatLng latLng = mapView.getMap().getProjection()
                .fromScreenLocation(pty);
        return latLng;
    }


    // 地图覆盖物
    private void initMapCover(ArrayList<HouseCover> houseCovers) {
        mBaiduMap.clear();
        for (int i = 0; i < houseCovers.size(); i++) {
            StringBuilder sb = new StringBuilder();
            HouseCover houseCover = houseCovers.get(i);
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.house_map_pop_layout, null);
            TextView mTxtName = (TextView) layout.findViewById(R.id.txt_name);
            TextView mTxtDesc = (TextView) layout.findViewById(R.id.txt_desc);
            mTxtName.setText(houseCover.name);
            int maplevel = (int) mapView.getMap().getMapStatus().zoom;
            if (maplevel > 15) {
                mTxtDesc.setText(("租" + houseCover.chuzunum + "售" + houseCover.chushounum));
            } else {
                mTxtDesc.setText((houseCover.chuzunum + houseCover.chushounum) + "套房源");
            }
            LatLng latlng = new LatLng(houseCover.lat, houseCover.lon);
            MarkerOptions markOption = new MarkerOptions().position(latlng)
                    .icon(getViewBitmap(layout));
            markOption.zIndex(i);
            // 掉下动画
            // markOption.animateType(MarkerOptions.MarkerAnimateType.drop).zIndex(10).draggable(true);
            mBaiduMap.addOverlay(markOption);
        }
    }

    // region 地图覆盖相关
    private BitmapDescriptor getViewBitmap(View addViewContent) {
        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());
        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // 根据经纬度查询房屋
    private void getHouseAddressByLocation(String main, int zoomlevel) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("main", main);
            obj.put("zoomlevel", zoomlevel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ClientHelper clientHelper = new ClientHelper(getActivity(),
                ServerUrl.METHOD_getListEstateMap, obj.toString(), mHandler,
                true);
        clientHelper.isShowProgress(true);
        clientHelper.setShowProgressMessage("正在获取...");
        clientHelper.sendPost(true);
    }

    @Override
    public void onSoftKeyboardClosed() {
        LogUtils.d("键盘关闭");
        mEdtHouseName.setFocusableInTouchMode(false);
        mLayoutRoot.setVisibility(View.VISIBLE);
        mLayoutRoot.setFocusable(true);
        mLayoutRoot.setFocusableInTouchMode(true);
        mLayoutRoot.requestFocus();


    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        // 获取反向地理编码结果
        PoiInfo mCurrentInfo = new PoiInfo();
        mCurrentInfo.address = reverseGeoCodeResult.getAddress();
        mCurrentInfo.location = reverseGeoCodeResult.getLocation();
        mCurrentInfo.name = reverseGeoCodeResult.getAddress();
        mTxtAddress.setText(mCurrentInfo.address);
    }
}
