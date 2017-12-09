package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.CompanyResult;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.ui.InsureCompanyPriceActivity;
import com.paulz.carinsurance.ui.InsureDetailActivity;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.Image13Loader;
import com.paulz.carinsurance.view.CommonDialog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * Created by pualbeben on 17/5/21.
 */

public class InsureCompanyPriceAdapter extends AbsMutipleAdapter<CompanyResult, InsureCompanyPriceAdapter.CustomerHolder> {



    public InsureCompanyPriceAdapter(Activity context) {
        super(context);
    }


    @Override
    public CustomerHolder onCreateViewHolder(int position, int viewType, ViewGroup parent) {
        return new CustomerHolder(mInflater.inflate(R.layout.item_insure_company_price, null));
    }

    @Override
    public void onBindViewHolder(int position, final CustomerHolder holder) {
        final CompanyResult company = (CompanyResult) getItem(position);


        holder.tvName.setText(company.insurance_company_name);
        Image13Loader.getInstance().loadImage(AppUrls.getInstance().IMG_INSCOMPANY + company.insurance_company_img, holder.ivIcon);

        if(company.isFakeResult){
            holder.tvNoPrice.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.requestTip.setVisibility(View.VISIBLE);
            holder.layoutNormal.setVisibility(View.GONE);
            holder.root.setOnClickListener(null);
            getPrice(position,company);
            return;
        }

        holder.requestTip.setVisibility(View.GONE);

        if(company.isReasonShown){
            holder.layoutReason.setVisibility(View.VISIBLE);
        }else {
            holder.layoutReason.setVisibility(View.GONE);
        }

        if(company.isLoading){
            holder.tvNoPrice.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
        }else {
            holder.progressBar.setVisibility(View.GONE);
            holder.tvNoPrice.setVisibility(View.VISIBLE);
        }


        if (company.isError()) {
            holder.layoutNormal.setVisibility(View.GONE);
            holder.tvNoPrice.setVisibility(View.VISIBLE);
            holder.tvReason.setText(company.error);
            holder.tvNoPrice.setText("暂无报价，点击查看原因");

        } else if (company.verify != null) {
            holder.layoutNormal.setVisibility(View.GONE);
            holder.tvNoPrice.setVisibility(View.VISIBLE);
            if(AppUtil.isNull(company.verifyError)){
                holder.tvNoPrice.setText("点击获取验证码");
            }else{
                holder.tvNoPrice.setText(company.verifyError);
            }

            if(company.isLoading){
            }else {
            }

        } else {
            holder.tvBouns.setText("￥" + company.insurance_xunjia_jiangjin);
            holder.tvPrice.setText("￥" + company.insurance_xunjia_amout);
            if (InsureCompanyPriceActivity.isShow) {
                holder.tvBouns.setVisibility(View.VISIBLE);
            } else {
                holder.tvBouns.setVisibility(View.GONE);
            }
            holder.layoutNormal.setVisibility(View.VISIBLE);
            holder.tvNoPrice.setVisibility(View.GONE);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (company.isError()) {
                    company.isReasonShown=!company.isReasonShown;
                    notifyDataSetChanged();
                } else if (company.verify != null) {
                    showVerifyDialog(company);
                } else {
                    InsureDetailActivity.invoke(mContext, company.insurance_xunjia_id);
                }

            }
        });
    }


    private void getPrice(final int position,final CompanyResult bean){

        if(bean.isLoading)return;
        bean.isLoading=true;
        ParamBuilder params=new ParamBuilder();
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("company",bean.insurance_company_id);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_INSURE_PRICE_SINGLE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<CompanyResult> object= GsonParser.getInstance().parseToObj(result,CompanyResult.class);
                    if(object!=null&&object.status==BaseObject.STATUS_OK&&object.data!=null){
                        mList.set(position,object.data);
                        notifyDataSetChanged();
                    }else {

                    }
                }
            }
        },requester,DESUtil.SECRET_DES);

    }



    private void showVerifyDialog(final CompanyResult company) {
        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.setTitle("输入验证码");
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_input_verify, null);
        final EditText et1 = (EditText) view.findViewById(R.id.et_captcha1);
        final EditText et2 = (EditText) view.findViewById(R.id.et_captcha2);
        final ImageView iv1 = (ImageView) view.findViewById(R.id.iv_captcha1);
        final ImageView iv2 = (ImageView) view.findViewById(R.id.iv_captcha2);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        dialog.setCustomView(view);
        dialog.setLefBtnText("提交");
        dialog.setRightBtnText("刷新");
        dialog.setAutoDismiss(false);
        dialog.setInsideClickCanceled(false);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                String code1=et1.getText().toString().trim();
                String code2=et2.getText().toString().trim();
                if(code1.length()==0||code2.length()==0){
                    AppUtil.showToast(mContext,"请输入验证码");
                    return;
                }
                company.isLoading=true;
                notifyDataSetChanged();
                dialog.dismiss();
                submitVerify(company,code1,code2);
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                refreshVerify(company,iv1,iv2,progressBar);

            }
        });
        Glide.with(mContext).load(getCaptchaUrl(company.verify.p3)).into(iv1);
        Glide.with(mContext).load(getCaptchaUrl(company.verify.p5)).into(iv2);

        dialog.show();
    }


    private String getCaptchaUrl(String content) {
        return content.startsWith("./") ? content.replace("./", AppUrls.getInstance().BASE_DOMAIN+"/") : (AppUrls.getInstance().BASE_DOMAIN + content);
    }


    private void submitVerify(final CompanyResult company, String code1, String code2){

        final HttpRequester requester=new HttpRequester();
        ParamBuilder params=new ParamBuilder();
        requester.getParams().put("logid",company.verify.p1);
        requester.getParams().put("company",company.insurance_company_code);
        requester.getParams().put("code1",code1);
        requester.getParams().put("code2",code2);

        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SUBMIT_PRICE_VERIFY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                company.isLoading=false;
                BaseObject<PriceData> obj=GsonParser.getInstance().parseToObj(result,PriceData.class);
                if(obj!=null&&obj.status==BaseObject.STATUS_OK&&obj.data!=null){
                    company.insurance_xunjia_amout=obj.data.amout;
                    company.insurance_xunjia_jiangjin=obj.data.jiangjin;
                    company.insurance_xunjia_id=obj.data.id;
                    company.verify=null;
                    company.verifyError=null;
//                    company.result=false;
//                    company.error=null;
                }else {
                    company.verifyError="验证码输入错误，点击重新输入";
                }

                notifyDataSetChanged();
            }
        },requester, DESUtil.SECRET_DES);

    }

    private void refreshVerify(final CompanyResult company, final ImageView iv1, final ImageView iv2, final ProgressBar progressBar){

        progressBar.setVisibility(View.VISIBLE);
        final HttpRequester requester=new HttpRequester();
        ParamBuilder params=new ParamBuilder();
        requester.getParams().put("company",company.insurance_company_id);
        requester.getParams().put("companycode",company.insurance_company_code);
        NetworkWorker.getInstance().post(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_REFRESH_PRICE_VERIFY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                progressBar.setVisibility(View.GONE);
                BaseObject<RefreshCode> obj=GsonParser.getInstance().parseToObj(result,RefreshCode.class);
                if(obj!=null&&obj.status==BaseObject.STATUS_OK&&obj.data!=null){
                    company.verify.p3=obj.data.p1;
                    company.verify.p5=obj.data.p2;
                    Glide.with(mContext).load(getCaptchaUrl(company.verify.p3)).into(iv1);
                    Glide.with(mContext).load(getCaptchaUrl(company.verify.p5)).into(iv2);
                }

            }
        },requester, DESUtil.SECRET_DES);

    }





    public static class CustomerHolder extends ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_bouns)
        TextView tvBouns;
        @BindView(R.id.layout_normal)
        LinearLayout layoutNormal;
        @BindView(R.id.tv_no_price)
        TextView tvNoPrice;
        @BindView(R.id.tv_reason)
        TextView tvReason;
        @BindView(R.id.layout_reason)
        LinearLayout layoutReason;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;
        @BindView(R.id.request_tip)
        TextView requestTip;

        public CustomerHolder(View view) {
            super(view);
        }
    }


    public static class PriceData{

        public String amout;
        public String jiangjin;
        public String id;
    }
    public static class RefreshCode{

        public String xunjiaid;
        public String p1;
        public String p2;
    }


}
