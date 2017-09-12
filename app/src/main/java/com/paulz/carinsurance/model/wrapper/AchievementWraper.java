package com.paulz.carinsurance.model.wrapper;

import com.paulz.carinsurance.model.Achievement;
import com.paulz.carinsurance.model.BounsRecord;

import java.util.ArrayList;
import java.util.List;

public class AchievementWraper implements BeanWraper<Achievement>{
	
	/**
	 * 
	 */
    public List<Achievement> list; //  当前页面所有的beans  order

    public int page_count=Integer.MAX_VALUE;//页码总数
    public String amount;
    public String bamount;
    public String camount;


    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<Achievement> getItems(){
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
