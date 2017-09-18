package com.eke.cust.api.auth;

import com.eke.cust.api.BaseRestApi;

import foundation.callback.ICallback1;

/**
 * Created by wujian on 2017/4/18.
 */

public class AuthApiHelper {

    private BaseRestApi baseRestApi;
    private ICallback1<BaseRestApi> restApiCallback;

    public AuthApiHelper(ICallback1<BaseRestApi> restApiCallback) {
        this.restApiCallback = restApiCallback;
    }


}
