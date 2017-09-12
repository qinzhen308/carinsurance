package com.paulz.carinsurance.httputil;

import com.paulz.carinsurance.HApplication;
import com.paulz.carinsurance.common.AppUrls;

import java.util.HashMap;

public class HttpRequester extends com.core.framework.net.HttpRequester{
	
	public HttpRequester() {
		mParams = new HashMap<String, Object>();
//		mParams.put("token", HApplication.getInstance().token);
//		mParams.put("sb_platform", "android");
		mParams.put("sessionid", HApplication.getInstance().session_id);
	}
	
	
}
