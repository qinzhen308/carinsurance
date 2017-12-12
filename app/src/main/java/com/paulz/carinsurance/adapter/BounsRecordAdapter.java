package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.BounsRecord;
import com.paulz.carinsurance.model.CashRecord;
import com.paulz.carinsurance.ui.OrderInfoActivity;
import com.paulz.carinsurance.utils.DateUtil;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/22.
 */

public class BounsRecordAdapter extends AbsMutipleAdapter<BounsRecord, BounsRecordAdapter.ViewHolderImpl> {




    public BounsRecordAdapter(Activity context) {
        super(context);
    }


    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_bouns_record, null));
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolderImpl holder) {
        final BounsRecord bean = (BounsRecord) getItem(position);

        holder.tvMoney.setText("￥"+(bean.money_type==1?"+":"-")+bean.money);
        holder.tvDate.setText(DateUtil.getYMDHMDate(bean.create_time * 1000));
        if (bean.is_paid == 1) {//已提现
            holder.tvStatus.setText("已入账");
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.base_green));
        } else {
            holder.tvStatus.setText("审核中");
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.main));
        }
        holder.tvName.setText(bean.insurance_carnumber+" - "+bean.insurance_name);
        holder.tvOder.setText("订单号："+bean.order_sn);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderInfoActivity.invoke(mContext,bean.order_sn);
            }
        });

    }


    public static class ViewHolderImpl extends ViewHolder {
        @BindView(R.id.tv_oder)
        TextView tvOder;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_money)
        TextView tvMoney;
        @BindView(R.id.tv_date)
        TextView tvDate;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }


}
