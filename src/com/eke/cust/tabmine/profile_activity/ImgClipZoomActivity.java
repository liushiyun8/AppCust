package com.eke.cust.tabmine.profile_activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.http.Callback;
import com.eke.cust.http.DataApi;
import com.eke.cust.http.HttpError;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.notification.NotificationKey;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.DialogUtil;
import com.eke.cust.utils.FileUtils;
import com.eke.cust.utils.MyLog;
import com.eke.cust.widget.imgclipzoom.ClipImageLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import foundation.notification.NotificationCenter;
import okhttp3.Response;



public class ImgClipZoomActivity extends BaseActivity {
    private ClipImageLayout mClipImageLayout;
    private DialogUtil mDialogUtil;
    private static final int UPLOAD_SUCCESS = 10;
    private static final int UPLOAD_FAIL = 11;

    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);

            if (msg != null) {
                switch (msg.what) {
                    case UPLOAD_SUCCESS: {
                        mDialogUtil.dissmissProgressDialog();
                        Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
                        AppContext.mIsProfileHeadUpdated = true;
                        finish();
                    }
                    break;

                    case UPLOAD_FAIL: {
                        mDialogUtil.dissmissProgressDialog();
                        Toast.makeText(getApplicationContext(), "上传失败!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                    case Constants.NO_NETWORK:
                        break;

                    case Constants.TAG_SUCCESS:
                        Bundle bundle = msg.getData();
                        String request_url = bundle.getString("request_url");
                        String resp = bundle.getString("resp");

                        break;

                    case Constants.TAG_FAIL:
                        Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.TAG_EXCEPTION:
                        Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    };

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_img_cut);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("裁切");
        registerLeftImageView(R.drawable.arrow_back);
        registerRightTextView("确定");
        mDialogUtil = new DialogUtil(ImgClipZoomActivity.this);
        initActivity();
    }

    private void initActivity() {
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.clipImageLayout);

        String imgUrl = getIntent().getStringExtra("img_url");

        int degreee = readBitmapDegree(imgUrl);
        Bitmap bitmap = createBitmap(imgUrl);
        if (bitmap != null) {
            if (degreee == 0) {
                mClipImageLayout.setImageBitmap(bitmap);
            } else {
                mClipImageLayout.setImageBitmap(rotateBitmap(degreee, bitmap));
            }
        }
    }

    @Override
    protected void goNext() {
        super.goNext();
        mDialogUtil.setProgressMessage("正在上传...");
        mDialogUtil.showProgressDialog();

        Bitmap bitmap = mClipImageLayout.clip();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 60) {
            baos.reset();
            options -= 2;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }

        String imgFilePath = FileUtils.getStorageDirectory();
        if (!new File(imgFilePath).exists()) {
            new File(imgFilePath).mkdir();
        }

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String temp_file = imgFilePath + File.separator + sf.format(new Date()) + ".png";

        try {
            byte[] data = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(temp_file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String upload_path = ServerUrl.BASE_URL + ServerUrl.METHOD_EmployeeIcon +
                        "?token=" + AppContext.getInstance().getAppPref().userToken();

                DataApi.uploadFile(new File(temp_file), upload_path, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        MyLog.d("UP", "res: " + s);
                        if (null != s) {
                            mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
                        } else {
                            mHandler.sendEmptyMessage(UPLOAD_FAIL);
                        }
                        Bitmap bitmap = BitmapUtils.sampleBitmap(temp_file);
                        CurrentUser user = AppContext.getInstance().getAppPref().getUser();
                        user.ekeicon = BitmapUtils.bitmapToBase64(bitmap);
                        AppContext.getInstance().getAppPref().setUser(user);
                        NotificationCenter.defaultCenter.postNotification(NotificationKey.update_user_info);

                    }

                    @Override
                    public void failure(HttpError error) {
                        MyLog.d("UP", "res: " + error.getLocalizedMessage());
                        mHandler.sendEmptyMessage(UPLOAD_FAIL);
                    }
                });
            }
        }).start();

    }


    /**
     * 创建图片
     *
     * @param path
     * @return
     */
    private Bitmap createBitmap(String path) {
        if (path == null) {
            return null;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        FileInputStream is = null;
        Bitmap bitmap = null;
        try {
            is = new FileInputStream(path);
            bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    // 读取图像的旋转度
    private int readBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    // 旋转图片
    private Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return resizedBitmap;
    }

}
