package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.CustomerCarAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.CustomerDetail;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/29.
 */

public class CustomerInfoActivity extends BaseListActivity {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_remarks)
    TextView tvRemarks;
    private CustomerCarAdapter mAdapter;

    public CustomerDetail data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_customer_info, false, true);
        setTitleText("", "客户信息", 0, true);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new CustomerCarAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("id",getIntent().getStringExtra("extra_id"));
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CUSTOMER_DETAIL), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<CustomerDetail> object = GsonParser.getInstance().parseToObj(result, CustomerDetail.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        data=object.data;
                        setData();
                    } else {

                    }
                }
            }
        });
    }


    private void setData(){
        if(data==null){
            showNodata();
        }
        tvName.setText(data.customer_name);
        tvRemarks.setText(data.customer_remark);

        mAdapter.setList(data.list);
        mAdapter.notifyDataSetChanged();

    }


    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {

    }

    @Override
    protected void loadError(String message, Throwable throwable, int page) {

    }

    @Override
    protected void loadTimeOut(String message, Throwable throwable) {

    }

    @Override
    protected void loadNoNet() {

    }

    @Override
    protected void loadServerError() {

    }


    public static void invoke(Context context,String id) {
        Intent intent = new Intent(context, CustomerInfoActivity.class);
        intent.putExtra("extra_id",id);
        context.startActivity(intent);
    }


    @OnClick({R.id.btn_edit_customer, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_customer:
                AddCustomerActivity.invoke(this,data);
                break;
            case R.id.btn_add:
                CarInsureActivity.invoke(this,null,data.customer_id);
                break;
        }
    }
}
