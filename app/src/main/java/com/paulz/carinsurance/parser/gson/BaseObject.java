package com.paulz.carinsurance.parser.gson;

import java.io.Serializable;

public class BaseObject<T> implements Serializable {
	
	public final static int STATUS_OK=1;
	public final static int STATUS_FAILED=0;
	public final static String LOGIN_EXPIRE="0";//用户登录过期，需要清除登录状态或者重新登录  和islogin比对


	public final static int ERROR_CODE_ACCOUNT=10002;//用户不存在或被禁用
	public final static int ERROR_CODE_PWD=10003;//密码错误
	public final static int ERROR_CODE_CAPTCHA=10004;//验证码错误


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int status;
	public int errorcode;
	public String islogin;
	public String msg;
//	public String token;
	public T data;
}
