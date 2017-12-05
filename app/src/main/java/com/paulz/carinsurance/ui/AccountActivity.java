package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseFragmentActivity;
import com.paulz.carinsurance.ui.fragment.AccountFragment;
import com.paulz.carinsurance.ui.fragment.AchievementFragment;
import com.paulz.carinsurance.view.PageSlidingIndicator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pualbeben on 17/5/21.
 */

public class AccountActivity extends BaseFragmentActivity {
    private IntegralPageAdapter mIntegralPageAdapter;

    private ViewPager mIntegralViewPager;
    private PageSlidingIndicator mPageIndicatorView;

    private String[] titles = { "全部", "核保中", "待支付" ,"已支付"};
    private Map<String, AccountFragment> mapFragments;
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
        setTitleText("","账户信息",R.drawable.hideeye_icon_topbanner,true);
        mapFragments = new HashMap<String, AccountFragment>();
        mIntegralViewPager = (ViewPager) findViewById(R.id.integral_viewPager);
        mPageIndicatorView = (PageSlidingIndicator) findViewById(R.id.page_indicator_interal);
        mPageIndicatorView.setShouldExpand(true);
        mIntegralPageAdapter = new IntegralPageAdapter(this);

        mIntegralViewPager.setAdapter(mIntegralPageAdapter);
        mPageIndicatorView.setViewPager(mIntegralViewPager);
        ivRight=(ImageView)findViewById(R.id.baseTitle_rightIv);
        ivRight.setImageResource(isShow?R.drawable.showeye_icon_topbanner:R.drawable.hideeye_icon_topbanner);
    }

    @Override
    public void onRightClick() {
        isShow=!isShow;
        for (AccountFragment f:mapFragments.values()){
            f.updateAdapter();
        }
        ivRight.setImageResource(isShow?R.drawable.showeye_icon_topbanner:R.drawable.hideeye_icon_topbanner);
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
            AccountFragment currentFragment = null;
            if (titles[i].equals("全部")) {
                currentFragment=AccountFragment.createInstance("all");

            } else if (titles[i].equals("核保中")) {
                currentFragment=AccountFragment.createInstance("waitorder");

            } else if (titles[i].equals("待支付")) {
                currentFragment=AccountFragment.createInstance("waitpay");

            }else if (titles[i].equals("已支付")) {
                currentFragment=AccountFragment.createInstance("paid");

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==100){
                Iterator<AccountFragment> fs=mapFragments.values().iterator();
                while (fs.hasNext()){
                    AccountFragment f=fs.next();
                    if(f.isAdded()){
                        f.onRefresh();
                    }
                }
            }
        }
    }

    public static void invoke(Context context, int tag){
        context.startActivity(new Intent(context,AccountActivity.class).putExtra("tag",tag));
    }
}
