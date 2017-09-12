package com.paulz.carinsurance.adapter;

import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by pualbeben on 17/5/21.
 */

public class ViewHolder{
    public View root;
    public ViewHolder(View view){
        root=view;
        ButterKnife.bind(this,root);
    }
}