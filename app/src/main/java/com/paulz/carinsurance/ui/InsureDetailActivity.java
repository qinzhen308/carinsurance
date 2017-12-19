package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.StringUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AbsMutipleAdapter;
import com.paulz.carinsurance.adapter.ViewHolder;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.InsureCate;
import com.paulz.carinsurance.model.InsureDetail;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.utils.Image13Loader;
import com.paulz.carinsurance.view.ListViewInScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/29.
 * 保单详情
 */

public class InsureDetailActivity extends BaseActivity {


    InsureDetail data;
    @BindView(R.id.tv_price_all)
    TextView tvPriceAll;
    @BindView(R.id.iv_company)
    ImageView ivCompany;
    @BindView(R.id.tv_company_name)
    TextView tvCompanyName;
    @BindView(R.id.layout_insure_detail)
    LinearLayout layoutInsureDetail;
    @BindView(R.id.tv_force_price1)
    TextView tvForcePrice1;
    @BindView(R.id.layout_force_all1)
    RelativeLayout layoutForceAll1;
    @BindView(R.id.tv_boat_price1)
    TextView tvBoatPrice1;
    @BindView(R.id.layout_boat_all1)
    RelativeLayout layoutBoatAll1;
    @BindView(R.id.layout_force_and_boat)
    LinearLayout layoutForceAndBoat;
    @BindView(R.id.lv_cate)
    ListViewInScrollView lvCate;
    @BindView(R.id.tv_force_price)
    TextView tvForcePrice;
    @BindView(R.id.layout_force_all)
    LinearLayout layoutForceAll;
    @BindView(R.id.tv_boat_price)
    TextView tvBoatPrice;
    @BindView(R.id.layout_boat_all)
    LinearLayout layoutBoatAll;
    @BindView(R.id.tv_business_price)
    TextView tvBusinessPrice;
    @BindView(R.id.tv_force_date)
    TextView tvForceDate;
    @BindView(R.id.layout_force_date)
    LinearLayout layoutForceDate;
    @BindView(R.id.tv_business_date)
    TextView tvBusinessDate;
    @BindView(R.id.layout_business_date)
    LinearLayout layoutBusinessDate;
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
    EditText tvOwnerPhone;
    @BindView(R.id.cb_same_insure)
    CheckBox cbSameInsure;
    @BindView(R.id.et_not_same_insure_name)
    EditText etNotSameInsureName;
    @BindView(R.id.et_not_same_insure_id)
    EditText etNotSameInsureId;
    @BindView(R.id.et_not_same_insure_phone)
    EditText etNotSameInsurePhone;
    @BindView(R.id.layout_not_same_insure)
    LinearLayout layoutNotSameInsure;
    @BindView(R.id.cb_same_insured)
    CheckBox cbSameInsured;
    @BindView(R.id.et_not_same_insured_name)
    EditText etNotSameInsuredName;
    @BindView(R.id.et_not_same_insured_id)
    EditText etNotSameInsuredId;
    @BindView(R.id.et_not_same_insured_phone)
    EditText etNotSameInsuredPhone;
    @BindView(R.id.layout_not_same_insured)
    LinearLayout layoutNotSameInsured;
    @BindView(R.id.btn_next)
    TextView btnNext;
    @BindView(R.id.tv_appoint)
    TextView tvAppoint;
    @BindView(R.id.layout_appoint)
    View layoutAppoint;
    @BindView(R.id.layout_business_all)
    LinearLayout layoutBusinessAll;

    @BindView(R.id.tag_view)
    View tagView;

    ItemAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_insure_detail, true, true);
        setTitleText("", "保单详情", 0, true);
        ButterKnife.bind(this);
        mAdapter=new ItemAdapter(this);
        lvCate.setAdapter(mAdapter);
        init();
    }


    private void init() {
        showLoading();
        ParamBuilder params = new ParamBuilder();
        params.append("id", getIntent().getStringExtra("insurance_xunjia_id"));
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_DETAIL), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (status == 200) {
                    showSuccess();
                    BaseObject<InsureDetail> obj = GsonParser.getInstance().parseToObj(result, InsureDetail.class);
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
        tvPriceAll.setText("￥" + data.insurance_xunjia_amout);
        tvBoatPrice.setText("￥" + data.insurance_xunjia_taxamount);
        tvBoatPrice1.setText("￥" + data.insurance_xunjia_taxamount);
        tvForcePrice1.setText("￥" + data.insurance_xunjia_camount);
        tvForcePrice.setText("￥" + data.insurance_xunjia_camount);
        tvBusinessPrice.setText("￥" + data.insurance_xunjia_bamount);
        if (data.insdata.insurance_compulsoryins == 1) {//有投保
            layoutBoatAll.setVisibility(View.VISIBLE);
            layoutForceAll.setVisibility(View.VISIBLE);
            layoutForceAndBoat.setVisibility(View.VISIBLE);
            layoutForceDate.setVisibility(View.VISIBLE);

        } else {
            layoutBoatAll.setVisibility(View.GONE);
            layoutForceAll.setVisibility(View.GONE);
            layoutForceAndBoat.setVisibility(View.GONE);
            layoutForceDate.setVisibility(View.GONE);
        }

        if (data.insdata.insurance_businessins == 1) {//有投保
            layoutBusinessDate.setVisibility(View.VISIBLE);
            lvCate.setVisibility(View.VISIBLE);
            layoutBusinessAll.setVisibility(View.VISIBLE);
            layoutBusinessDate.setVisibility(View.VISIBLE);

        } else {
            layoutBusinessDate.setVisibility(View.GONE);
            lvCate.setVisibility(View.GONE);
            layoutBusinessAll.setVisibility(View.GONE);
            layoutBusinessDate.setVisibility(View.GONE);

        }

        mAdapter.setList(data.valuelist);
        mAdapter.notifyDataSetChanged();

        tvForceDate.setText(DateUtil.getYMDHMDate(data.insdata.insurance_compulsoryinsdate * 1000));
        tvBusinessDate.setText(DateUtil.getYMDHMDate(data.insdata.insurance_businessinsdate * 1000));

        tvCompanyName.setText(data.insurance_company_name);
        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_INSCOMPANY + data.insurance_company_img, ivCompany);

        tvCarCode.setText(data.insdata.insurance_carmodel_vin);
        tvCarId.setText(AppUtil.isNull(data.insdata.insurance_carnumber)?"未上牌":data.insdata.insurance_carnumber);

        tvOwnerName.setText(data.insdata.insurance_name);
        tvOwnerId.setText(data.insdata.insurance_sfz);
        tvOwnerPhone.setText(data.insdata.insurance_tel==null?"":data.insdata.insurance_tel);

        tvEngineCode.setText(data.insdata.insurance_carmodel_en);


        layoutAppoint.setVisibility(View.GONE);

//        if(AppUtil.isNull(data.insdata.insurance_carmodel_teyue)){
//            layoutAppoint.setVisibility(View.GONE);
//        }else {
//            layoutAppoint.setVisibility(View.VISIBLE);
//            tvAppoint.setText(data.insdata.insurance_carmodel_teyue);
//        }


//        etNotSameInsuredName.setText(data.insdata.insurance_name);
//        etNotSameInsuredId.setText(data.insdata.insurance_sfz);
//        etNotSameInsuredPhone.setText(data.insdata.insurance_tel==null?"":data.insdata.insurance_tel);

