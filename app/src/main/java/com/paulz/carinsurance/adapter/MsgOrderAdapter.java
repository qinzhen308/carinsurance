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
import com.paulz.carinsurance.model.CustomerDetail;
import com.paulz.carinsurance.model.Msg;
import com.paulz.carinsurance.model.Order;
import com.paulz.carinsurance.ui.CommonWebActivity;
import com.paulz.carinsurance.ui.CustomerInfoActivity;
import com.paulz.carinsurance.ui.NameVerifyActivity;
import com.paulz.carinsurance.ui.OrderInfoActivity;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class MsgOrderAdapter extends AbsMutipleAdapter<Msg, MsgOrderAdapter.ViewHolderImpl> {



    public MsgOrderAdapter(Activity context) {
        super(context);
    }


    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_msg_order, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {
        final Msg msg=(Msg)getItem(position);
        holder.tvName.setText(msg.title);
        holder.tvDate.setText(msg.date);
        holder.tvDescrib.setText(msg.abstractStr);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //消息类型 0无跳转无详情，1公告类型，图文消息，2以上为各种跳转链接，3人工核保结果 ，4支付完成（保单生效），
                // 5入账，6提现处理，7订单即将关闭，8车险即将到期，9实名认证审核结果
                if(msg.type==1){
                    ParamBuilder params=new ParamBuilder();
                    params.append("id",msg.id);
                    CommonWebActivity.invoke(mContext, APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_MSG_DETAIL),"公告详情");
                }else if(msg.type==7||msg.type==4||msg.type==3){
                    OrderInfoActivity.invoke(mContext,msg.extra.sn);
                }else if(msg.type==9){
                    NameVerifyActivity.invoke(mContext);
                }else if(msg.type==5||msg.type==6||msg.type==8){
                    CustomerInfoActivity.invoke(mContext,msg.extra.id);
                }else if(msg.type==2){
                    CommonWebActivity.invoke(mContext, msg.extra.url,"");
                }

            }
        });

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
