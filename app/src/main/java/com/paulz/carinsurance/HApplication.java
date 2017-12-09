package com.paulz.carinsurance;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.core.framework.app.devInfo.DeviceInfo;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DESUtil;
import com.paulz.carinsurance.common.APIUtil;
import com.paulz.carinsurance.common.AppStatic;
import com.paulz.carinsurance.common.AppUrls;
import com.paulz.carinsurance.httputil.ParamBuilder;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.utils.AppUtil;
import com.paulz.carinsurance.utils.Image13Loader;
import com.core.framework.app.MyApplication;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.app.oSinfo.SuNetEvn;
import com.core.framework.store.DB.beans.Preferences;
import com.core.framework.store.sharePer.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;


public class HApplication extends MyApplication {
	public String token="";
	public String session_id="";
	private static HApplication instance;

	public String push_regestion_id;//推送服务唯一表示




	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance=this;
	}
	
	public static HApplication getInstance(){
		return instance;
	}


	public void saveToken(String token){
		this.token=token;
		PreferencesUtils.putString(AppStatic.ACCESS_TOKEN, token);
	}


	@Override
	public void checkService() {
	}

	@Override
	public void doBackTransaction() {
		ScreenUtil.setContextDisplay(this);
		initDatabase();
	}

	@Override
	public void doBusyTransaction() {
		Image13Loader.getInstance().resume();
		AppStatic.getInstance().isLogin = PreferencesUtils.getBoolean("isLogin");
		token = PreferencesUtils.getString(AppStatic.ACCESS_TOKEN);
		AppStatic.getInstance().setmUserInfo(AppStatic.getInstance().getUser());
		SuNetEvn.getInstance();
		session_id=PreferencesUtils.getString("session_id");

		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
		if (TextUtils.isEmpty(PreferencesUtils.getString("registerId"))) {
			AppStatic.registerId = JPushInterface
					.getRegistrationID(getApplicationContext());
			PreferencesUtils.putString("registerId", AppStatic.registerId);
		} else {
			AppStatic.registerId = PreferencesUtils.getString("registerId");
		}
	}

	private void initDatabase() {
		Preferences.getInstance();
	}



	public boolean isAppOnForeground() {
		ActivityManager mActivityManager = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		String mPackageName = getPackageName();
		List<ActivityManager.RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (mPackageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}


	public void loadToken(){
		ParamBuilder params=new ParamBuilder();
		params.append("sessionid",session_id);

		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_GET_TOKEN), new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				JSONObject object= null;
				try {
					object = new JSONObject(result);
					if(object.optInt("status")==1){
						String tk=object.optJSONObject("data").optString("token");
						token=DESUtil.decrypt(tk,DESUtil.SECRET_DES_KEY);
						LogUtil.d("---token="+token);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}


	public void refreshSessionid(){
		session_id= UUID.randomUUID().toString();
	}



}