//        etNotSameInsureName.setText(data.insdata.insurance_name);
//        etNotSameInsureId.setText(data.insdata.insurance_sfz);
//        etNotSameInsurePhone.setText(data.insdata.insurance_tel==null?"":data.insdata.insurance_tel);
        if(!AppUtil.isEmpty(data.carerlist)){
            for(String[] item:data.carerlist){
                addCarInfoItem(item[0],item[1]);
            }
        }

    }


    private void addCarInfoItem(String name , String value){
        View v=LayoutInflater.from(this).inflate(R.layout.item_car_info,null);
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



    public static void invoke(Context context, String insurance_xunjia_id) {
        Intent intent = new Intent(context, InsureDetailActivity.class);
        intent.putExtra("insurance_xunjia_id", insurance_xunjia_id);
        context.startActivity(intent);
    }


    @OnClick(R.id.btn_next)
    public void onViewClicked() {
        submit();

    }

    @OnCheckedChanged(R.id.cb_same_insure)
    public void sameInsure(boolean isChecked) {
        layoutNotSameInsure.setVisibility(isChecked ? View.GONE : View.VISIBLE);
    }

    @OnCheckedChanged(R.id.cb_same_insured)
    public void sameInsured(boolean isChecked) {
        layoutNotSameInsured.setVisibility(isChecked ? View.GONE : View.VISIBLE);
    }


    public class ItemAdapter extends AbsMutipleAdapter<InsureDetail.InsureCate, ViewHolderImpl> {


        public ItemAdapter(Activity context) {
            super(context);
        }

        @Override
        public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
            return new ViewHolderImpl(View.inflate(mContext, R.layout.item_business_cate, null));
        }

        @Override
        public void onBindViewHolder(int position, ViewHolderImpl holder) {
            final InsureDetail.InsureCate cate = (InsureDetail.InsureCate) getItem(position);
            holder.tvCate.setText(cate.insurance_values_typename);

            holder.tvValue.setText(cate.insurance_values_name==null?"":cate.insurance_values_name);
            holder.tvPrice.setText("￥"+cate.money);

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

    public void submit(){
        String phone=tvOwnerPhone.getText().toString().trim();

        if(phone.length()==0){
            AppUtil.showToast(getApplicationContext(),"请输入手机号码");
            return;
        }
        if(!AppUtil.isMobilePhone(phone)){
            AppUtil.showToast(getApplicationContext(),"手机号码格式不正确");
            return;
        }


        HttpRequester requester=new HttpRequester();

        if(cbSameInsure.isChecked()){
            requester.getParams().put("tbr","1");
        }else {
            String insurePhone=etNotSameInsurePhone.getText().toString().trim();
            if(insurePhone.length()==0){
                AppUtil.showToast(getApplicationContext(),"请输入手机号码");
                return;
            }
            if(!AppUtil.isMobilePhone(insurePhone)){
                AppUtil.showToast(getApplicationContext(),"投保人手机号码格式不正确");
                return;
            }

            String insureName=etNotSameInsureName.getText().toString().trim();
            if(insureName.length()==0){
                AppUtil.showToast(getApplicationContext(),"请输入投保人");
                return;
            }
            if(insureName.length()<2){
                AppUtil.showToast(getApplicationContext(),"请输入正确投保人姓名");
                return;
            }
            if(!StringUtil.isMatchingChiness(insureName)){
                AppUtil.showToast(getApplicationContext(),"投保人姓名必须是中文");
                return;
            }
            String insureId=etNotSameInsureId.getText().toString().trim();
            if(insureId.length()==0){
                AppUtil.showToast(getApplicationContext(),"请输入投保人身份证");
                return;
            }
            if(!StringUtil.isMatchingPersonId(insureId)){
                AppUtil.showToast(getApplicationContext(),"投保人身份证格式不正确");
                return;
            }
            requester.getParams().put("tbtel",insurePhone);
            requester.getParams().put("tbname",insureName);
            requester.getParams().put("tbsfz",insureId);
            requester.getParams().put("tbr","0");
        }

        if(cbSameInsured.isChecked()){
            requester.getParams().put("btbr","1");
        }else {
            String insuredPhone=etNotSameInsuredPhone.getText().toString().trim();
            if(insuredPhone.length()==0){
                AppUtil.showToast(getApplicationContext(),"请输入手机号码");
                return;
            }
            if(!AppUtil.isMobilePhone(insuredPhone)){
                AppUtil.showToast(getApplicationContext(),"被投保人手机号码格式不正确");
                return;
            }

            String insuredName=etNotSameInsuredName.getText().toString().trim();
            if(insuredName.length()==0){
                AppUtil.showToast(getApplicationContext(),"请输入被投保人");
                return;
            }
            if(insuredName.length()<2){
                AppUtil.showToast(getApplicationContext(),"请输入正确被保人姓名");
                return;
            }
            if(!StringUtil.isMatchingChiness(insuredName)){
                AppUtil.showToast(getApplicationContext(),"投保人姓名必须是中文");
                return;
            }
            String insuredId=etNotSameInsuredId.getText().toString().trim();
            if(insuredId.length()==0){
                AppUtil.showToast(getApplicationContext(),"请输入被投保人身份证");
                return;
            }
            if(!StringUtil.isMatchingPersonId(insuredId)){
                AppUtil.showToast(getApplicationContext(),"被投保人身份证格式不正确");
                return;
            }
            requester.getParams().put("btbtel",insuredPhone);
            requester.getParams().put("btbname",insuredName);
            requester.getParams().put("btbsfz",insuredId);
            requester.getParams().put("btbr","0");
        }



        DialogUtil.showDialog(lodDialog);
        ParamBuilder params=new ParamBuilder();
        requester.getParams().put("insurance_tel",phone);

        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_DETAIL_SUBMIT), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    BaseObject<Object> object= GsonParser.getInstance().parseToObj(result,Object.class);
                    if(object!=null&&object.status==BaseObject.STATUS_OK){

                        AddressActivity.invoke(InsureDetailActivity.this);
                    }else {
                        AppUtil.showToast(getApplicationContext(),"提交失败");
                    }
                }
            }
        },requester, DESUtil.SECRET_DES);
    }


}
