package com.paulz.carinsurance.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.CustomerAdapter;
import com.paulz.carinsurance.adapter.SalesmanDetailAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.wrapper.BeanWraper;
import com.paulz.carinsurance.model.wrapper.InsureFeeDetailWraper;
import com.paulz.carinsurance.model.wrapper.SalesmanDetailWraper;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.view.CircleImageView;
import com.paulz.carinsurance.view.CommonDialog;
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
public class SalesmanDetailActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener, PullToRefreshBase.OnRefreshListener {


    SalesmanDetailAdapter mAdapter;



    String id;
    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_team)
    TextView tvTeam;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_invited_name)
    TextView tvInvitedName;
    @BindView(R.id.tv_invite_count)
    TextView tvInviteCount;
    @BindView(R.id.tv_status)
    TextView tvStatus;

    SalesmanDetailWraper.Rate rate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        setActiviyContextView(R.layout.activity_salesman_detail, false, true);
        mLoadStateController = new LoadStateController(this, (ViewGroup) findViewById(R.id.load_state_container));
        hasLoadingState = true;
        ButterKnife.bind(this);
        setTitleTextRightText("", "业务员详情", "查看费用比例", true);
        initView();
        setListener();
        initData(false);

    }


    private void initView() {
        mPullListView = (PullListView) findViewById(R.id.listview);
        mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
        mListView = mPullListView.getRefreshableView();
        mListView.setDividerHeight(2);
        mAdapter = new SalesmanDetailAdapter(this);
        mListView.setAdapter(mAdapter);

    }



    private void setListener() {
        mListView.setOnScrollListener(new MyOnScrollListener());
        mLoadStateController.setOnLoadErrorListener(this);
        mPullListView.setOnRefreshListener(this);
        mAdapter.setICallPhone(new CustomerAdapter.ICallPhone() {
            @Override
            public void onCall(String phone) {
                SalesmanDetailActivityPermissionsDispatcher.showCallPhoneWithCheck(SalesmanDetailActivity.this, phone);
            }
        });
    }

    @OnClick({R.id.tv_invited_name, R.id.tv_invite_count})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_invited_name:
                break;
            case R.id.tv_invite_count:

                break;
        }
    }

    @Override
    public void onRightClick() {
        if(rate==null)return;
        final CommonDialog dialog = new CommonDialog(this);
        StringBuilder sb = new StringBuilder();
        sb.append(rate.crate);
        for (String b : rate.brate) {
            sb.append("\n").append(b);
        }
        dialog.setDesc(sb.toString());
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected BeanWraper newBeanWraper() {
        return new SalesmanDetailWraper();
    }

    private void initData(boolean isRefresh) {
        if (!isRefresh) {
            showLoading();
        }

        ParamBuilder params = new ParamBuilder();
        params.append("id", id);

        if (isRefresh) {
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SALESMAN_DETAILS), SalesmanDetailWraper.class);
        } else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SALESMAN_DETAILS), SalesmanDetailWraper.class);
        }
    }

    private void setHeader() {
        SalesmanDetailWraper wraper = (SalesmanDetailWraper) getBeanWraper();

        tvName.setText(AppUtil.isNull(wraper.name)?wraper.member_username:wraper.name);
        tvTeam.setText(wraper.store_name);
        tvInvitedName.setText(wraper.recomname);
        tvInviteCount.setText("已邀请人数："+wraper.recomtotal+"人");
        tvStatus.setText(wraper.renzheng);
        tvDate.setText(wraper.createtime);
        rate=wraper.rate;

        Glide.with(this).load(AppUrls.getInstance().BASE_IMG_URL+wraper.member_avatar).into(ivAvatar);
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
        Intent intent = new Intent(context, SalesmanDetailActivity.class);
        if (!AppUtil.isNull(id)) {
            intent.putExtra("id", id);
        }
        context.startActivity(intent);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SalesmanDetailActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCallPhone(String phone) {
        AppUtil.callTo(this, phone);
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
        Toast.makeText(this, "您已经禁用打电话功能，请到系统设置开启权限", Toast.LENGTH_SHORT).show();
    }


}
