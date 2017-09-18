package com.eke.cust.tabhouse.image_viewer_activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.chat.widget.photoview.PhotoViewAttacher;
import com.eke.cust.utils.BitmapUtils;

import org.droidparts.annotation.inject.InjectBundleExtra;
import org.droidparts.annotation.inject.InjectView;

public class ImageViewerActivity extends BaseActivity implements PhotoViewAttacher.OnViewTapListener {
    @InjectBundleExtra(key = "data")
    private  String path;
    @InjectView(id = R.id.image)
    private ImageView mImageView;
    @InjectView(id = R.id.image)
    private PhotoViewAttacher mAttacher;



    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_tab_house_image_viewer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity();

    }

    private void initActivity() {
        mAttacher = new PhotoViewAttacher(mImageView);

        mAttacher.setOnViewTapListener(this);

        Bitmap bitmap = BitmapUtils.decodeFileAsBitmap(path);
        mImageView.setImageBitmap(bitmap);
        mAttacher.update();
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    @Override
    public void onViewTap(View view, float x, float y) {
        finish();

    }
}
