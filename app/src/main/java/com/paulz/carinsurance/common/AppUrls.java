package com.paulz.carinsurance.common;

public class AppUrls {
	private static AppUrls mUrls;

	public static AppUrls getInstance() {
		if (mUrls == null) {
			synchronized (AppUrls.class) {
				if (mUrls == null) {
					mUrls = new AppUrls();
				}
			}
		}
		return mUrls;
	}


	public String SESSION_ID="1231321";


	public String BASE_DOMAIN="http://120.27.222.234:58580/bxagency";//测试域名
//	public String BASE_DOMAIN="http://www.hgbaoxian.cn";//正式域名
	public String BASE_URL=BASE_DOMAIN+"/index.php?s=/Api/";//测试域名
	public String BASE_WAP_URL=BASE_DOMAIN+"/index.php?s=/Home/";//测试域名
	public String BASE_IMG_URL=BASE_DOMAIN+"/Res/Avatar/";//图片

	public String URL_THIRD_LOGIN_QQ = BASE_URL + "method=customer/customer_login_by_qq";//尝试qq三方登录
	public String URL_THIRD_LOGIN_WX = BASE_URL + "method=customer/login_by_weichat";//尝试微信三方登录


	public String URL_CHECK_UPDATE=BASE_URL+"Index/index";//升级


	public String URL_GET_CAPTCHA=BASE_URL+"Public/verify.html";
	public String URL_LOGIN=BASE_URL+"Public/login.html";//登录


	public String URL_ACTIVE_HOME=BASE_URL+"activity.php?act=list";//首页活动页

	public String URL_HOME_DATA=BASE_URL+"Index/index";//首页基本数据
	public String URL_TEMP_SUBMIT_CAR_INFO=BASE_URL+"Process/stepone.html";//立即报价
	public String URL_CAR_HISTORY=BASE_URL+"Index/carhistory.html";//车辆历史
	public String URL_ADD_CAR_PAGE=BASE_URL+"Process/customeraskprice.html";//报价页面数据
	public String URL_VIN_SEARCH_CAR=BASE_URL+"Index/vinchoice.html";//vin码搜索车型
	public String URL_BRAND_CHOICE_CAR=BASE_URL+"Index/brandchoice";//关键字搜索车型
	public String URL_VIN_SEARCH_CAR_IN_CUSTOMER=BASE_URL+"Customer/vinchoice.html";//vin码搜索车型
	public String URL_SEARCH_CAR_MODEL=BASE_URL+"Index/carmodellist.html";//根据关键字搜索车型
	public String URL_ADD_CAR_PAGE2=BASE_URL+"Index/carinfo.html";//
	public String URL_SUBMIT_CAR_INFO=BASE_URL+"Process/steptwo.html";//报价提交第二步
	public String URL_USER_INFO=BASE_URL+"Usercenter/index.html";//用户中心
	public String URL_MODIFY_NAME=BASE_URL+"Usercenter/editnickname.html";//修改昵称
	public String URL_MODIFY_AVATAR=BASE_URL+"Usercenter/editavatar.html";//上传头像
	public String URL_MY_ADDRESS=BASE_URL+"Usercenter/address.html";//地址管理列表
	public String URL_SET_DEFAULT_ADDRESS=BASE_URL+"Usercenter/setaddress.html";//设置默认
	public String URL_DELETE_ADDRESS=BASE_URL+"Usercenter/deladdress.html";//删除地址
	public String URL_ADD_ADDRESS=BASE_URL+"Usercenter/addaddress.html";//新增地址
	public String URL_EDIT_ADDRESS=BASE_URL+"Usercenter/editaddress.html";//编辑地址
	public String URL_BANK_LIST=BASE_URL+"Usercenter/bank.html";//银行卡
	public String URL_BANK_ADD_BANK=BASE_URL+"Usercenter/addbank.html";//添加银行卡页面信息,以及提交
	public String URL_BANK_DELETE=BASE_URL+"Usercenter/delbank.html";//删除银行卡
	public String URL_NAME_VERIFY=BASE_URL+"Usercenter/authenticate.html";//实名认真页面
	public String URL_NAME_VERIFY_SUBMIT=BASE_URL+"Usercenter/authenticate.html";//实名认真页面 提交
	public String URL_SAFE_PAGE_INFO=BASE_URL+"Usercenter/accuntsafe.html";//账户与安全页面数据
	public String URL_GET_CAPTCHA_COMMON=BASE_URL+"Captcha/modphone.html";//获取验证码
	public String URL_VERIFY_OR_MODIFY_TEL=BASE_URL+"Usercenter/modphone.html";//修改手机号和提交验证码
	public String URL_GET_CAPTCHA_SET_PHONE=BASE_URL+"Captcha/setphone.html";//设置新手机验证码
	public String URL_GET_CAPTCHA_WITHDRAW=BASE_URL+"Captcha/modpaypassword.html";//修改提现密码验证码
	public String URL_GET_CAPTCHA_LOGIN_PWD=BASE_URL+"Captcha/modloginpassword.html";//修改登录密码验证码
	public String URL_MODIFY_LOGIN_PWD=BASE_URL+"Usercenter/modloginpassword.html";//修改登录密码
	public String URL_MODIFY_WITHDRAW_PWD=BASE_URL+"Usercenter/modpaypassword.html";//修改提现密码

