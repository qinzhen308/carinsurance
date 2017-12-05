package com.paulz.carinsurance.model.wrapper;

import com.paulz.carinsurance.model.Salesman;
import com.paulz.carinsurance.model.SalesmanDetail;

import java.util.ArrayList;
import java.util.List;

public class SalesmanWraper implements BeanWraper<Salesman>{
	
	/**
	 * 
	 */
    public List<Salesman> list; //  当前页面所有的beans  order

    public int total=1;//页码总数




    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<Salesman> getItems(){
    	if(list==null){
            list=new ArrayList<>();
    	}
    	return list;
    }
    
    @Override
    public int getTotalPage(){
    	return total;
    }
    
}
