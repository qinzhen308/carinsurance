package com.paulz.carinsurance.model;

import java.io.Serializable;

/**
 * Created by pualbeben on 17/6/17.
 */

public class Company implements Serializable{
    public String insurance_company_id;
    public String insurance_company_name;
    public String insurance_company_discount;
    public String insurance_company_img;
    public String insurance_company_code;

    public boolean isSelected;
}
