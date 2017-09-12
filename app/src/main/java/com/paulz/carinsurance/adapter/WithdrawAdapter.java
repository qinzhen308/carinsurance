package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.Address;
import com.paulz.carinsurance.model.CashRecord;
import com.paulz.carinsurance.utils.DateUtil;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/22.
 */

public class WithdrawAdapter extends AbsMutipleAdapter<CashRecord, WithdrawAdapter.ViewHolderImpl> {


    public WithdrawAdapter(Activity context) {
        super(context);
    }


    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_withdraw_record, null));
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolderImpl holder) {
        final CashRecord bean = (CashRecord) getItem(position);

        holder.tvMoney.setText(bean.member_atm_money);
        holder.tvDate.setText(DateUtil.getYMDHMDate(bean.member_atm_create_time*1000));
        if(bean.member_atm_status==1){//已提现
            holder.tvStatus.setText("已提现");
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.base_green));
        }else {
            holder.tvStatus.setText("审核中");
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.main));
        }
        holder.tvOperate.setText("操作：提现");


    }


    public static class ViewHolderImpl extends ViewHolder {

        @BindView(R.id.tv_money)
        TextView tvMoney;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_operate)
        TextView tvOperate;
        @BindView(R.id.tv_date)
        TextView tvDate;


        public ViewHolderImpl(View view) {
            super(view);
        }
    }


}
