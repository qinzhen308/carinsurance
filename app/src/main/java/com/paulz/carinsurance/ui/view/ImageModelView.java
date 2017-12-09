package com.paulz.carinsurance.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
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
import com.paulz.carinsurance.model.UploadProfileConfig;
import com.paulz.carinsurance.ui.viewmodel.ImageModelDouble;
import com.paulz.carinsurance.utils.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ImageModelView extends RelativeLayout implements IViewModel<ImageModelDouble>, IEventer {


    ImageModelDouble mData;
    @BindView(R.id.iv_img1)
    ImageView ivImg1;
    @BindView(R.id.btn_add1)
    TextView btnAdd1;
    @BindView(R.id.btn_delete1)
    ImageView btnDelete1;
    @BindView(R.id.item_left)
    RelativeLayout itemLeft;
    @BindView(R.id.iv_img2)
    ImageView ivImg2;
    @BindView(R.id.btn_add2)
    TextView btnAdd2;
    @BindView(R.id.btn_delete2)
    ImageView btnDelete2;
    @BindView(R.id.item_right)
    RelativeLayout itemRight;

    int mPosition;
    @BindView(R.id.progress_bar1)
    FrameLayout progressBar1;
    @BindView(R.id.progress_bar2)
    FrameLayout progressBar2;


    private CircleTransform circleTransform;


    public ImageModelView(Context context) {
        super(context);
        init();
    }

    public ImageModelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_image_model, this);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());
    }


    @Override
    public void bindData(int position, ImageModelDouble data) {
        mData = data;
        mPosition = position;

        showItem(btnDelete1, btnAdd1, ivImg1,progressBar1, data.left);


        if (data.right == null) {
            itemRight.setVisibility(INVISIBLE);
            btnDelete2.setOnClickListener(null);
            btnAdd2.setOnClickListener(null);
            ivImg2.setOnClickListener(null);

        } else {
            itemRight.setVisibility(VISIBLE);
            showItem(btnDelete2, btnAdd2, ivImg2, progressBar2,data.right);
        }

    }

    private void showItem(ImageView deleteV, TextView addV, ImageView picV,View loadingBar, final UploadProfileConfig.ImageModel item) {
        addV.setText(mData.left.title);
        if(!mData.enable){
            picV.setVisibility(VISIBLE);
            deleteV.setVisibility(GONE);
            addV.setVisibility(GONE);
            Glide.with(getContext())
                    .load(AppUrls.getInstance().DOMAIN + item.img)
                    .placeholder(R.drawable.img_list_default)
                    .error(R.drawable.img_list_default)
                    .into(picV);

            return;
        }

        if(item.uploading){
            loadingBar.setVisibility(VISIBLE);
        }else {
            loadingBar.setVisibility(GONE);
        }

        if (AppUtil.isNull(item.img)) {
            picV.setVisibility(GONE);
            deleteV.setVisibility(GONE);
            addV.setVisibility(VISIBLE);
            if(item.imgFile!=null){
                picV.setVisibility(VISIBLE);
                Glide.with(getContext())
                        .load(item.imgFile)
                        .placeholder(R.drawable.img_list_default)
                        .error(R.drawable.img_list_default)
                        .into(picV);
            }


        } else {
            addV.setVisibility(GONE);
            picV.setVisibility(VISIBLE);
            Glide.with(getContext())
                    .load(AppUrls.getInstance().DOMAIN + item.img)
                    .placeholder(R.drawable.img_list_default)
                    .error(R.drawable.img_list_default)
                    .into(picV);
            if (item.op.equals("del")) {
                //只有删除
                deleteV.setVisibility(VISIBLE);

            } else if (item.op.equals("up")) {
                //只能更新
                deleteV.setVisibility(GONE);

            }else {
                deleteV.setVisibility(GONE);

            }
        }


        addV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    //添加
                    mCallback.onEvent(EventCallback.EVENT_1, item, mData, mPosition);
                }
            }
        });

        deleteV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除
                if (mCallback != null) {
                    mCallback.onEvent(EventCallback.EVENT_2, item, mData, mPosition);
                }
            }
        });

        picV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!item.op.equals("up")){
                    return;
                }
                //替换
                if (mCallback != null) {
                    mCallback.onEvent(EventCallback.EVENT_3, item, mData, mPosition);
                }
            }
        });


    }


    EventCallback mCallback;

    @Override
    public void setEventCallback(EventCallback callback) {
        mCallback = callback;
    }
}
