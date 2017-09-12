package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AddressAdapter;
import com.paulz.carinsurance.adapter.BankCardAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Address;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.view.CommonDialog;
import com.paulz.carinsurance.view.pulltorefresh.PullListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/22.
 */

public class AddressListActivity extends BaseListActivity {

    TextView btnAdd;
    private AddressAdapter mAdapter;

    public final static int REQUEST_CODE=123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_bank_card, false, true);
        setTitleText("", "配送地址", 0, true);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();

    }

    private void initView() {
        mPullListView = (PullListView) findViewById(R.id.listview);
        mPullListView.setMode(-1);
        mListView = mPullListView.getRefreshableView();
        mListView.setDividerHeight(ScreenUtil.dip2px(this, 10));
        View footer=View.inflate(this, R.layout.layout_footer, null);
        mListView.addFooterView(footer);
        mAdapter = new AddressAdapter(this);
        mListView.setAdapter(mAdapter);
        btnAdd=(TextView) footer.findViewById(R.id.btn_add);
        btnAdd.setText("+添加地址");
        btnAdd.setOnClickListener(this);
        mAdapter.setSelectListener(new AddressAdapter.SelectListener() {
            @Override
            public void seleted(int position, Address bean) {
                setDefaultAddress(bean);
            }

            @Override
            public void delete(int position, Address bean) {
                showDeleteDialog(bean);
            }
        });
    }

    private void showDeleteDialog(final Address bean){
        CommonDialog dialog=new CommonDialog(this);
        dialog.setDesc("确定要删除该配送地址？");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                deleteAddress(bean);
            }
        });
        dialog.show();
    }


    private void loadData() {

        ParamBuilder params=new ParamBuilder();

        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MY_ADDRESS), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<PageData> object= GsonParser.getInstance().parseToObj(result,PageData.class);
                    if(object!=null&&object.status==BaseObject.STATUS_OK&&object.data!=null){
                        showSuccess();
                        mAdapter.setList(object.data.list);
                        mAdapter.notifyDataSetChanged();
                    }else {
                        showNodata();
                    }
                }else {
                    showFailture();
                }
            }
        });


    }

    private void setDefaultAddress(final Address bean){
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("id",bean.id);
        requester.getParams().put("member_logistics_default","1");
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SET_DEFAULT_ADDRESS), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK ) {
//                        AppUtil.showToast(getApplicationContext(),"修改成功");
                        loadData();
                    } else {
                        AppUtil.showToast(getApplicationContext(),object!=null?object.msg:"设置失败");

                    }
                }
            }
        },requester, DESUtil.SECRET_DES);


    }

    private void deleteAddress(final Address bean){
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("id",bean.id);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_DELETE_ADDRESS), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK ) {
//                        AppUtil.showToast(getApplicationContext(),"修改成功");
                        loadData();
                    } else {
                        AppUtil.showToast(getApplicationContext(),object!=null?object.msg:"删除失败");

                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"删除失败");

                }
            }
        });


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


    public static void invoke(Activity context, boolean editable) {
        Intent intent = new Intent(context, AddressListActivity.class);
        intent.putExtra("extra_editable",editable);
        context.startActivityForResult(intent,REQUEST_CODE);
    }


    @Override
    public void onClick(View arg0) {
        if(arg0==btnAdd){
            EditAddressActivity.invoke(this);
        }else {
            super.onClick(arg0);
        }
    }

    private class PageData{
        List<Address> list;

    }
}
