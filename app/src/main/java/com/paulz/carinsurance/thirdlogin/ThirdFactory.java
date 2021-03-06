package com.paulz.carinsurance.thirdlogin;

import com.paulz.carinsurance.HApplication;
import com.paulz.carinsurance.thirdlogin.qq.QQLogin;
import com.paulz.carinsurance.thirdlogin.wechat.WechatLogin;

public class ThirdFactory {
	public static final int TYPE_QQ=1;
	public static final int TYPE_WECHAT=2;
	private static IThirdLogin qqInstance;
	private static IThirdLogin wxInstance;
	

	public synchronized static IThirdLogin getInstance(int type){
		if(type==TYPE_QQ){
			if(qqInstance==null){
				qqInstance=new QQLogin(HApplication.getInstance());
			}
			return qqInstance;
		}else if(type==TYPE_WECHAT){
			if(wxInstance==null){
				wxInstance=new WechatLogin(HApplication.getInstance());
			}
			return wxInstance;
		}
		return null;
	}
}
