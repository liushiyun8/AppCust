package com.eke.cust.http;

import com.eke.cust.AppContext;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * Created by luffyjet on 2015/5/20.
 * project: PixivAPI
 * site: www.luffyjet.com
 */
public class CCPlusAPI {

    private static CCPlusAPI instance = new CCPlusAPI();

    private Executor httpExecutor = AppContext.getInstance().mThreadPool;

    private OkHttpClient mHttpClient = AppContext.getInstance().mHttpClient;

    private CCPlusAPI() {
    }

    public enum Method {
        GET, POST
    }

    public static CCPlusAPI getInstance() {
        return instance;
    }

    public OkHttpClient getOkHttpClient() {
        return mHttpClient;
    }


    public void requestGET(Callback<?> callback, String url, Class<?> clz) {
        requestCall(callback, Method.GET, url, null, null, clz);
    }

    public void requestPOST(Callback<?> callback, String url, RequestBody requestBody, Class<?> clz) {
        requestCall(callback, Method.POST, url, null, requestBody, clz);
    }


    @SuppressWarnings("unchecked")
    private void requestCall(final Callback<?> callback, final Method method, final String url, final Map<String, String> headers, final RequestBody requestBody, final Class<?> clz) {
        httpExecutor.execute(new CallbackRunnable(callback) {
            @Override
            public ResponseWrapper obtainResponse() {
                return invokeRequest(method, url, headers, requestBody, clz);
            }
        });
    }

    public ResponseWrapper invokeRequest(Method method, String url, Map<String, String> headers, RequestBody requestBody, Class<?> clz) {
        Request request = null;

        Request.Builder builder = new Request.Builder();

        if (null != headers) {
            for (String key : headers.keySet()) {
                builder.header(key, headers.get(key));
            }
        }

        try {
            if (Method.GET == method) {
                request = builder.url(url).build();
            } else if (Method.POST == method && null != requestBody) {
                request = builder.url(url).post(requestBody).build();
            }

            Response response = mHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {

                if (response.body() == null) {
                    return new ResponseWrapper(response, null);
                }

//                try {
//                    Object convert = GsonUtil.convert(response.body().string(), clz);
                	
                    return new ResponseWrapper(response, response.body().string());
//                } 
            }
            throw HttpError.httpError(url, response, clz);
        } catch (HttpError e) {
            throw e; // Pass through our own errors.
        } catch (IOException e) {
            throw HttpError.networkError(url, e);
        } catch (Throwable t) {
            throw HttpError.unexpectedError(url, t);
        } finally {

        }
    }


}
