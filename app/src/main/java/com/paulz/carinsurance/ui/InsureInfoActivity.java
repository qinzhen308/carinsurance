package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AbsMutipleAdapter;
import com.paulz.carinsurance.adapter.ViewHolder;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Area;
import com.paulz.carinsurance.model.InsureCate;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.view.ListViewInScrollView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/28.
 * 选择投保方案
 */

public class InsureInfoActivity extends BaseActivity {


    @BindView(R.id.btn_city)
    TextView btnCity;
    @BindView(R.id.btn_address)
    TextView btnAddress;
    @BindView(R.id.btn_get_effective_date)
    TextView btnGetEffectiveDate;
    @BindView(R.id.cb_force_and_car)
    CheckBox cbForceAndCar;
    @BindView(R.id.btn_effective_date1)
    TextView btnEffectiveDate1;
    @BindView(R.id.cb_business)
    CheckBox cbBusiness;
    @BindView(R.id.btn_effective_date2)
    TextView btnEffectiveDate2;
    @BindView(R.id.btn_next)
    TextView btnNext;
    @BindView(R.id.layout_force)
    LinearLayout layoutForce;
    @BindView(R.id.layout_business)
    LinearLayout layoutBusiness;
    @BindView(R.id.listview)
    ListViewInScrollView listview;
    @BindView(R.id.label_business)
    TextView labelBusiness;

    private boolean neenBusiness;
    private boolean neenForce;

    ItemAdapter itemAdapter;

