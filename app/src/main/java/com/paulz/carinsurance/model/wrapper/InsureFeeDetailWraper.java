package com.paulz.carinsurance.model.wrapper;

import com.paulz.carinsurance.model.InsureDetailFee;
import com.paulz.carinsurance.model.InsureFee;

import java.util.ArrayList;
import java.util.List;

public class InsureFeeDetailWraper implements BeanWraper<InsureDetailFee>{
	
	/**
	 * 
	 */
    public List<InsureDetailFee> list; //  当前页面所有的beans  order

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
    public List<InsureDetailFee> getItems(){
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
