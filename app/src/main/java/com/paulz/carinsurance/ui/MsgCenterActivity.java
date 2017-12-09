package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.adapter.MsgNoticeAdapter;
import com.paulz.carinsurance.adapter.MsgOrderAdapter;
import com.paulz.carinsurance.base.BaseFragmentActivity;
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

    private String[] titles = { "公告", "消息", "提醒"};
    private Map<String, Fragment> mapFragments;
    private int mCheckFlag = 0;
    private ImageView ivRight;

    public static boolean isShow=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_my_achievement,false,true);
        setTitleText("","消息中心",0,true);
        mapFragments = new HashMap<String, Fragment>();
        mIntegralViewPager = (ViewPager) findViewById(R.id.integral_viewPager);
        mPageIndicatorView = (PageSlidingIndicator) findViewById(R.id.page_indicator_interal);
        mPageIndicatorView.setShouldExpand(true);
        mIntegralPageAdapter = new IntegralPageAdapter(this);

        mIntegralViewPager.setAdapter(mIntegralPageAdapter);
        mPageIndicatorView.setViewPager(mIntegralViewPager);
//        ivRight=(ImageView)findViewById(R.id.baseTitle_rightIv);
//        ivRight.setImageResource(isShow?R.drawable.showeye_icon_topbanner:R.drawable.hideeye_icon_topbanner);
    }

    @Override
    public void onRightClick() {

    }



    private void initData() {
        mCheckFlag=getIntent().getIntExtra("tag",0);
        mIntegralViewPager.setCurrentItem(mCheckFlag);
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
            if (titles[i].equals("公告")) {
                currentFragment= MsgNoticeFragment.createInstance("1");

            } else if (titles[i].equals("消息")) {
                currentFragment= MsgOrderFragment.createInstance("2");

            } else if (titles[i].equals("提醒")) {
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
