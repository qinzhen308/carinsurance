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
import com.paulz.carinsurance.model.CustomerDetail;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.view.CommonDialog;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 */

public class AddCustomerActivity extends BaseActivity {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_id)
    EditText etId;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.btn_save)
    TextView btnSave;
    CustomerDetail detail;
    DatePickView datePickView;
    @BindView(R.id.btn_delete)
    TextView btnDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExtra();
        initView();
    }

    private void setExtra() {
        Object object = getIntent().getSerializableExtra("extra_data");
        if (object != null) {
            detail = (CustomerDetail) object;
        }

    }


    private void initView() {
        setActiviyContextView(R.layout.activity_add_customer, false, true);
        setTitleText("", detail == null ? "添加客户" : "编辑客户", 0, true);
        ButterKnife.bind(this);
        if (detail != null) {
            tvBirthday.setText(detail.customer_birthday==0?"":DateUtil.getYMDDate(new Date(detail.customer_birthday * 1000)));
            etName.setText(detail.customer_name);
            etPhone.setText(detail.customer_tel);
            etId.setText(detail.customer_sfz);
            etRemark.setText(detail.customer_remark);
            btnDelete.setVisibility(View.VISIBLE);
        }
    }


    private void showDataPicker() {
        if (datePickView == null) {
            datePickView = new DatePickView(this);
            datePickView.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    tvBirthday.setText(date);
                }
            });
        }
        datePickView.show();
    }


    @OnClick({R.id.btn_save, R.id.tv_birthday,R.id.btn_delete})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                submit();
                break;
            case R.id.tv_birthday:
                showDataPicker();
                break;
            case R.id.btn_delete:
                showDeleteDialog();
                break;
        }

    }


    private void showDeleteDialog() {
        CommonDialog dialog = new CommonDialog(this);
        dialog.setDesc("确定要删除该客户？");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                delete();
            }
        });
        dialog.show();
    }

    private void delete(){
        if(detail==null)return;
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("id",detail.customer_id);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CUSTOMER_DELETE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK ) {
                        AppUtil.showToast(getApplicationContext(),"删除成功");
                        MainActivity.invoke(AddCustomerActivity.this);
                    } else {
                        AppUtil.showToast(getApplicationContext(),object!=null?object.msg:"删除失败");

                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"删除失败");

                }
            }
        },requester, DESUtil.SECRET_DES);


    }

    private void submit() {
        String name = etName.getText().toString().trim();
        if (name.length() == 0) {
            AppUtil.showToast(getApplicationContext(), "请输入名字");
            return;
        }
        if (name.length() <2) {
            AppUtil.showToast(getApplicationContext(), "请输入正确姓名");
            return;
        }
        if (!StringUtil.isMatchingChiness(name)) {
            AppUtil.showToast(getApplicationContext(), "请输入中文名字");
            return;
        }
        String phone = etPhone.getText().toString().trim();
        if (phone.length() == 0) {
            AppUtil.showToast(getApplicationContext(), "请输入电话");
            return;
        }
        if (!AppUtil.isMobilePhone(phone)) {
            AppUtil.showToast(getApplicationContext(), "请输入正确电话号码");
            return;
        }
        String sfz = etId.getText().toString().trim();

        if (sfz.length() > 0) {
            if (!StringUtil.isMatchingPersonId(sfz)) {
                AppUtil.showToast(getApplicationContext(), "请输入正确身份证号");
                return;
            }
        }

        String birthday = tvBirthday.getText().toString().trim();
        String remark = etRemark.getText().toString().trim();
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("customer_name", name);
        requester.getParams().put("customer_tel", phone);
        requester.getParams().put("customer_sfz", sfz);
        requester.getParams().put("customer_birthday", birthday);
        requester.getParams().put("customer_remark", remark);
        String url = "";
        if (detail != null) {
            requester.getParams().put("customer_id", detail.customer_id);
            url = AppUrls.getInstance().URL_CUSTOMER_EDIT;
        } else {
            url = AppUrls.getInstance().URL_CUSTOMER_ADD;

        }
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), url), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(), detail == null ? "添加成功" : "修改成功");
                        finish();
                    } else {
                        AppUtil.showToast(getApplicationContext(), object != null ? object.msg : (detail == null ? "添加失败" : "修改失败"));

                    }
                }
            }
        }, requester,DESUtil.SECRET_DES);

    }

    public static void invoke(Context context, CustomerDetail detail) {
        Intent intent = new Intent(context, AddCustomerActivity.class);
        intent.putExtra("extra_data", detail);
        context.startActivity(intent);
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, AddCustomerActivity.class);
        context.startActivity(intent);
    }


}
