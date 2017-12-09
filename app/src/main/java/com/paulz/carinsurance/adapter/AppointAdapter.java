package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.Appoint;
import com.paulz.carinsurance.ui.AppointEditlActivity;
import com.paulz.carinsurance.ui.AppointOtherEditActivity;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class AppointAdapter extends AbsMutipleAdapter<Appoint, AppointAdapter.AppointHolder> {




    public AppointAdapter(Activity context) {
        super(context);
    }


    @Override
    public AppointHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new AppointHolder(mInflater.inflate(R.layout.item_appoint, null));
    }

    @Override
    public void onBindViewHolder(int position, AppointHolder holder) {
        final Appoint bean = (Appoint) getItem(position);
        holder.tvTitle.setText(bean.teyue_engagecname);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bean.teyue_changeable==3){
                    AppointOtherEditActivity.invoke((Activity) mContext,bean.teyue_id,false);
                }else {
                    AppointEditlActivity.invoke((Activity) mContext,bean.teyue_id,false);
                }
            }
        });
    }

    public static class AppointHolder extends ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;

        public AppointHolder(View view) {
            super(view);
        }
    }

    ICallPhone iCallPhone;

    public void setICallPhone(ICallPhone iCallPhone) {
        this.iCallPhone = iCallPhone;
    }

    public interface ICallPhone {
        void onCall(String phone);
    }
}
