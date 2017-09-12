package com.paulz.carinsurance.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.BankCard;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.view.CommonDialog;
import com.paulz.carinsurance.view.wheel.ArrayWheelAdapter;
import com.paulz.carinsurance.view.wheel.OnWheelChangedListener;
import com.paulz.carinsurance.view.wheel.OnWheelClickedListener;
import com.paulz.carinsurance.view.wheel.WheelView;
import com.paulz.carinsurance.view.wheel.WheelViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 * 提现申请
 */

public class WithdrawApplyActivity extends BaseActivity {

    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.et_captcha)
    EditText etCaptcha;
    @BindView(R.id.btn_get_captcha)
    TextView btnGetCaptcha;
    @BindView(R.id.btn_submit)
    TextView btnSubmit;

    PageData data;
    @BindView(R.id.btn_bank_card)
    TextView btnBankCard;

    private BankCard selectedBankCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_withdraw_apply, false, true);
        ButterKnife.bind(this);
        setTitleText("", "申请提现", 0, true);
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, WithdrawApplyActivity.class);
        context.startActivity(intent);
    }


    @OnClick({R.id.btn_get_captcha, R.id.btn_submit, R.id.btn_forget_pwd, R.id.btn_change_phone,R.id.btn_bank_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_captcha:
                getCaptcha();
                break;
            case R.id.btn_submit:
                submit();
                break;
            case R.id.btn_forget_pwd:
                ModifyWithdrawPwdActivity.invoke(this, data.data.tel);
                break;
            case R.id.btn_change_phone:
                ModifyPhoneActivity.invoke(this, data.data.tel);
                break;
            case R.id.btn_bank_card:
                showDialog();
                break;
        }
    }

    Dialog bankcardDialog;
    WheelView wheelView;
    private void showDialog(){
        if(data==null){
            AppUtil.showToast(getApplicationContext(),"加载失败");
            return;
        }

        if(AppUtil.isEmpty(data.list)){
            showBindCardDialog();
            return;
        }

        if(bankcardDialog==null){
            View view= LayoutInflater.from(this).inflate(R.layout.wheel_common,null);
            wheelView=(WheelView) view.findViewById(R.id.wheel);
            ArrayWheelAdapter<Object> bankCardArrayWheelAdapter=new ArrayWheelAdapter<>(this,data.list.toArray());
            bankCardArrayWheelAdapter.setTextSize(15);
            wheelView.setViewAdapter(bankCardArrayWheelAdapter);

            view.findViewById(R.id.wheel_okTv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedBankCard=data.list.get(wheelView.getCurrentItem());
                    btnBankCard.setText(selectedBankCard.toString());
                    DialogUtil.dismissDialog(bankcardDialog);
                }
            });
            bankcardDialog=DialogUtil.getMenuDialog2(this,view, ScreenUtil.dip2px(this,200));
            bankcardDialog.setCanceledOnTouchOutside(true);
        }
        DialogUtil.showDialog(bankcardDialog);
    }

    private void showBindCardDialog(){
        final CommonDialog dialog=new CommonDialog(this);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                BankCardActivity.invoke(WithdrawApplyActivity.this);
            }
        });
        dialog.setDesc("绑定银行卡后才可以提现");
        dialog.setRightBtnText("去绑定");
        dialog.show();
    }





    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_WITHDRAW_APPLY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        data=object.data;
                        etMoney.setHint("当前可提现金额：￥"+data.data.atming);
                        if (TextUtils.isEmpty(object.data.data.tel)) {
                            tvPhone.setText("未绑定");
                        } else {
                            data = object.data;
                            tvPhone.setText(object.data.data.tel);
                        }
                    } else {
                        tvPhone.setText("未绑定");
                        AppUtil.showToast(getApplicationContext(), "加载失败");
                    }
                }
            }
        });

    }


    private void submit() {
        String code = etCaptcha.getText().toString();
        if (code.length() == 0) {
            AppUtil.showToast(getApplicationContext(), "请输入验证码");
            return;
        }


        String pwd = etPwd.getText().toString();
        if (pwd.length() == 0) {
            AppUtil.showToast(getApplicationContext(), "请输入提现密码");
            return;
        }
        if (pwd.length() < 6) {
            AppUtil.showToast(getApplicationContext(), "密码长度不能小于6位");
            return;
        }
        String money = etMoney.getText().toString().trim();
        if(money.length()==0){
            AppUtil.showToast(getApplicationContext(), "请输入提现金额");
            return;
        }
        if(Float.compare(Float.valueOf(money), 0.01f) < 0){
            AppUtil.showToast(getApplicationContext(), "提现金额不能小于0.01元");
            return;
        }
        if (Float.compare(Float.valueOf(money), Float.valueOf(data.data.atming)) >= 0) {
            AppUtil.showToast(getApplicationContext(), "提现金额不能超出当前余额");
            return;
        }

        if (selectedBankCard==null) {
            AppUtil.showToast(getApplicationContext(), "请选择提现的银行卡");
            return;
        }

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("vkey", code);
        requester.getParams().put("password", pwd);
        requester.getParams().put("money", money);
        requester.getParams().put("bankcard", selectedBankCard.member_bankcard_id);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_WITHDRAW_APPLY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        AppUtil.showToast(getApplicationContext(), "申请成功");
                        finish();

                    } else {
                        AppUtil.showToast(getApplicationContext(), object!=null?object.msg:"申请失败");
                    }
                }else {
                    AppUtil.showToast(getApplicationContext(), "请求失败");
                }
            }
        }, requester, DESUtil.SECRET_DES);


    }


    private void getCaptcha() {
        if (AppUtil.isNull(data.data.tel)) {
            AppUtil.showToast(getApplicationContext(), "未绑定手机，不能修改");
            return;
        }

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("telephone", data.data.tel);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_GET_CAPTCHA_WITHDRAW_APPLY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        countDown();

                    } else {

                        AppUtil.showToast(getApplicationContext(), "获取失败");
                    }
                }
            }
        }, requester, DESUtil.SECRET_DES);


    }


    CountDownTimer timer;

    private void countDown() {
        btnGetCaptcha.setText("60s");
        btnGetCaptcha.setEnabled(false);
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished / 1000;
                btnGetCaptcha.setText(time + "s");
            }

            @Override
            public void onFinish() {
                btnGetCaptcha.setEnabled(true);
                btnGetCaptcha.setText("重新获取");
            }
        }.start();
    }



    private class PageData {
        BaseData data;
        List<BankCard> list;
    }

    private class BaseData {
        String tel;
        String atming;
    }



}
