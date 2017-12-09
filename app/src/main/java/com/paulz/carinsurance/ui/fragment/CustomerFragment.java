package com.paulz.carinsurance.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.CustomerAdapter;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.wrapper.BeanWraper;
import com.paulz.carinsurance.model.wrapper.CustomerWraper;
import com.paulz.carinsurance.ui.AddCustomerActivity;
import com.paulz.carinsurance.ui.MsgCenterActivity;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.ImageUtil;
import com.paulz.carinsurance.view.pulltorefresh.PullListView;
import com.paulz.carinsurance.view.pulltorefresh.PullToRefreshBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
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
public class CustomerFragment extends BaseListFragment implements PullToRefreshBase.OnRefreshListener,LoadStateController.OnLoadErrorListener{

    @BindView(R.id.search_bar)
    EditText searchBar;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    Unbinder unbinder;
    CustomerAdapter mAdapter;

    String keyword="";

    private PopupWindow pop;

    String[] orders={"车险到期时间","报价时间","添加时间"};
    String[] filters={"全部客户","未投保客户","已投保客户"};

    int curOrder=0;
    int curFilter=0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_customer, false);
        mLoadStateController=new LoadStateController(getActivity(), (ViewGroup) baseLayout.findViewById(R.id.load_state_container));
        hasLoadingState=true;
        unbinder = ButterKnife.bind(this, baseLayout);
        return baseLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData(false);
    }

    private void initView() {
        mPullListView = (PullListView) baseLayout.findViewById(R.id.listview);
        mListView = mPullListView.getRefreshableView();
        mAdapter = new CustomerAdapter(getActivity());
        mAdapter.setICallPhone(new CustomerAdapter.ICallPhone() {
            @Override
            public void onCall(String phone) {
                CustomerFragmentPermissionsDispatcher.showCallPhoneWithCheck(CustomerFragment.this,phone);

            }
        });
        mListView.setAdapter(mAdapter);
    }


    private void setListener(){
        mListView.setOnScrollListener(new MyOnScrollListener());
        mLoadStateController.setOnLoadErrorListener(this);
        mPullListView.setOnRefreshListener(this);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    keyword=searchBar.getText().toString().trim();
                    onRefresh();
                    AppUtil.hideSoftInputMethod(getActivity(),searchBar);
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.btn_add_customer,R.id.tv_sort,R.id.tv_filter,R.id.btn_search,R.id.tv_msg_count,R.id.btn_msg})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_add_customer:
                AddCustomerActivity.invoke(getActivity());
                break;
            case R.id.tv_sort:
                showCateDialog(tvSort,orders);
                break;
            case R.id.tv_filter:
                showCateDialog(tvFilter,filters);
                break;
            case R.id.btn_search:
                keyword=searchBar.getText().toString().trim();
                onRefresh();
                AppUtil.hideSoftInputMethod(getActivity(),searchBar);
                break;
            case R.id.tv_msg_count:
            case R.id.btn_msg:
                MsgCenterActivity.invoke(getActivity(),0);
                break;

            default:
                break;
        }

    }



    @Override
    public void heavyBuz() {

    }

    @Override
    protected BeanWraper newBeanWraper() {
        return new CustomerWraper();
    }

    private void initData(boolean isRefresh){
        if(!isRefresh){
            showLoading();
        }

        ParamBuilder params=new ParamBuilder();
        params.append("search",keyword);
        params.append("order",curOrder+1+"");
        params.append("filt",curFilter+"");
        if(isRefresh){
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CUSTOMER_LIST), CustomerWraper.class);
        }else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CUSTOMER_LIST), CustomerWraper.class);
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


    private void showCateDialog(View tag, final String[] ops) {
        List<String > list=new ArrayList<>();
        for(String op:ops){
            list.add(op);
        }
        this.pop = initSimpalPopWindow(getActivity(), list, new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pop.dismiss();
                        if(ops==orders){
                            curOrder=position;
                            tvSort.setText(orders[curOrder]);
                        }else if(ops==filters){
                            curFilter=position;
                            tvFilter.setText(filters[curFilter]);
                        }

                        onRefresh();

                    }
                }
                , tag);
    }

    public PopupWindow initSimpalPopWindow(Activity paramActivity, List<String> paramList, AdapterView.OnItemClickListener paramOnItemClickListener, View paramView) {
        Object localObject = null;
        if (paramActivity != null) {
            localObject = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_popwindow_listview1, null);
            ((ListView) localObject).setAdapter(new ArrayAdapter<String>(paramActivity, R.layout.layout_popwindow_listview_item2, R.id.popwindow_tv, paramList));
            ((ListView) localObject).setOnItemClickListener(paramOnItemClickListener);
            localObject = new PopupWindow((View) localObject, getResources().getDimensionPixelSize(R.dimen.ad_btn_height), WindowManager.LayoutParams.WRAP_CONTENT);
            ((PopupWindow) localObject).setFocusable(true);
            ((PopupWindow) localObject).setTouchable(true);
            ((PopupWindow) localObject).setBackgroundDrawable(new BitmapDrawable());
            ((PopupWindow) localObject).setOutsideTouchable(true);
            ((PopupWindow) localObject).showAsDropDown(paramView, -20, 0);
        }
        return (PopupWindow) localObject;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CustomerFragmentPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }


    @NeedsPermission(Manifest.permission.CAMERA)
    void showCallPhone(String phone){
        AppUtil.callTo(getActivity(),phone);
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
        Toast.makeText(getActivity(), "您已经禁用打电话功能，请到系统设置开启权限",Toast.LENGTH_SHORT).show();
    }

}
