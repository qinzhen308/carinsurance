package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.core.framework.develop.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.model.Area;
import com.paulz.carinsurance.model.IdCard;
import com.paulz.carinsurance.parser.gson.BaseObjectList;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.FileUtils;
import com.paulz.carinsurance.view.RegionSelectLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/6/15.
 */

public class SelectCityActivity extends BaseActivity {

    public final static int REQUEST_CODE=10020;

    @BindView(R.id.root)
    RegionSelectLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActiviyContextView(R.layout.activity_select_city, false, true);
        setTitleTextRightText("", "选择城市", "确定", true);
        ButterKnife.bind(this);
        load();
        initView();
    }

    private void initView() {

    }

    @Override
    public void onRightClick() {
        root.selectAndBack();
    }

    private void load(){
        try {
            List<Area> cities=null;

            if(getIntent().hasExtra("extra_cities")){
                cities=(ArrayList<Area>)getIntent().getSerializableExtra("extra_cities");
            }else {
                InputStream is=getResources().getAssets().open(getIntent().getBooleanExtra("extra_all",true)?"city1":"city0");
                int size = is.available();

                // Read the entire asset into a local byte buffer.
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                // Convert the buffer into a string.
                String text = new String(buffer, "UTF-8");
//            BaseObjectList<Area> list= GsonParser.getInstance().parseToObj4List(text, Area.class);
                cities=new Gson().fromJson(text,new TypeToken<ArrayList<Area>>(){}.getType());
            }

            LogUtil.d("citys="+cities.size());
            int[] positions=handleSelects(cities);
            if(positions!=null){
                root.setList(cities,positions);
            }else  {
                root.setList(cities);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] handleSelects(List<Area> cities){
        String idStr=getIntent().getStringExtra("extra_selects");
        if(TextUtils.isEmpty(idStr))return null;
        String[] ids=idStr.split(",");
        int[] positions=new int[ids.length];
        findPosition(cities,ids,positions,0);
        return positions;
    }


    /**
     *
     * @param list
     * @param ids
     * @param positions
     * @param deep
     * @return  true代表跳出本方法
     */
    private boolean findPosition(List<Area> list,String[] ids,int[] positions,int deep){
        for(int i=0;i<list.size();i++){
            Area a=list.get(i);
            if(a.id.equals(ids[deep])){
                positions[deep]=i;
                if(AppUtil.isEmpty(a.child)||ids.length-1==deep){
                    return true;
                }else if(findPosition(a.child,ids,positions,deep+1)){
                    return true;
                }
            }
        }
        return true;
    }


    public static void invoke(Activity context,boolean all,String ids,ArrayList<Area> cities){
        Intent intent=new Intent(context,SelectCityActivity.class).putExtra("extra_all",all).putExtra("extra_selects",ids);
        if(!AppUtil.isEmpty(cities)){
            intent.putExtra("extra_cities",cities);
        }
        context.startActivityForResult(intent,REQUEST_CODE);
    }

}
