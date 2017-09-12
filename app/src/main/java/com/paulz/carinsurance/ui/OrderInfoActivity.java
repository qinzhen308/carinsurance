package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.OrderDetail;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.utils.Image13Loader;
import com.paulz.carinsurance.view.UpperCaseEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/29.
 * 订单信息
 */

public class OrderInfoActivity extends BaseActivity {


    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;
    @BindView(R.id.iv_company)
    ImageView ivCompany;
    @BindView(R.id.tv_company_name)
    TextView tvCompanyName;
    @BindView(R.id.tv_insure_date)
    TextView tvInsureDate;
    @BindView(R.id.layout_insure_detail)
    RelativeLayout layoutInsureDetail;
    @BindView(R.id.tv_label1)
    TextView tvLabel1;
    @BindView(R.id.tv_business_price)
    TextView tvBusinessPrice;
    @BindView(R.id.tv_business_date)
    TextView tvBusinessDate;
    @BindView(R.id.tv_label2)
    TextView tvLabel2;
    @BindView(R.id.tv_force_price)
    TextView tvForcePrice;
    @BindView(R.id.tv_force_time)
    TextView tvForceTime;
    @BindView(R.id.tv_insure_city)
    TextView tvInsureCity;
    @BindView(R.id.tv_car_id)
    TextView tvCarId;
    @BindView(R.id.tv_car_code)
    TextView tvCarCode;
    @BindView(R.id.tv_engine_code)
    TextView tvEngineCode;
    @BindView(R.id.tv_owner_name)
    TextView tvOwnerName;
    @BindView(R.id.tv_owner_id)
    TextView tvOwnerId;
    @BindView(R.id.tv_owner_phone)
    TextView tvOwnerPhone;
    @BindView(R.id.tv_insured_person_name)
    TextView tvInsuredPersonName;
    @BindView(R.id.tv_insured_person_phone)
    TextView tvInsuredPersonPhone;
    @BindView(R.id.tv_insured_person_address)
    TextView tvInsuredPersonAddress;
    @BindView(R.id.btn_operation)
    TextView btnOperation;
    @BindView(R.id.tv_same_insure)
    TextView tvSameInsure;
    @BindView(R.id.tv_same_insured)
    TextView tvSameInsured;

    OrderDetail data;
    @BindView(R.id.tv_price)
    TextView tvPrice;

    String orderOpTexts[] = {"未完成订单", "核保中", "核保失败", "支付", "已支付", "交易关闭"};
    int orderOpBgColors[] = {R.color.main, R.color.main,
            R.color.gray, R.color.main,
            R.color.green_light, R.color.gray};
    @BindView(R.id.layout_business)
    RelativeLayout layoutBusiness;
    @BindView(R.id.layout_force)
    RelativeLayout layoutForce;
    @BindView(R.id.tv_insure_name)
    TextView tvInsureName;
    @BindView(R.id.layout_insure_name)
    LinearLayout layoutInsureName;
    @BindView(R.id.tv_insure_id)
    TextView tvInsureId;
    @BindView(R.id.layout_insure_id)
    LinearLayout layoutInsureId;
    @BindView(R.id.tv_insure_phone)
    TextView tvInsurePhone;
    @BindView(R.id.layout_insure_phone)
    LinearLayout layoutInsurePhone;
    @BindView(R.id.tv_insured_name)
    TextView tvInsuredName;
    @BindView(R.id.layout_insured_name)
    LinearLayout layoutInsuredName;
    @BindView(R.id.tv_insured_id)
    TextView tvInsuredId;
    @BindView(R.id.layout_insured_id)
    LinearLayout layoutInsuredId;
    @BindView(R.id.tv_insured_phone)
    TextView tvInsuredPhone;
    @BindView(R.id.layout_insured_phone)
    LinearLayout layoutInsuredPhone;

