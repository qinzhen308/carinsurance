package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.StringUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Address;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 * 编辑地址
 */

public class EditAddressActivity extends BaseActivity {


    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.btn_address)
    TextView btnAddress;
    @BindView(R.id.et_address_detail)
    EditText etAddressDetail;
    @BindView(R.id.btn_save)
    TextView btnSave;

    private Address address;
    private String cityName;
    private String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExtra();
        initView();
    }

    private void setExtra() {
        Object obj=getIntent().getSerializableExtra("address");
        if(obj!=null){
            address=(Address)obj;
        }
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_edit_address, false, true);
        ButterKnife.bind(this);
        init();
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, EditAddressActivity.class);
        context.startActivity(intent);
    }

    public static void invoke(Context context, Address address) {
        Intent intent = new Intent(context, EditAddressActivity.class);
        intent.putExtra("address",address);
        context.startActivity(intent);
    }

    private void init() {
        if (address==null) {
            setTitleText("", "新增地址", 0, true);

        } else {
            etName.setText(address.name);
            String city="";
            String detail="";
            String[] addrs=address.addr.trim().split(" ");
            String[] citys=address.city.name;
            pid="";
            for(int i=0;i<citys.length;i++){
                city+=citys[i];
                pid+=address.city.id[i]+",";
            }
            if (pid.endsWith(",")) {
                pid = pid.substring(0, pid.length() - 1);
            }

            for(int i=citys.length;i<addrs.length;i++){
                detail+=addrs[i];
            }
            etAddressDetail.setText(detail);
            btnAddress.setText(city);
            etPhone.setText(address.tel);
            setTitleText("", "编辑地址", 0, true);
        }

    }


    private void submit() {
        if(address==null){
            addAddress();
        }else {
            editAddress();
        }
    }

    @OnClick({R.id.btn_address, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_address:
                selectAddress();
                break;
            case R.id.btn_save:
                submit();
                break;
        }
    }

    private void selectAddress(){
        SelectCityActivity.invoke(this,true,pid);
    }


    private void editAddress(){
        String name=etName.getText().toString();
        if(name.length()==0){
            AppUtil.showToast(this,"请输入名字");
            return;
        }

        String phone=etPhone.getText().toString();
        if(phone.length()==0){
            AppUtil.showToast(this,"请输入电话号码");
            return;
        }
        if(!StringUtil.isMatchingPhone(phone)){
            AppUtil.showToast(this,"请输入正确电话号码");
            return;
        }
        if(pid.length()==0){
            AppUtil.showToast(this,"请选择省市区");
            return;
        }
        String detail=etAddressDetail.getText().toString();
        if(detail.length()==0){
            AppUtil.showToast(this,"请填写具体地址");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("area",pid);
        requester.getParams().put("id",address.id);
        requester.getParams().put("member_logistics_name",name);
        requester.getParams().put("member_logistics_tel",phone);
        requester.getParams().put("member_logistics_address",detail);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_EDIT_ADDRESS), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(),"修改成功");
                        finish();
                    } else {
                        AppUtil.showToast(getApplicationContext(),object!=null?object.msg:"修改失败");

                    }
                }
            }
        },requester, DESUtil.SECRET_DES);

    }

    private void addAddress(){
        String name=etName.getText().toString();
        if(name.length()==0){
            AppUtil.showToast(this,"请输入名字");
            return;
        }
        String phone=etPhone.getText().toString();
        if(phone.length()==0){
            AppUtil.showToast(this,"请输入电话号码");
            return;
        }
        if(!StringUtil.isMatchingPhone(phone)){
            AppUtil.showToast(this,"请输入正确电话号码");
            return;
        }
        if(AppUtil.isNull(pid)){
            AppUtil.showToast(this,"请选择省市区");
            return;
        }
        String detail=etAddressDetail.getText().toString();
        if(detail.length()==0){
            AppUtil.showToast(this,"请填写具体地址");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("area",pid);
        requester.getParams().put("member_logistics_name",name);
        requester.getParams().put("member_logistics_tel",phone);
        requester.getParams().put("member_logistics_address",detail);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ADD_ADDRESS), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(),"添加成功");
                        finish();

                    } else {
                        AppUtil.showToast(getApplicationContext(),object!=null?object.msg:"添加失败");

                    }
                }
            }
        },requester, DESUtil.SECRET_DES);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectCityActivity.REQUEST_CODE) {
                cityName = "";
                pid = "";
                for (int i = 0; i < 3; i++) {
                    if (data.hasExtra("regionId_" + i)) {
                        pid += data.getStringExtra("regionId_" + i) + ",";
                    }
                    if (data.hasExtra("regionName_" + i)) {
                        cityName += data.getStringExtra("regionName_" + i);
                    }
                }
                if (pid.endsWith(",")) {
                    pid = pid.substring(0, pid.length() - 1);
                }
                btnAddress.setText(cityName);
            }
        }
    }
}
