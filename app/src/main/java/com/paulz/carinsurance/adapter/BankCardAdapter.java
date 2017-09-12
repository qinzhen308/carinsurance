package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Bank;
import com.paulz.carinsurance.model.BankCard;
import com.paulz.carinsurance.model.Model;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.Image13Loader;
import com.paulz.carinsurance.view.CommonDialog;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/22.
 */

public class BankCardAdapter extends AbsMutipleAdapter<BankCard, BankCardAdapter.RecordHolder> {



    public BankCardAdapter(Activity context) {
        super(context);
    }



    @Override
    public RecordHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new RecordHolder(mInflater.inflate(R.layout.item_bank_card, null));
    }

    @Override
    public void onBindViewHolder(int position, RecordHolder holder) {
        final BankCard bean=(BankCard)getItem(position);
        holder.tvName.setText(bean.bank.name);
        holder.tvCardNumber.setText(""+bean.member_bankcard_no);
        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_BANKIMG+bean.bank.logo,holder.ivIcon);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(bean);
            }
        });
    }

    private void showDeleteDialog(final BankCard bankCard){
        final CommonDialog dialog=new CommonDialog(mContext);
        dialog.setDesc("确定删除银行卡");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                if(mContext instanceof OnDeleteListener){
                    ((OnDeleteListener)mContext).onDelete(bankCard);
                }

            }
        });
        dialog.show();
    }



    public static class RecordHolder extends ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_card_number)
        TextView tvCardNumber;


        public RecordHolder(View view) {
            super(view);
        }
    }

    public interface OnDeleteListener{
        void onDelete(BankCard bean);
    }
}
