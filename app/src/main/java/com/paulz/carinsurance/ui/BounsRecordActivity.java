package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.BounsRecordAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.wrapper.BeanWraper;
import com.paulz.carinsurance.model.wrapper.BounsRecordWraper;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.view.pulltorefresh.PullListView;
import com.paulz.carinsurance.view.pulltorefresh.PullToRefreshBase;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/5/22.
 * 奖金明细
 */

public class BounsRecordActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener,PullToRefreshBase.OnRefreshListener{

    private BounsRecordAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_bouns_record, true, true);
        setTitleText("", "佣金明细", 0, true);
        ButterKnife.bind(this);
        initView();
        setListener();
        initData(false);
    }


    private void initView() {
        mPullListView = (PullListView) findViewById(R.id.listview);
        mPullListView.setMode(-1);
        mListView = mPullListView.getRefreshableView();
        mAdapter = new BounsRecordAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    private void setListener(){
        mListView.setOnScrollListener(new MyOnScrollListener());
        mLoadStateController.setOnLoadErrorListener(this);
        mPullListView.setOnRefreshListener(this);
    }


    @Override
    protected BeanWraper newBeanWraper() {
        return new BounsRecordWraper();
    }

    private void initData(boolean isRefresh){
        if(!isRefresh){
            showLoading();
        }

        ParamBuilder params=new ParamBuilder();
        if(isRefresh){
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_BOUNS_RECORD), BounsRecordWraper.class);
        }else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_BOUNS_RECORD), BounsRecordWraper.class);
        }


    }

    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        if(AppUtil.isEmpty(allData)){
            showNodata();
        }else {
            showSuccess();
        }
        mAdapter.setList(allData);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void loadError(String message, Throwable throwable, int page) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        showFailture();
    }

    @Override
    protected void loadTimeOut(String message, Throwable throwable) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        showFailture();
    }

    @Override
    protected void loadNoNet() {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        showFailture();
    }

    @Override
    protected void loadServerError() {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        showFailture();

    }


    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        if(!isLoading()){
            initData(true);
        }
    }

    @Override
    public void onAgainRefresh() {
        // TODO Auto-generated method stub
        initData(false);
    }



    public static void invoke(Context context) {
        Intent intent = new Intent(context, BounsRecordActivity.class);
        context.startActivity(intent);
    }


}
