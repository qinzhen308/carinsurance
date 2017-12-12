package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.CarModelAdapter;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.CarModeInfo;
import com.paulz.carinsurance.model.wrapper.BeanWraper;
import com.paulz.carinsurance.model.wrapper.CarModeInfoWraper;
import com.paulz.carinsurance.model.wrapper.SalesmanWraper;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.view.pulltorefresh.PullListView;
import com.paulz.carinsurance.view.pulltorefresh.PullToRefreshBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 * 查找车型
 */

public class SearchCarModelActivity extends BaseListActivity implements PullToRefreshBase.OnRefreshListener, LoadStateController.OnLoadErrorListener{


    CarModelAdapter mAdapter;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.search_bar)
    EditText searchBar;
    @BindView(R.id.btn_help)
    TextView btnHelp;

    @BindView(R.id.tab1)
    TextView tab1;
    @BindView(R.id.tab2)
    TextView tab2;
    @BindView(R.id.tab3)
    TextView tab3;
    @BindView(R.id.layout_tab)
    LinearLayout layoutTab;

    public final static int REQUEST_CODE=125;


    String keywords;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_search_car_model, false, true);
        mLoadStateController=new LoadStateController(this, (ViewGroup) findViewById(R.id.load_state_container));
        hasLoadingState=true;
        setTitleText("", "选择车型", 0, true);
        ButterKnife.bind(this);
        init();
        setListener();
    }


    private void init() {
        keywords=getIntent().getStringExtra("extra_keywords");
        searchBar.setText(keywords==null?"":keywords);
        mAdapter = new CarModelAdapter(this);
        mPullListView=(PullListView)findViewById(R.id.list_view);
        mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
        mListView=mPullListView.getRefreshableView();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.main_bg)));
        mListView.setDividerHeight(2);
        mListView.setAdapter(mAdapter);
        if(!AppUtil.isNull(keywords)){
            searchCar(false);
        }
        if(getIntent().getBooleanExtra("just_show",false)){
            justShowSearchCar(false);
        }

    }

    private void setListener() {
        mListView.setOnScrollListener(new MyOnScrollListener());
        mLoadStateController.setOnLoadErrorListener(this);
        mPullListView.setOnRefreshListener(this);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    search();
                    AppUtil.hideSoftInputMethod(SearchCarModelActivity.this,searchBar);
                }
                return false;
            }
        });

    }


    private void search() {
        keywords = searchBar.getText().toString();
        searchCar(false);
    }


    private void searchCar(boolean isRefresh) {
        if (AppUtil.isNull(keywords)) {
            AppUtil.showToast(this, "请输入车型");
            return;
        }
        if (!isRefresh) {
            showLoading();
        }

        ParamBuilder params = new ParamBuilder();
        HttpRequester requester = new HttpRequester();
        requester.setMethod(com.core.framework.net.HttpRequester.METHOD_POST);
        requester.setSecretMode(DESUtil.SECRET_DES);
        requester.getParams().put("keywords", keywords);
        String url=null;
        if(getIntent().getBooleanExtra("is_customer",false)){
            url=AppUrls.getInstance().URL_CUSTOMER_BRAND_CHOICE_CAR;
        }else {
            url=AppUrls.getInstance().URL_BRAND_CHOICE_CAR;
        }
        setHttpRequester(requester);
        if (isRefresh) {
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), url), CarModeInfoWraper.class);
        } else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), url), CarModeInfoWraper.class);
        }
    }

    private void justShowSearchCar(boolean isRefresh) {

        btnHelp.setVisibility(View.GONE);
        searchBar.setVisibility(View.GONE);
        if (!isRefresh) {
            showLoading();
        }

        ParamBuilder params = new ParamBuilder();
//        HttpRequester requester = new HttpRequester();
        String url=null;
        if(getIntent().getBooleanExtra("is_customer",false)){
            url=AppUrls.getInstance().URL_CUSTOMER_SEARCH_CAR_MODEL;
        }else {
            url=AppUrls.getInstance().URL_SEARCH_CAR_MODEL;
        }

        if (isRefresh) {
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), url), CarModeInfoWraper.class);
        } else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), url), CarModeInfoWraper.class);
        }
    }


    @Override
    protected BeanWraper newBeanWraper() {
        return new CarModeInfoWraper();
    }

    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        if (AppUtil.isEmpty(allData)) {
            showNodata();
        } else {
            showSuccess();
            if(!AppUtil.isNull(keywords)){
                SelectCarModelActivity.clearCarMode=true;
                setResult(100);
            }
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
            if(!AppUtil.isNull(keywords)){
                searchCar(true);
            }
            if(getIntent().getBooleanExtra("just_show",false)){
                justShowSearchCar(true);
            }
        }
    }

    @Override
    public void onAgainRefresh() {
        // TODO Auto-generated method stub

        if(!AppUtil.isNull(keywords)){
            searchCar(false);
        }
        if(getIntent().getBooleanExtra("just_show",false)){
            justShowSearchCar(false);
        }
    }

    private void showVinExample(){
        final Dialog dialog=new Dialog(this,R.style.CommonDialog);
        Window dialogWindow = getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_trans_image);
        ((ImageView)dialog.findViewById(R.id.iv_img)).setImageResource(R.drawable.img_brand_example);
        dialog.findViewById(R.id.common_dialog_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();
    }


    public static void invoke(Activity context,boolean justShow,boolean fromCustomer) {
        Intent intent = new Intent(context, SearchCarModelActivity.class);
        intent.putExtra("just_show",justShow);
        intent.putExtra("is_customer", fromCustomer);
        context.startActivityForResult(intent,REQUEST_CODE);
    }


    @OnClick({R.id.search_bar_btn, R.id.btn_help,R.id.tab1, R.id.tab2, R.id.tab3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_bar_btn:
                search();
                AppUtil.hideSoftInputMethod(SearchCarModelActivity.this,searchBar);
                break;
            case R.id.btn_help:
                showVinExample();
                break;
            case R.id.tab1:
                break;
            case R.id.tab2:
                break;
            case R.id.tab3:
                break;
        }
    }


    private class PageData {
        List<CarModeInfo> list;
    }



}
