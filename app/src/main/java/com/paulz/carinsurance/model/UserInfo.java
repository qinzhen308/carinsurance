package com.paulz.carinsurance.model;

import java.io.Serializable;

/**
 * 用户实体
 * 
 * @author paul
 * 
 */
public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String member_id;// 是否登录
	
	public String member_lastlogintime;//最后登录时间
	public String member_store;
	public String member_username;
	public String member_nickname;


	public String member_money;
	public String member_avatar;
	public int member_realname;//大于0为已认证

	public String teammanage;//机构/团队/个人主页按钮名称
	public int teamtype;//按钮名称/按钮类型标识分别有: 机构管理/2,团队管理/1,我的团队/0








}
