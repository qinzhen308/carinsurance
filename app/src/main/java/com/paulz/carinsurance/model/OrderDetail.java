package com.paulz.carinsurance.model;

/**
 * Created by pualbeben on 17/6/12.
 */

public class OrderDetail {

    public String amount;
    public String insurance_carnumber;
    public String insurance_name;
    public int order_status;
    public String insurance_company_name;
    public String insapprove_time;
    public String insurance_company_img ;
    public String order_sn;
    public String insurance_xunjia_bamount;
    public String insurance_xunjia_camount;
    public int insurance_compulsoryins;
    public int insurance_businessins;
    public long insurance_compulsoryinsdate;
    public long insurance_businessinsdate;
    public String insurance_city;
    public Car car;
    public String insurance_sfz;
    public String shippingaddr;
    public String shippingtel;
    public String shippingname;
    public String tbname;
    public String tbsfz;
    public String tbtel;
    public String btbname;
    public String btbsfz;
    public String btbtel;
    //（支付前上传）影像事前上传状态0无需上传1待上传2审核中3审核失败4审核通过5对接通过6对接失败
    public int sqimgstatus;
    //（支付后上传） 影像事后上传状态0无需上传1待上传2审核中3审核失败4审核通过5对接通过6对接失败
    public int shimgstatus;
    //上传提示语
    public String uploadmsg;
    public String insurance_xunjia_teyue;
    public int imgedit;

    public String[][] carerlist;



    public class Car{
        public String insurance_carmodel_vin;
        public String insurance_carmodel_en;

    }

}
