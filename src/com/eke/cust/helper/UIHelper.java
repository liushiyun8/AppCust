package com.eke.cust.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;

import com.eke.cust.auth.CompleteMaterialDialog;
import com.eke.cust.auth.LoginDialog;
import com.eke.cust.tabmine.UserHeadDialog;

import java.io.Serializable;

import static com.eke.cust.R.style.dialog;

public class UIHelper {
    /**
     * 跳转
     *
     * @param ctx
     * @param cls
     */
    public static void startActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        ctx.startActivity(intent);

    }

    /**
     * 跳转加传递参数
     *
     * @param ctx
     * @param cls
     * @param serializable
     */

    public static void startActivity(Context ctx, Class<?> cls,
                                     Serializable serializable) {
        Intent intent = new Intent(ctx, cls);
        intent.putExtra("data", serializable);
        ctx.startActivity(intent);

    }

    /**
     * 跳转加传递参数
     *
     * @param ctx
     * @param cls
     * @param serializable
     */

    public static void startActivity(Context ctx, Class<?> cls,
                                     Serializable serializable, Serializable serializable1) {
        Intent intent = new Intent(ctx, cls);
        intent.putExtra("data", serializable);
        intent.putExtra("data1", serializable1);
        ctx.startActivity(intent);

    }

    /**
     * 跳转
     *
     * @param ctx
     * @param
     */

    public static void startToLogin(Activity ctx) {
            LoginDialog loginActivity = new LoginDialog();
            loginActivity.setStyle(DialogFragment.STYLE_NORMAL, dialog);
            loginActivity.show(ctx);



    }

    public static void showComplete(Activity ctx) {
        CompleteMaterialDialog loginActivity = new CompleteMaterialDialog();
        loginActivity.setStyle(DialogFragment.STYLE_NORMAL, dialog);
        loginActivity.show(ctx);

    }

    /**
     * 跳转
     *
     * @param ctx
     * @param
     */
    public static void startShowImage(Activity ctx) {
        UserHeadDialog userHeadDialog = new UserHeadDialog();
        userHeadDialog.setStyle(DialogFragment.STYLE_NORMAL, dialog);
        userHeadDialog.show(ctx);

    }
}
