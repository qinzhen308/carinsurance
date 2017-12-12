package com.paulz.carinsurance.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.CustomerAdapter;
import com.paulz.carinsurance.adapter.TeamInsureDetailFeeAdapter;
import com.paulz.carinsurance.adapter.TeamInsureFeeAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.wrapper.BeanWraper;
import com.paulz.carinsurance.model.wrapper.InsureFeeDetailWraper;
import com.paulz.carinsurance.model.wrapper.InsureFeeWraper;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.view.pulltorefresh.PullListView;
import com.paulz.carinsurance.view.pulltorefresh.PullToRefreshBase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by pualbeben on 17/5/21.
 * 总保费和出单量进来的页面
 */
@RuntimePermissions
public class TeamAchievementDetailActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener, PullToRefreshBase.OnRefreshListener {


    TeamInsureDetailFeeAdapter mAdapter;
    @BindView(R.id.btn_start_date)
    TextView btnStartDate;
    @BindView(R.id.btn_end_date)
    TextView btnEndDate;
    @BindView(R.id.layout_date)
    LinearLayout layoutDate;


    @BindView(R.id.tv_total_fee)
    TextView tvTotalFee;
    @BindView(R.id.tv_business_fee)
    TextView tvBusinessFee;
    @BindView(R.id.tv_force_fee)
    TextView tvForceFee;


    String start;
    String end;
    String tempStart;
    String tempEnd;

    String teamId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_team_achievement, false, true);
        teamId=getIntent().getStringExtra("team_id");
        mLoadStateController = new LoadStateController(this, (ViewGroup) findViewById(R.id.load_state_container));
        hasLoadingState = true;
        if(AppUtil.isNull(teamId)){
            setTitleText("", AppStatic.getInstance().getmUserInfo().teamtype==2?"机构保费":"团队保费",0, true);
        }else {
            setTitleText("", "团队出单明细",0, true);
        }
//        setTitleText("", AppStatic.getInstance().getmUserInfo().teamtype==2?"机构保费":"团队保费",0, true);
        ButterKnife.bind(this);
        initView();
        setListener();
        initData(false);

    }


    private void initView() {
        mPullListView=(PullListView) findViewById(R.id.listview);
        mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
        mListView = mPullListView.getRefreshableView();
        mListView.setDividerHeight(2);
        mAdapter = new TeamInsureDetailFeeAdapter(this);
        initHeader();
        mListView.setAdapter(mAdapter);

    }

    TextView tvTotal;
    private void initHeader(){
        View v = LayoutInflater.from(this).inflate(R.layout.layout_footer_total_count,null);
        tvTotal=(TextView)v.findViewById(R.id.tv_total);
        mListView.addFooterView(v);
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
        mAdapter.setICallPhone(new CustomerAdapter.ICallPhone() {
            @Override
            public void onCall(String phone) {
                TeamAchievementDetailActivityPermissionsDispatcher.showCallPhoneWithCheck(TeamAchievementDetailActivity.this,phone);
            }
        });
    }


    DatePickView datePickView1;

    private void showDataPicker1() {
        if (datePickView1 == null) {
            datePickView1 = new DatePickView(this);
            datePickView1.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    tempStart = date;
                    btnStartDate.setText(date);
                    start = tempStart;
                    onRefresh();
                }
            });
        }
        datePickView1.show();
    }

    DatePickView datePickView2;

    private void showDataPicker2() {
        if (datePickView2 == null) {
            datePickView2 = new DatePickView(this);
            datePickView2.setDatePickListener(new DatePickView.DatePickListener() {
                @Override
                public void onSelected(String date) {
                    tempEnd = date;
                    btnEndDate.setText(date);
                    end = tempEnd;
                    onRefresh();
                }
            });
        }
        datePickView2.show();
    }


    @Override
    protected BeanWraper newBeanWraper() {
        return new InsureFeeDetailWraper();
    }

    private void initData(boolean isRefresh) {
        if (!isRefresh) {
            showLoading();
        }

        ParamBuilder params = new ParamBuilder();
        params.append("starttime", start);
        params.append("endtime", end);

        if (isRefresh) {
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_ORDER_DETAIL_FEE_LIST), InsureFeeDetailWraper.class);
        } else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_ORDER_DETAIL_FEE_LIST), InsureFeeDetailWraper.class);
        }
    }

    private void setHeader() {
        InsureFeeDetailWraper wraper = (InsureFeeDetailWraper) getBeanWraper();
        tvTotalFee.setText(wraper.ains+"元");
        tvForceFee.setText(wraper.cins+"元");
        tvBusinessFee.setText(wraper.bins+"元");
        tvTotal.setText("共有"+wraper.total+"单");
    }

    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        if (AppUtil.isEmpty(allData)) {
            showNodata();
//            showSuccess();
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


    public static void invoke(Context context, String id) {
        Intent intent = new Intent(context, TeamAchievementDetailActivity.class);
        if (!AppUtil.isNull(id)) {
            intent.putExtra("team_id", id);
        }
        context.startActivity(intent);

    }


    @OnClick({R.id.btn_start_date, R.id.btn_end_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_date:
                showDataPicker1();
                break;
            case R.id.btn_end_date:
                showDataPicker2();
                break;
            /*case R.id.btn_search:
                start = tempStart;
                end = tempEnd;
                onRefresh();
                break;*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TeamAchievementDetailActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCallPhone(String phone){
        AppUtil.callTo(this,phone);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCallPhone(final PermissionRequest request) {
        request.proceed();

    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCallPhone() {

    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCallPhone() {
        Toast.makeText(this, "您已经禁用打电话功能，请到系统设置开启权限",Toast.LENGTH_SHORT).show();
    }
}
