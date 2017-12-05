package com.paulz.carinsurance.model.wrapper;

import com.paulz.carinsurance.model.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamWraper implements BeanWraper<Team>{
	
	/**
	 * 
	 */
    public List<Team> list; //  当前页面所有的beans  order

    public int page_count=Integer.MAX_VALUE;//页码总数


    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<Team> getItems(){
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
