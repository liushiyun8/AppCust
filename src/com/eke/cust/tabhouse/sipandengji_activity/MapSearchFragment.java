package com.eke.cust.tabhouse.sipandengji_activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.service.LocationService;
import com.eke.cust.widget.AutoCompleteEditText;

import foundation.base.fragment.BaseFragment;

/**
 * Created by yangjie on 2016/5/15.
 */
public class MapSearchFragment extends BaseFragment implements
        BaiduMap.OnMapStatusChangeListener {
    private TextView mTxtAddress;
    private TextureMapView mapView;
    private AutoCompleteEditText mEdtAddress;

    private BaiduMap mBaiduMap;

    public double latitude = 0.0;
    public double longitude = 0.0;


    /**
     * 是否是第一次定位
     */
    private boolean isFirstLoc = true;

    public LocationService locationService;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_search, null);
        return view;
    }

    private String address = "";

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        /***,
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(AppContext.getInstance()
                .getApplicationContext());
        locationService.start();
        locationService.registerListener(mListener);
        initViews();

    }


    private void initViews() {
        mapView= (TextureMapView) getView().findViewById(R.id.bmapView);
        mTxtAddress= (TextView) getView().findViewById(R.id.tv_current_address);
        mBaiduMap = mapView.getMap();
        View child = mapView.getChildAt(1);
        if (child != null
                && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        mapView.showZoomControls(false);


        mapView.invalidate();// 刷新地图
        mBaiduMap = mapView.getMap();
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


        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));

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

                    isFirstLoc = false;
                    LatLng latLng = new LatLng(bdLocation.getLatitude(),
                            bdLocation.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(latLng).zoom(18.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    Address address=bdLocation.getAddress();
                  //  mTxtAddress.setText(address.city+address.country+address.street);
                    mTxtAddress.setText("湖北省武汉市黄陂区");
                    // drawRealTimePoint(locationLatLng);
                    // 获取城市，待会用于POISearch

                }
            }

        }

    };



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
}
