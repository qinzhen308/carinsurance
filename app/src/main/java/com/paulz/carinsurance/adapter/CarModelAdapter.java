package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.CarModeInfo;
import com.paulz.carinsurance.model.Model;
import com.paulz.carinsurance.ui.SelectCarModelActivity;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class CarModelAdapter extends AbsMutipleAdapter<CarModeInfo, CarModelAdapter.CustomerHolder> {



    public CarModelAdapter(Activity context) {
        super(context);
    }



    @Override
    public CustomerHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new CustomerHolder(mInflater.inflate(R.layout.item_car_model, null));
    }

    @Override
    public void onBindViewHolder(final int position, CustomerHolder holder) {
        final CarModeInfo bean=(CarModeInfo) getItem(position);
        final String model=bean.name;
        holder.tvContent.setText(model);
        holder.tvPrice.setText("参考价："+bean.money);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("extra_car_model_id",bean.vinmodelid);
                intent.putExtra("extra_car_model",model);
                SelectCarModelActivity.clearCarMode=false;
                if(mContext instanceof SelectCarModelActivity){
                    intent.putExtra("extra_vin",((SelectCarModelActivity)mContext).resultVin);
                    intent.putExtra("extra_date",((SelectCarModelActivity)mContext).resultDate);
                }
                intent.putExtra("extra_seat",bean.seat);
                ((Activity)mContext).setResult(Activity.RESULT_OK,intent);
                ((Activity)mContext).finish();
            }
        });
    }

    public static class CustomerHolder extends ViewHolder {

        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_price)
        TextView tvPrice;

        public CustomerHolder(View view) {
            super(view);
        }
    }
}
