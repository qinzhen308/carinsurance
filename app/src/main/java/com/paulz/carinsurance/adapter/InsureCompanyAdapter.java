package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.model.Company;
import com.paulz.carinsurance.model.Model;
import com.paulz.carinsurance.utils.Image13Loader;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class InsureCompanyAdapter extends AbsMutipleAdapter<Company, InsureCompanyAdapter.CustomerHolder> {




    public InsureCompanyAdapter(Activity context) {
        super(context);
    }


    @Override
    public CustomerHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new CustomerHolder(mInflater.inflate(R.layout.item_insure_company, null));
    }

    @Override
    public void onBindViewHolder(final int position, CustomerHolder holder) {
        final Company company=(Company)getItem(position);

        holder.state.setSelected(company.isSelected);

        holder.tvName.setText(company.insurance_company_name);
        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_INSCOMPANY+company.insurance_company_img,holder.ivIcon);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                company.isSelected=!company.isSelected;
                notifyDataSetChanged();
                if(mContext instanceof OnSelectChangeListener){
                    ((OnSelectChangeListener) mContext).onSelect(position);
                }
            }
        });
    }

    public static class CustomerHolder extends ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.state)
        TextView state;

        public CustomerHolder(View view) {
            super(view);
        }
    }

    public interface OnSelectChangeListener{
        void onSelect(int position);
    }
}
