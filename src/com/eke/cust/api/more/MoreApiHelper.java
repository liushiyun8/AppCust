package com.eke.cust.api.more;

import com.eke.cust.AppContext;
import com.eke.cust.api.ApiHelper;
import com.eke.cust.api.BaseRestApi;
import com.eke.cust.net.ServerUrl;

import org.json.JSONException;
import org.json.JSONObject;

import foundation.callback.ICallback1;
import foundation.util.AppUtils;

import static com.eke.cust.net.ServerUrl.getAbsoluteUrl;

/**
 * Created by wujian on 2017/5/3.
 */

public class MoreApiHelper {

    private BaseRestApi baseRestApi;
    private ICallback1<BaseRestApi> restApiCallback;

    public MoreApiHelper(ICallback1<BaseRestApi> restApiCallback) {
        this.restApiCallback = restApiCallback;
    }

    /**
     * 关于页面
     */

    public void getAboutUs() {
        baseRestApi = new BaseRestApi(getAbsoluteUrl(ServerUrl.METHOD_getAboutUs)) {
            @Override
            protected JSONObject requestJson() throws JSONException {
                JSONObject requestJson = new JSONObject();
                requestJson.put("paramdata", AppUtils.getVersionName(AppContext.getInstance()));
                return requestJson;
            }
        };

        ApiHelper.callApi(baseRestApi, restApiCallback);

    }
    /**
     * 获取配置
     *
     * @param refname PropertyTax 税费说明
     *                  PropertyLook 看房方式
     *                  PropertyDirection 朝向
     *                  PropertyDecoration 装修程度
     *                  PropertyFurniture
     */

    public void queryListReference(final String refname) {
        baseRestApi = new BaseRestApi(getAbsoluteUrl(ServerUrl.METHOD_getTableEnumOne)) {
            @Override
            protected JSONObject requestJson() throws JSONException {
                JSONObject requestJson = new JSONObject();
                requestJson.put("tableName", refname);
                return requestJson;
            }
        };
        baseRestApi.setTag(refname);//一个接口多个不同请求参数时设置的判断值
        ApiHelper.callApi(baseRestApi, restApiCallback);

    }

}
