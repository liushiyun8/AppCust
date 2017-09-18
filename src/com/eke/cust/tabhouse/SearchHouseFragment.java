package com.eke.cust.tabhouse;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
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
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.UserLocationHelper;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.model.HouseCover;
import com.eke.cust.model.OpenCity;
import com.eke.cust.model.UserLocation;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.service.LocationService;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.JSONUtils;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.ScreenUtils;
import com.eke.cust.utils.StringCheckHelper;
import com.eke.cust.widget.AutoCompleteEditText;
import com.fab.FloatingActionButton;

import org.droidparts.annotation.inject.InjectView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foundation.base.adapter.CommonListAdapter;
import foundation.base.adapter.ViewHolder;
import foundation.base.fragment.BaseFragment;
import foundation.log.LogUtils;
import foundation.notification.NotificationCenter;
import foundation.toast.ToastManager;
import foundation.widget.SoftKeyboardStateHelper;

import static com.eke.cust.Constants.DEFAULT;
import static com.eke.cust.R.style.dialog;

/**
 * Created by yangjie on 2016/5/15.
 */
public class SearchHouseFragment extends BaseFragment implements
        View.OnClickListener, BaiduMap.OnMapStatusChangeListener, AutoCompleteEditText.OnClickItemListener, OnGetGeoCoderResultListener, SoftKeyboardStateHelper.SoftKeyboardStateListener {

    @InjectView(id = R.id.nav_bar_more, click = true)
    private ImageView mIvMore;
    @InjectView(id = R.id.bmapView)
    private MapView mapView;
    @InjectView(id = R.id.search_house)
    private AutoCompleteTextView mEdtHouseName;

    @InjectView(id = R.id.iv_location, click = true)
    private ImageView mIvLocation;
    @InjectView(id = R.id.txt_location, click = true)
    private TextView mTxtTitle;
    @InjectView(id = R.id.layout_head)
    private RelativeLayout mLayoutHead;
    @InjectView(id = R.id.layout_root)
    private RelativeLayout mLayoutRoot;
    // 显示或者隐藏底部按钮
    @InjectView(id = R.id.iv_showbottom, click = true)
    private FloatingActionButton mIvShowBottom;

    private BaiduMap mBaiduMap;

    private double latitude = 0.0;
    private double longitude = 0.0;
    /**
     * 是否是第一次定位
     */
    private boolean isFirstLoc = true;

    public LocationService locationService;
    private String cityId;
    private int defaultZoom = 16;

    /**
     * 反地理编码
     */
    private GeoCoder mSearch;

    SupportMapFragment map;
    // 已经开放的城市
    private ArrayList<OpenCity> cities = new ArrayList<OpenCity>();
    // 覆盖物
    private ArrayList<HouseCover> houseCovers = new ArrayList<HouseCover>();

    SoftKeyboardStateHelper softKeyboardStateHelper;
    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {

                    case UserLocationHelper.MSG_GLOBAL_LOCATION_FAIL: {

                    }
                    break;
                    case Constants.NO_NETWORK:
                        break;
                    case DEFAULT:
                        if (houseCovers.size() == 0) {
                            ToastUtils.show(getActivity(), "没有相关楼盘信息");
                        } else {
                            setAdapter(getList(houseCovers));
                        }
                        break;

                    case Constants.TAG_SUCCESS:
                        hideLoading();
                        Bundle bundle = msg.getData();
                        String request_url = bundle.getString("request_url");
                        String resp = bundle.getString("resp");
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            String result = jsonObject.optString("result", "");
                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                if (request_url.equals(ServerUrl.METHOD_queryListCity)) {
                                    JSONArray jsonArray = JSONUtils.getJSONArray(jsonObject, "data", null);
                                    if (jsonArray != null) {
                                        cities = JSONUtils.getObjectList(jsonArray, OpenCity.class);
                                        //  initCity();
                                    }

                                } else if (request_url
                                        .equals(ServerUrl.METHOD_getListEstateMap)) {
                                    JSONArray jsonArray = JSONUtils.getJSONArray(
                                            jsonObject, "data", null);
                                    if (jsonArray != null) {
                                        houseCovers = JSONUtils.getObjectList(
                                                jsonArray, HouseCover.class);
                                    }
                                    initMapCover(houseCovers);
                                } else if (request_url
                                        .equals(ServerUrl.METHOD_queryListByName)) {
                                    hideLoading();

                                    JSONObject obj_data = jsonObject.optJSONObject("data");
                                    if (obj_data != null) {
                                        JSONArray array_data = obj_data.optJSONArray("data");
                                        if (array_data != null) {
                                            houseCovers = JSONUtils.getObjectList(array_data, HouseCover.class);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mHandler.sendEmptyMessage(DEFAULT);
                                                }
                                            }, 200);

                                        }


                                    }
                                }
                            } else if (result.equals(Constants.RESULT_ERROR)) {
                                hideLoading();
                                String errorMsg = jsonObject.optString("errorMsg", "出错!");
                                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            hideLoading();

                            e.printStackTrace();
                            ToastUtils
                                    .show(getActivity(), R.string.http_json_error);
                        }
                        break;

                    case Constants.TAG_FAIL:
                        ToastUtils.show(getActivity(), R.string.http_error);
                        hideLoading();

                        break;
                    case Constants.TAG_EXCEPTION:
                        hideLoading();

                        ToastUtils.show(getActivity(), R.string.http_error);

                        break;
                }

            }
        }
    };


    @Override
    protected View onCreateContentView() {
        View view = inflateContentView(R.layout.fragment_search_house);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        softKeyboardStateHelper = new SoftKeyboardStateHelper(_rootView);
        softKeyboardStateHelper.addSoftKeyboardStateListener(this);
        /***
         * , 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(AppContext.getInstance()
                .getApplicationContext());
        locationService.start();
        locationService.registerListener(mListener);
        cityId = AppContext.getInstance().getAppPref().cityId();
        initViews();
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        getCityList();

    }


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
                HouseCover estateNodeInfo = houseCovers.get(position);

                clickLocation(new LatLng(estateNodeInfo.lat, estateNodeInfo.lon), getZoomlevel());
            }
        });
    }

    private void getCityLocation(String cityName, String address) {
        // 创建GeoCoder实例对象
        showLoading();
        // 发起反地理编码请求(经纬度->地址信息)
        mSearch.geocode(new GeoCodeOption().city(
                cityName).address(cityName));

    }

    private void getCityList() {
        JSONObject obj = new JSONObject();
        ClientHelper clientHelper = new ClientHelper(getActivity(),
                ServerUrl.METHOD_queryListCity, obj.toString(), mHandler, true);
        clientHelper.isShowProgress(false);
        clientHelper.setShowProgressMessage("正在获取...");
        clientHelper.sendPost(true);
    }

    // 根据经纬度查询房屋
    private void getHouseAddressByLocation(String main, int zoomlevel) {
        showLoading();
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

    private void initViews() {

        mBaiduMap = mapView.getMap();
        View child = mapView.getChildAt(1);
        if (child != null
                && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        mapView.showZoomControls(false);
        setTtitle();

        mapView.invalidate();// 刷新地图
        mBaiduMap = mapView.getMap();
        // 获取坐标，待会用于POI信息点与定位的距离
        currentLatLng = new LatLng(latitude, longitude);
        // drawRealTimePoint(currentLatLng);
        // 定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().zoom(defaultZoom).build();

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
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
                            UIHelper.startActivity(mContext,
                                    RentalHouseActivity.class, houseCover);
                        } else {
                            clickLocation(new LatLng(houseCover.lat, houseCover.lon), getZoomlevel() + 3);
                        }

//
                        return false;
                    }
                });


//
        mEdtHouseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.d(TAG, "hasFocus=" + hasFocus);
                if (hasFocus) {//如果有焦点就显示软件盘
                    SoftKeyboardStateHelper.showSoftKeyboard(mEdtHouseName);
                    String name = mEdtHouseName.getText().toString();
                    if (!TextUtils.isEmpty( name )){
                        getLoupanData(name);
                    }
                } else {//否则就隐藏

                }
            }
        });
        mEdtHouseName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String name = mEdtHouseName.getText().toString();
                    getLoupanData(name);
                    return true;
                }
                return false;
            }
        });

        mEdtHouseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    getLoupanData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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


    }

    private void setTtitle() {
        UserLocation userLocation = AppContext.getInstance().getAppPref()
                .getUserLocation();
        if (userLocation != null && !StringCheckHelper.isEmpty(userLocation.city)) {
            if (userLocation.city.contains("市")) {
                String city = userLocation.city.replace("市", "");
                mTxtTitle.setText(city);

            } else {
                mTxtTitle.setText(userLocation.city);

            }
        } else {
            mTxtTitle.setText("深圳");

        }
    }


    public void setCity(String city) {
        mTxtTitle.setText("深圳");
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        locationService.unregisterListener(mListener); // 注销掉监听
        locationService.stop(); // 停止定位服务
        // 退出时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);

        // activity 销毁时同时销毁地图控件
        mapView.onDestroy();

        mapView = null;
        // 释放资源
        if (mSearch != null) {
            mSearch.destroy();
        }
    }

    private boolean isShowBottom = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_location:
                showHouseList();
                break;
            case R.id.nav_bar_more:
                clickMore();
                break;
            case R.id.iv_location:
                String cityName = AppContext.getInstance().getAppPref().getUserLocation().city;
                mTxtTitle.setText(!StringCheckHelper.isEmpty(cityName) ? cityName : "深圳");
                clickLocation(currentLatLng, defaultZoom);
                break;
            case R.id.iv_showbottom:
                if (SearchHouseFragment.this.isVisible()) {
                    NotificationCenter.defaultCenter
                            .postNotification(NotificationKey.show_bottom);

                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (SearchHouseFragment.this.isVisible()) {
                            NotificationCenter.defaultCenter
                                    .postNotification(NotificationKey.show_bottom);

                        }

                    }
                }, 5000);
                break;

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
        getHouseAddressByLocation(buildMain(), getZoomlevel());
        if (SearchHouseFragment.this.isVisible()) {
            NotificationCenter.defaultCenter
                    .postNotification(NotificationKey.show_bottom);

        }

    }

    private LatLng currentLatLng;
    private String currentCity;
    private Marker currentMarker;
    private OpenCity city;

    // 点击定位
    public void clickLocation(LatLng currentLatLng, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(currentLatLng).zoom(zoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder
                .build()));
        hideLoading();
    }

    // 房源列表
    public void showHouseList() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dlg_city, null, false);
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setAnimationStyle(R.style.AnimationTopFade);//设置动画
        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(true);   //设置外部点击关闭ppw窗口
        popWindow.setFocusable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效

        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(mLayoutHead, 0, 0);

        final ListView listview = (ListView) view.findViewById(R.id.recycleview);

        listview.setAdapter(new CommonListAdapter<OpenCity>(getActivity(),
                R.layout.item_search_house_menu, cities) {
            @Override
            public void convert(ViewHolder holder, OpenCity openCity,
                                int position) {
                ImageView mIvIcon = holder.findViewById(R.id.iv_city_icon);
                TextView mTxtName = holder.findViewById(R.id.txt_city_name);
                mTxtName.setText(openCity.txt);
                if (StringCheckHelper.isEmpty(openCity.cityicon)) {
                    mIvIcon.setImageBitmap(BitmapUtils.stringtoBitmap(cities.get(0).cityicon));
                } else {
                    mIvIcon.setImageBitmap(BitmapUtils.stringtoBitmap(openCity.cityicon));

                }
                mIvIcon.setVisibility(View.GONE);
                holder.findViewById(R.id.menu_devid).setVisibility(View.GONE);

            }
        });
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                city = (OpenCity) parent.getItemAtPosition(position);
                cityId = city.val;
                mTxtTitle.setText(city.txt);
                getCityLocation(city.txt, city.val);
                houseCovers = null;
                popWindow.dismiss();
            }
        });

    }


    // 点击更多
    public void clickMore() {
        final Dialog dlg = new Dialog(getActivity(), dialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewContent = inflater
                .inflate(R.layout.dlg_tab_house_action, null);
        final RelativeLayout rl_page = (RelativeLayout) viewContent
                .findViewById(R.id.rl_page);
        final ListView listview = (ListView) viewContent
                .findViewById(R.id.listview_action);

        rl_page.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.dismiss();
            }
        });

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), getData(),
                R.layout.layout_tab_search_house_action_list_item,
                new String[]{"action"}, new int[]{R.id.tv_action});

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                dlg.dismiss();

                switch (position) {

                    case 0:// 条件查找
                    {
                        UIHelper.startActivity(getActivity(),
                                FindConditionsActivity.class);
                    }
                    break;

                    default:
                        break;
                }
            }
        });

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                }
                return false;
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.RIGHT | Gravity.TOP);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = dm.widthPixels;
        lp.height = dm.heightPixels;
        lp.dimAmount = 0.5f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);

        dlg.show();
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map = new HashMap<String, Object>();
        map.put("action", "条件查找");
        list.add(map);
        return list;
    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // TODO Auto-generated method stub
            if (null != bdLocation
                    && bdLocation.getLocType() != BDLocation.TypeServerError) {
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

                    // 设置定位数据
                    mBaiduMap.setMyLocationData(data);
                    // 获取坐标，待会用于POI信息点与定位的距离
                    currentLatLng = new LatLng(bdLocation.getLatitude(),
                            bdLocation.getLongitude());
                    UserLocation userlocation = new UserLocation();
                    userlocation.mLongitude = bdLocation.getLongitude();
                    userlocation.mLatitude = bdLocation.getLatitude();
                    userlocation.city = bdLocation.getCity();
                    userlocation.citycode = bdLocation.getCityCode();
                    userlocation.address = bdLocation.getAddrStr();
                    MyLog.d(TAG, "当前城市" + bdLocation.getCity());
                    AppContext.getInstance().getAppPref()
                            .setUserLocation(userlocation);
                    isFirstLoc = false;
                    LatLng latLng = new LatLng(bdLocation.getLatitude(),
                            bdLocation.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(latLng).zoom(16.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                            .newMapStatus(builder.build()));
                    getHouseAddressByLocation(buildMain(), 16);

                    // drawRealTimePoint(locationLatLng);
                    // 获取城市，待会用于POISearch

                }
            }

        }

    };

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

    // 获取左上角地理位置
    public LatLng getLeftTopLatLng() {
        Point pt = new Point();
        pt.x = 0;
        pt.y = 0;
        LatLng latLng = null;
        try {
            latLng = mapView.getMap().getProjection().fromScreenLocation(pt);
        }catch (Exception e){
            e.printStackTrace();
        }

        return latLng;
    }

    // 获取右下角位置
    public LatLng getRightBottomLatLng() {
        Point pty = new Point();
        pty.x = ScreenUtils.getScreenWidth(mContext);
        pty.y = ScreenUtils.getScreenHeight(mContext);
        LatLng latLng = null;
        try {
            latLng = mapView.getMap().getProjection()
                    .fromScreenLocation(pty);
        }catch (Exception e){
            e.printStackTrace();
        }

        return latLng;
    }

    private String buildMain() {
        StringBuilder stringBuilder = new StringBuilder();
        LatLng leftLatlng = getLeftTopLatLng();
        LatLng rightLatlng = getRightBottomLatLng();
        if (leftLatlng!=null&&rightLatlng!=null){
            stringBuilder.append(leftLatlng.longitude).append(",")
                    .append(leftLatlng.latitude).append(";")
                    .append(rightLatlng.longitude).append(",")
                    .append(rightLatlng.latitude).append(";");
        }

        return stringBuilder.toString();

    }

    // 地图覆盖物
    private void initMapCover(ArrayList<HouseCover> houseCovers) {
        mBaiduMap.clear();
        for (int i = 0; i < houseCovers.size(); i++) {
            StringBuilder sb = new StringBuilder();
            HouseCover houseCover = houseCovers.get(i);
            LinearLayout layout = (LinearLayout) inflateContentView(R.layout.map_pop_layout);
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

    // endregion
    private void getLoupanData(String estatename) {
//        if (houseCovers == null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("estatename", estatename);
                obj.put("cityid", cityId);
                ClientHelper clientHelper = new ClientHelper(
                        mContext,
                        ServerUrl.METHOD_queryListByName, obj.toString(),
                        mHandler);
                clientHelper.setShowProgressMessage("正在获取楼盘数据, 请稍候...");
                clientHelper.isShowProgress(true);
                clientHelper.sendPost(true);

            } catch (JSONException e) {
                e.printStackTrace();
            }
//        } else {
//            setAdapter(getList(houseCovers));
//        }


    }

    private ArrayList<String> getList(ArrayList<HouseCover> house) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < house.size(); i++) {
            list.add(house.get(i).estatename);
        }
        return list;
    }

    //根据名字查房源

    public HouseCover getHouse(String name) {
        HouseCover houseCover = null;
        for (int i = 0; i < houseCovers.size(); i++) {
            HouseCover house = houseCovers.get(i);
            if (name.equals(house.estatename)) {
                houseCover = house;
                break;
            }
        }

        return houseCover;
    }


    @Override
    public void onClickListener(String name) {
        HouseCover houseCover = getHouse(name);
        if (houseCover != null) {
            clickLocation(new LatLng(houseCover.lat, houseCover.lon), getZoomlevel());

        }

    }


    private int getZoomlevel() {
        int maplevel = (int) mapView.getMap().getMapStatus().zoom;

        return maplevel;
    }


    //zoomlevel=12以下(含)：范围内的区县；13-14：片区；15-19：楼盘；初始化or定位=16（楼盘级）

    public void addCustomElements() {
        // 添加多边形
        LatLng pt1 = new LatLng(39.93923, 116.357428);
        LatLng pt2 = new LatLng(39.91923, 116.327428);
        LatLng pt3 = new LatLng(39.89923, 116.347428);
        LatLng pt4 = new LatLng(39.89923, 116.367428);
        LatLng pt5 = new LatLng(39.91923, 116.387428);
        List<LatLng> pts = new ArrayList<LatLng>();
        pts.add(pt1);
        pts.add(pt2);
        pts.add(pt3);
        pts.add(pt4);
        pts.add(pt5);
        OverlayOptions ooPolygon = new PolygonOptions().points(pts)
                .stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAA00FF00);
        mBaiduMap.addOverlay(ooPolygon);


    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        hideLoading();
        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {

            ToastManager.manager.show("抱歉，未能找到结果");
            return;
        }
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(geoCodeResult
                .getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                geoCodeResult.getLocation().latitude, geoCodeResult.getLocation().longitude);
        LogUtils.d("result", strInfo);

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        hideLoading();

    }


    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        LogUtils.d("键盘打开");

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
}
