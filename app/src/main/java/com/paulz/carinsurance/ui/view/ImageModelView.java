package com.paulz.carinsurance.ui.view;

import android.content.Context;
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
import com.paulz.carinsurance.model.UploadProfileConfig;
import com.paulz.carinsurance.ui.viewmodel.ImageModelDouble;
import com.paulz.carinsurance.utils.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ImageModelView extends RelativeLayout implements IViewModel<ImageModelDouble> ,IEventer{


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



        showItem(btnDelete1,btnAdd1,ivImg1,data.left);




        if(data.right==null){
            itemRight.setVisibility(INVISIBLE);
        }else {
            itemRight.setVisibility(VISIBLE);
            showItem(btnDelete2,btnAdd2,ivImg2,data.right);
        }

    }

    private void showItem(ImageView deleteV,TextView addV,ImageView picV , UploadProfileConfig.ImageModel item){
        addV.setText(mData.left.title);

        if(AppUtil.isNull(item.img)){
            picV.setVisibility(GONE);
            deleteV.setVisibility(GONE);
        }else {
            Glide.with(getContext())
                    .load(AppUrls.getInstance().DOMAIN + item.img)
                    .placeholder(R.drawable.loadmin_icon_case)
                    .error(R.drawable.img_list_default)
                    .into(ivImg1);
            if(item.type==3){//多张
                //可以无限添加
                if(item.required==1){
                    //删除了但是需要站位
                    deleteV.setVisibility(VISIBLE);

                }else {
                    //删除了只留一个添加按钮
                    deleteV.setVisibility(VISIBLE);

                }
            }else if(item.type==1){//单张图片

                if(item.required==1){
                    //只能更新
                    deleteV.setVisibility(GONE);

                }else {
                    //可以删除，但删除了要显示上传
                    deleteV.setVisibility(VISIBLE);
                }
            }
        }


    }



    EventCallback mCallback;
    @Override
    public void setEventCallback(EventCallback callback) {
        mCallback=callback;
    }
}
