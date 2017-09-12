package com.paulz.carinsurance.model.wrapper;

import com.paulz.carinsurance.model.Customer;
import com.paulz.carinsurance.model.Order;

import java.util.ArrayList;
import java.util.List;

public class CustomerWraper implements BeanWraper<Customer>{
	
	/**
	 * 
	 */
    public List<Customer> list; //  当前页面所有的beans  order

    public int page_count=Integer.MAX_VALUE;//页码总数


    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<Customer> getItems(){
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
