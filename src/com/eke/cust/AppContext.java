package com.eke.cust;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.support.multidex.MultiDex;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.eke.cust.api.ApiHelper;
import com.eke.cust.bean.LaunchModel;
import com.eke.cust.chat.ChatHelper;
import com.eke.cust.crashhandler.CustomCrashHandler;
import com.eke.cust.http.OkHttpUtil;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.model.UserLocation;
import com.eke.cust.preference.AppPref;
import com.eke.cust.service.LocationService;
import com.eke.cust.tabhouse.HouseSourceNodeInfo;
import com.eke.cust.tabmine.EstateNodeInfo;
import com.eke.cust.utils.ACache;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.GlobalSPA;
import com.eke.cust.utils.MyLog;
import com.eke.cust.utils.TransformUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import app.BaseAppContext;
import foundation.SimpleCache;
import okhttp3.OkHttpClient;

public class AppContext extends BaseAppContext {
    private String TAG = "AppContext";

    public static Context applicationContext;
    private static AppContext instance;

    private static ACache acache;
    private List<EstateNodeInfo> mUserDaiLiEstates = new ArrayList<EstateNodeInfo>();
    // login user name
    public final String PREF_USERNAME = "username";
    static SharedPreferences userinfo;

    public static String currentUserNick = "";

    public static DisplayImageOptions mDisplayImageOptions_no_round_corner;
    public static DisplayImageOptions mDisplayImageOptions_with_round_corner;

    public Executor mThreadPool = defaultHttpExecutor();
    public OkHttpClient mHttpClient;

    public static boolean mIsProfileHeadUpdated = false;

    public static HouseSourceNodeInfo mHouseSourceNodeSetFengMian;

    // 百度地图
    public LocationService locationService;
    private UserLocation userlocation;

    private AppPref _appPref;


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        acache = ACache.get(this);
        mHttpClient = OkHttpUtil.getOkHttpClient();
        _appPref = new AppPref(this);
        ChatHelper.getInstance().init(applicationContext);
        ApiHelper.initHttpClient(this);
        mDisplayImageOptions_no_round_corner = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();

        mDisplayImageOptions_with_round_corner = new DisplayImageOptions.Builder()

                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(
                        new RoundedBitmapDisplayer(DensityUtil.dip2px(
                                applicationContext, 40))).build();

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(this);

        locationService.start();
        locationService.registerListener(mListener);


        CustomCrashHandler crashHandler = CustomCrashHandler.getInstance();
        crashHandler.init(this);
        SDKInitializer.initialize(this);

        //app 更新sdk
        //环信对域名进行校验 导致了下载域名无法识别
        HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {

                return true;
            }
        });
//        Beta.autoInit = false;//true表示app启动自动初始化升级模块; false不会自动初始化;
        Beta.autoCheckUpgrade = false;//设置不自动检查

        Bugly.init(getApplicationContext(), "e7f89a74d7", false);
        imageLoaderInit();
    }

    public static SharedPreferences getSharedPreferences() {

        return userinfo;
    }

    /**
     * 初始化mageloader
     */
    private void imageLoaderInit() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
                .cacheInMemory(true) //
                .cacheOnDisk(true) //
                /* .bitmapConfig(Bitmap.Config.RGB_565) */
                .build();//
        int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
        int cacheSize = maxMemory / 16;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration//
                .Builder(getApplicationContext())
                //
                .defaultDisplayImageOptions(defaultOptions)
                //
                .diskCacheSize(500 * 1024 * 1024)
                //
                .memoryCacheSize(cacheSize)
                .diskCacheFileCount(500)
                .writeDebugLogs()
                //
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .threadPoolSize(Thread.NORM_PRIORITY - 2)
                // .memoryCache(new WeakMemoryCache())
                .build();//
        ImageLoader.getInstance().init(config);
    }

    public static AppContext getInstance() {
        return instance;
    }

    public static ACache getACache() {
        if (acache == null) {
            acache = ACache.get(getInstance());
        }
        return acache;
    }

    public void setUserDaiLiEstates(List<EstateNodeInfo> mUserDaiLiEstates) {
        this.mUserDaiLiEstates = mUserDaiLiEstates;
    }


    public AppPref getAppPref() {
        if (_appPref == null) {
            _appPref = new AppPref(this);
        }
        return _appPref;
    }

    public List<EstateNodeInfo> getUserDaiLiEstates() {
        List<EstateNodeInfo> mUserDaiLiEstates = new ArrayList<EstateNodeInfo>();
        JSONArray array_listEstate = getACache().getAsJSONArray(
                GlobalSPA.KEY_USERDAILIESTATES);
        for (int i = 0; i < array_listEstate.length(); i++) {
            JSONObject object;
            try {
                object = array_listEstate.getJSONObject(i);
                if (object != null) {
                    try {
                        EstateNodeInfo node = TransformUtil.getEntityFromJson(
                                object, EstateNodeInfo.class);
                        if (node != null) {
                            mUserDaiLiEstates.add(node);
                        }

                    } catch (InstantiationException e) {
                        // TODO Auto-generated catch
                        // block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch
                        // block
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }
        return mUserDaiLiEstates;
    }





    public void addUserDaiLiEstate(EstateNodeInfo object) {
        if (this.mUserDaiLiEstates != null) {
            this.mUserDaiLiEstates.add(object);
        }

    }

    Executor defaultHttpExecutor() {
        return Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process
                                .setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        r.run();
                    }
                }, "ec-IDLE");
            }
        });
    }

    public boolean isLogin() {
        return !_appPref.userToken().equals(Constants.DEFAULT_TOKEN);
    }


    public void loginout() {
        _appPref.setUserToken("");
        _appPref.setUser(null);
        _appPref.setUserPhone("");
        _appPref.setUserPassword("");
    }

    public void onLogin(CurrentUser user) {
        _appPref.setUser(user);
        _appPref.setUserToken(user.token);
        _appPref.setUserPhone(user.custtel);

    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    // 获取用户的地理位置
    public UserLocation getUserLocation() {
        return userlocation;

    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    public BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location
                    && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");
                sb.append(location.getCityCode());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                sb.append("\nDistrict : ");
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");
                sb.append(location.getStreet());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\nDescribe: ");
                sb.append(location.getLocationDescribe());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());
                sb.append("\nPoi: ");
                if (location.getPoiList() != null
                        && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
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
                userlocation = new UserLocation();
                userlocation.mLongitude = location.getLongitude();
                userlocation.mLatitude = location.getLatitude();
                userlocation.city = location.getCity();
                userlocation.citycode = location.getCityCode();
                userlocation.address = location.getAddrStr();
                AppContext.getInstance().getAppPref()
                        .setUserLocation(userlocation);

                MyLog.d(TAG, "latitude: " + userlocation.mLatitude);
                MyLog.d(TAG, "longitude: " + userlocation.mLongitude);
                locationService.unregisterListener(mListener); // 注销掉监听
                locationService.stop(); // 停止定位服务

            }
        }

    };



}
