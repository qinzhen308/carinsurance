package com.paulz.carinsurance.model;

import java.util.List;

/**
 * Created by pualbeben on 17/12/8.
 */

public class AppointDetailEdit {

    public String title;
    public String des;
    public int type;
    public List<Symble> buttonlist;

    public class Symble{
        public String title;
        public String value;
    }

}
