package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.core.framework.util.StringUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.Customer;
import com.paulz.carinsurance.model.CustomerCar;
import com.paulz.carinsurance.model.Model;
import com.paulz.carinsurance.ui.AddCarInfoActivity;
import com.paulz.carinsurance.ui.CarInsureActivity;
import com.paulz.carinsurance.ui.CustomerInfoActivity;
import com.paulz.carinsurance.ui.OrderInfoActivity;
import com.paulz.carinsurance.utils.AppUtil;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/29.
 */

public class CustomerCarAdapter extends AbsMutipleAdapter<CustomerCar, CustomerCarAdapter.ViewHolderImpl> {



    public CustomerCarAdapter(Activity context) {
        super(context);
    }



    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_customer_car, null));
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolderImpl holder) {

        final CustomerCar bean=(CustomerCar)getItem(position);
        holder.tvCarId.setText(AppUtil.isNull(bean.insurance_carnumber)?"未上牌":bean.insurance_carnumber);
        holder.tvInsureCity.setText((AppUtil.isNull(bean.provname)?"":bean.provname)+bean.cityname);

        holder.btnQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OrderInfoActivity.invoke(mContext,bean.customer_carmodel_id);
                String sfz=((CustomerInfoActivity)mContext).data.customer_sfz;
                if(AppUtil.isNull(sfz)|| !StringUtil.isMatchingPersonId(sfz)){
                    AppUtil.showToast(mContext,"请先完善客户身份信息");
                    return;
                }
                AddCarInfoActivity.invoke(mContext,bean.customer_carmodel_id);
            }
        });

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarInsureActivity.invoke(mContext,bean.customer_carmodel_id,((CustomerInfoActivity)mContext).data.customer_id);
            }
        });


    }

    public static class ViewHolderImpl extends ViewHolder {

        @BindView(R.id.tv_car_id)
        TextView tvCarId;
        @BindView(R.id.tv_insure_city)
        TextView tvInsureCity;
        @BindView(R.id.btn_quote)
        TextView btnQuote;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }


}
