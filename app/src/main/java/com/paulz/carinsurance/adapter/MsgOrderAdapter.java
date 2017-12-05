package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.Order;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class MsgOrderAdapter extends AbsMutipleAdapter<Order, MsgOrderAdapter.ViewHolderImpl> {



    public MsgOrderAdapter(Activity context) {
        super(context);
    }


    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_msg_order, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {


    }

    public static class ViewHolderImpl extends ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_describ)
        TextView tvDescrib;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }
}
