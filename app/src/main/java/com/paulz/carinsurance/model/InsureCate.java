package com.paulz.carinsurance.model;

import java.util.List;

/**
 * Created by pualbeben on 17/6/16.
 */

public class InsureCate {
    public String insurance_types_id;
    public String insurance_types_name;
    public String insurance_types_default;
    public int insurance_types_bjmpdefault;
    public int insurance_types_hasbjmp;
    public String insurance_types_code;
    public String insurance_types_sort;
    public List<Option> option;

    public class Option{
        public String insurance_valuetype_id;
        public String insurance_valuetype_name;
        public String insurance_valuetype_value;
        public int insurance_valuetype_yndefault;


    }
}
