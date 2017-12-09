package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Msg;
import com.paulz.carinsurance.model.Order;
import com.paulz.carinsurance.ui.CommonWebActivity;
import com.paulz.carinsurance.ui.MsgNoticeDetailActivity;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class MsgNoticeAdapter extends AbsMutipleAdapter<Msg, MsgNoticeAdapter.ViewHolderImpl> {



    public MsgNoticeAdapter(Activity context) {
        super(context);
    }


    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_msg_notice, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {
        final Msg msg=(Msg)getItem(position);
        holder.tvName.setText(msg.title);
        holder.tvDate.setText(msg.date);
        holder.tvDescrib.setText(msg.abstractStr);
        Glide.with(mContext).load(AppUrls.getInstance().DOMAIN+msg.img).into(holder.ivPic);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MsgNoticeDetailActivity.invoke(mContext,"");
                ParamBuilder params=new ParamBuilder();
                params.append("id",msg.id);
                CommonWebActivity.invoke(mContext, APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_MSG_DETAIL),"公告详情");
            }
        });
    }

    public static class ViewHolderImpl extends ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.iv_pic)
        ImageView ivPic;
        @BindView(R.id.tv_describ)
        TextView tvDescrib;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }
}
