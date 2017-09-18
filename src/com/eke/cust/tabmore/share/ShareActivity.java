package com.eke.cust.tabmore.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;

import com.eke.cust.share.TPManager;
import com.eke.cust.share.utils.ShareHelper;
import com.eke.cust.widget.BottomView;

import org.droidparts.util.intent.IntentFactory;

import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 分享有礼
 *
 * @author wujian
 */
public class ShareActivity extends BaseActivity  {

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_share);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TPManager.getInstance().initWXConfig(TPManager.getInstance().getWXAppId(), TPManager.getInstance().getWXAppSecret());

        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                goBack();

            }
        });
        // 分享
        findViewById(R.id.btn_share).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showShare();

            }

        });

    }

    private void showShare() {
        final BottomView bottomView = new BottomView(this,
                R.style.loading_dialog, R.layout.share_layout);
        bottomView.setAnimation(R.style.BottomToTopAnim);
        bottomView.showBottomView(true);
        bottomView.getView().findViewById(R.id.txt_wechatmoments)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomView.dismissBottomView();
                        ShareHelper.showShareApp(mContext, WechatMoments.NAME);

                    }
                });
        bottomView.getView().findViewById(R.id.txt_wechat)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomView.dismissBottomView();
                        ShareHelper.showShareApp(mContext, Wechat.NAME);



                    }
                });
        bottomView.getView().findViewById(R.id.txt_shortmessage)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomView.dismissBottomView();
                        Intent intent = IntentFactory.getSendSMSIntent("亲,[待命名]租房APP太好用!深圳、广州中介费说降就降,房源真、照片真、价格真,预约即有专人服务陪到签约!试试看http://www.ekeae.com");
                        startActivity(intent);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
