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
 * 核保
 */

public class UnderwritingActivity extends BaseActivity {


    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.layout_order_detail)
    LinearLayout layoutOrderDetail;
    @BindView(R.id.tv_step1)
    TextView tvStep1;
    @BindView(R.id.layout_step1)
    LinearLayout layoutStep1;
    @BindView(R.id.tv_step2)
    TextView tvStep2;
    @BindView(R.id.layout_step2)
    LinearLayout layoutStep2;
    @BindView(R.id.tv_step3)
    TextView tvStep3;
    @BindView(R.id.layout_step3)
    LinearLayout layoutStep3;
    @BindView(R.id.tv_describ1)
    TextView tvDescrib1;
    @BindView(R.id.tv_describ2)
    TextView tvDescrib2;
    @BindView(R.id.layout_describ)
    LinearLayout layoutDescrib;
    @BindView(R.id.btn_next)
    TextView btnNext;

    PageData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_underwriting, true, true);
        setTitleText("", "核保", 0, true);
        ButterKnife.bind(this);
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, UnderwritingActivity.class);
        context.startActivity(intent);
    }


    public void loadData() {

        showLoading();
        ParamBuilder params = new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_UNDERWRITING), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        data=object.data;
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
        tvTitle.setText(data.insurance_company_name+"-"+data.insurance_carnumber);
        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_INSCOMPANY+data.insurance_company_img,ivIcon);
        if(data.insurance_xiadan_hbres==0){
            tvStep2.setText("核保失败");
            layoutDescrib.setVisibility(View.VISIBLE);
            tvDescrib2.setVisibility(View.GONE);
            tvDescrib1.setText(data.insurance_xiadan_hbdesc);
            btnNext.setText("重选保险公司");

        }else if(data.insurance_xiadan_hbres==1){
            tvStep2.setText("人工核保");
            layoutDescrib.setVisibility(View.VISIBLE);
            tvDescrib2.setText("商业险保单号："+data.insurance_xiadan_bsalesn);
            tvDescrib1.setText("交强险保单号："+data.insurance_xiadan_csalesn);
            btnNext.setText("查看订单");

        }else if(data.insurance_xiadan_hbres==2){
            layoutStep2.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);

        }else if(data.insurance_xiadan_hbres==3){
            tvStep2.setText("核保成功");
            layoutDescrib.setVisibility(View.VISIBLE);
            layoutStep3.setVisibility(View.VISIBLE);
            tvStep3.setText("支付保单");
            tvDescrib2.setText("初始报价："+data.amount);
            tvDescrib1.setText("核保价格："+data.amount);
            btnNext.setText("在线支付");

        }
    }

    @OnClick({R.id.layout_order_detail, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_order_detail:
                OrderInfoActivity.invoke(this,data.order_sn);
                break;
            case R.id.btn_next:
                if(data.insurance_xiadan_hbres==1){
                    MainActivity.invokeWithAction(this,IActions.ACTION_TO_ODER_LIST);
                }else if(data.insurance_xiadan_hbres==3){
                    pay();
                }else if(data.insurance_xiadan_hbres==0){
                    SelectInsureCompanyActivity.invoke(this);
                }
                break;
        }
    }

    private void pay(){
        final ParamBuilder params=new ParamBuilder();
        params.append("sn",data.order_sn);
        if(true){
            CommonWebActivity.invoke(UnderwritingActivity.this,APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_PAY),"");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_PAY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    BaseObject<Object> object=GsonParser.getInstance().parseToObj(result,Object.class);
                    if(object!=null&&object.status==BaseObject.STATUS_OK){
                        CommonWebActivity.invoke(UnderwritingActivity.this,APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().WAP_ORDER_PAY),null);
                    }else{
                        AppUtil.showToast(getApplicationContext(),object==null?"操作失败":object.msg);
                    }

                }else {
                    AppUtil.showToast(getApplicationContext(),"操作失败");
                }
            }
        });
    }


    private class PageData {
        public String order_sn;
        public String insurance_company_img;
        public String insurance_company_name;
        public String insurance_carnumber;
        public int insurance_xiadan_hbres;
        public String amount;
        public String insurance_xiadan_hbdesc;
        public String insurance_xiadan_bsalesn;
        public String insurance_xiadan_csalesn;
    }

}
