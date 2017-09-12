package com.paulz.carinsurance.model;

/**
 * Created by pualbeben on 17/6/21.
 */

public class CarCard {
    public final String KEY_VIN="车辆识别代号";
    public final String KEY_ENGINE="发动机号码";
    public final String KEY_REG_DATE="注册日期";
//    public final String KEY_REG_DATE="发证日期";

    public Msg message;
    public CardInfo[] cardsinfo;

    public boolean success(){
        return message!=null&&message.status==6;
    }


    public class Msg{
        public int status;
        public String value;
    }

    public class CardInfo{
        int type;
        Item[] items;

    }


    public class Item{
        String desc;
        String content;
    }

    public String getVIN(){
        if(cardsinfo==null||cardsinfo.length==0)return "";
        for(Item item:cardsinfo[0].items){
            if(KEY_VIN.equals(item.desc))return item.content;
        }
        return "";
    }

    public String getRegDate(){
        if(cardsinfo==null)return "";
        for(Item item:cardsinfo[0].items){
            if(KEY_REG_DATE.equals(item.desc))return item.content;
        }
        return "";
    }

    public String getEngine(){
        if(cardsinfo==null)return "";
        for(Item item:cardsinfo[0].items){
            if(KEY_ENGINE.equals(item.desc))return item.content;
        }
        return "";
    }

}
