package com.eke.cust.http;

import okhttp3.OkHttpClient;



public class OkHttpUtil {

    private static OkHttpClient mHttpClient;


    public static OkHttpClient getOkHttpClient() {
        mHttpClient = new OkHttpClient();
//        setStetho(mHttpClient);
        return mHttpClient;
    }




}
