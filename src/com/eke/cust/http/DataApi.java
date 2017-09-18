package com.eke.cust.http;


import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * title: 
 * author: liudx
 * since: 2015/6/9 11:10
 * project: ccplus
 */
public class DataApi {
    public static  void  uploadFile(File file , String upload, Callback<String> callback){
        MultipartBody requestBody = new MultipartBody.Builder("AaB03x")
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", null, new MultipartBody.Builder("BbC04y")
                        .addPart(Headers.of("Content-Disposition", "form-data; filename=\"img.png\""),
                                RequestBody.create(MediaType.parse("image/png"), file))
                        .build())
                .build();


        CCPlusAPI.getInstance().requestPOST(callback, upload, requestBody, String.class);
    }


}
