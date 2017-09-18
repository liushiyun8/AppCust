package com.eke.cust.helper;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by wujian on 2017/4/16.
 */

public class LocationHelper {
    private LocationClient mLocationClient = null;

    /**
     * 接受百度地图定位的回调类，该类是派生于百度地图的一个地图定位的监听类，用于定位后信息的返回。
     */
    private BDLocationListener mMyBDLocationListener = null;

    /**
     * 自定义的定位回调接口监听器,该接口是自定义的一个接口，用于在使用该对象时，把定位信息进行回调;
     */
    private BDLocationListener mLocationListener = null;

    private Context ct;

    public LocationHelper(Context ct) {

        this.ct = ct;
    }

    /**
     * 设置自定义的监听器;
     *
     * @param locationListener
     */
    public void setMrLocationListener(BDLocationListener locationListener) {

        this.mLocationListener = locationListener;
    }

    /**
     * 停止定位;
     */
    public void stop() {

        if (mLocationClient != null) {

            mLocationClient.stop();
        }
    }

    /**
     * 启动定位;
     */
    public void start() {

        //新建百度地图定位客户端类；

        mLocationClient = new LocationClient(ct);
        mMyBDLocationListener = new BDLocationListener();

        //注册监听器；

        mLocationClient.registerLocationListener(mMyBDLocationListener);


        //创建定位选项；

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);            // 打开gps;
        option.setCoorType("bd09ll");   // 设置坐标类型;
        option.setScanSpan(1000);         // 设置定位间隙，每隔1秒定位一次；
        option.setAddrType("all");         // 注意：该参数是设置地址类型，如果不设置就获取不到地址信息；

        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * 实现百度地图的接口;
     *
     * @author Render;
     */
    public class BDLocationListener implements com.baidu.location.BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (mLocationListener != null) {

                //回调自定义的监听对象；

                mLocationListener.onReceiveLocation(location);
            }
        }


    }
}
