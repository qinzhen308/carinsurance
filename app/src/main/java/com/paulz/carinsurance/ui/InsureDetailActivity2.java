package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AbsMutipleAdapter;
import com.paulz.carinsurance.adapter.ViewHolder;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.InsureDetail2;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.utils.Image13Loader;
import com.paulz.carinsurance.view.ListViewInScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/5/29.
 * 保单详情----订单详情里面的
 */

public class InsureDetailActivity2 extends BaseActivity {


    ItemAdapter mAdapter;
    @BindView(R.id.iv_company)
    ImageView ivCompany;
    @BindView(R.id.tv_company_name)
    TextView tvCompanyName;
    @BindView(R.id.tv_insure_city)
    TextView tvInsureCity;
    @BindView(R.id.tv_force_sn)
    TextView tvForceSn;
    @BindView(R.id.tv_business_sn)
    TextView tvBusinessSn;
    @BindView(R.id.tv_business_price)
    TextView tvBusinessPrice;
    @BindView(R.id.layout_business_all)
    LinearLayout layoutBusinessAll;
    @BindView(R.id.layout_business_1)
    LinearLayout layoutBusiness1;
    @BindView(R.id.lv_cate)
    ListViewInScrollView lvCate;
    @BindView(R.id.tv_business_date)
    TextView tvBusinessDate;
    @BindView(R.id.layout_business_date)
    LinearLayout layoutBusinessDate;
    @BindView(R.id.layout_business_2)
    LinearLayout layoutBusiness2;
    @BindView(R.id.tv_force_price)
    TextView tvForcePrice;
    @BindView(R.id.layout_force_all)
    LinearLayout layoutForceAll;
    @BindView(R.id.tv_force_price1)
    TextView tvForcePrice1;
    @BindView(R.id.layout_force_all1)
    RelativeLayout layoutForceAll1;
    @BindView(R.id.tv_boat_price1)
    TextView tvBoatPrice1;
    @BindView(R.id.layout_boat_all1)
    RelativeLayout layoutBoatAll1;
    @BindView(R.id.tv_force_date)
    TextView tvForceDate;
    @BindView(R.id.layout_force_date)
    LinearLayout layoutForceDate;
    @BindView(R.id.layout_force_and_boat)
    LinearLayout layoutForceAndBoat;
    @BindView(R.id.tv_force_tsn)
    TextView tvForceTSn;
    @BindView(R.id.tv_business_tsn)
    TextView tvBusinessTSn;

    InsureDetail2 data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_insure_detail2, true, true);
        setTitleText("", "保单详情", 0, true);
        ButterKnife.bind(this);
        mAdapter = new ItemAdapter(this);
        lvCate.setAdapter(mAdapter);
        init();
    }


    private void init() {
        showLoading();
        ParamBuilder params = new ParamBuilder();
        params.append("sn", getIntent().getStringExtra("insurance_xunjia_id"));
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_INSURE_DETAIL), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (status == 200) {
                    showSuccess();
                    BaseObject<InsureDetail2> obj = GsonParser.getInstance().parseToObj(result, InsureDetail2.class);
                    if (obj != null && obj.data != null) {
                        data = obj.data;
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
        tvBoatPrice1.setText("￥" + data.insurance_xunjia_taxamount);
        tvForcePrice1.setText("￥" + data.insurance_xunjia_camount);
        tvForcePrice.setText("￥" + data.insurance_xunjia_camount);
        tvBusinessPrice.setText("￥" + data.insurance_xunjia_bamount);
        if (data.insurance_compulsoryins == 1) {//有投保
            layoutForceAndBoat.setVisibility(View.VISIBLE);
            tvForceSn.setVisibility(View.VISIBLE);
            tvForceTSn.setVisibility(View.VISIBLE);

        } else {
            layoutForceAndBoat.setVisibility(View.GONE);
            tvForceSn.setVisibility(View.GONE);
            tvForceTSn.setVisibility(View.GONE);
        }

        if (data.insurance_businessins == 1) {//有投保
            layoutBusiness1.setVisibility(View.VISIBLE);
            layoutBusiness2.setVisibility(View.VISIBLE);
            lvCate.setVisibility(View.VISIBLE);
            tvBusinessSn.setVisibility(View.VISIBLE);
            tvBusinessTSn.setVisibility(View.VISIBLE);

        } else {
            layoutBusiness1.setVisibility(View.GONE);
            lvCate.setVisibility(View.GONE);
            layoutBusiness2.setVisibility(View.GONE);
            tvBusinessSn.setVisibility(View.GONE);
            tvBusinessTSn.setVisibility(View.GONE);

        }

        mAdapter.setList(data.valuelist);
        mAdapter.notifyDataSetChanged();

        tvForceDate.setText(DateUtil.getYMDHMDate(data.insurance_compulsoryinsdate * 1000));
        tvBusinessDate.setText(DateUtil.getYMDHMDate(data.insurance_businessinsdate * 1000));

        tvCompanyName.setText(data.insurance_company_name);
        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_INSCOMPANY + data.insurance_company_img, ivCompany);
        tvInsureCity.setText("投保城市："+data.insurance_city);

        if(AppUtil.isNull(data.insurance_csn)){
            tvForceSn.setText("交强险保单号：");
        }else {
            tvForceSn.setText("交强险保单号："+data.insurance_csn);
        }
        if(AppUtil.isNull(data.insurance_bsn)){
            tvBusinessSn.setText("商业险保单号：");
        }else {
            tvBusinessSn.setText("商业险保单号："+data.insurance_bsn);
        }
        if(AppUtil.isNull(data.insurance_csalesn)){
            tvForceTSn.setText("交强险投保单号：");
        }else {
            tvForceTSn.setText("交强险投保单号："+data.insurance_csalesn);
        }
        if(AppUtil.isNull(data.insurance_bsalesn)){
            tvBusinessTSn.setText("商业险投保单号：");
        }else {
            tvBusinessTSn.setText("商业险投保单号："+data.insurance_bsalesn);
        }


    }


    public static void invoke(Context context, String insurance_xunjia_id) {
        Intent intent = new Intent(context, InsureDetailActivity2.class);
        intent.putExtra("insurance_xunjia_id", insurance_xunjia_id);
        context.startActivity(intent);
    }





    public class ItemAdapter extends AbsMutipleAdapter<InsureDetail2.InsureCate, ViewHolderImpl> {


        public ItemAdapter(Activity context) {
            super(context);
        }

        @Override
        public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
            return new ViewHolderImpl(View.inflate(mContext, R.layout.item_business_cate, null));
        }

        @Override
        public void onBindViewHolder(int position, ViewHolderImpl holder) {
            final InsureDetail2.InsureCate cate = (InsureDetail2.InsureCate) getItem(position);
            holder.tvCate.setText(cate.insurance_values_typename);

            holder.tvValue.setText(cate.insurance_values_name == null ? "" : cate.insurance_values_name);
            holder.tvPrice.setText("￥" + cate.money);

        }


    }


    public static class ViewHolderImpl extends ViewHolder {
        @BindView(R.id.tv_cate)
        TextView tvCate;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.tv_price)
        TextView tvPrice;

        public ViewHolderImpl(View view) {
            super(view);
        }

    }


}
