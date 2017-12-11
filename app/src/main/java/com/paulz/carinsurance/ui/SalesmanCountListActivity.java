package com.paulz.carinsurance.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.CustomerAdapter;
import com.paulz.carinsurance.adapter.SalesmanCountAdapter;
import com.paulz.carinsurance.adapter.TeamInsureFeeAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.wrapper.BeanWraper;
import com.paulz.carinsurance.model.wrapper.InsureFeeWraper;
import com.paulz.carinsurance.model.wrapper.SalesmanCountWraper;
import com.paulz.carinsurance.model.wrapper.TeamWraper;
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
 */
@RuntimePermissions
public class SalesmanCountListActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener, PullToRefreshBase.OnRefreshListener {


    SalesmanCountAdapter mAdapter;
    @BindView(R.id.btn_start_date)
    TextView btnStartDate;
    @BindView(R.id.btn_end_date)
    TextView btnEndDate;
    @BindView(R.id.layout_date)
    LinearLayout layoutDate;

    @BindView(R.id.search_bar)
    EditText searchBar;

    String keyword = "";


    String start;
    String end;
    String tempStart;
    String tempEnd;

    String id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_salesman_count_list, false, true);
        id=getIntent().getStringExtra("mid");
        mLoadStateController = new LoadStateController(this, (ViewGroup) findViewById(R.id.load_state_container));
        hasLoadingState = true;
        setTitleText("", "业务员数", 0, true);
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
        mAdapter = new SalesmanCountAdapter(this);
        initHeader();
        mListView.setAdapter(mAdapter);

        searchBar.setHint(AppStatic.getInstance().getmUserInfo().teamtype==2?"请输入团队名称、名字、手机号、邀请人":"请输入名字、手机号、邀请人");

    }


    TextView tvTotal;
    private void initHeader(){
        View v = LayoutInflater.from(this).inflate(R.layout.layout_footer_total_count,null);
        tvTotal=(TextView)v.findViewById(R.id.tv_total);
        mListView.addFooterView(v);
    }

    public void handleHeader(){
        SalesmanCountWraper wraper=(SalesmanCountWraper) getBeanWraper();
        tvTotal.setText("共有"+wraper.total+"人");
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

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    keyword = searchBar.getText().toString().trim();
                    onRefresh();
                    AppUtil.hideSoftInputMethod(SalesmanCountListActivity.this, searchBar);
                    return true;
                }
                return false;
            }
        });
        mAdapter.setICallPhone(new CustomerAdapter.ICallPhone() {
            @Override
            public void onCall(String phone) {
                SalesmanCountListActivityPermissionsDispatcher.showCallPhoneWithCheck(SalesmanCountListActivity.this,phone);
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
        return new SalesmanCountWraper();
    }

    private void initData(boolean isRefresh) {
        if (!isRefresh) {
            showLoading();
        }

        ParamBuilder params = new ParamBuilder();
        params.append("starttime", start);
        params.append("endtime", end);
        params.append("search", keyword);
        params.append("mid", AppUtil.isNull(id)?"":id);


        if (isRefresh) {
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SALESMAN_COUNT_LIST), SalesmanCountWraper.class);
        } else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SALESMAN_COUNT_LIST), SalesmanCountWraper.class);
        }
    }



    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        handleHeader();
        if (AppUtil.isEmpty(allData)) {
            showNodata();
//            showSuccess();
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


    public static void invoke(Context context, String mid) {
        Intent intent = new Intent(context, SalesmanCountListActivity.class);
        if (!AppUtil.isNull(mid)) {
            intent.putExtra("mid", mid);
        }
        context.startActivity(intent);

    }


    @OnClick({R.id.btn_start_date, R.id.btn_end_date,R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_date:
                showDataPicker1();
                break;
            case R.id.btn_end_date:
                showDataPicker2();
                break;
            case R.id.btn_search:
                keyword = searchBar.getText().toString().trim();
                onRefresh();
                AppUtil.hideSoftInputMethod(SalesmanCountListActivity.this, searchBar);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SalesmanCountListActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
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
