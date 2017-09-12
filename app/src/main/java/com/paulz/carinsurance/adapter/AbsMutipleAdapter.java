package com.paulz.carinsurance.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.paulz.carinsurance.R;
import com.paulz.carinsurance.controller.AbstractListAdapter;

import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/5/21.
 */

public abstract class AbsMutipleAdapter<T,H extends ViewHolder> extends AbstractListAdapter<T>{
    public AbsMutipleAdapter(Activity context) {
        super(context);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        H holder=null;
        int type=getItemViewType(i);
        if(view==null){
            holder=onCreateViewHolder(i,type,viewGroup);
            view=holder.root;
            view.setTag(R.id.list_view_type,type);
            view.setTag(R.id.list_view_holder,holder);
        }else {
            if(type!=((int)view.getTag(R.id.list_view_type))){
                holder=onCreateViewHolder(i,type,viewGroup);
                view=holder.root;
                view.setTag(R.id.list_view_type,type);
                view.setTag(R.id.list_view_holder,holder);
            }else {
                holder=(H)view.getTag(R.id.list_view_holder);
            }
        }
        onBindViewHolder(i,holder);
        return view;
    }



    public abstract H onCreateViewHolder(int position,int viewType,ViewGroup parent);
    public abstract void onBindViewHolder(int position,H holder);

//    public static class ViewHolder{
//        public View root;
//        public ViewHolder(View view){
//            root=view;
//            ButterKnife.bind(this,root);
//        }
//    }
}
