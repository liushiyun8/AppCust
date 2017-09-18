package com.eke.cust.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.bean.DialogMenu;

import java.util.ArrayList;

import foundation.widget.recyclerView.DividerGridItemDecoration;
import foundation.widget.recyclerView.adapter.BaseRecyclerViewAdapter;
import foundation.widget.recyclerView.viewholder.RecycleviewViewHolder;

/**
 * Created by wujian on 2017/3/1.
 */

public class DialogHelper {

    //账号在别的地方登录
    public static void showExitDialog() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AppManager.getAppManager().currentActivity());
//        alertDialog.setTitle("你的账号在别的设备登录").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                AppContext.getInstance().userLogout();
//                UIHelper.startActivity(AppManager.getAppManager().currentActivity(), LoginActivity.class);
//                dialog.cancel();
//            }
//        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        alertDialog.show();
    }


    public static AlertDialog.Builder getDialog(Context context) {
        Activity activity = (Activity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.App_Theme_Dialog_Alert);
        WindowManager m = activity.getWindowManager();

        return builder;
    }

    /**
     * 获取一个普通的消息对话框，没有取消按钮
     */
    public static AlertDialog.Builder getMessageDialog(
            Context context,
            String title,
            String message,
            boolean cancelable) {
        return getDialog(context)
                .setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", null);
    }

    /**
     * 获取一个普通的消息对话框，没有取消按钮
     */
    public static AlertDialog.Builder getMessageDialog(
            Context context,
            String title,
            String message) {
        return getMessageDialog(context, title, message, false);
    }
    /**
     * 显示菜单对话框
     *
     * @param context
     * @param
     */
    public static AlertDialog getViewDialog(Context context, View view) {
        AlertDialog alertDialog = getDialog(context)
                .setView(view).create();
        return alertDialog;
    }
    /**
     * 获取一个普通的消息对话框，没有取消按钮
     */
    public static AlertDialog.Builder getMessageDialog(Context context, String message) {
        return getMessageDialog(context, "", message, false);
    }

    /**
     * 获取一个普通的消息对话框，没有取消按钮
     */
    public static AlertDialog.Builder getMessageDialog(
            Context context,
            String title,
            String message,
            String positiveText) {
        return getDialog(context)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, null);
    }

    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title,
                                                       View view,
                                                       DialogInterface.OnClickListener positiveListener) {
        return getDialog(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", null);
    }

    /**
     * 显示菜单对话框
     *
     * @param context
     * @param dialogMenus
     */
    public static void showMenuDialog(Context context,
                                      ArrayList<DialogMenu> dialogMenus, final ItemMenuClickListener itemMenuClickListener) {
        RecyclerView recycleView = new RecyclerView(context);
        recycleView.setLayoutManager(new GridLayoutManager(context, 3));
        recycleView.addItemDecoration(new DividerGridItemDecoration(context));
        final AlertDialog alertDialog = getDialog(context)
                .setView(recycleView).create();
        recycleView.setAdapter(new BaseRecyclerViewAdapter(context, dialogMenus) {
            @Override
            public void setUpData(final RecyclerView.ViewHolder holder, int position, int viewType, Object data) {
                final DialogMenu menu = (DialogMenu) data;
                RecycleviewViewHolder viewHolder = (RecycleviewViewHolder) holder;
                ImageView mIvIcon = viewHolder.getView(R.id.iv_icon);
                TextView mTxtName = viewHolder.getView(R.id.txt_menu_name);
                if (menu != null) {
                    mIvIcon.setImageResource(menu.getMenuResource());
                    mTxtName.setText(menu.getName());
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemMenuClickListener != null && menu != null) {
                            itemMenuClickListener.clickMenu(menu, holder.getLayoutPosition());
                            alertDialog.cancel();

                        }

                    }
                });

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecycleviewViewHolder(LayoutInflater.from(mContext).inflate(R.layout.dialog_gird_menu, null));

            }
        });
        alertDialog.show();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        WindowManager.LayoutParams p = alertDialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width = (int) (displayMetrics.widthPixels * 0.8);    //宽度设置为屏幕的

    }

    /**
     * 显示菜单对话框
     *
     * @param context
     * @param
     */
    public static AlertDialog getInputViewDialog(Context context, View view) {
        AlertDialog alertDialog = getDialog(context)
                .setView(view).create();
        return alertDialog;
    }



    /**
     * 获取一个验证对话框
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String title,
            String message,
            String positiveText,
            String negativeText,
            boolean cancelable,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        return getDialog(context)
                .setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener);
    }

    /**
     * 获取一个验证对话框
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context, String message,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        return getDialog(context)
                .setMessage(message)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", negativeListener);
    }

    public static AlertDialog.Builder getSingleChoiceDialog(
            Context context,
            String title,
            String[] arrays,
            int selectIndex,
            DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setSingleChoiceItems(arrays, selectIndex, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setNegativeButton("取消", null);
        return builder;
    }


    /**
     * 获取一个验证对话框，没有点击事件
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String title,
            String message,
            String positiveText,
            String negativeText,
            boolean cancelable,
            DialogInterface.OnClickListener positiveListener) {
        return getConfirmDialog(
                context, title, message, positiveText,
                negativeText, cancelable, positiveListener, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String title,
            String message,
            String positiveText,
            String negativeText,
            DialogInterface.OnClickListener positiveListener) {
        return getConfirmDialog(
                context, title, message, positiveText, negativeText, false, positiveListener, null);
    }


    /**
     * 获取一个验证对话框，没有点击事件
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String title,
            String message,
            String positiveText,
            String negativeText,
            boolean cancelable) {
        return getConfirmDialog(
                context, title, message, positiveText, negativeText, cancelable, null, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String message,
            String positiveText,
            String negativeText,
            boolean cancelable) {
        return getConfirmDialog(context, "", message, positiveText, negativeText
                , cancelable, null, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String title,
            String message,
            boolean cancelable) {
        return getConfirmDialog(context, title, message, "确定", "取消", cancelable, null, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String message,
            boolean cancelable,
            DialogInterface.OnClickListener positiveListener) {
        return getConfirmDialog(context, "", message, "确定", "取消", cancelable, positiveListener, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String message,
            DialogInterface.OnClickListener positiveListener) {
        return getConfirmDialog(context, "", message, "确定", "取消", positiveListener);
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String title,
            String message) {
        return getConfirmDialog(context, title, message, "确定", "取消", false, null, null);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(
            Context context,
            String title,
            AppCompatEditText editText,
            String positiveText,
            String negativeText,
            boolean cancelable,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        return getDialog(context)
                .setCancelable(cancelable)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(
            Context context, String title,
            AppCompatEditText editText,
            String positiveText,
            String negativeText,
            boolean cancelable,
            DialogInterface.OnClickListener positiveListener) {
        return getInputDialog(
                context,
                title,
                editText,
                positiveText,
                negativeText,
                cancelable,
                positiveListener,
                null);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(
            Context context,
            String title,
            AppCompatEditText editText,
            boolean cancelable,
            DialogInterface.OnClickListener positiveListener) {
        return getInputDialog(context, title, editText, "确定", "取消"
                , cancelable, positiveListener, null);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(
            Context context, String title, AppCompatEditText editText, String positiveText,
            boolean cancelable,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        return getInputDialog(
                context, title, editText, positiveText, "取消", cancelable
                , positiveListener, negativeListener);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(
            Context context, String title, AppCompatEditText editText,
            boolean cancelable,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        return getInputDialog(
                context, title, editText, "确定", "取消", cancelable
                , positiveListener, negativeListener);
    }


    /**
     * 获取一个等待对话框
     */
    public static ProgressDialog getProgressDialog(Context context) {
        return new ProgressDialog(context);
    }

    /**
     * 获取一个等待对话框
     */
    public static ProgressDialog getProgressDialog(Context context, boolean cancelable) {
        ProgressDialog dialog = getProgressDialog(context);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    /**
     * 获取一个等待对话框
     */
    public static ProgressDialog getProgressDialog(Context context, String message) {
        ProgressDialog dialog = getProgressDialog(context);
        dialog.setMessage(message);
        return dialog;
    }

    /**
     * 获取一个等待对话框
     */
    public static ProgressDialog getProgressDialog(
            Context context, String title, String message, boolean cancelable) {
        ProgressDialog dialog = getProgressDialog(context);
        dialog.setCancelable(cancelable);
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog;
    }

    /**
     * 获取一个等待对话框
     */
    public static ProgressDialog getProgressDialog(
            Context context, String message, boolean cancelable) {
        ProgressDialog dialog = getProgressDialog(context);
        dialog.setCancelable(cancelable);
        dialog.setMessage(message);
        return dialog;
    }

    public static AlertDialog.Builder getSelectDialog(
            Context context, String title, String[] items,
            String positiveText,
            DialogInterface.OnClickListener itemListener) {
        return getDialog(context)
                .setTitle(title)
                .setItems(items, itemListener)
                .setPositiveButton(positiveText, null);

    }

    public static AlertDialog.Builder getSelectDialog(
            Context context, String[] items,
            String positiveText,
            DialogInterface.OnClickListener itemListener) {
        return getDialog(context)
                .setItems(items, itemListener)
                .setPositiveButton(positiveText, null);

    }

    public static AlertDialog getSelectDialog(Context context, View view) {

        return getDialog(context) .setView(view).create();
    }
}
