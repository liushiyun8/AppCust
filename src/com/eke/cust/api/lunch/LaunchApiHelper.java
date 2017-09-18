package com.eke.cust.api.lunch;

import com.eke.cust.api.ApiHelper;
import com.eke.cust.api.BaseRestApi;

import foundation.callback.ICallback1;

import static com.eke.cust.net.ServerUrl.METHOD_getLinkextSwitch;

/**
 * Created by wujian on 2017/4/16.
 * <p>
 * 启动页相关api
 */

public class LaunchApiHelper {

    public static  void  getLinkextSwitch(ICallback1<BaseRestApi> apiICallback1) {

        BaseRestApi baseRestApi = new BaseRestApi(METHOD_getLinkextSwitch);
        ApiHelper.callApi(baseRestApi, apiICallback1);
    }
}
