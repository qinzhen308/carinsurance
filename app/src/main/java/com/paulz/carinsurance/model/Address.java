package com.paulz.carinsurance.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pualbeben on 17/6/10.
 */

public class Address implements Serializable{
    public String id;
    public String tel;
    public String name;
    public String addr;
    @SerializedName("default")
    public int defaul;
    public Cities city;


    public class Cities implements Serializable{
        public String[] id;
        public String[] name;
    }


}
