package com.paulz.carinsurance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AccountOrderAdapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 */

public class AccountFragment extends BaseListFragment implements LoadStateController.OnLoadErrorListener, PullToRefreshBase.OnRefreshListener {


    AccountOrderAdapter mAdapter;
    @BindView(R.id.search_bar)
    EditText searchBar;

    private String tag;
    private String keyword = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = getArguments().getString("tag");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_account_order, false);
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
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    keyword = searchBar.getText().toString().trim();
                    onRefresh();
                    AppUtil.hideSoftInputMethod(getActivity(), searchBar);
                    return true;
                }
                return false;
            }
        });
    }


    private void initView() {
        mPullListView = (PullListView) baseLayout.findViewById(R.id.listview);
        mListView = mPullListView.getRefreshableView();
        mListView.setDividerHeight(15);
        mAdapter = new AccountOrderAdapter(getActivity());
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
        params.append("keyword", keyword);
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


    public static AccountFragment createInstance(String tag) {
        AccountFragment fragment = new AccountFragment();
        Bundle data = new Bundle();
        data.putString("tag", tag);
        fragment.setArguments(data);
        return fragment;
    }




    @OnClick(R.id.btn_search)
    public void onViewClicked() {
        keyword = searchBar.getText().toString().trim();
        onRefresh();
        AppUtil.hideSoftInputMethod(getActivity(), searchBar);
    }
}
