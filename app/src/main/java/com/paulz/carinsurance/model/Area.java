package com.paulz.carinsurance.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pualbeben on 17/6/15.
 */

public class Area implements Serializable{
    public String name;
    public String id;
    public List<Area> child;
}
