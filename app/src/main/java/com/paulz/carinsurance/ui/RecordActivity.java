package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.RecordAdapter;
import com.paulz.carinsurance.base.BaseListActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.CarInfo;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.view.pulltorefresh.PullListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/5/21.
 */

public class RecordActivity extends BaseListActivity {

    private RecordAdapter mAdapter;

    @BindView(R.id.tv_label)
    TextView tvLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_record,true,true);
        setTitleText("","最近查询车辆",0,true);
        ButterKnife.bind(this);
        initView();
        loadData();
    }


    private void initView() {
        mPullListView = (PullListView) findViewById(R.id.listview);
        mPullListView.setMode(-1);
        mListView = mPullListView.getRefreshableView();
        mListView.setDividerHeight(2);
        mAdapter=new RecordAdapter(this);
        mListView.setAdapter(mAdapter);

    }


    private void loadData() {
        ParamBuilder params=new ParamBuilder();

        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CAR_HISTORY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<PageData> object= GsonParser.getInstance().parseToObj(result,PageData.class);
                    if(object!=null&&object.status==BaseObject.STATUS_OK&&object.data!=null){
                        showSuccess();
                        mAdapter.setList(object.data.list);
                        mAdapter.notifyDataSetChanged();
                    }else {
                        showNodata();
                    }
                }
            }
        });

    }

    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {

    }

    @Override
    protected void loadError(String message, Throwable throwable, int page) {

    }

    @Override
    protected void loadTimeOut(String message, Throwable throwable) {

    }

    @Override
    protected void loadNoNet() {

    }

    @Override
    protected void loadServerError() {

    }


    public static void invoke(Context context){
        Intent intent=new Intent(context,RecordActivity.class);
        context.startActivity(intent);
    }

    private class PageData{
        List<CarInfo> list;
    }
}
