package com.paulz.carinsurance.model.wrapper;

import com.paulz.carinsurance.model.SalesmanDetail;
import com.paulz.carinsurance.model.Team;

import java.util.ArrayList;
import java.util.List;

public class SalesmanDetailWraper implements BeanWraper<SalesmanDetail>{
	
	/**
	 * 
	 */
    public List<SalesmanDetail> list; //  当前页面所有的beans  order

    public int total=1;//页码总数

    public String member_id;
    public String member_username;
    public String member_tel;
    public String member_avatar;
    public String name;
    public String recomname;
    public String recomtotal;
    public String renzheng;
    public String store_name;
    public String createtime;
    public Rate rate;


    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<SalesmanDetail> getItems(){
    	if(list==null){
            list=new ArrayList<>();
    	}
    	return list;
    }
    
    @Override
    public int getTotalPage(){
    	return total;
    }

    public class Rate{
        public String crate;
        public String[] brate;
    }
    
}
