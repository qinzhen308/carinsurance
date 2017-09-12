package com.paulz.carinsurance.model;

import java.util.List;

/**
 * Created by pualbeben on 17/6/21.
 */

public class IdCard {
    public final String KEY_NAME="姓名";
    public final String KEY_SEX="性别";
    public final String KEY_NATION="民族";
    public final String KEY_BIRTHDAY="出生";
    public final String KEY_ADDRESS="住址";
    public final String KEY_ID="公民身份号码";

    public Msg message;
    public CardInfo[] cardsinfo;

    public boolean success(){
        return message!=null&&message.status==2;
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

    public String getName(){
        if(cardsinfo==null||cardsinfo.length==0)return "";
        for(Item item:cardsinfo[0].items){
            if(KEY_NAME.equals(item.desc))return item.content;
        }
        return "";
    }

    public String getId(){
        if(cardsinfo==null)return "";
        for(Item item:cardsinfo[0].items){
            if(KEY_ID.equals(item.desc))return item.content;
        }
        return "";
    }

}