    PageInfo data;
    String areaId;
    String areaName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_insure_info, false, true);
        setTitleText("", "投保方案选择", 0, true);
        ButterKnife.bind(this);
        itemAdapter = new ItemAdapter(this);
        listview.setAdapter(itemAdapter);
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, InsureInfoActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.btn_city, R.id.btn_address, R.id.btn_get_effective_date, R.id.btn_effective_date1, R.id.btn_effective_date2, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_city:
                SelectCityActivity.invoke(this, false,data!=null?data.cityid:"",data!=null&&data.data!=null?data.data.citylist:null);
                break;
            case R.id.btn_address:
                if(data==null||AppUtil.isNull(data.cityid)){
                    AppUtil.showToast(getApplicationContext(),"请先选择省市");
                }else {
                    SelectAreaActivity.invoke(this,data.cityid,areaId);
                }
                break;
            case R.id.btn_get_effective_date:
                requestEffectiveDate();
                break;
            case R.id.btn_effective_date1:
                showDatePicker1();
                break;
            case R.id.btn_effective_date2:
                showDatePicker2();
                break;
            case R.id.btn_next:
                submit();
                break;
        }
    }


    private void requestEffectiveDate(){
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_GET_EFFECTIVE_DATE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<EffectiveDate> object = GsonParser.getInstance().parseToObj(result, EffectiveDate.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        btnEffectiveDate1.setText(object.data.cdate);
                        btnEffectiveDate2.setText(object.data.bdate);
                    } else {
                        AppUtil.showToast(getApplicationContext(), object!=null?object.msg:"获取失败");
                    }
                }
            }
        });
    }

    @OnCheckedChanged(R.id.cb_business)
    public void checkBusiness(boolean isChecked) {
        neenBusiness = isChecked;
        layoutBusiness.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        listview.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        labelBusiness.setVisibility(isChecked ? View.VISIBLE : View.GONE);

    }

    @OnCheckedChanged(R.id.cb_force_and_car)
    public void checkForce(boolean isChecked) {
        neenForce = isChecked;
        layoutForce.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    public class ItemAdapter extends AbsMutipleAdapter<InsureCate, ViewHolderImpl> {


        public ItemAdapter(Activity context) {
            super(context);
        }

        @Override
        public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
            return new ViewHolderImpl(View.inflate(mContext, R.layout.item_insure_cate, null));
        }

        @Override
        public void onBindViewHolder(int position, ViewHolderImpl holder) {
            final InsureCate cate = (InsureCate) getItem(position);
            holder.title.setText(cate.insurance_types_name);
            if (cate.insurance_types_hasbjmp == 1) {
//                holder.btnIgnore.setSelected();
                holder.btnIgnore.setVisibility(View.VISIBLE);
                for(InsureCate.Option op:cate.option){
                    if("不投".equals(op.insurance_valuetype_name)){
                        if(op.insurance_valuetype_yndefault==1){
                            holder.btnIgnore.setVisibility(View.INVISIBLE);
                        }
                        break;
                    }
                }
                if (cate.insurance_types_bjmpdefault == 1) {
                    holder.btnIgnore.setBackgroundResource(R.drawable.deductible_icon_case_active);
                } else {
                    holder.btnIgnore.setBackgroundResource(R.drawable.deductible_icon_case_defulat);
                }

            } else {
                holder.btnIgnore.setVisibility(View.INVISIBLE);
            }
            for (InsureCate.Option op : cate.option) {
                if (op.insurance_valuetype_yndefault == 1) {
                    holder.btnFuck.setText(op.insurance_valuetype_name);
                    break;
                }
            }
            holder.btnIgnore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cate.insurance_types_bjmpdefault = cate.insurance_types_bjmpdefault == 1 ? 0 : 1;
                    notifyDataSetChanged();
                }
            });
            holder.btnFuck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(cate);
                }
            });
        }


    }

    public static class ViewHolderImpl extends ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.btn_ignore)
        TextView btnIgnore;
        @BindView(R.id.btn_fuck)
        TextView btnFuck;

        public ViewHolderImpl(View view) {
            super(view);
        }

    }

    private void showMenu(final InsureCate cate) {
        IOSDialogUtil.OnSheetItemClickListener l = new IOSDialogUtil.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                for (int i = 0; i < cate.option.size(); i++) {
                    if (which - 1 == i) {
                        cate.option.get(i).insurance_valuetype_yndefault = 1;
                    } else {
                        cate.option.get(i).insurance_valuetype_yndefault = 0;
                    }
                    itemAdapter.notifyDataSetChanged();
                }
            }
        };
        IOSDialogUtil iosDialogUtil = new IOSDialogUtil(this);
        for (InsureCate.Option option : cate.option) {
            if(option.insurance_valuetype_yndefault==1){
                iosDialogUtil.addSheetItem(option.insurance_valuetype_name, IOSDialogUtil.SheetItemColor.Blue, l);
            }else {
                iosDialogUtil.addSheetItem(option.insurance_valuetype_name, IOSDialogUtil.SheetItemColor.Black, l);
            }
        }
        iosDialogUtil.builder().show();

    }

    private void loadData() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_INFO), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageInfo> object = GsonParser.getInstance().parseToObj(result, PageInfo.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        data = object.data;
                        handleData();
                    } else {
                        AppUtil.showToast(getApplicationContext(), "加载失败");
                    }
                }
            }
        });

    }

    private void handleData() {
        itemAdapter.setList(data.typelist);
        itemAdapter.notifyDataSetChanged();
        btnCity.setText(data.cityname);
        data.compulsoryinsdate=null;
        data.businessinsdate=null;
//        btnEffectiveDate1.setText(data.compulsoryinsdate);
//        btnEffectiveDate2.setText(data.businessinsdate);
        btnEffectiveDate1.setText("");
        btnEffectiveDate2.setText("");
    }


    private class PageInfo {
        public String cityid;
        public String cityname;
        public String compulsoryinsdate;
        public String businessinsdate;
        List<InsureCate> typelist;
        ArrayList<Area> citylist;
        public CityData data;

    }

    private class EffectiveDate {
        public String cdate;
        public String bdate;
    }

    private class CityData{
        ArrayList<Area> citylist;
    }

    DateAfterPickView datePickView1;

    private void showDatePicker1() {
        if (datePickView1 == null) {
            datePickView1 = new DateAfterPickView(this);
            datePickView1.setDatePickListener(new DateAfterPickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    if(DateUtil.afterToday(date,true)){
                        data.compulsoryinsdate = date;
                        btnEffectiveDate1.setText(date);
                    }else {
                        AppUtil.showToast(getApplication(),"请选择今天或以后的日期");
                    }

                }
            });
        }
        datePickView1.show();
    }

    DateAfterPickView datePickView2;

    private void showDatePicker2() {
        if (datePickView2 == null) {
            datePickView2 = new DateAfterPickView(this);
            datePickView2.setDatePickListener(new DateAfterPickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    if(DateUtil.afterToday(date,true)){
                        data.businessinsdate = date;
                        btnEffectiveDate2.setText(date);
                    }else {
                        AppUtil.showToast(getApplication(),"请选择今天或以后的日期");
                    }

                }
            });
        }
        datePickView2.show();
    }

    public void submit() {

        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        if(AppUtil.isNull(data.cityid)){
            AppUtil.showToast(getApplicationContext(),"请选择投保城市");
            return;
        }
        requester.getParams().put("area", data.cityid);
        if(!AppUtil.isNull(areaId)){
            requester.getParams().put("insurance_distsid", areaId);
        }
        requester.getParams().put("insurance_compulsoryins", cbForceAndCar.isChecked() ? "1" : "0");
        requester.getParams().put("insurance_businessins", cbBusiness.isChecked() ? "1" : "0");
        if(cbForceAndCar.isChecked()){
            if(AppUtil.isNull(data.compulsoryinsdate)){
                AppUtil.showToast(getApplicationContext(),"请选择交强险的生效日期");
                return;
            }
            requester.getParams().put("insurance_compulsoryinsdate", data.compulsoryinsdate);
        }
        if(cbBusiness.isChecked()){
            if(AppUtil.isNull(data.businessinsdate)){
                AppUtil.showToast(getApplicationContext(),"请选择商业险险的生效日期");
                return;
            }
            requester.getParams().put("insurance_businessinsdate", data.businessinsdate);
        }
        DialogUtil.showDialog(lodDialog);
        if (cbBusiness.isChecked()) {
            for (InsureCate cate : data.typelist) {
                requester.getParams().put("bjmp" + cate.insurance_types_id, "" + cate.insurance_types_bjmpdefault);
                for (InsureCate.Option op : cate.option) {
                    if (op.insurance_valuetype_yndefault == 1) {
                        requester.getParams().put("optionval" + cate.insurance_types_id, op.insurance_valuetype_id + "," + op.insurance_valuetype_value + "," + op.insurance_valuetype_name);
                        break;
                    }
                }
            }
        }
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_SUBMIT), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        SelectInsureCompanyActivity.invoke(InsureInfoActivity.this);

                    } else {
                        AppUtil.showToast(getApplicationContext(), "加载失败");
                    }
                }
            }
        }, requester, DESUtil.SECRET_DES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectCityActivity.REQUEST_CODE) {
                this.data.cityname = "";
                this.data.cityid = "";
                areaName = "";
                areaId = "";
                int tagIndex = 0;
                if (data.getBooleanExtra("extra_all", false)) {//包括区县
                    for (int i = 0; i < 2; i++) {
                        if (data.hasExtra("regionId_" + i)) {
                            this.data.cityid += data.getStringExtra("regionId_" + i) + ",";
                            tagIndex = i + 1;
                        }
                        if (data.hasExtra("regionName_" + i)) {
                            this.data.cityname += data.getStringExtra("regionName_" + i);
                        }
                    }
                    areaName = data.getStringExtra("regionName_" + tagIndex);
                    areaId = data.getStringExtra("regionId_" + tagIndex);
                } else {
                    for (int i = 0; i < 2; i++) {
                        if (data.hasExtra("regionId_" + i)) {
                            this.data.cityid += data.getStringExtra("regionId_" + i) + ",";
                        }
                        if (data.hasExtra("regionName_" + i)) {
                            this.data.cityname += data.getStringExtra("regionName_" + i);
                        }
                    }
                }
                if (this.data.cityid.endsWith(",")) {
                    this.data.cityid = this.data.cityid.substring(0, this.data.cityid.length() - 1);
                }
                btnCity.setText(this.data.cityname);
                btnAddress.setText(areaName);
            }else if(requestCode==SelectAreaActivity.REQUEST_CODE){
                areaId=data.getStringExtra("extra_area_id");
                areaName=data.getStringExtra("extra_area_name");
                btnAddress.setText(areaName);
            }
        }
    }


}
