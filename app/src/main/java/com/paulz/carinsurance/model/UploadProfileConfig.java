package com.paulz.carinsurance.model;

import java.util.List;

/**
 * Created by pualbeben on 17/12/2.
 */

public class UploadProfileConfig {


    public String message;
    public String sn;
    public String statustag;
    public String imgstatus;
    public List<ImageGroup> list;

    public class ImageGroup{
        public String title;
        public List<ImageModel> imglist;
        public String sic;
        public String required;
        public String id;
    }


    public class ImageModel{
        public int type;
        public String op;
        public String title;
        public int required;
        public String img;
        public String id;
        public String imgid;
    }

}
