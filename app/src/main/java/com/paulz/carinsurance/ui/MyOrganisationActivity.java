package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.common.recyclerview.CircleTransform;
import com.paulz.carinsurance.controller.AbstractListAdapter;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.view.BillboardLayout;
import com.paulz.carinsurance.view.CircleImageView;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pualbeben on 17/5/21.
 * 我的团队/机构
 */

public class MyOrganisationActivity extends BaseActivity {


   /* @BindView(R.id.tv_notice)
    TextView tvNotice;*/
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tab1)
    RadioButton tab1;
    @BindView(R.id.tab2)
    RadioButton tab2;
    @BindView(R.id.tab3)
    RadioButton tab3;
    @BindView(R.id.tab4)
    RadioButton tab4;
    @BindView(R.id.tab_bar)
    RadioGroup tabBar;
    @BindView(R.id.layout_detail)
    View layoutDetail;
    @BindView(R.id.lv_links)
    ListView lvLinks;
    LinkAdapter linkAdapter;

    PageData mData;
    @BindView(R.id.tv_value1)
    TextView tvValue1;
    @BindView(R.id.tv_title1)
    TextView tvTitle1;
    @BindView(R.id.tv_value2)
    TextView tvValue2;
    @BindView(R.id.tv_title2)
    TextView tvTitle2;
    @BindView(R.id.tv_value3)
    TextView tvValue3;
    @BindView(R.id.tv_title3)
    TextView tvTitle3;
    @BindView(R.id.layout_tab_content)
    View layoutTabContent;

    @BindView(R.id.layout_billboard)
    BillboardLayout layoutBillboard;




    TextView[] tvTitles;
    TextView[] tvValues;


    @BindColor(R.color.text_grey_french1)
    int unit_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_my_organisation, false, true);
        setTitleTextRightText("", AppStatic.getInstance().getmUserInfo().teamtype==2?"我的机构":"我的团队", "去邀请", true);
        ButterKnife.bind(this);
        tvTitles=new TextView[]{tvTitle1,tvTitle2,tvTitle3};
        tvValues=new TextView[]{tvValue1,tvValue2,tvValue3};
        tabBar.check(R.id.tab1);
        tabBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.tab1:
                        changeGroup(0);
                        break;
                    case R.id.tab2:
                        changeGroup(1);

                        break;
                    case R.id.tab3:
                        changeGroup(2);

                        break;
                    case R.id.tab4:
                        changeGroup(3);

                        break;
                }
            }
        });
        linkAdapter = new LinkAdapter(this);
        lvLinks.setAdapter(linkAdapter);
        layoutBillboard.setSingleLine(true);
        lvLinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Link link = (Link) linkAdapter.getItem(position);
                //按钮类型：1.团队列表   2.业务员列表    0wap页（会返回要跳转的相应地址，并且自动附加上Token和SESSION）
                if (link.type == 0) {
                    ParamBuilder params=new ParamBuilder();
                    String url=APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().DOMAIN+mData.recommonurl);
                    CommonWebActivity.invoke(MyOrganisationActivity.this,"https://www.baidu.com",link.title);
                } else if (link.type == 1) {
                    SalesmanListActivity.invoke(MyOrganisationActivity.this);
                } else if (link.type == 2) {
                    TeamListActivity.invoke(MyOrganisationActivity.this);
                }
            }
        });
    }

    @Override
    public void onRightClick() {
        if(mData==null)return;
        ParamBuilder params=new ParamBuilder();
        String url=APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().DOMAIN+mData.recommonurl);
        CommonWebActivity.invoke(this,url,"去邀请");
    }

    private void handleData(PageData data) {
        tvName.setText(data.storetitle);
        tvStatus.setText(data.authenticate);
        linkAdapter.setList(data.linklist);
        linkAdapter.notifyDataSetChanged();
        if(AppUtil.isEmpty(data.datalist)){
            tabBar.setVisibility(View.GONE);
            layoutTabContent.setVisibility(View.GONE);
        }else {
            for(int i=0, size=data.datalist.size();i<4;i++){
                if(i<size){
                    RadioButton rb=(RadioButton) tabBar.getChildAt(i);
                    rb.setText(data.datalist.get(i).title);
                    rb.setVisibility(View.VISIBLE);
                    rb.setTag(data.datalist.get(i).type);
                }else {
                    tabBar.getChildAt(i).setVisibility(View.GONE);
                }
            }
            changeGroup(0);
        }

        layoutBillboard.showView(data.message);
        Glide.with(this).load(AppUrls.getInstance().DOMAIN+data.avatar).transform(new CircleTransform(this)).placeholder(R.drawable.user2).error(R.drawable.user2).into(ivAvatar);


    }


    private void changeGroup(int groupIndex) {
        TabGroup group = mData.datalist.get(groupIndex);
        for(int i=0;i<tvValues.length;i++){
            tvTitles[i].setText(group.list.get(i).des);
            int unitPosition=group.list.get(i).unit.length();
            SpannableString sb=new SpannableString(group.list.get(i).price+group.list.get(i).unit);
            sb.setSpan(new RelativeSizeSpan(0.7f),sb.length()-unitPosition,sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(unit_color),sb.length()-unitPosition,sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvValues[i].setText(sb);
        }

    }


    private void loadData() {

        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ORGANIZATION_HOME), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        mData = object.data;
                        handleData(object.data);
                    } else {
                        AppUtil.showToast(getApplicationContext(), object != null ? object.msg : "加载失败");

                    }
                } else {
                    AppUtil.showToast(getApplicationContext(), "加载失败");

                }
            }
        });
    }

    private void tabContentOperate(){
        int type=(int)tabBar.findViewById(tabBar.getCheckedRadioButtonId()).getTag();
        if(type==1){
            TeamAchievementDetailActivity.invoke(this,"");

        }else if(type==2){
            TeamAchievementActivity.invoke(this,"");

        }else if(type==3){
            TeamAchievementDetailActivity.invoke(this,"");

        }else if(type==4){
            SalesmanCountListActivity.invoke(this,"");

        }
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, MyOrganisationActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.layout_detail,R.id.layout_tab_content})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_detail:
                TeamInfoActivity.invoke(this, "");
                break;
            case R.id.layout_tab_content:
                tabContentOperate();

                break;

        }
    }

    private class PageData {
        String authenticate;
        String avatar;
        String storetitle;
        String recommonurl;
        List<Link> linklist;
        List<TabGroup> datalist;
        String[] message;

    }

    private class TabGroup {
        String title;
        int type;//按钮类型        4.业务员数   3.出单量    2.团队保费    1.总保费
        List<Tab> list;

    }

    private class Tab {
        String des;
        String price;
        String unit;

    }

    private class Link {
        String title;
        int type;
    }

    public class LinkAdapter extends AbstractListAdapter<Link> {

        public LinkAdapter(Activity context) {
            super(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_link_btn, null);
            Link link = mList.get(i);
            ((TextView) v.findViewById(R.id.tv_name)).setText(link.title);
            return v;
        }
    }


}
