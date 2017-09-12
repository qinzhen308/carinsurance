package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
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
 * Created by pualbeben on 17/6/18.
 * 保险流程---选择送货地址
 */

public class AddressActivity extends BaseActivity {


    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.btn_next)
    TextView btnNext;

    String curId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData(null);
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_receive_address, false, true);
        setTitleText("", "配送地址", 0, true);
        ButterKnife.bind(this);
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, AddressActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.btn_next,R.id.layout_address})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                submit();
                break;
            case R.id.layout_address:
                AddressListActivity.invoke(this,false);
                break;
        }
    }


    private void loadData(String id) {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        if(!TextUtils.isEmpty(id)){
            params.append("id",id);
        }
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_RECEIVE_ADDRESS), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Address> object = GsonParser.getInstance().parseToObj(result, Address.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        handleData(object.data);
                    } else {
                        AppUtil.showToast(getApplicationContext(), "加载失败");
                    }
                }
            }
        });

    }

    private void handleData(Address address) {

        if(address==null){
            tvName.setText("您还没有收货地址，请先添加");
            tvAddress.setText("");
            tvPhone.setText("");
            return;
        }
        curId=address.id;
        tvName.setText(address.name);
        tvAddress.setText(address.addr);
        tvPhone.setText(address.tel);

    }


    public void submit() {

        if(TextUtils.isEmpty(curId)){
            AppUtil.showToast(getApplicationContext(),"请选择地址");
            return;
        }
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("id", curId);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SUBMIT_RECEIVE_ADDRESS), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {

                        UnderwritingActivity.invoke(AddressActivity.this);

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
            if (requestCode == AddressListActivity.REQUEST_CODE) {
                handleData((Address) data.getSerializableExtra("extra_address"));
            }
        }
    }
}