    @BindView(R.id.tag_view)
    View tagView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_order_info, true, true);
        setTitleText("", "订单信息", 0, true);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        showLoading();
        ParamBuilder params = new ParamBuilder();
        params.append("sn", getIntent().getStringExtra("order_sn"));
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_DETAIL), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (status == 200) {
                    showSuccess();
                    BaseObject<OrderDetail> obj = GsonParser.getInstance().parseToObj(result, OrderDetail.class);
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
        tvName.setText((AppUtil.isNull(data.insurance_carnumber)?"未上牌":data.insurance_carnumber) + " - " + data.insurance_name);
        tvOrderId.setText("订单号：" + data.order_sn);
        if (data.order_status == 0) {
            tvOrderStatus.setText("未完成订单");
        } else if (data.order_status == 1) {
            tvOrderStatus.setText("待核保");
        } else if (data.order_status == 2) {
            tvOrderStatus.setText("核保失败");
        } else if (data.order_status == 3) {
            tvOrderStatus.setText("待支付");
            btnOperation.setText("去支付");
        } else if (data.order_status == 4) {
            tvOrderStatus.setText("已支付");
        } else if (data.order_status == 5) {
            tvOrderStatus.setText("交易关闭");
        }
        tvCompanyName.setText(data.insurance_company_name);
        tvInsureDate.setText("核保时间：" + (TextUtils.isEmpty(data.insapprove_time)||Long.valueOf(data.insapprove_time)==0? "未核保" : DateUtil.getYMDHMDate(Long.valueOf(data.insapprove_time) * 1000)));
        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_INSCOMPANY + data.insurance_company_img, ivCompany);
        tvOwnerName.setText(data.insurance_name);
        tvOwnerId.setText(data.insurance_sfz);
        tvOwnerPhone.setText(data.btbtel);
        tvBusinessPrice.setText("￥" + data.insurance_xunjia_bamount);
        tvBusinessDate.setText(DateUtil.getYMDHMDate(data.insurance_businessinsdate * 1000));
        tvForcePrice.setText("￥" + data.insurance_xunjia_camount);
        tvForceTime.setText(DateUtil.getYMDHMDate(data.insurance_compulsoryinsdate * 1000));
        tvInsureCity.setText(data.insurance_city);
        tvCarCode.setText(data.car.insurance_carmodel_vin);
        tvEngineCode.setText(AppUtil.isNull(data.car.insurance_carmodel_en)?"":UpperCaseEditText.upper(data.car.insurance_carmodel_en));
        tvCarId.setText(AppUtil.isNull(data.insurance_carnumber)?"未上牌":data.insurance_carnumber);
        tvInsuredPersonName.setText(data.shippingname);
        tvInsuredPersonAddress.setText(data.shippingaddr);
        tvInsuredPersonPhone.setText(data.shippingtel);
        if (data.insurance_businessins == 1) {//有投保
            layoutBusiness.setVisibility(View.VISIBLE);
        } else {
            layoutBusiness.setVisibility(View.GONE);

        }
        if (data.insurance_compulsoryins == 1) {//有投保
            layoutForce.setVisibility(View.VISIBLE);
        } else {
            layoutForce.setVisibility(View.GONE);

        }
        if (data.tbsfz.equals(data.insurance_sfz)) {//投保人和车主是一个人
            tvSameInsure.setVisibility(View.VISIBLE);
            layoutInsurePhone.setVisibility(View.GONE);
            layoutInsureName.setVisibility(View.GONE);
            layoutInsureId.setVisibility(View.GONE);
        } else {
            tvSameInsure.setVisibility(View.GONE);
            layoutInsurePhone.setVisibility(View.VISIBLE);
            layoutInsureName.setVisibility(View.VISIBLE);
            layoutInsureId.setVisibility(View.VISIBLE);
            tvInsureName.setText(data.tbname);
            tvInsurePhone.setText(data.tbtel);
            tvInsureId.setText(data.tbsfz);
        }
        if (data.btbsfz.equals(data.insurance_sfz)) {//投保人和车主是一个人
            tvSameInsured.setVisibility(View.VISIBLE);
            layoutInsuredPhone.setVisibility(View.GONE);
            layoutInsuredName.setVisibility(View.GONE);
            layoutInsuredId.setVisibility(View.GONE);
        } else {
            tvSameInsured.setVisibility(View.GONE);
            layoutInsuredPhone.setVisibility(View.VISIBLE);
            layoutInsuredName.setVisibility(View.VISIBLE);
            layoutInsuredId.setVisibility(View.VISIBLE);
            tvInsuredName.setText(data.btbname);
            tvInsuredPhone.setText(data.btbtel);
            tvInsuredId.setText(data.btbsfz);
        }
        tvPrice.setText("￥" + (Float.valueOf(data.insurance_xunjia_bamount) + Float.valueOf(data.insurance_xunjia_camount)));

        btnOperation.setText(orderOpTexts[data.order_status]);
        btnOperation.setBackgroundColor(getResources().getColor(orderOpBgColors[data.order_status]));

        if(!AppUtil.isEmpty(data.carerlist)){
            for(String[] item:data.carerlist){
                addCarInfoItem(item[0],item[1]);
            }
        }


    }


    private void addCarInfoItem(String name , String value){
        View v= LayoutInflater.from(this).inflate(R.layout.item_car_info,null);
        ViewGroup parent=(ViewGroup)tagView.getParent();
        TextView tvName=(TextView) v.findViewById(R.id.tv_name);
        TextView tvValue=(TextView) v.findViewById(R.id.tv_value);
        tvName.setText(name);
        tvValue.setText(value);
        int index=0;
        for(int i=0;i<parent.getChildCount();i++){
            if(tagView==parent.getChildAt(i)){
                index=i;
                break;
            }
        }
        parent.addView(v,index);
    }


    private void doOperate() {

        if (data.order_status == 0) {//未完成订单，点击从选择配送地址开始重新核保
            updateOrder();
        } else if (data.order_status == 1) {//核保中，无操作功能
        } else if (data.order_status == 2) {//核保失败，从选择保险公司开始重新核保
            recreateOrder();
        } else if (data.order_status == 3) {//支付，直接去支付
            pay();
        } else if (data.order_status == 4) {//已支付，无操作
        } else if (data.order_status == 5) {//交易关闭，无操作
        }
    }

    private void updateOrder() {
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("sn", data.order_sn);
        params.append("type", "wwc");
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_BUTTON), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        AddressActivity.invoke(OrderInfoActivity.this);
                    } else {
                        AppUtil.showToast(getApplicationContext(), object == null ? "操作失败" : object.msg);
                    }

                } else {
                    AppUtil.showToast(getApplicationContext(), "操作失败");
                }
            }
        });

    }

    private void recreateOrder() {
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("sn", data.order_sn);
        params.append("type", "filed");
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_BUTTON), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        SelectInsureCompanyActivity.invoke(OrderInfoActivity.this);
                    } else {
                        AppUtil.showToast(getApplicationContext(), object == null ? "操作失败" : object.msg);
                    }

                } else {
                    AppUtil.showToast(getApplicationContext(), "操作失败");
                }
            }
        });

    }

    private void pay() {
        final ParamBuilder params = new ParamBuilder();
        params.append("sn", data.order_sn);
        if(true){
            CommonWebActivity.invoke(this,APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_PAY),"");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_PAY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        CommonWebActivity.invoke(OrderInfoActivity.this, APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().WAP_ORDER_PAY), null);
                    } else {
                        AppUtil.showToast(getApplicationContext(), object == null ? "操作失败" : object.msg);
                    }

                } else {
                    AppUtil.showToast(getApplicationContext(), "操作失败");
                }
            }
        });
    }


    public static void invoke(Context context, String order_sn) {
        Intent intent = new Intent(context, OrderInfoActivity.class);
        intent.putExtra("order_sn", order_sn);
        context.startActivity(intent);
    }

    @OnClick({R.id.layout_insure_detail, R.id.btn_operation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_insure_detail:
                InsureDetailActivity2.invoke(this,data.order_sn);
                break;
            case R.id.btn_operation:
                doOperate();
                break;
        }
    }
}
