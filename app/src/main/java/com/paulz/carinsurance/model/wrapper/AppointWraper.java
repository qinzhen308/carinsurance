package com.paulz.carinsurance.model.wrapper;

import com.paulz.carinsurance.model.Appoint;
import com.paulz.carinsurance.model.Company;
import com.paulz.carinsurance.model.Team;

import java.util.ArrayList;
import java.util.List;

public class AppointWraper implements BeanWraper<Appoint>{
	
	/**
	 * 
	 */
    public List<Appoint> list; //  当前页面所有的beans  order

    public int page_count=Integer.MAX_VALUE;//页码总数

    public List<AppointCompany> company;


    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<Appoint> getItems(){
    	if(list==null){
            list=new ArrayList<>();
    	}
    	return list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    }


    public class AppointCompany{
        public String id;
        public String title;
    }

}
