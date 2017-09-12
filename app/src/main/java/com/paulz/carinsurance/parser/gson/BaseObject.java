package com.paulz.carinsurance.parser.gson;

import java.io.Serializable;

public class BaseObject<T> implements Serializable {
	
	public final static int STATUS_OK=1;
	public final static int STATUS_FAILED=0;
	public final static String LOGIN_EXPIRE="0";//用户登录过期，需要清除登录状态或者重新登录  和islogin比对

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int status;
	public String islogin;
	public String msg;
//	public String token;
	public T data;
}
