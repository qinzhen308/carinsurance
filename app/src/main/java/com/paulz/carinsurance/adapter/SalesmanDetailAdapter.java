package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.InsureDetailFee;
import com.paulz.carinsurance.model.SalesmanDetail;
import com.paulz.carinsurance.utils.AppUtil;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */
public class SalesmanDetailAdapter extends AbsMutipleAdapter<SalesmanDetail, SalesmanDetailAdapter.ViewHolderImpl> {

    public SalesmanDetailAdapter(Activity context) {
        super(context);
    }


    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_salesman_detail, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {
        final SalesmanDetail bean = (SalesmanDetail) getItem(position);
        holder.tvName.setText(bean.carnumber);

        holder.tvDate.setText(bean.order_createtime);
        holder.tvBusinessFee.setText("￥" + bean.bamount);
        holder.tvForceFee.setText("￥" + bean.camount);
       /* holder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iCallPhone!=null){
                    iCallPhone.onCall(bean.tel);
                }
            }
        });*/
    }

    public static class ViewHolderImpl extends ViewHolder {


        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
      /*  @BindView(R.id.iv_call)
        ImageView ivCall;*/
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
