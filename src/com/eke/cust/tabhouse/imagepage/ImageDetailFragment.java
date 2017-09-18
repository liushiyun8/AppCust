package com.eke.cust.tabhouse.imagepage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eke.cust.R;
import foundation.base.fragment.BaseFragment;
import com.eke.cust.chat.widget.photoview.PhotoViewAttacher;
import com.eke.cust.model.ImageResource;
import com.eke.cust.utils.LoadImageUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


public class ImageDetailFragment extends BaseFragment implements PhotoViewAttacher.OnViewTapListener {
    private ImageResource imageResource;
    private ImageView mImageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;


    public static ImageDetailFragment newInstance(ImageResource imageResource) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putSerializable("imageResource", imageResource);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageResource = (ImageResource) (getArguments() != null ? getArguments().getSerializable("imageResource") : null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.image);
        mAttacher = new PhotoViewAttacher(mImageView);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        mAttacher.setOnViewTapListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadImage();

    }

    private void loadImage() {
        LoadImageUtil.getInstance().displayImage(imageResource.basePath+imageResource.filePath,
                mImageView, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        String message = null;
                        switch (failReason.getType()) {
                            case IO_ERROR:
                                message = getString(R.string.imagedetailfragment_io_error);
                                break;
                            case DECODING_ERROR:
                                message = getString(R.string.imagedetailfragment_decoding_error);
                                break;
                            case NETWORK_DENIED:
                                message = getString(R.string.imagedetailfragment_network_denied);
                                break;
                            case OUT_OF_MEMORY:
                                message = getString(R.string.imagedetailfragment_out_of_memory);
                                break;
                            case UNKNOWN:
                                message = getString(R.string.imagedetailfragment_unknown);
                                break;
                        }
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                        mAttacher.update();
                    }
                });


    }

    @Override
    public void onViewTap(View view, float v, float v1) {
        this.getActivity().finish();
    }
}