	public String URL_BOUNS=BASE_URL+"Order/backmoney.html";//奖金
	public String URL_BOUNS_RECORD=BASE_URL+"Order/incomedetail.html";//奖金明细
	public String URL_WITHDRAW_HISTORY=BASE_URL+"Order/presentation.html";//提现记录

	public String URL_ACHIEVEMENT=BASE_URL+"Usercenter/achievement.html";//业绩
	public String URL_ORDER_LIST=BASE_URL+"Order/indexlist.html";//保险订单列表
	public String URL_ORDER_DETAIL=BASE_URL+"Order/detail.html";//保险订单详情页
	public String URL_CUSTOMER_LIST=BASE_URL+"Customer/index.html";//客户列表
	public String URL_CUSTOMER_DETAIL=BASE_URL+"Customer/detail.html";//客户详情
	public String URL_CUSTOMER_ADD=BASE_URL+"Customer/add.html";//添加客户
	public String URL_CUSTOMER_EDIT=BASE_URL+"Customer/edit.html";//编辑客户
	public String URL_CUSTOMER_DELETE=BASE_URL+"Customer/delcus.html";//删除客户
	public String URL_CUSTOMER_CAR_DELETE=BASE_URL+"Customer/delcusmodel.html";//删除客户的车辆
	public String URL_CUSTOMER_CAR_ADD=BASE_URL+"Customer/addcar.html";//添加车辆
	public String URL_CUSTOMER_SEARCH_CAR_MODEL=BASE_URL+"Customer/carmodellist.html";//根据关键字搜索车型
	public String URL_CUSTOMER_BRAND_CHOICE_CAR=BASE_URL+"Customer/brandchoice";//关键字搜索车型
	public String URL_INSURE_INFO=BASE_URL+"Index/insuranceinfo.html";//投保选择页面
	public String URL_INSURE_CHOICECOMPANY=BASE_URL+"Process/choicecompany.html";//提交选择的保险公司
	public String URL_INSURE_SUBMIT=BASE_URL+"Process/stepthree.html";//提交投保信息
	public String URL_INSURE_COMPANY_LIST=BASE_URL+"Index/insurancecompany.html";//保险公司
	public String URL_INSURE_COMPANY_PRICE=BASE_URL+"Index/insuranceprice.html";//保险公司报价结果
	public String URL_INSURE_DETAIL=BASE_URL+"Index/insuranceconfirm.html";//保单详情页（确认页）
	public String URL_INSURE_DETAIL_SUBMIT=BASE_URL+"Process/cearteorder.html";//保单详情页（确认页）提交
	public String URL_RECEIVE_ADDRESS=BASE_URL+"Index/logistics.html";//配送信息
	public String URL_GET_AREA=BASE_URL+"process/getdistrict.html";//根据id查区域

	public String URL_SUBMIT_RECEIVE_ADDRESS=BASE_URL+"Process/getaddr.html";//提交配送信息
	public String URL_UNDERWRITING=BASE_URL+"Index/insuranceapprove.html";//核保


	public String URL_ORDER_BUTTON=BASE_URL+"Order/orderbutton.html";//订单支付流程   第一步
	public String URL_ORDER_INSURE_DETAIL=BASE_URL+"Order/insurance.html";//订单详情里的 保单详情
	public String WAP_ORDER_PAY=BASE_WAP_URL+"Order/pay.html";//支付
	public String URL_ORDER_PAY=BASE_URL+"Order/pay.html";//支付
	public String URL_WITHDRAW_APPLY=BASE_URL+"Order/withdrawals.html";//申请提现
	public String URL_GET_CAPTCHA_WITHDRAW_APPLY=BASE_URL+"Captcha/withdrawals.html";//申请提现页面获取验证码

	public String URL_LOGOUT=BASE_URL+"Public/logout.html";//退出登录


	public String URL_GET_TOKEN=BASE_URL+"Public/gettoken.html";//获取token

	public String URL_SUBMIT_PRICE_VERIFY=BASE_URL+"Process/checkxunjiav.html";//提交报价验证码
	public String URL_REFRESH_PRICE_VERIFY=BASE_URL+"Process/shuaxinxunjiav.html";//刷新报价验证码



	public String URL_WAP_FORGET_PASSWORD=BASE_WAP_URL+"Public/forget.html&form=andr";//忘记密码页面
	public String URL_WAP_REGISTION=BASE_WAP_URL+"Public/register.html&form=andr";//注册页面





	/*支付宝回调地址*/
	public String URL_ZFB_NOTIFY="http://jk.m1ju.com/app/respond_alipay.php";


	public String IMG_INSCOMPANY=BASE_DOMAIN+"/Uploads/company/";
	public String IMG_AVATAR=BASE_DOMAIN+"/Res/Avatar/";
	public String IMG_AUTHEIMG=BASE_DOMAIN+"/Uploads/authenticate/";
	public String IMG_PAYIMG=BASE_DOMAIN+"/Uploads/paylogo/";
	public String IMG_BANKIMG=BASE_DOMAIN+"/Uploads/bank/";


}
