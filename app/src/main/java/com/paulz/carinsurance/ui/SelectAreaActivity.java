package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.core.framework.net.NetworkWorker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AreaAdapter;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Area;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/6/15.
 * 只选择区县
 */

public class SelectAreaActivity extends BaseActivity {

    public final static int REQUEST_CODE = 10021;
    @BindView(R.id.listView)
    ListView listView;
    AreaAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_select_area, false, true);
        setTitleText("", "选择区县",0, true);
        ButterKnife.bind(this);
        initView();
        load();
    }

    private void initView() {
        mAdapter=new AreaAdapter(this);
        listView.setAdapter(mAdapter);
    }


    private int findAreaPosition(List<Area> list){
        String curId=getIntent().getStringExtra("extra_curAreaId");
        if(TextUtils.isEmpty(curId))return -1;
        for(int i=0;i<list.size();i++){
            if(list.get(i).id.equals(curId))return i;
        }

        return -1;
    }


    private void load() {

        ParamBuilder params = new ParamBuilder();
        params.append("id", getIntent().getStringExtra("extra_id"));
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_GET_AREA), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (status == 200) {
                    List<Area> cities = new Gson().fromJson(result, new TypeToken<ArrayList<Area>>() {}.getType());
                    mAdapter.setList(cities);
                    int position=findAreaPosition(cities);
                    mAdapter.setSelected(position);
                    mAdapter.notifyDataSetChanged();
                    listView.setSelection(position);
                }
            }
        });
    }


    public static void invoke(Activity context, String id,String curAreaId) {
        context.startActivityForResult(new Intent(context, SelectAreaActivity.class).putExtra("extra_id", id).putExtra("extra_curAreaId", curAreaId==null?"":curAreaId), REQUEST_CODE);
    }

}
