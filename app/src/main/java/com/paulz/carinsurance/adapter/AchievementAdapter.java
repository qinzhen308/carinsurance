package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.Achievement;
import com.paulz.carinsurance.model.Model;
import com.paulz.carinsurance.ui.OrderInfoActivity;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class AchievementAdapter extends AbsMutipleAdapter<Achievement, AchievementAdapter.ViewHolderImpl> {



    public AchievementAdapter(Activity context) {
        super(context);
    }



    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_achievement, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {
        final Achievement bean=(Achievement)getItem(position);
        holder.tvName.setText(bean.insurance_carnumber+" - "+bean.insurance_name);
        holder.tvChannel.setText(bean.insurance_company_name);
        holder.tvPrice.setText("￥"+bean.amount);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderInfoActivity.invoke(mContext,bean.order_sn);
            }
        });
    }

    public static class ViewHolderImpl extends ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_channel)
        TextView tvChannel;
        @BindView(R.id.tv_price)
        TextView tvPrice;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }
}
