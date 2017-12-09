package com.paulz.carinsurance.model;

import java.util.List;

/**
 * Created by pualbeben on 17/6/17.
 */

public class InsureDetail {
    public String insurance_company_id;
    public String insurance_company_name;
    public String insurance_company_img;
    public String insurance_company_code;
    public String insurance_xunjia_id;
    public String insurance_xunjia_amout;
    public String insurance_xunjia_bamount;
    public String insurance_xunjia_taxamount;
    public String insurance_xunjia_camount;
    public String insurance_xunjia_jiangjin;
    public InsureData insdata;
    public String[][] carerlist;

    public List<InsureCate> valuelist;
    public class InsureData{

        public String insurance_id;
        public String insurance_name;
        public String insurance_tel;
        public String insurance_sfz;
        public String insurance_sfztype;
        public String insurance_carnumber;
        public String insurance_modelid;
        public String insurance_provsid;
        public String insurance_cityid;
        public String insurance_distsid;
        public int insurance_compulsoryins;
        public long insurance_compulsoryinsdate;
        public int insurance_businessins;
        public long insurance_businessinsdate;
        public long insurance_createtime;
        public long insurance_updatetime;
        public String insurance_lock;
        public String insurance_carmodel_id;
        public String insurance_carmodel_name;
        public String insurance_carmodel_remark;
        public String insurance_carmodel_seat;
        public String insurance_carmodel_vin;
        public String insurance_carmodel_en;
        public String insurance_carmodel_teyue;
        public long insurance_carmodel_regdate;

    }

    public class InsureCate{
        public String insurance_values_id;
        public String insurance_values_typename;
        public String insurance_values_name;
        public String insurance_values_val;
        public String insurance_values_ynbjmp;
        public String money;

    }
}
