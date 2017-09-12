package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.Model;
import com.paulz.carinsurance.model.Order;
import com.paulz.carinsurance.ui.AccountActivity;
import com.paulz.carinsurance.ui.OrderInfoActivity;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class AccountOrderAdapter extends AbsMutipleAdapter<Order, AccountOrderAdapter.ViewHolderImpl> {

    int[] bgs={R.drawable.bg_order_status,R.drawable.bg_order_status1,R.drawable.bg_order_status2,R.drawable.bg_order_status3,R.drawable.bg_order_status4,R.drawable.bg_order_status5};


    public AccountOrderAdapter(Activity context) {
        super(context);
    }



    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_account_order, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {
        final Order bean=(Order)getItem(position);
        if(AccountActivity.isShow){
            holder.tvBouns.setVisibility(View.VISIBLE);
        }else {
            holder.tvBouns.setVisibility(View.GONE);
        }
        holder.tvBouns.setText("佣金：￥"+bean.backmoney);
        holder.tvPrice.setText("￥"+bean.amount);
        holder.tvDate.setText(DateUtil.getYMDHMDate(bean.insurance_createtime*1000));
        holder.tvCustomer.setText("客户："+ (AppUtil.isNull(bean.insurance_carnumber)?"未上牌":bean.insurance_carnumber)+" - "+bean.insurance_name);
        holder.tvChannel.setText("险企："+bean.insurance_company_name);
        holder.tvStatus.setBackgroundResource(bgs[bean.order_status]);
        if(bean.order_status==0){
            holder.tvStatus.setText("未完成订单");
        }else if(bean.order_status==1){
            holder.tvStatus.setText("待核保");
        }else if(bean.order_status==2){
            holder.tvStatus.setText("核保失败");
        }else if(bean.order_status==3){
            holder.tvStatus.setText("待支付");
        }else if(bean.order_status==4){
            holder.tvStatus.setText("已支付");
        }else if(bean.order_status==5){
            holder.tvStatus.setText("交易关闭");
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderInfoActivity.invoke(mContext,bean.order_sn);
            }
        });

    }

    public static class ViewHolderImpl extends ViewHolder {
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_bouns)
        TextView tvBouns;
        @BindView(R.id.tv_customer)
        TextView tvCustomer;
        @BindView(R.id.tv_channel)
        TextView tvChannel;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }
}
