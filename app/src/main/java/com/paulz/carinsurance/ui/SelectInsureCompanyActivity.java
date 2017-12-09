package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.InsureCompanyAdapter;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Company;
import com.paulz.carinsurance.model.InsureCate;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/5/28.
 * 保险公司选择
 */

public class SelectInsureCompanyActivity extends BaseActivity implements InsureCompanyAdapter.OnSelectChangeListener{

    @BindView(R.id.listview)
    ListView listview;
    InsureCompanyAdapter mAdapter;
    TextView state;
    TextView btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_select_insure_company, false, true);
        setTitleText("", "选择保险公司", 0, true);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent!=null){
            state.setSelected(false);
            loadData();
        }
    }

    private void initView() {
        View v = View.inflate(this, R.layout.layout_company_footer, null);
        state=(TextView) v.findViewById(R.id.state);
        btnNext=(TextView) v.findViewById(R.id.btn_next);
        v.findViewById(R.id.layout_state).setOnClickListener(this);
        btnNext.setOnClickListener(this);
        listview.addFooterView(v);
        mAdapter=new InsureCompanyAdapter(this);
        listview.setAdapter(mAdapter);
    }


    private void loadData(){

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_COMPANY_LIST), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    BaseObject<PageInfo> object= GsonParser.getInstance().parseToObj(result,PageInfo.class);
                    if(object!=null&&object.status==BaseObject.STATUS_OK&&object.data!=null){
                        mAdapter.setList(object.data.list);
                        mAdapter.notifyDataSetChanged();
                    }else {
                        AppUtil.showToast(getApplicationContext(),"加载失败");
                    }
                }
            }
        });

    }

    private void selectedAll(boolean isSelected){
        if(mAdapter.getList()==null)return;
        for(Company c:mAdapter.getList()){
            c.isSelected=isSelected;
        }
        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.layout_state:
                boolean isSelected=!state.isSelected();
                state.setSelected(isSelected);
                selectedAll(isSelected);
                break;
            case R.id.btn_next:
                submit();
                break;
            default:
                super.onClick(arg0);
                break;
        }
    }
    public void submit(){
        if(mAdapter.getList()==null){
            AppUtil.showToast(getApplicationContext(),"请选择保险公司");
            return;
        }

        ArrayList companys=new ArrayList();
        JSONArray array=new JSONArray();
        for(Company c:mAdapter.getList()){
            if(c.isSelected){
                array.put(Integer.valueOf(c.insurance_company_id));
                companys.add(c);
            }
        }

        if(array.length()==0){
            AppUtil.showToast(getApplicationContext(),"请选择保险公司");
            return;
        }


        InsureCompanyPriceActivity.invoke(SelectInsureCompanyActivity.this,companys);


        /*DialogUtil.showDialog(lodDialog);
        ParamBuilder params=new ParamBuilder();
        HttpRequester requester=new HttpRequester();

        requester.getParams().put("company",array);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_CHOICECOMPANY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    BaseObject<Object> object= GsonParser.getInstance().parseToObj(result,Object.class);
                    if(object!=null&&object.status==BaseObject.STATUS_OK){
                        InsureCompanyPriceActivity.invoke(SelectInsureCompanyActivity.this);

                    }else {
                        AppUtil.showToast(getApplicationContext(),"加载失败");
                    }
                }
            }
        },requester, DESUtil.SECRET_DES);*/
    }


    public static void invoke(Activity context){
        Intent intent=new Intent(context,SelectInsureCompanyActivity.class);
        context.startActivity(intent);

    }

    @Override
    public void onSelect(int position) {
        if(mAdapter.getList()==null)return;
        for(Company c:mAdapter.getList()){
            if(!c.isSelected){
                state.setSelected(false);
                return;
            }
        }
        state.setSelected(true);
    }


    private class PageInfo{
        List<Company> list;
    }


}
