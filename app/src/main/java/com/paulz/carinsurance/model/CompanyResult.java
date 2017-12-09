package com.paulz.carinsurance.model;

import android.text.TextUtils;

/**
 * Created by pualbeben on 17/6/17.
 */

public class CompanyResult {
    public String insurance_company_id;
    public String insurance_company_code;
    public String insurance_xunjia_id;
    public String insurance_company_name;
    public String insurance_xunjia_amout;
    public String insurance_company_img;
    public String insurance_xunjia_jiangjin;

    public String error;
    public boolean result;
    public String reason;
    public Verify verify;

    public boolean isReasonShown;

    public boolean isLoading;
    public String verifyError;

    public boolean isFakeResult;//true 询价接口还没请求回来


    public boolean isError(){
        if(result|| !TextUtils.isEmpty(error))return true;

        return false;

    }

    public CompanyResult(){

    }

    public CompanyResult(Company company){
        this.insurance_company_id=company.insurance_company_id;
        this.insurance_company_name=company.insurance_company_name;
        this.insurance_company_img=company.insurance_company_img;
        this.insurance_company_code=company.insurance_company_code;
        isFakeResult=true;
    }


    /**
     "verify":{
     "p1":"1353742",
     "p2":"V0101DBIC500017001501680576147",
     "p3":"./Uploads/Dubang/2017/08/02/15016806421.png",
     "p4":"01DBIC500017001501680576044970",
     "p5":"./Uploads/Dubang/2017/08/02/15016806422.png"
     }
     */

    public static class Verify{
        public String p1;
        public String p2;
        public String p3;
        public String p4;
        public String p5;

    }

}
