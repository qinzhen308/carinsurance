package com.paulz.carinsurance.ui;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;

import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.view.wheel.NumericWheelAdapter;
import com.paulz.carinsurance.view.wheel.OnWheelScrollListener;
import com.paulz.carinsurance.view.wheel.WheelView;

import java.util.Calendar;

/**
 * Created by pualbeben on 17/6/6.
 */

public class DateAfterPickView {


    private WheelView year;
    private WheelView month;
    private WheelView day;

    private int mYear = 2010;
    private int mMonth = 3;//min=0
    private int mDay = 3;
    private View view;
    private Dialog mDialog;
    Activity context;

    private String selectedDate;
    private int startYear,startMonth,startDay;


    public DateAfterPickView(Activity context){
        this.context=context;
        Calendar calendar=Calendar.getInstance();
        mDay=calendar.get(Calendar.DAY_OF_MONTH);
        mMonth=calendar.get(Calendar.MONTH);
        startYear=mYear=calendar.get(Calendar.YEAR);
        selectedDate= DateUtil.getYMDDate(calendar.getTime());
        mDialog = DialogUtil.getMenuDialog2(context, getDataPick(), ScreenUtil.getScreenWH(context)[1] / 2);
        mDialog.setCanceledOnTouchOutside(true);
    }

    public DateAfterPickView(Activity context,int start){
        this.context=context;
        Calendar calendar=Calendar.getInstance();
        mDay=calendar.get(Calendar.DAY_OF_MONTH);
        mMonth=calendar.get(Calendar.MONTH);
        mYear=calendar.get(Calendar.YEAR);
        startYear=start;
        selectedDate= DateUtil.getYMDDate(calendar.getTime());
        mDialog = DialogUtil.getMenuDialog2(context, getDataPick(), ScreenUtil.getScreenWH(context)[1] / 2);
        mDialog.setCanceledOnTouchOutside(true);
    }

    private void initMonth(int year){
        if(year==startYear){

        }else {

        }

    }


    private void initDay(int year, int month) {
        // 设置天数
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context, 1, getDay(year, month), "%02d");
        numericWheelAdapter.setLabel("日");
        day.setViewAdapter(numericWheelAdapter);
    }


    /**
     *
     * @param year
     * @param month
     * @return int
     * @author lilifeng
     */
    private int getDay(int year, int month) {
        int day = 31;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 4:
                day = 30;
                break;
            case 6:
                day = 30;
                break;
            case 9:
                day = 30;
                break;
            case 11:
                day = 30;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 31;
                break;
        }
        return day;
    }

    /**
     * 时间选择控价
     *
     * @return
     */
    private View getDataPick() {
        int curYear = mYear;
        int curMonth = mMonth + 1;
        int curDate = mDay;

        view = LayoutInflater.from(context).inflate(R.layout.dialog_wheel, null);
        view.findViewById(R.id.wheel_okTv).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mDialog.isShowing()){
                    mDialog.dismiss();
                }
                if(mDatePickListener!=null){
                    mDatePickListener.onSelected(selectedDate);
                }

            }
        });
        year = (WheelView) view.findViewById(R.id.wheel_yearWv);
        /**
         * 设置年份
         */
        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(context, startYear, startYear+20);
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(true);// 是否可循环滑动
        year.addScrollingListener(scrollListener);

        month = (WheelView) view.findViewById(R.id.wheel_monthWv);
        /**
         * 设置月份
         */
        NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(context, 1, 12, "%02d");
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(true);
        month.addScrollingListener(scrollListener);

        day = (WheelView) view.findViewById(R.id.wheel_dayWv);
        initDay(curYear, curMonth);
        day.addScrollingListener(scrollListener);
        day.setCyclic(true);

        year.setVisibleItems(9);// 设置显示行数
        month.setVisibleItems(9);
        day.setVisibleItems(9);

        year.setCurrentItem(curYear - startYear);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);

        return view;
    }


    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + startYear;// 年
            int n_month = month.getCurrentItem() + 1;// 月

            initDay(n_year, n_month);

            selectedDate = new StringBuilder().append((year.getCurrentItem() + startYear)).append("-")
                    .append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1)
                            : (month.getCurrentItem() + 1))
                    .append("-").append(((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1)
                            : (day.getCurrentItem() + 1))
                    .toString();

        }
    };

    public void show(){
        if(mDialog.isShowing())return;
        mDialog.show();
    }

    DatePickListener mDatePickListener;
    public void setDatePickListener(DatePickListener datePickListener){
        mDatePickListener=datePickListener;
    }

    public interface DatePickListener{
        void onSelected(String date);
    }
}
