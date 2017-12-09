package com.paulz.carinsurance.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.core.framework.develop.LogUtil;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.paulz.carinsurance.HApplication;
import com.paulz.carinsurance.common.GlobeFlags;
import com.paulz.carinsurance.utils.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * Created by paulz on 2016/6/13.
 */
public class JPushMsgReceiver extends JPushMessageReceiver{

    private static List<MsgListener> msgObservers=new ArrayList<>();


    public static void addMsgListener(MsgListener msgListener){
        msgObservers.add(msgListener);
    }
    public static void removeMsgListener(MsgListener msgListener){
        msgObservers.remove(msgListener);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    public static void clearListeners(){
        msgObservers.clear();
    }

    public interface MsgListener{
        public void onUpdateCount(Object... args);
    }

    private void jumpTo(Context context,Intent intent){
        Intent tagIntent=null;
        Bundle bundle = intent.getExtras();
        String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String pushId= bundle.getString(JPushInterface.EXTRA_MSG_ID);
        LogUtil.d("jpush---receiver---open---extra="+message);
        JSONObject msgJson= null;
        try {
            msgJson = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* if(msgJson!=null){
            int type=msgJson.optInt("ptype");
            String link_id=msgJson.optString("link_id");
            if(type==1||type==2){
                tagIntent=new Intent(context,BBSPostsDetailActivity.class);
                tagIntent.putExtra(GlobeFlags.FLAG_PUSH_ID,pushId);
                tagIntent.putExtra("posts_id",link_id);
                tagIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }else if(type==3){
                tagIntent=new Intent(context,SupporterActivity.class);
                tagIntent.putExtra(GlobeFlags.FLAG_PUSH_ID,pushId);
                tagIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }else {
                tagIntent=new Intent(context,MainActivity.class);
                tagIntent.putExtra(GlobeFlags.FLAG_PUSH_ID,pushId);
                tagIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }else {
            tagIntent=new Intent(context,MainActivity.class);
            tagIntent.putExtra(GlobeFlags.FLAG_PUSH_ID,pushId);
            tagIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }*/
        context.startActivity(tagIntent);
    }


}
