package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.AbsMutipleAdapter;
import com.paulz.carinsurance.adapter.ViewHolder;
import com.paulz.carinsurance.base.BaseActivity;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/5/24.
 * 选择车牌
 */

public class SelectCarNumberActivity extends BaseActivity {
    public final static int REQUEST_CODE = 1002;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.gridview)
    GridView gridview;

    ProvinceAdapter provinceAdapter;
    WordAdapter wordAdapter;
    @BindColor(R.color.white)
    int bg_white;
    @BindColor(R.color.grayEF)
    int bg_grey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        init();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_select_car_number, false, true);
//        setTitleText("", "选择车牌", 0, true);
        setTitleTextRightText("", "选择车牌", "确定", true);
        ButterKnife.bind(this);
        provinceAdapter = new ProvinceAdapter(this);
        provinceAdapter.setList(getResources().getStringArray(R.array.province_short));
        listView.setAdapter(provinceAdapter);

        wordAdapter=new WordAdapter(this);
        wordAdapter.setList(getResources().getStringArray(R.array.word_abcd));
        gridview.setAdapter(wordAdapter);

    }


    private void init(){
        String cb=getIntent().getStringExtra("car_number");
        if(cb!=null&&cb.length()==2){
            provinceAdapter.setSelection(""+cb.charAt(0));
            wordAdapter.setSelection(""+cb.charAt(1));
        }
    }


    @Override
    public void onRightClick() {
        Intent intent = new Intent();
        intent.putExtra("car_number",provinceAdapter.getSelectedItem()+wordAdapter.getSelectedItem());
        setResult(RESULT_OK, intent);
        finish();

    }

    public static void invoke(Activity context,String car) {
        Intent intent = new Intent(context, SelectCarNumberActivity.class);
        if(car!=null){
            intent.putExtra("car_number",car);
        }
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void invoke(Fragment context, String car) {
        Intent intent = new Intent(context.getActivity(), SelectCarNumberActivity.class);
        if(car!=null){
            intent.putExtra("car_number",car);
        }
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    public class ProvinceAdapter extends AbsMutipleAdapter<String, ProvinceHolder> {

        private int selected;

        public ProvinceAdapter(Activity context) {
            super(context);
        }

        @Override
        public ProvinceHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
            return new ProvinceHolder(View.inflate(mContext, R.layout.item_car_number_province, null));
        }

        @Override
        public void onBindViewHolder(final int position, ProvinceHolder holder) {
            holder.tvProvince.setText((String) getItem(position));
            if (selected == position) {
                holder.state.setBackgroundColor(bg_white);
            } else {
                holder.state.setBackgroundColor(bg_grey);
            }
            holder.state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selected==position)return;
                    selected = position;
                    notifyDataSetChanged();
                }
            });
        }

        public String getSelectedItem() {
            return (String) getItem(selected);
        }

        public void setSelection(int position){
            selected=position;
            notifyDataSetChanged();
            listView.smoothScrollToPosition(selected);
        }

        public void setSelection(String str){
            for(int i=0;i<getCount();i++){
                if(getItem(i).equals(str)){
                    selected=i;
                }
            }
            notifyDataSetChanged();
            listView.smoothScrollToPosition(selected);
        }


    }

    public class WordAdapter extends AbsMutipleAdapter<String, WordHolder> {


        private int selected;

        public WordAdapter(Activity context) {
            super(context);
        }

        @Override
        public WordHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
            return new WordHolder(View.inflate(mContext, R.layout.item_car_number_word, null));
        }

        @Override
        public void onBindViewHolder(final int position, WordHolder holder) {
            holder.tvWord.setText((String) getItem(position));
            if (selected == position) {
                holder.tvWord.setSelected(true);
            } else {
                holder.tvWord.setSelected(false);
            }
            holder.tvWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selected==position)return;
                    selected = position;
                    notifyDataSetChanged();
                }
            });
        }

        public String getSelectedItem() {
            return (String) getItem(selected);
        }

        public void setSelection(int position){
            selected=position;
            notifyDataSetChanged();
        }

        public void setSelection(String str){
            for(int i=0;i<getCount();i++){
                if(getItem(i).equals(str)){
                    selected=i;
                }
            }
            notifyDataSetChanged();
        }

    }


    public class ProvinceHolder extends ViewHolder {
        @BindView(R.id.tv_province)
        TextView tvProvince;
        @BindView(R.id.state)
        LinearLayout state;

        public ProvinceHolder(View view) {
            super(view);
        }
    }


    public class WordHolder extends ViewHolder {
        @BindView(R.id.tv_word)
        TextView tvWord;
        public WordHolder(View view) {
            super(view);
        }
    }

}
