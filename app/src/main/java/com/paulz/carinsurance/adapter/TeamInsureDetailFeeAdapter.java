package com.paulz.carinsurance.adapter;

import android.Manifest;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.InsureDetailFee;
import com.paulz.carinsurance.utils.AppUtil;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by pualbeben on 17/5/21.
 */
public class TeamInsureDetailFeeAdapter extends AbsMutipleAdapter<InsureDetailFee, TeamInsureDetailFeeAdapter.ViewHolderImpl> {

    public TeamInsureDetailFeeAdapter(Activity context) {
        super(context);
    }


    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_team_insure_detail_fee, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {
        final InsureDetailFee bean = (InsureDetailFee) getItem(position);
        holder.tvName.setText(bean.name);
        if(AppUtil.isNull(bean.store_name)){
            holder.tvTeamName.setVisibility(View.GONE);
        }else {
            holder.tvTeamName.setText(bean.store_name);
            holder.tvTeamName.setVisibility(View.VISIBLE);
        }
        holder.tvDate.setText(bean.create_time);
        holder.tvBusinessFee.setText("￥" + bean.bamount);
        holder.tvForceFee.setText("￥" + bean.camount);
        holder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OrderInfoActivity.invoke(mContext,bean.store_id);
                if(iCallPhone!=null){
                    iCallPhone.onCall(bean.member_tel);
                }
            }
        });
    }

    public static class ViewHolderImpl extends ViewHolder {


        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_team_name)
        TextView tvTeamName;
        @BindView(R.id.iv_call)
        ImageView ivCall;
        @BindView(R.id.tv_business_fee)
        TextView tvBusinessFee;
        @BindView(R.id.tv_force_fee)
        TextView tvForceFee;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }




    CustomerAdapter.ICallPhone iCallPhone;

    public void setICallPhone(CustomerAdapter.ICallPhone iCallPhone) {
        this.iCallPhone = iCallPhone;
    }

    public interface ICallPhone{
        void onCall(String phone);
    }
}
