package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AppointAdapter;
import com.paulz.carinsurance.adapter.TeamAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.wrapper.AppointWraper;
import com.paulz.carinsurance.model.wrapper.BeanWraper;
import com.paulz.carinsurance.model.wrapper.TeamWraper;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.view.pulltorefresh.PullListView;
import com.paulz.carinsurance.view.pulltorefresh.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/22.
 * 特约
 */

public class AppointListActivity extends BaseListActivity implements PullToRefreshBase.OnRefreshListener, LoadStateController.OnLoadErrorListener {

    @BindView(R.id.search_bar)
    EditText searchBar;

    TextView tvMiddle;


    private AppointAdapter mAdapter;

    public final static int REQUEST_CODE = 123;
    private PopupWindow pop;

    String keyword = "";


    AppointWraper.AppointCompany selectedCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_appoint_list, false, true);
        setTitleText("", "中国人保特别约定", 0, true);
        tvMiddle=(TextView) findViewById(R.id.baseTitle_milddleTv);
        ButterKnife.bind(this);
        initView();
        setListener();
        initData(false);
    }


    private void initView() {
        mPullListView = (PullListView) findViewById(R.id.listview);
        mListView = mPullListView.getRefreshableView();
        mAdapter = new AppointAdapter(this);
        mLoadStateController=new LoadStateController(this, (ViewGroup) findViewById(R.id.load_state_container));
        hasLoadingState=true;
        mListView.setAdapter(mAdapter);
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
                    AppUtil.hideSoftInputMethod(AppointListActivity.this, searchBar);
                    return true;
                }
                return false;
            }
        });

    }

    @OnClick({R.id.btn_search,R.id.baseTitle_milddleTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                keyword = searchBar.getText().toString().trim();
                onRefresh();
                AppUtil.hideSoftInputMethod(AppointListActivity.this, searchBar);
                break;

            case R.id.baseTitle_milddleTv:
                showCateDialog(tvMiddle);
                break;

        }
    }


    private void showCateDialog(View tag ) {
        List<String> list = new ArrayList<>();
        for (AppointWraper.AppointCompany c : getCompanys()) {
            list.add(c.title);
        }
        this.pop = initSimpalPopWindow(this, list, new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pop.dismiss();
                        selectedCompany=getCompanys().get(position);

                        tvMiddle.setText(selectedCompany.title+"特别约定");

                        onRefresh();

                    }
                }
                , tag);
    }

    public PopupWindow initSimpalPopWindow(Activity paramActivity, List<String> paramList, AdapterView.OnItemClickListener paramOnItemClickListener, View paramView) {
        Object localObject = null;
        if (paramActivity != null) {
            localObject = (ListView) LayoutInflater.from(this).inflate(R.layout.layout_popwindow_listview1, null);
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
    protected BeanWraper newBeanWraper() {
        return new AppointWraper();
    }

    private void initData(boolean isRefresh) {
        if (!isRefresh) {
            showLoading();
        }

        ParamBuilder params = new ParamBuilder();
        params.append("keywords", keyword);
        if(selectedCompany!=null){
            params.append("company", selectedCompany.id);
        }
        if (isRefresh) {
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_APPOINT_LIST), AppointWraper.class);
        } else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_APPOINT_LIST), AppointWraper.class);
        }
    }

    public void handleHeader(){
        AppointWraper wraper=(AppointWraper)getBeanWraper();
        if(!AppUtil.isEmpty(wraper.company)){
        }
    }


    private List<AppointWraper.AppointCompany> getCompanys(){

        AppointWraper wraper=(AppointWraper)getBeanWraper();

        return wraper.company;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==AppointEditlActivity.REQUEST_CODE){
                setResult(RESULT_OK,data);
                finish();
            }
        }
    }

    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        handleHeader();
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


    public static void invoke(Activity context) {
        Intent intent = new Intent(context, AppointListActivity.class);
        context.startActivityForResult(intent,AppointEditlActivity.REQUEST_CODE);
    }



}
