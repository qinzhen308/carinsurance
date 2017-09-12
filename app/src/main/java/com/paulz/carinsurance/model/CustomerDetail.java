package com.paulz.carinsurance.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pualbeben on 17/6/13.
 */

public class CustomerDetail implements Serializable{

    public ArrayList<CustomerCar> list;

    public String customer_id;
    public String customer_name;
    public String customer_remark;

    public String customer_tel;
    public String customer_sfz;
    public long customer_birthday;
    public String customer_createtime;
    public String customer_status;
}
