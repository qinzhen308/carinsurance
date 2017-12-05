package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.common.IActions;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.Image13Loader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/6/18.
 * 核保失败原因
 */

public class InsureFailedReasonActivity extends BaseActivity {


    PageData data;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.layout_order_detail)
    LinearLayout layoutOrderDetail;
    @BindView(R.id.tv_reason)
    TextView tvReason;
    @BindView(R.id.btn_next)
    TextView btnNext;

    private String orderSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderSn=getIntent().getStringExtra("order_sn");
        initView();
        loadData();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_failed_season, true, true);
        setTitleText("", "核保失败", 0, true);
        ButterKnife.bind(this);
    }


    public static void invoke(Context context,String orderSn) {
        Intent intent = new Intent(context, InsureFailedReasonActivity.class);
        intent.putExtra("order_sn",orderSn);
        context.startActivity(intent);
    }


    public void loadData() {

        showLoading();
        ParamBuilder params = new ParamBuilder();
        params.append("sn",orderSn);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_FAILED_REASON), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        data = object.data;
                        showSuccess();
                        handleData();

                    } else {
                        showNodata();
                    }
                } else {
                    showFailture();
                }
            }
        });
    }

    private void handleData() {
        tvTitle.setText(data.companyname + "-" + data.carnumber);
        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_INSCOMPANY + data.img, ivIcon);
        tvReason.setText(data.hbdesc);

    }

    @OnClick({R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_next:

                InsureInfoActivity.invoke(this);
                break;
        }
    }



    private class PageData {
        public String img;
        public String carnumber;
        public String companyname;
        public String hbdesc;
    }

}
