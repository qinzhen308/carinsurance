package com.paulz.carinsurance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.paulz.carinsurance.R;
import com.paulz.carinsurance.base.BaseFragment;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.model.MsgUnread;
import com.paulz.carinsurance.model.UserInfo;
import com.paulz.carinsurance.model.wrapper.MsgWraper;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.ui.AccountActivity;
import com.paulz.carinsurance.ui.AppointListActivity;
import com.paulz.carinsurance.ui.CommonWebActivity;
import com.paulz.carinsurance.ui.MsgCenterActivity;
import com.paulz.carinsurance.ui.MyAchievementActivity;
import com.paulz.carinsurance.ui.MyBounsActivity;
import com.paulz.carinsurance.ui.MyOrganisationActivity;
import com.paulz.carinsurance.ui.NameVerifyActivity;
import com.paulz.carinsurance.ui.TeamInfoActivity;
import com.paulz.carinsurance.ui.UserInfoActivity;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.Image13Loader;
import com.paulz.carinsurance.view.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 首页--用户中心
 * Created by paulz on 2016/5/31.
 */
public class UserCenterFragment extends BaseFragment {


    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_order)
    TextView tvOrder;
    @BindView(R.id.tv_paying_order)
    TextView tvPayingOrder;
    @BindView(R.id.tv_payed_order)
    TextView tvPayedOrder;
    @BindView(R.id.cb_show_bouns)
    CheckBox cbShowBouns;
    @BindView(R.id.tv_bonus)
    TextView tvBonus;
    @BindView(R.id.layout_bonus)
    RelativeLayout layoutBonus;
    @BindView(R.id.layout_achievement)
    TextView layoutAchievement;
    @BindView(R.id.layout_server)
    TextView layoutServer;
    @BindView(R.id.layout_feedback)
    TextView layoutFeedback;
    @BindView(R.id.layout_more)
    TextView layoutMore;
    Unbinder unbinder;
    @BindView(R.id.tv_paying_count)
    TextView tvPayingCount;
    @BindView(R.id.tv_payed_count)
    TextView tvPayedCount;
    @BindView(R.id.layout_organization)
    TextView layoutOrganization;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;

    @Override
    public void heavyBuz() {


    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            loadMsgCount();
        }
    }

    private void loadMsgCount(){
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MSG_UNREAD), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<MsgUnread> obj= GsonParser.getInstance().parseToObj(result,MsgUnread.class);
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK){
                        if(obj.data.total>99){
                            tvMsgCount.setText("99+");
                            tvMsgCount.setVisibility(View.VISIBLE);
                        }else if(obj.data.total>0){
                            tvMsgCount.setText(""+obj.data.total);
                            tvMsgCount.setVisibility(View.VISIBLE);
                        }else {
                            tvMsgCount.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

    }


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_user_center, false);
        unbinder = ButterKnife.bind(this, baseLayout);
        return baseLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
