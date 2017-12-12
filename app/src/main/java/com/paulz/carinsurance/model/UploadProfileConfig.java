package com.paulz.carinsurance.model;

import java.io.File;
import java.util.List;

/**
 * Created by pualbeben on 17/12/2.
 */

public class UploadProfileConfig {


    public String message;
    public String sn;
    public String statustag;
    public String imgstatus;//上传状态，1待上传2下发修改3待审核4审核通过
    public String edit;//上传状态，1修改2不能修改
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

        public File imgFile;
        public boolean uploading;
    }

}
