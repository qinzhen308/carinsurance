package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 * 用户信息
 */

public class ModifyPhoneStep2Activity extends BaseActivity {


    @BindView(R.id.et_new_phone)
    EditText etNewPhone;
    @BindView(R.id.et_captcha)
    EditText etCaptcha;
    @BindView(R.id.btn_get_captcha)
    TextView btnGetCaptcha;
    @BindView(R.id.btn_save)
    TextView btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_modify_phone2, false, true);
        ButterKnife.bind(this);
        setTitleText("", "设置新的手机号", 0, true);
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, ModifyPhoneStep2Activity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.btn_get_captcha, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_captcha:
                getCaptcha();
                break;
            case R.id.btn_save:
                modify();
                break;
        }
    }

    private void modify(){
        String tel=etNewPhone.getText().toString();
        if(tel.length()==0){
            AppUtil.showToast(getApplicationContext(),"请输入新手机号");
            return;
        }
        if(!StringUtil.isMatchingPhone(tel)){
            AppUtil.showToast(getApplicationContext(),"请输入正确手机号");
            return;
        }
        String code=etCaptcha.getText().toString();
        if(code.length()==0){
            AppUtil.showToast(getApplicationContext(),"请输入验证码");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("vkey",code);
        requester.getParams().put("vphone",tel);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_VERIFY_OR_MODIFY_TEL), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK ) {
                        AppUtil.showToast(getApplicationContext(), "设置成功");
                        finish();

                    } else {

                        AppUtil.showToast(getApplicationContext(), "设置失败");
                    }
                }
            }
        },requester, DESUtil.SECRET_DES);


    }


    private void getCaptcha(){
        String tel=etNewPhone.getText().toString();
        if(tel.length()==0){
            AppUtil.showToast(getApplicationContext(),"请输入新手机号");
            return;
        }
        if(!StringUtil.isMatchingPhone(tel)){
            AppUtil.showToast(getApplicationContext(),"请输入正确手机号");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("telephone",tel);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_GET_CAPTCHA_SET_PHONE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK ) {
                        countDown();

                    } else {
                        AppUtil.showToast(getApplicationContext(), object!=null&&!AppUtil.isNull(object.msg)?object.msg:"设置失败");

                    }
                }else {
                    AppUtil.showToast(getApplicationContext(), "获取失败");

                }
            }
        },requester, DESUtil.SECRET_DES);


    }


    CountDownTimer timer;

    private void countDown(){
        btnGetCaptcha.setText("60s");
        btnGetCaptcha.setEnabled(false);
        timer=new CountDownTimer(60*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time=millisUntilFinished/1000;
                btnGetCaptcha.setText(time+"s");
            }

            @Override
            public void onFinish() {
                btnGetCaptcha.setEnabled(true);
                btnGetCaptcha.setText("重新获取");
            }
        }.start();
    }

}
