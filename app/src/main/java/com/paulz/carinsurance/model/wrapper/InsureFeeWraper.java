package com.paulz.carinsurance.model.wrapper;

import com.paulz.carinsurance.model.InsureFee;
import com.paulz.carinsurance.model.Team;

import java.util.ArrayList;
import java.util.List;

public class InsureFeeWraper implements BeanWraper<InsureFee>{
	
	/**
	 * 
	 */
    public List<InsureFee> list; //  当前页面所有的beans  order

    public int page_count=Integer.MAX_VALUE;//页码总数

    public String ains;
    public String bins;
    public String cins;
    public int total;



    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<InsureFee> getItems(){
    	if(list==null){
            list=new ArrayList<>();
    	}
    	return list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    }
    
}
