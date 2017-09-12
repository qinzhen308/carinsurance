package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.BankCardAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Bank;
import com.paulz.carinsurance.model.BankCard;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.ui.fragment.UserCenterFragment;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.view.CommonDialog;
import com.paulz.carinsurance.view.pulltorefresh.PullListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by pualbeben on 17/5/22.
 */

public class BankCardActivity extends BaseListActivity implements BankCardAdapter.OnDeleteListener{

    TextView btnAdd;
    private BankCardAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_bank_card, false, true);
        setTitleText("", "银行卡管理", 0, true);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        mPullListView = (PullListView) findViewById(R.id.listview);
        mPullListView.setMode(-1);
        mListView = mPullListView.getRefreshableView();
        mListView.setDividerHeight(ScreenUtil.dip2px(this, 10));
        View footer=View.inflate(this, R.layout.layout_footer, null);
        mListView.addFooterView(footer);
        mAdapter = new BankCardAdapter(this);
        mListView.setAdapter(mAdapter);
        btnAdd=(TextView) footer.findViewById(R.id.btn_add);
        btnAdd.setText("+添加银行卡");
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void showNameVerifyDialog(){
        CommonDialog dialog=new CommonDialog(this);
        dialog.setDesc("实名认证后才可以绑定银行卡提现");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                NameVerifyActivity.invoke(BankCardActivity.this);
            }
        });
        dialog.setRightBtnText("去认证");
        dialog.show();
    }

    private void loadData() {
        showLoading();
        ParamBuilder params = new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_BANK_LIST), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                showSuccess();
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        mAdapter.setList(object.data.list);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        AppUtil.showToast(getApplicationContext(),object==null?"暂无银行卡":object.msg);
                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"加载失败");

                }
            }
        });

    }

    private void deleteCard(BankCard bankCard){
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("id",bankCard.member_bankcard_id);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_BANK_DELETE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        AppUtil.showToast(getApplicationContext(),"删除成功");
                        loadData();
                    } else {
                        AppUtil.showToast(getApplicationContext(),object==null?"删除失败":object.msg);
                    }
                }else {
                    AppUtil.showToast(getApplication(),"删除失败");

                }
            }
        },requester, DESUtil.SECRET_DES);

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


    public static void invoke(Context context) {
        Intent intent = new Intent(context, BankCardActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View arg0) {
        if(arg0==btnAdd){
            if(AppStatic.getInstance().getmUserInfo().member_realname>0){
                AddBankCardActivity.invoke(this);
            }else {
                showNameVerifyDialog();
            }
        }else {
            super.onClick(arg0);
        }
    }

    @Override
    public void onDelete(BankCard bean) {
        deleteCard(bean);
    }



    private class PageData{
        List<BankCard> list;
    }
}
