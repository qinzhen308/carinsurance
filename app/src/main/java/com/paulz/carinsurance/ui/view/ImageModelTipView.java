package com.paulz.carinsurance.ui.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.common.recyclerview.CircleTransform;
import com.paulz.carinsurance.common.recyclerview.EventCallback;
import com.paulz.carinsurance.common.recyclerview.IEventer;
import com.paulz.carinsurance.common.recyclerview.IViewModel;
import com.paulz.carinsurance.ui.viewmodel.ImageModelDouble;
import com.paulz.carinsurance.ui.viewmodel.ImageModelTip;

import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ImageModelTipView extends RelativeLayout implements IViewModel<ImageModelTip> ,IEventer{


    ImageModelTip mData;
    @BindView(R.id.tv_tip)
    TextView tvTip;


    private CircleTransform circleTransform;


    public ImageModelTipView(Context context) {
        super(context);
        init();
    }

    public ImageModelTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_upload_image_tip, this);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());
    }


    @Override
    public void bindData(int position, ImageModelTip data) {
        mData = data;
        String tip=mData.msg.replace("\\n","\n");
        tvTip.setText(tip);


    }


    EventCallback mCallback;
    @Override
    public void setEventCallback(EventCallback callback) {
        mCallback=callback;
    }
}
