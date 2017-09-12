package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.model.Address;
import com.paulz.carinsurance.model.Model;
import com.paulz.carinsurance.ui.EditAddressActivity;
import com.paulz.carinsurance.utils.AppUtil;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/22.
 */

public class AddressAdapter extends AbsMutipleAdapter<Address, AddressAdapter.ViewHolderImpl> {


    private boolean editable;

    public AddressAdapter(Activity context) {
        super(context);
        editable=context.getIntent().getBooleanExtra("extra_editable",true);
    }



    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_address, null));
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolderImpl holder) {
        final Address bean=(Address) getItem(position);
        if(bean.defaul==1){
            holder.btnDefault.setSelected(true);
        }else {
            holder.btnDefault.setSelected(false);
        }
        holder.tvName.setText(bean.name);
        holder.tvAddress.setText(bean.addr);
        holder.tvPhone.setText(bean.tel);
        if(!editable){
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent();
                    intent.putExtra("extra_address",bean);
                    ((Activity)mContext).setResult(Activity.RESULT_OK,intent);
                    ((Activity)mContext).finish();

                }
            });
        }

        holder.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bean.defaul==1){
                    AppUtil.showToast(mContext,"已设置为默认地址");
                }
                defaultAddress(position,bean);
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditAddressActivity.invoke(mContext,bean);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectListener!=null){
                    mSelectListener.delete(position,bean);
                }
            }
        });

    }

    public static class ViewHolderImpl extends ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_phone)
        TextView tvPhone;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.btn_default)
        TextView btnDefault;
        @BindView(R.id.btn_edit)
        TextView btnEdit;
        @BindView(R.id.btn_delete)
        TextView btnDelete;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }

    private int selected;
    public void defaultAddress(int selected,Address bean){
        this.selected=selected;
        if(mSelectListener!=null){
            mSelectListener.seleted(selected,bean);
        }

    }

    SelectListener mSelectListener;

    public void setSelectListener(SelectListener selectListener){
        mSelectListener=selectListener;
    }

    public interface SelectListener{
        public void seleted(int position,Address bean);
        public void delete(int position,Address bean);
    }
}
