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
import com.paulz.carinsurance.adapter.TeamAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.controller.LoadStateController;
import com.paulz.carinsurance.httputil.ParamBuilder;
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
 */

public class TeamListActivity extends BaseListActivity implements PullToRefreshBase.OnRefreshListener, LoadStateController.OnLoadErrorListener {

    @BindView(R.id.search_bar)
    EditText searchBar;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.tv_filter)
    TextView tvFilter;

    private TeamAdapter mAdapter;

    public final static int REQUEST_CODE = 123;
    private PopupWindow pop;

    String keyword = "";


    String[] orders = {"由近到远", "月保费", "总保费"};
    String[] filters = {"不限", "已出单", "未出单"};

    int curOrder = 0;
    int curFilter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_team_list, false, true);
        setTitleText("", "团队列表", 0, true);
        ButterKnife.bind(this);
        initView();
        setListener();
        initData(false);
    }


    private void initView() {
        mPullListView = (PullListView) findViewById(R.id.listview);
        mListView = mPullListView.getRefreshableView();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.main_bg)));
        mListView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.all_margin));
        mAdapter = new TeamAdapter(this);
        mLoadStateController=new LoadStateController(this, (ViewGroup) findViewById(R.id.load_state_container));
        hasLoadingState=true;
        initHeader();
        mListView.setAdapter(mAdapter);
        searchBar.setHint("请输入团队名称、负责人名称、手机号");
    }

    TextView tvTotal;
    private void initHeader(){
        View v =LayoutInflater.from(this).inflate(R.layout.layout_footer_total_count,null);
        tvTotal=(TextView)v.findViewById(R.id.tv_total);
        mListView.addFooterView(v);
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
                    AppUtil.hideSoftInputMethod(TeamListActivity.this, searchBar);
                    return true;
                }
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeamInfoActivity.invoke(TeamListActivity.this,mAdapter.getList().get(position-mListView.getHeaderViewsCount()).store_id);

            }
        });
    }

    @OnClick({R.id.btn_search, R.id.tv_sort, R.id.tv_filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                keyword = searchBar.getText().toString().trim();
                onRefresh();
                AppUtil.hideSoftInputMethod(TeamListActivity.this, searchBar);
                break;
            case R.id.tv_sort:
                showCateDialog(tvSort,orders);
                break;
            case R.id.tv_filter:
                showCateDialog(tvFilter,filters);
                break;
        }
    }


    private void showCateDialog(View tag, final String[] ops) {
        List<String> list = new ArrayList<>();
        for (String op : ops) {
            list.add(op);
        }
        this.pop = initSimpalPopWindow(this, list, new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pop.dismiss();
                        if (ops == orders) {
                            curOrder = position;
                            tvSort.setText(orders[curOrder]);
                        } else if (ops == filters) {
                            curFilter = position;
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
        return new TeamWraper();
    }

    private void initData(boolean isRefresh) {
        if (!isRefresh) {
            showLoading();
        }

        ParamBuilder params = new ParamBuilder();
        params.append("search", keyword);
        params.append("px", curOrder + "");
        params.append("sx", curFilter + "");
        if (isRefresh) {
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_TEAM_LIST), TeamWraper.class);
        } else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_TEAM_LIST), TeamWraper.class);
        }
    }

    public void handleHeader(){
        TeamWraper wraper=(TeamWraper) getBeanWraper();
        tvTotal.setText("共有"+wraper.total+"项");
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
        Intent intent = new Intent(context, TeamListActivity.class);
        context.startActivity(intent);
    }



}
