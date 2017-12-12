package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.MsgNoticeAdapter;
import com.paulz.carinsurance.adapter.MsgOrderAdapter;
import com.paulz.carinsurance.base.BaseFragmentActivity;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.MsgUnread;
import com.paulz.carinsurance.model.wrapper.MsgWraper;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.ui.fragment.AccountFragment;
import com.paulz.carinsurance.ui.fragment.MsgNoticeFragment;
import com.paulz.carinsurance.ui.fragment.MsgOrderFragment;
import com.paulz.carinsurance.ui.fragment.MsgTipFragment;
import com.paulz.carinsurance.view.PageSlidingIndicator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pualbeben on 17/5/21.
 * 消息中心
 */

public class MsgCenterActivity extends BaseFragmentActivity {
    private IntegralPageAdapter mIntegralPageAdapter;

    private ViewPager mIntegralViewPager;
    private PageSlidingIndicator mPageIndicatorView;

    private TextView btn_set_read;


    private String[] titles = { "公告", "消息", "提醒"};
    private Map<String, Fragment> mapFragments;
    private int mCheckFlag = 0;
    private ImageView ivRight;

    public static boolean isShow=true;

    public int[] msgcounts=new int[3];
    public TextView[] msgcountView=new TextView[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMsgCount();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_msg_center,false,true);
        setTitleText("","消息中心",0,true);
        mapFragments = new HashMap<String, Fragment>();
        mIntegralViewPager = (ViewPager) findViewById(R.id.integral_viewPager);
        btn_set_read = (TextView) findViewById(R.id.btn_set_read);
        msgcountView[0] = (TextView) findViewById(R.id.tv_msg_count1);
        msgcountView[1] = (TextView) findViewById(R.id.tv_msg_count2);
        msgcountView[2] = (TextView) findViewById(R.id.tv_msg_count3);
        mPageIndicatorView = (PageSlidingIndicator) findViewById(R.id.page_indicator_interal);
        mPageIndicatorView.setShouldExpand(true);
        mIntegralPageAdapter = new IntegralPageAdapter(this);

        mIntegralViewPager.setAdapter(mIntegralPageAdapter);
        mPageIndicatorView.setViewPager(mIntegralViewPager);

        btn_set_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReaded();

            }
        });
//        ivRight=(ImageView)findViewById(R.id.baseTitle_rightIv);
//        ivRight.setImageResource(isShow?R.drawable.showeye_icon_topbanner:R.drawable.hideeye_icon_topbanner);
        mIntegralViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCheckFlag=position;
                showSetReadedBtn();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onRightClick() {

    }




    private void initData() {
        mCheckFlag=getIntent().getIntExtra("tag",0);
        mIntegralViewPager.setCurrentItem(mCheckFlag);
    }


    public void loadMsgCount(){
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MSG_UNREAD), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<MsgUnread> obj= GsonParser.getInstance().parseToObj(result,MsgUnread.class);
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK&&obj.data!=null){
                        for(int i=0;i<3;i++){
                            msgcounts[i]=obj.data.list.get(i).total;
                            setMsgCount(msgcountView[i],msgcounts[i]);
                        }

                        showSetReadedBtn();

                    }
                }
            }
        });

    }

    public void showSetReadedBtn(){
        if(msgcounts[mCheckFlag]>0){
            btn_set_read.setVisibility(View.VISIBLE);
        }else {
            btn_set_read.setVisibility(View.GONE);
        }

    }


    public void setReaded(){
        ParamBuilder params=new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("category",(mCheckFlag+1)+"");
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MSG_SET_READ), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<Object> obj= GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK){
                        msgcounts[mCheckFlag]=0;
                        showSetReadedBtn();
                        setMsgCount(msgcountView[mCheckFlag],msgcounts[mCheckFlag]);
                    }
                }
            }
        },requester, DESUtil.SECRET_DES);

    }

    private void setMsgCount(TextView v,int count){
        if(count>99){
            v.setText("99+");
            v.setVisibility(View.VISIBLE);
        }else if(count>0){
            v.setText(""+count);
            v.setVisibility(View.VISIBLE);
        }else {
            v.setVisibility(View.GONE);
        }

    }




    class IntegralPageAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public IntegralPageAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment currentFragment = null;
            if (titles[i].contains("公告")) {
                currentFragment= MsgNoticeFragment.createInstance("1");

            } else if (titles[i].contains("消息")) {
                currentFragment= MsgOrderFragment.createInstance("2");

            } else if (titles[i].contains("提醒")) {
                currentFragment= MsgTipFragment.createInstance("3");

            }
            mapFragments.put(titles[i], currentFragment);
            return currentFragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];

        }
    }


    public static void invoke(Context context,int tag){
        context.startActivity(new Intent(context,MsgCenterActivity.class).putExtra("tag",tag));
    }
}
