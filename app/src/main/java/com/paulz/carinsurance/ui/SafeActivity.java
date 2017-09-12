package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
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

public class SafeActivity extends BaseActivity {


    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.layout_phone)
    LinearLayout layoutPhone;
    @BindView(R.id.layout_login_pwd)
    TextView layoutLoginPwd;
    @BindView(R.id.layout_withdraw_pwd)
    TextView layoutWithdrawPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();

    }

    private void initView() {
        setActiviyContextView(R.layout.activity_safe, false, true);
        ButterKnife.bind(this);
        setTitleText("", "账号与安全", 0, true);
    }

    @OnClick({R.id.layout_phone, R.id.layout_login_pwd, R.id.layout_withdraw_pwd})
    public void onViewClicked(View view) {
        String phone=tvPhone.getText().toString();
        switch (view.getId()) {
            case R.id.layout_phone:
                if(phone.length()>0){
                    ModifyPhoneActivity.invoke(this,phone);
                }else {
                    ModifyPhoneStep2Activity.invoke(this);
                }
                break;
            case R.id.layout_login_pwd:
                ModifyLoginPwdActivity.invoke(this,phone);
                break;
            case R.id.layout_withdraw_pwd:
                ModifyWithdrawPwdActivity.invoke(this,phone);
                break;
        }
    }


    private void loadData() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("id", ""+ AppStatic.getInstance().getUser().member_realname);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SAFE_PAGE_INFO), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        if(TextUtils.isEmpty(object.data.tel)){

                            tvPhone.setText("未绑定");
                        }else {
                            tvPhone.setText(object.data.tel);

                        }
                    } else {
                        tvPhone.setText("未绑定");

                        AppUtil.showToast(getApplicationContext(), "加载失败");
                    }
                }
            }
        });

    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, SafeActivity.class);
        context.startActivity(intent);
    }

    private class PageData{
        String tel;
    }
}
