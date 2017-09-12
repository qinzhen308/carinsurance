package com.paulz.carinsurance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseFragmentActivity;
import com.paulz.carinsurance.ui.fragment.AchievementFragment;
import com.paulz.carinsurance.view.PageSlidingIndicator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pualbeben on 17/5/21.
 */

public class MyAchievementActivity extends BaseFragmentActivity {
    private IntegralPageAdapter mIntegralPageAdapter;

    private ViewPager mIntegralViewPager;
    private PageSlidingIndicator mPageIndicatorView;

    private String[] titles = { "今天", "本月", "自定义" };
    private Map<String, Fragment> mapFragments;
    private int mCheckFlag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        setActiviyContextView(R.layout.activity_my_achievement,false,true);
        setTitleText("","我的业绩",0,true);
        mapFragments = new HashMap<String, Fragment>();
        mIntegralViewPager = (ViewPager) findViewById(R.id.integral_viewPager);
        mPageIndicatorView = (PageSlidingIndicator) findViewById(R.id.page_indicator_interal);
        mPageIndicatorView.setShouldExpand(true);
        mIntegralPageAdapter = new IntegralPageAdapter(this);

        mIntegralViewPager.setAdapter(mIntegralPageAdapter);
        mPageIndicatorView.setViewPager(mIntegralViewPager);

    }


    private void initData() {
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
            if (titles[i].equals("今天")) {
                currentFragment=AchievementFragment.createInstance(i);
            } else if (titles[i].equals("本月")) {
                currentFragment=AchievementFragment.createInstance(i);

            } else if (titles[i].equals("自定义")) {
                currentFragment=AchievementFragment.createInstance(i);

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


    public static void invoke(Context context){
        context.startActivity(new Intent(context,MyAchievementActivity.class));

    }
}
