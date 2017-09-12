package com.paulz.carinsurance.view;

/**
 * Created by pualbeben on 17/6/10.
 */

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.framework.app.devInfo.ScreenUtil;
import com.paulz.carinsurance.R;

public class CommonDialog extends Dialog {
    private TextView mTitle;
    private TextView mDesc;
    private RelativeLayout mNormal;
    private FrameLayout mContainer;
    private Button mLeftButton;
    private Button mRightButton;
    private OnClickListener leftListener;
    private OnClickListener rightListener;
    private String titleStr;
    private String descStr;
    private String okStr;
    public CommonDialog(Context context) {
        super(context, R.style.CommonDialog);
        init();
    }
    public CommonDialog(Context context, int themeResId) {
        super(context, R.style.CommonDialog);
        init();
    }
    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }
    private void init() {
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        setContentView(R.layout.dialog_common);
        mTitle = (TextView) findViewById(R.id.common_dialog_title);
        mDesc = (TextView) findViewById(R.id.common_dialog_desc);
        mContainer = (FrameLayout) findViewById(R.id.common_dialog_container);
        FrameLayout rootLay = (FrameLayout) findViewById(R.id.common_dialog_root);
        rootLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mNormal = (RelativeLayout) findViewById(R.id.common_dialog_normal_wrapper);
        mLeftButton = (Button) findViewById(R.id.left_btn);
        mRightButton = (Button) findViewById(R.id.right_btn);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        int width = ScreenUtil.WIDTH - 2 * getContext().getResources().getDimensionPixelSize(R.dimen.ad_btn_width);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAutoDismiss) dismiss();
                if (leftListener != null) {
                    leftListener.onClick();
                }
            }
        });
        mRightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isAutoDismiss) dismiss();
                if (rightListener != null) {
                    rightListener.onClick();
                }
            }
        });
//        View view = initCustomerView();
//        if (view != null){
//            mTitle.setVisibility(View.GONE);
//            mDesc.setVisibility(View.GONE);
//            mContainer.addView(view);
//        }else {
//            mContainer.setVisibility(View.GONE);
//        }
    }
    public void setDialogLayout(int w, int h) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(w, h);
    }
    public void setCustomView(View customView) {
        if (mContainer != null) {
            mContainer.setVisibility(View.VISIBLE);
            mContainer.addView(customView);
        }
        mDesc.setVisibility(View.GONE);
    }
    //    protected View initCustomerView(){
//        return null;
//    }
    @Override
    public void show() {
        if (TextUtils.isEmpty(titleStr) && TextUtils.isEmpty(descStr)) {
            mNormal.setVisibility(View.GONE);
        } else {
            mNormal.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(titleStr)) {
            mTitle.setText(titleStr);
        } else {
            mTitle.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(descStr)) {
            mDesc.setText(descStr);
        } else {
            mDesc.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(okStr)) {
            mRightButton.setText(okStr);
        }
        super.show();
    }
    public void setOkText(String str) {
        okStr = str;
    }
    public void setOnLeftClickListener(OnClickListener l) {
        leftListener = l;
    }
    public void setOnRightClickListener(OnClickListener l) {
        rightListener = l;
    }
    public void setTitle(int resId) {
        setTitle(getContext().getString(resId));
    }
    public void setTitle(String title) {
        titleStr = title;
    }
    public void setDesc(int resId) {
        setDesc(getContext().getString(resId));
    }
    public void setLefBtnText(int resId) {
        mLeftButton.setText(resId);
    }
    public void setLefBtnText(String text) {
        mLeftButton.setText(text);
    }
    public void setRightBtnText(String resId) {
        mRightButton.setText(resId);
    }
    public void setDesc(String desc) {
        descStr = desc;
    }
    public interface OnClickListener {
        void onClick();
    }

    private boolean isAutoDismiss=true;
    public void setAutoDismiss(boolean isAuto){
        isAutoDismiss=isAuto;
    }

    public void setInsideClickCanceled(boolean canCancel){
        if(!canCancel){
            findViewById(R.id.common_dialog_main_content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else {
            findViewById(R.id.common_dialog_main_content).setOnClickListener(null);
        }

    }
}