package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Msg;
import com.paulz.carinsurance.model.Order;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.ui.CommonWebActivity;
import com.paulz.carinsurance.ui.MsgCenterActivity;
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
        if(msg.unread==0){
            holder.vUnread.setVisibility(View.GONE);
        }else {
            holder.vUnread.setVisibility(View.VISIBLE);
        }
        Glide.with(mContext).load(AppUrls.getInstance().DOMAIN+msg.img).into(holder.ivPic);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MsgNoticeDetailActivity.invoke(mContext,"");
                ParamBuilder params=new ParamBuilder();
                params.append("id",msg.id);
                if(msg.unread==1){
                    setReaded(msg);
                }
                CommonWebActivity.invoke(mContext, APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_MSG_DETAIL),"公告详情");
            }
        });
    }

    public void setReaded(final Msg msg){
        ParamBuilder params=new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("id",msg.id);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_MSG_DETAIL ), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<Object> obj= GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK){
                        msg.unread=0;
                        notifyDataSetChanged();
                        ((MsgCenterActivity)mContext).loadMsgCount();
                    }
                }
            }
        },requester, DESUtil.SECRET_DES);

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
        @BindView(R.id.v_unread)
        View vUnread;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }
}
