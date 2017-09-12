package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.InsureCompanyAdapter;
import com.paulz.carinsurance.adapter.InsureCompanyPriceAdapter;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Company;
import com.paulz.carinsurance.model.CompanyResult;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.ui.fragment.AccountFragment;
import com.paulz.carinsurance.utils.AppUtil;

import org.json.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/5/28.
 * 保险公司报价
 */

public class InsureCompanyPriceActivity extends BaseActivity {

    @BindView(R.id.listview)
    ListView listview;
    InsureCompanyPriceAdapter mAdapter;

    public static boolean isShow=true;
    private ImageView ivRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_select_insure_company, false, true);
        setTitleText("", "报价结果", R.drawable.hideeye_icon_topbanner, true);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    private void initView() {
        mAdapter=new InsureCompanyPriceAdapter(this);
        listview.setAdapter(mAdapter);
        ivRight=(ImageView)findViewById(R.id.baseTitle_rightIv);
        ivRight.setImageResource(isShow?R.drawable.showeye_icon_topbanner:R.drawable.hideeye_icon_topbanner);
    }

    @Override
    public void onRightClick() {
        isShow=!isShow;
        mAdapter.notifyDataSetChanged();
        ivRight.setImageResource(isShow?R.drawable.showeye_icon_topbanner:R.drawable.hideeye_icon_topbanner);
    }


    private void loadData(){

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_COMPANY_PRICE), new NetworkWorker.ICallback() {
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


    public static void invoke(Activity context){
        Intent intent=new Intent(context,InsureCompanyPriceActivity.class);
        context.startActivity(intent);

    }


    private class PageInfo{
        List<CompanyResult> list;
    }
}
