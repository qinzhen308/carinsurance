package com.paulz.carinsurance.model.wrapper;

import com.paulz.carinsurance.model.Appoint;
import com.paulz.carinsurance.model.Msg;

import java.util.ArrayList;
import java.util.List;

public class MsgWraper implements BeanWraper<Msg>{
	
	/**
	 * 
	 */
    public List<Msg> list; //  当前页面所有的beans  order

    public int page_count=Integer.MAX_VALUE;//页码总数

    public List<Category> category;


    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<Msg> getItems(){
    	if(list==null){
            list=new ArrayList<>();
    	}
    	return list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    }


    public class Category{
        public String id;
        public String title;
        public int total;
    }

}
