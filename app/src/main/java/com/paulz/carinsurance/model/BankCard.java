package com.paulz.carinsurance.model;

/**
 * Created by pualbeben on 17/6/10.
 */

public class BankCard {
    public String member_bankcard_id;
    public String member_bankcard_no;
    public String member_bankcard_type;
    public String member_bankcard_name;
    public int member_bankcard_status;
    public String member_bankcard_uid;

    public Bank bank;


    @Override
    public String toString() {
        return bank.name+"  -  "+member_bankcard_no;
    }
}
