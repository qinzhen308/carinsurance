package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.Model;
import com.paulz.carinsurance.model.Order;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.ui.AccountActivity;
import com.paulz.carinsurance.ui.InsureFailedReasonActivity;
import com.paulz.carinsurance.ui.OrderInfoActivity;
import com.paulz.carinsurance.ui.UploadProfileActivity;
import com.paulz.carinsurance.ui.fragment.UserCenterFragment;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.DateUtil;
import com.paulz.carinsurance.view.CommonDialog;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class AccountOrderAdapter extends AbsMutipleAdapter<Order, AccountOrderAdapter.ViewHolderImpl> {

    int[] bgs={R.drawable.bg_order_status,R.drawable.bg_order_status1,R.drawable.bg_order_status2,R.drawable.bg_order_status3,R.drawable.bg_order_status4,R.drawable.bg_order_status5};

    Dialog lodDialog;

    public AccountOrderAdapter(Activity context) {
        super(context);
        lodDialog = DialogUtil.getCenterDialog(context, LayoutInflater.from(context)
                .inflate(R.layout.load_doag, null));
    }



    @Override
    public ViewHolderImpl onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new ViewHolderImpl(mInflater.inflate(R.layout.item_account_order, null));
    }

    @Override
    public void onBindViewHolder(int position, ViewHolderImpl holder) {
        final Order bean=(Order)getItem(position);
        if(AccountActivity.isShow){
            holder.tvBouns.setVisibility(View.VISIBLE);
        }else {
            holder.tvBouns.setVisibility(View.GONE);
        }
        holder.tvBouns.setText("佣金：￥"+bean.backmoney);
        holder.tvPrice.setText("￥"+bean.amount);
        holder.tvDate.setText(DateUtil.getYMDHMDate(bean.insurance_createtime*1000));
        holder.tvCustomer.setText("客户："+ (AppUtil.isNull(bean.insurance_carnumber)?"未上牌":bean.insurance_carnumber)+" - "+bean.insurance_name);
        holder.tvChannel.setText("险企："+bean.insurance_company_name);
        holder.tvStatus.setBackgroundResource(bgs[bean.order_status]);
        if(bean.order_status==0){
            holder.tvStatus.setText("未完成订单");
        }else if(bean.order_status==1){
            holder.tvStatus.setText("待核保");
        }else if(bean.order_status==2){
            holder.tvStatus.setText("核保失败");
        }else if(bean.order_status==3){
            holder.tvStatus.setText("待支付");
        }else if(bean.order_status==4){
            holder.tvStatus.setText("已支付");
        }else if(bean.order_status==5){
            holder.tvStatus.setText("交易关闭");
        }

        if(!AppUtil.isEmpty(bean.buttonlist)){
            holder.btnOperation.setVisibility(View.VISIBLE);

            Order.ButtonModel buttonModel=bean.buttonlist.get(0);
            holder.btnOperation.setText(buttonModel.title);
        }else {
            holder.btnOperation.setVisibility(View.GONE);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderInfoActivity.invoke(mContext,bean.order_sn);
            }
        });

        holder.btnOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppUtil.isEmpty(bean.buttonlist)){
                    Order.ButtonModel buttonModel=bean.buttonlist.get(0);
                    if(buttonModel.type==1){
                        showDeleteDialog(bean.order_sn,buttonModel);
                    }else if(buttonModel.type==2){
                        UploadProfileActivity.invoke(mContext,bean.order_sn);
                    }else if(buttonModel.type==3){
                        InsureFailedReasonActivity.invoke(mContext,bean.order_sn);
                    }
                }
            }
        });
    }


    private void showDeleteDialog(final String ordersn, final Order.ButtonModel model){
        CommonDialog dialog=new CommonDialog(mContext);
        dialog.setDesc(model.confirm);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                deleteOrder(ordersn,model.apiuri);
            }
        });
        dialog.show();

    }

    private void deleteOrder(String ordersn,String api){
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        params.append("sn",ordersn);
        String url=null;
        if(AppUrls.getInstance().BASE_DOMAIN.contains("bxagency")){
            url=AppUrls.getInstance().BASE_DOMAIN.substring(0,AppUrls.getInstance().BASE_DOMAIN.lastIndexOf("/"))+api;
        }else {
            url=AppUrls.getInstance().BASE_DOMAIN+api;
        }
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), url), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!((Activity)mContext).isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<Object> object = GsonParser.getInstance().parseToObj(result, Object.class);
                    if (object != null && object.status == BaseObject.STATUS_OK) {
                        ((AccountActivity)mContext).onActivityResult(100,Activity.RESULT_OK,null);
                    } else {
                        AppUtil.showToast(mContext,object==null?"删除失败":object.msg);
                    }
                }
            }
        });

    }

    public static class ViewHolderImpl extends ViewHolder {
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_bouns)
        TextView tvBouns;
        @BindView(R.id.tv_customer)
        TextView tvCustomer;
        @BindView(R.id.tv_channel)
        TextView tvChannel;
        @BindView(R.id.btn_operation)
        TextView btnOperation;

        public ViewHolderImpl(View view) {
            super(view);
        }
    }
}
