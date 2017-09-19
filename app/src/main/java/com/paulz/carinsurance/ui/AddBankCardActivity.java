package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Bank;
import com.paulz.carinsurance.model.BankCard;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 * 添加银行卡
 */

public class AddBankCardActivity extends BaseActivity {


    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.btn_created_bank)
    TextView btnCreatedBank;
    @BindView(R.id.btn_city)
    TextView btnCity;
    @BindView(R.id.et_card_number)
    EditText etCardNumber;
    @BindView(R.id.btn_save)
    TextView btnSave;

    PageData data;
    Bank selectedBank;

    private String cityName;
    private String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_add_bank_card, false, true);
        setTitleText("", "添加银行卡", 0, true);
        ButterKnife.bind(this);
        init();
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, AddBankCardActivity.class);
        context.startActivity(intent);
    }

    private void init() {
        if (true) {
            setTitleText("", "新增地址", 0, true);
        } else {
            setTitleText("", "编辑地址", 0, true);

        }

    }

    private void loadData(){
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_BANK_ADD_BANK), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        data=object.data;
                        tvName.setText(data.name);
//                        mAdapter.setList(object.data.banklist);
//                        mAdapter.notifyDataSetChanged();
                    } else {
                        AppUtil.showToast(getApplicationContext(),object==null?"暂无银行卡":object.msg);
                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"加载失败");

                }
            }
        });

    }



    private void submit() {
        String no=etCardNumber.getText().toString().trim();
        if(no.length()==0){
            AppUtil.showToast(getApplicationContext(),"请输入银行卡号");
            return;
        }
        if(selectedBank==null){
            AppUtil.showToast(getApplicationContext(),"请选择开户行");
            return;
        }

        if(TextUtils.isEmpty(pid)){
            AppUtil.showToast(getApplicationContext(),"请选择开户城市");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("typeid",selectedBank.id);
        requester.getParams().put("city",pid);
        requester.getParams().put("member_bankcard_no",no);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_BANK_ADD_BANK), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(),"添加成功");
                        finish();
                    } else {
                        AppUtil.showToast(getApplicationContext(),object==null?"添加失败":object.msg);
                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"添加失败");

                }
            }
        },requester, DESUtil.SECRET_DES);
    }


    private void selectCity() {
        SelectCityActivity.invoke(this,false,pid,null);
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
                btnCity.setText(cityName);
            }
        }
    }


    IOSDialogUtil bankDialog;
    private void selectBank() {
        if(data==null||data.banklist==null)return;
        bankDialog=new IOSDialogUtil(this);
        for(int i=0;i<data.banklist.size();i++){
            Bank bank=data.banklist.get(i);
            if(selectedBank==bank){

                bankDialog.addSheetItem(bank.name, IOSDialogUtil.SheetItemColor.Blue,bankLitener);
            }else {

                bankDialog.addSheetItem(bank.name, IOSDialogUtil.SheetItemColor.Black,bankLitener);
            }
        }
        bankDialog.builder();
        bankDialog.show();
    }


    IOSDialogUtil.OnSheetItemClickListener bankLitener=new IOSDialogUtil.OnSheetItemClickListener() {
        @Override
        public void onClick(int which) {
            selectedBank=data.banklist.get(which-1);
            btnCreatedBank.setText(selectedBank.name);
        }
    };


    @OnClick({R.id.btn_created_bank, R.id.btn_city, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_created_bank:
                selectBank();
                break;
            case R.id.btn_city:
                selectCity();
                break;
            case R.id.btn_save:
                submit();
                break;
        }
    }


    private class PageData{
        List<Bank> banklist;
        String name;

    }

}
