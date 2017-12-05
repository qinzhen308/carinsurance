package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.InsureDetailFee;
import com.paulz.carinsurance.model.SalesmanCount;
import com.paulz.carinsurance.utils.AppUtil;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */
public class SalesmanCountAdapter extends AbsMutipleAdapter<SalesmanCount, SalesmanCountAdapter.ViewHolderImpl> {



    public SalesmanCountAdapter(Activity context) {
        super(context);
    }


    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_salesman_count, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {
        final SalesmanCount bean = (SalesmanCount) getItem(position);
        holder.tvName.setText(bean.name);
        holder.tvTeamName.setText(bean.store_name);
        holder.tvDate.setText(bean.createtime);
        holder.tvInvitedName.setText(bean.recomname);
        holder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OrderInfoActivity.invoke(mContext,bean.store_id);
                if (iCallPhone != null) {
                    iCallPhone.onCall(bean.tel);
                }
            }
        });
    }

    public static class ViewHolderImpl extends ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.iv_call)
        ImageView ivCall;
        @BindView(R.id.tv_team_name)
        TextView tvTeamName;
        @BindView(R.id.tv_invited_name)
        TextView tvInvitedName;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }


    CustomerAdapter.ICallPhone iCallPhone;

    public void setICallPhone(CustomerAdapter.ICallPhone iCallPhone) {
        this.iCallPhone = iCallPhone;
    }

    public interface ICallPhone {
        void onCall(String phone);
    }
}
