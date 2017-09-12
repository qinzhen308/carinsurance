package com.paulz.carinsurance.common;

import android.app.Activity;
import android.content.Context;

import com.paulz.carinsurance.httputil.HttpRequester;
import com.paulz.carinsurance.model.UserInfo;
import com.paulz.carinsurance.parser.gson.BaseObject;
import com.paulz.carinsurance.parser.gson.GsonParser;
import com.paulz.carinsurance.ui.MainActivity;
import com.paulz.carinsurance.utils.AppUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;

import java.util.HashMap;
import java.util.Iterator;

public class AppStatic {
	private static Context mContext;
	public static String ACCESS_TOKEN = "token";
	public static String WX_APPID = "wx87618c7611c51777";
	public static String WX_secret = "d9a7e4d6ffe1f10277f5259d6c5bc883";
	public static String Sina_APPKEY = "2889898484";
	public static String Sina_secret = "cbbc5863ce1e178a4f4ba285911c6c14";
	public static String QQ_APPID = "1105355193";
	public static String QQ_APPKEY = "dD2H3NMoun6hqJP4";
	public static final String OCRkey = "VkgyoDAPmwfJmRJNJ4h6DR";
	public static final String OCRSecret = "bcd80e2dc08d453091d5768d5a882b54";
	private static AppStatic mInstance;
	private UserInfo mUserInfo;
	public boolean isLogin = false;// 用户是否登录
	public String captcha = "";
	public static boolean isRefrshHome=false;
	private HashMap<String, Activity> mActivityMap = new HashMap<String, Activity>();

	public static AppStatic getInstance() {
		if (mInstance == null) {
			mInstance = new AppStatic();
		}
		return mInstance;
	}

	public void setmUserInfo(UserInfo mUserInfo) {
		this.mUserInfo = mUserInfo;
	}

	public UserInfo getmUserInfo() {
		return mUserInfo;
	}

	public void init(Context context) {
		mContext = context;
	}
	
	public void clearLoginStatus(){
		if(isLogin){
			clearUser();
			mUserInfo=null;
			isLogin=false;
			PreferencesUtils.putBoolean("isLogin",false);
			//关闭所有页面，重新打开登录页面
			Iterator<Activity> it = mActivityMap.values().iterator();
			LogUtil.d("clearLoginStatus---mActivityMap size---"+mActivityMap.size());
			if (it.hasNext()) {
				LogUtil.d("clearLoginStatus---重新登录---");
				Activity a=it.next();
				MainActivity.invoke(a,true);
			}
		}
	}

	public void justClearLoginStatus(){
		if(isLogin){
			clearUser();
			mUserInfo=null;
			isLogin=false;
			PreferencesUtils.putBoolean("isLogin",false);
		}
	}

	/**
	 * 添加
	 *
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		mActivityMap.put(activity.getClass().getSimpleName(), activity);
	}

	/**
	 * 删除
	 * 
	 * @param activity
	 */
	public void removeActivity(Activity activity) {
		mActivityMap.remove(activity.getClass().getSimpleName());
	}

	/**
	 * 退出
	 */
	public void exit() {
		Iterator<Activity> it = mActivityMap.values().iterator();
		while (it.hasNext()) {
			it.next().finish();
		}
		System.exit(0);
	}

	/**
	 * 清除
	 */
	public void clearActivityMap() {
		mActivityMap.clear();
	}

	public HashMap<String, Activity> getActivityMap() {
		return mActivityMap;
	}

	public void clearUser() {
		PreferencesUtils.remove("member_id");
		PreferencesUtils.remove("member_lastlogintime");
		PreferencesUtils.remove("member_username");
		PreferencesUtils.remove("member_store");
		PreferencesUtils.remove("member_nickname");
		PreferencesUtils.remove("member_realname");

	}

	/**
	 * 保存用户数据
	 * 
	 * @param user
	 */
	public void saveUser(UserInfo user) {
		clearUser();
		putData("member_id", user.member_id);
		putData("member_lastlogintime", user.member_lastlogintime);
		putData("member_username", user.member_username);
		putData("member_nickname", user.member_nickname);
		putData("member_store", user.member_store);
		PreferencesUtils.putInteger("member_realname", user.member_realname);
	}


	private void putData(String key, String value) {
		if (value == null) {
			value = "";
		}
		PreferencesUtils.putString(key, value);
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public UserInfo getUser() {
		UserInfo user = new UserInfo();
		user.member_id=(PreferencesUtils.getString("member_id"));
		user.member_lastlogintime=(PreferencesUtils.getString("member_lastlogintime"));
		user.member_username=(PreferencesUtils.getString("member_username"));
		user.member_store=(PreferencesUtils.getString("member_store"));
		user.member_nickname=(PreferencesUtils.getString("member_nickname"));
		user.member_realname=(PreferencesUtils.getInteger("member_realname"));
		return user;
	}


}
