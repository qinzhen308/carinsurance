package com.paulz.carinsurance.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.common.recyclerview.CircleTransform;
import com.paulz.carinsurance.common.recyclerview.EventCallback;
import com.paulz.carinsurance.common.recyclerview.IEventer;
import com.paulz.carinsurance.common.recyclerview.IViewModel;
import com.paulz.carinsurance.model.UploadProfileConfig;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ImageModelLabelView extends RelativeLayout implements IViewModel<UploadProfileConfig.ImageGroup>, IEventer {


    UploadProfileConfig.ImageGroup mData;
    @BindView(R.id.tv_label)
    TextView tvLabel;


    private CircleTransform circleTransform;


    public ImageModelLabelView(Context context) {
        super(context);
        init();
    }

    public ImageModelLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_upload_image_group_label, this);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());
    }


    @Override
    public void bindData(int position, UploadProfileConfig.ImageGroup data) {
        mData = data;

        tvLabel.setText(data.title);


    }


    EventCallback mCallback;

    @Override
    public void setEventCallback(EventCallback callback) {
        mCallback = callback;
    }
}
