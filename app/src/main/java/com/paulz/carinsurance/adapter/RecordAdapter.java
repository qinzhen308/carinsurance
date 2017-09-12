package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.CarInfo;
import com.paulz.carinsurance.model.Model;
import com.paulz.carinsurance.ui.AddCarInfoActivity;
import com.paulz.carinsurance.utils.DateUtil;

import java.util.Date;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class RecordAdapter extends AbsMutipleAdapter<CarInfo, RecordAdapter.RecordHolder> {



    public RecordAdapter(Activity context) {
        super(context);
    }


    @Override
    public RecordHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new RecordHolder(mInflater.inflate(R.layout.item_record, null));
    }

    @Override
    public void onBindViewHolder(int position, RecordHolder holder) {
        final CarInfo bean=(CarInfo) getItem(position);
        holder.tvName.setText(bean.customer_name);
        if(TextUtils.isEmpty(bean.insurance_carnumber)){
            holder.tvCarid.setText("未上牌");
        }else {
            holder.tvCarid.setText(bean.insurance_carnumber);
        }
        holder.tvDate.setText(DateUtil.getYMDHMSDate(new Date(bean.customer_carmodel_updatetime*1000)));
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCarInfoActivity.invoke(mContext,bean.customer_carmodel_id);

            }
        });
    }

    public static class RecordHolder extends ViewHolder {
        @BindView(R.id.tv_carid)
        TextView tvCarid;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;


        public RecordHolder(View view) {
            super(view);
        }
    }
}
