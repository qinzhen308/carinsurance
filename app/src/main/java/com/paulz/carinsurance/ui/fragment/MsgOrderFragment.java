package com.paulz.carinsurance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.MsgNoticeAdapter;
import com.paulz.carinsurance.adapter.MsgOrderAdapter;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.wrapper.BeanWraper;
import com.paulz.carinsurance.model.wrapper.OrderWraper;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.view.pulltorefresh.PullListView;
import com.paulz.carinsurance.view.pulltorefresh.PullToRefreshBase;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/11/1.
 */

public class MsgOrderFragment extends BaseListFragment implements LoadStateController.OnLoadErrorListener, PullToRefreshBase.OnRefreshListener {


    MsgOrderAdapter mAdapter;

    private String tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = getArguments().getString("tag");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_msg_notice, false);
        mLoadStateController = new LoadStateController(getActivity(), (ViewGroup) baseLayout.findViewById(R.id.load_state_container));
        hasLoadingState = true;
        ButterKnife.bind(this, baseLayout);
        return baseLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
        initData(false);
    }

    @Override
    public void heavyBuz() {

    }

    private void setListener() {
        mListView.setOnScrollListener(new MyOnScrollListener());
        mLoadStateController.setOnLoadErrorListener(this);
        mPullListView.setOnRefreshListener(this);

    }


    private void initView() {
        mPullListView = (PullListView) baseLayout.findViewById(R.id.listview);
        mListView = mPullListView.getRefreshableView();
        mListView.setDividerHeight(15);
        mAdapter = new MsgOrderAdapter(getActivity());
        mListView.setAdapter(mAdapter);
    }

//    @OnClick({R.id.btn_add_customer})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_add_customer:
//                AddCustomerActivity.invoke(getActivity());
//                break;
//            default:
//                break;
//        }
//
//    }


    @Override
    protected BeanWraper newBeanWraper() {
        return new OrderWraper();
    }

    private void initData(boolean isRefresh) {
        if (!isRefresh) {
            showLoading();
        }

        ParamBuilder params = new ParamBuilder();
        params.append("status", tag);
        if (isRefresh) {
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_LIST), OrderWraper.class);
        } else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORDER_LIST), OrderWraper.class);
        }
    }

    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        if (AppUtil.isEmpty(allData)) {
            showNodata();
        } else {
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
        if (!isLoading()) {
            initData(true);
        }
    }

    @Override
    public void onAgainRefresh() {
        // TODO Auto-generated method stub
        initData(false);
    }

    public void updateAdapter() {
        mAdapter.notifyDataSetChanged();
    }


    public static MsgOrderFragment createInstance(String tag) {
        MsgOrderFragment fragment = new MsgOrderFragment();
        Bundle data = new Bundle();
        data.putString("tag", tag);
        fragment.setArguments(data);
        return fragment;
    }



}
