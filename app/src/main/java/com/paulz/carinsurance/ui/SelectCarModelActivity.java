package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.CarModelAdapter;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.CarModeInfo;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.view.UpperCaseEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 * 车辆识别代码
 */

public class SelectCarModelActivity extends BaseActivity {


    @BindView(R.id.et_vin)
    EditText etVin;
    @BindView(R.id.btn_select_date)
    TextView btnSelectDate;
    @BindView(R.id.btn_search)
    TextView btnSearch;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.btn_other_search)
    TextView btnOtherSearch;

    CarModelAdapter mAdapter;
    DatePickView datePickView;
    @BindView(R.id.layout_module1)
    LinearLayout layoutModule1;

    boolean isResultMode;

    public final static int REQUEST_CODE=124;


    public static String vinEtCache="";


    public static boolean clearCarMode=false;


    public String resultVin="";
    public String resultDate="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearCarMode=false;
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_select_carmodel, false, true);
        setTitleText("", "车辆识别代码", 0, true);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        String vin=getIntent().getStringExtra("extra_vin");
        if(AppUtil.isNull(vin)){

        }else {
            vinEtCache=vin;
        }
        etVin.setText(vinEtCache);
        btnSelectDate.setText(getIntent().getStringExtra("extra_date"));
        mAdapter = new CarModelAdapter(this);
        listView.setAdapter(mAdapter);
        etVin.setText(vinEtCache);
        etVin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                vinEtCache=s.toString();
            }
        });

        if(getIntent().getBooleanExtra("showCache",false)){
            justShowSearchCar();
        }else if(!AppUtil.isNull(vin)&&getIntent().getBooleanExtra("doSearch",false)){
            search();
        }

    }


    private void search() {
        clearCarMode=true;
        String vin = etVin.getText().toString();
        vin= UpperCaseEditText.upper(vin);
        searchCar(vin);

    }


    private void step(boolean isResult){
        isResultMode=isResult;
        if(isResult){
            layoutModule1.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            tvTip.setVisibility(View.VISIBLE);
            btnOtherSearch.setVisibility(View.VISIBLE);
        }else {
            layoutModule1.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            tvTip.setVisibility(View.GONE);
            btnOtherSearch.setVisibility(View.GONE);
        }

    }


    private void searchCar(final String vin) {
//        final String date = btnSelectDate.getText().toString();
        final String date = DateUtil.getYMDDate(new Date());
        if (AppUtil.isNull(vin)) {
            AppUtil.showToast(this, "请输入VIN码");
            return;
        }
        if(vin.length()!=17){
            AppUtil.showToast(this, "请输入正确VIN码");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("vin", vin);
        requester.getParams().put("regtime", date);
        String url="";
        if(getIntent().getBooleanExtra("is_customer",false)){
            url=AppUrls.getInstance().URL_VIN_SEARCH_CAR_IN_CUSTOMER;
        }else {
            url=AppUrls.getInstance().URL_VIN_SEARCH_CAR;
        }
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), url), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                step(true);
                resultVin=vin;
                resultDate=date;
                setResultVin();
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null&&object.data.data!=null) {
                        mAdapter.setList(object.data.data.list);
                        mAdapter.notifyDataSetChanged();
                        if(mAdapter.getCount()!=0){
//                            setResultVin();
                        }

                    } else {
                        mAdapter.setList(new ArrayList<CarModeInfo>());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, requester, DESUtil.SECRET_DES);
    }

    @Override
    public void onBackPressed() {
        if(isResultMode){
            step(false);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onLeftClick() {
        if(isResultMode){
            step(false);
            return;
        }
        super.onLeftClick();
    }

    private void showDataPicker() {
        if (datePickView == null) {
            datePickView = new DatePickView(this);
            datePickView.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    btnSelectDate.setText(date);
                }
            });
        }
        datePickView.show();
    }

    public void setResultVin(){
        Intent data=new Intent();
        data.putExtra("extra_vin",resultVin);
        data.putExtra("extra_date",resultDate);
        setResult(RESULT_OK,data);
    }

    private void showVinExample(){
        final Dialog dialog=new Dialog(this,R.style.CommonDialog);
        Window dialogWindow = getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_trans_image);
        ((ImageView)dialog.findViewById(R.id.iv_img)).setImageResource(R.drawable.img_vin_example);
        dialog.findViewById(R.id.common_dialog_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();
    }


    private void justShowSearchCar() {

        step(true);
        tvTip.setVisibility(View.GONE);
        isResultMode=false;
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        String url=null;
        if(getIntent().getBooleanExtra("is_customer",false)){
            url=AppUrls.getInstance().URL_CUSTOMER_SEARCH_CAR_MODEL;
        }else {
            url=AppUrls.getInstance().URL_SEARCH_CAR_MODEL;
        }
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), url), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageDataInner> object = GsonParser.getInstance().parseToObj(result, PageDataInner.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        mAdapter.setList(object.data.list);
                        mAdapter.notifyDataSetChanged();
                        if(AppUtil.isEmpty(object.data.list)){
                            step(false);
                        }

                    } else {
                        mAdapter.setList(new ArrayList<CarModeInfo>());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        },requester, DESUtil.SECRET_DES);
    }



    public static void invoke(Activity context, String vin,String date,boolean fromCustomer,boolean doSearch,boolean showCache) {
        Intent intent = new Intent(context, SelectCarModelActivity.class);
        intent.putExtra("extra_vin", vin);
        intent.putExtra("extra_date", date);
        intent.putExtra("is_customer", fromCustomer);
        intent.putExtra("doSearch", doSearch);
        intent.putExtra("showCache", showCache);
        context.startActivityForResult(intent,REQUEST_CODE);
    }


    @OnClick({R.id.btn_select_date, R.id.btn_search, R.id.btn_other_search,R.id.btn_help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select_date:
                showDataPicker();
                break;
            case R.id.btn_search:
                search();
                break;
            case R.id.btn_other_search:
                SearchCarModelActivity.invoke(this,false,getIntent().getBooleanExtra("is_customer",false));
                break;
            case R.id.btn_help:
                showVinExample();
                break;
        }
    }

    private class PageData {
        PageDataInner data;

    }

    public class PageDataInner {
        List<CarModeInfo> list;
    }

    private int needSetResult=-100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK==resultCode){
            if(requestCode==SearchCarModelActivity.REQUEST_CODE){
                data.putExtra("extra_vin",resultVin);
                data.putExtra("extra_date",resultDate);
                setResult(RESULT_OK,data);
                finish();
            }
        }else if(resultCode==100){
            needSetResult=resultCode;
            setResult(resultCode);
            finish();
        }
    }
}
