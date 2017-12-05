package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.Achievement;
import com.paulz.carinsurance.model.InsureFee;
import com.paulz.carinsurance.ui.OrderInfoActivity;
import com.paulz.carinsurance.ui.TeamInfoActivity;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class TeamInsureFeeAdapter extends AbsMutipleAdapter<InsureFee, TeamInsureFeeAdapter.ViewHolderImpl> {



    public TeamInsureFeeAdapter(Activity context) {
        super(context);
    }



    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_team_insure_fee, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {
        final InsureFee bean=(InsureFee)getItem(position);
        holder.tvName.setText(bean.store_name);
        holder.tvBusinessFee.setText("￥"+bean.bamount);
        holder.tvForceFee.setText("￥"+bean.camount);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamInfoActivity.invoke(mContext,bean.store_id);
            }
        });
    }

    public static class ViewHolderImpl extends ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_business_fee)
        TextView tvBusinessFee;
        @BindView(R.id.tv_force_fee)
        TextView tvForceFee;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }
}
