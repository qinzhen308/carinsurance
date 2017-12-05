package com.paulz.carinsurance.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable{

	public String id;
	public String order_sn;
	public String pay_sn;
	public String insurance_bsn;
	public String insurance_bsalesn;
	public String insurance_csn;
	public String mid;
	public String aid;
	public String sid;
	public String amount;
	public String backmoney;
	public String backmoneyres;
	public String rate;
	public int order_status;
	public String xunjiaid;
	public String companyid;
	public String insurance_company_id;
	public String insurance_company_name;
	public String insurance_company_discount;
	public String insurance_company_img;
	public String insurance_company_code;
	public String insurance_name;
	public String insurance_sfz;
	public String insurance_sfztype;
	public String insurance_carnumber;
	public String insurance_modelid;
	public String insurance_provsid;
	public String insurance_cityid;
	public long insurance_createtime;
	public String insurance_businessins;

	public List<ButtonModel> buttonlist;


	public class ButtonModel{
		public int type;
		public String title;
		public String confirm;
		public String apiuri;

	}

}