//        initData();
    }

    private void setViews(PageData data) {
        UserInfo user = data.data;
        AppStatic.getInstance().setmUserInfo(data.data);
        AppStatic.getInstance().saveUser(data.data);
        if (AppStatic.getInstance().getmUserInfo() != null) {
            Image13Loader.getInstance().loadImage(AppUrls.getInstance().BASE_IMG_URL + user.member_avatar, ivAvatar, R.drawable.user2);
//            Image13Loader.getInstance().loadNoStubImageFade(user.member_avatar,ivAvatar);
            tvName.setText(user.member_nickname);
            tvStatus.setText(user.member_realname > 0 ? "已认证" : "尚未认证，去认证");
            if(user.member_realname >0){
                tvStatus.setTextColor(getResources().getColor(R.color.green_light));
            }else {
                tvStatus.setTextColor(getResources().getColor(R.color.text_grey_french1));
            }
            tvBonus.setText("￥" + user.member_money);
            layoutOrganization.setText(user.teammanage);
        }
        handleCount(data.paidcount,tvPayedCount);
        handleCount(data.waitpaycount,tvPayingCount);

        //获取实名认真信息
        loadNameVerity();
    }

    private void handleCount(int count ,TextView v){
        if(count<0){
            v.setVisibility(View.GONE);
        }else if(count<10){
            v.setText(""+count);
            v.setVisibility(View.VISIBLE);
        }else {
            v.setText("9+");
            v.setVisibility(View.VISIBLE);
        }
    }


    private void loadNameVerity() {

        ParamBuilder params = new ParamBuilder();
        params.append("id", ""+AppStatic.getInstance().getUser().member_realname);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_NAME_VERIFY), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (status == 200) {
                    BaseObject<NameVerifyActivity.PageInfo> object = GsonParser.getInstance().parseToObj(result, NameVerifyActivity.PageInfo.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        setNameVerity(object.data);
                    }
                }
            }
        });

    }

    private void setNameVerity(NameVerifyActivity.PageInfo data) {
        if (data == null) {
            return;
        }
        if(AppUtil.isNull(data.authenticate_status)){
            tvStatus.setTextColor(getResources().getColor(R.color.text_grey_french1));
            tvStatus.setText("尚未认证，去认证");
        }else if (data.authenticate_status.equals("2")) {
            tvStatus.setText("已认证");
            tvStatus.setTextColor(getResources().getColor(R.color.green_light));
        } else if (data.authenticate_status.equals("1")) {
            tvStatus.setText("认证中");
            tvStatus.setTextColor(getResources().getColor(R.color.base_yellow));
        } else if(data.authenticate_status.equals("0")){
            tvStatus.setText("认证失败");
            tvStatus.setTextColor(getResources().getColor(R.color.base_red));
        }

    }



    private void initData() {
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params = new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_USER_INFO), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if (!getActivity().isFinishing()) DialogUtil.dismissDialog(lodDialog);
                if (status == 200) {
                    BaseObject<PageData> object = GsonParser.getInstance().parseToObj(result, PageData.class);
                    if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
                        setViews(object.data);
                    } else {
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        loadMsgCount();
    }

    @OnClick({R.id.layout_more, R.id.layout_bonus, R.id.layout_achievement, R.id.layout_server,
            R.id.layout_feedback, R.id.tv_order, R.id.tv_paying_order, R.id.tv_payed_order,
            R.id.iv_avatar, R.id.tv_status,R.id.layout_organization,R.id.tv_msg_count,R.id.btn_msg})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_more:
                CommonWebActivity.invoke(getActivity(),APIUtil.parseGetUrlHasMethod(new ParamBuilder().getParamList(), AppUrls.getInstance().BASE_DOMAIN+"/index.php?s=/Api/tools/index"),"更多工具");
                break;
            case R.id.layout_bonus:
                MyBounsActivity.invoke(getActivity());
                break;
            case R.id.layout_achievement:
                MyAchievementActivity.invoke(getActivity());
                break;
            case R.id.layout_server:
                CommonWebActivity.invoke(getActivity(),APIUtil.parseGetUrlHasMethod(new ParamBuilder().getParamList(), AppUrls.getInstance().BASE_DOMAIN+"/index.php?s=/Api/tools/customerservice"),"联系客服");
                break;
            case R.id.layout_feedback:
                CommonWebActivity.invoke(getActivity(),APIUtil.parseGetUrlHasMethod(new ParamBuilder().getParamList(), AppUrls.getInstance().BASE_DOMAIN+"/index.php?s=/Api/tools/feedback"),"我要反馈");
                break;
            case R.id.tv_order:
                AccountActivity.invoke(getActivity(), 0);
                break;
            case R.id.tv_paying_order:
                AccountActivity.invoke(getActivity(), 2);
                break;
            case R.id.tv_payed_order:
                AccountActivity.invoke(getActivity(), 3);
                break;
            case R.id.iv_avatar:
                UserInfoActivity.invoke(getActivity());
                break;
            case R.id.tv_status:
                NameVerifyActivity.invoke(getActivity());
                break;
            case R.id.layout_organization:
                if(AppStatic.getInstance().getmUserInfo().teamtype==0){
                    TeamInfoActivity.invoke(getActivity(),"");
                }else {
                    MyOrganisationActivity.invoke(getActivity());
                }
                break;
            case R.id.tv_msg_count:
            case R.id.btn_msg:
                MsgCenterActivity.invoke(getActivity(),0);
                break;
        }
    }

    @OnCheckedChanged(R.id.cb_show_bouns)
    public void showBouns(boolean isChecked) {
        if (isChecked) {
            tvBonus.setVisibility(View.VISIBLE);
        } else {
            tvBonus.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class PageData {
        UserInfo data;
        int paidcount;
        int waitpaycount;


    }
}
