package com.paulz.carinsurance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AchievementAdapter;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.wrapper.AchievementWraper;
import com.paulz.carinsurance.model.wrapper.BeanWraper;
import com.paulz.carinsurance.ui.DatePickView;
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

public class AchievementFragment extends BaseListFragment implements LoadStateController.OnLoadErrorListener, PullToRefreshBase.OnRefreshListener {


    @BindView(R.id.tv_price_all)
    TextView tvPriceAll;
    @BindView(R.id.tv_price_force)
    TextView tvPriceForce;
    @BindView(R.id.tv_price_buz)
    TextView tvPriceBuz;
    AchievementAdapter mAdapter;
    @BindView(R.id.btn_start_date)
    TextView btnStartDate;
    @BindView(R.id.btn_end_date)
    TextView btnEndDate;
    @BindView(R.id.layout_date)
    LinearLayout layoutDate;
    @BindView(R.id.listview)
    PullListView listview;

    private int tag;

    String start;
    String end;
    String tempStart;
    String tempEnd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = getArguments().getInt("tag");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_achievement, false);
        ButterKnife.bind(this, baseLayout);
        mLoadStateController=new LoadStateController(getActivity(),(ViewGroup) baseLayout.findViewById(R.id.load_state_container));
        hasLoadingState=true;
        return baseLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
        initData(false);
    }

    private void initView() {
        mPullListView = (PullListView) baseLayout.findViewById(R.id.listview);
        mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
        mListView = mPullListView.getRefreshableView();
        mListView.setDividerHeight(2);
        mAdapter = new AchievementAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        if (tag == 2) {
            layoutDate.setVisibility(View.VISIBLE);
        }
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

    private void setListener() {
        mListView.setOnScrollListener(new MyOnScrollListener());
        mLoadStateController.setOnLoadErrorListener(this);
        mPullListView.setOnRefreshListener(this);
    }


    DatePickView datePickView1;

    private void showDataPicker1() {
        if (datePickView1 == null) {
            datePickView1 = new DatePickView(getActivity());
            datePickView1.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    tempStart = date;
                    btnStartDate.setText(date);
                }
            });
        }
        datePickView1.show();
    }

    DatePickView datePickView2;

    private void showDataPicker2() {
        if (datePickView2 == null) {
            datePickView2 = new DatePickView(getActivity());
            datePickView2.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    tempEnd = date;
                    btnEndDate.setText(date);
                }
            });
        }
        datePickView2.show();
    }


    @Override
    public void heavyBuz() {

    }


    @Override
    protected BeanWraper newBeanWraper() {
        return new AchievementWraper();
    }

    private void initData(boolean isRefresh) {
        if (!isRefresh) {
            showLoading();
        }

        ParamBuilder params = new ParamBuilder();
        if (tag == 2) {
            params.append("type", "" + tag);
            params.append("starttime", start);
            params.append("endtime", end);

        } else {
            params.append("type", "" + tag);
        }
        if (isRefresh) {
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ACHIEVEMENT), AchievementWraper.class);
        } else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ACHIEVEMENT), AchievementWraper.class);
        }
    }

    private void setHeader() {
        AchievementWraper wraper = (AchievementWraper) getBeanWraper();
        tvPriceAll.setText("￥" + wraper.amount);
        tvPriceForce.setText("￥" + wraper.camount);
        tvPriceBuz.setText("￥" + wraper.bamount);
    }

    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        if (AppUtil.isEmpty(allData)) {
//            showNodata();
            showSuccess();
        } else {
            showSuccess();
        }
        setHeader();
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


    public static AchievementFragment createInstance(int tag) {
        AchievementFragment fragment = new AchievementFragment();
        Bundle data = new Bundle();
        data.putInt("tag", tag);
        fragment.setArguments(data);
        return fragment;
    }


    @OnClick({R.id.btn_start_date, R.id.btn_end_date, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_date:
                showDataPicker1();
                break;
            case R.id.btn_end_date:
                showDataPicker2();
                break;
            case R.id.btn_search:
                start=tempStart;
                end=tempEnd;
                onRefresh();
                break;
        }
    }
}
