package com.ywk.helpme.model;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/6/8 0008.
 */
public class ToiletEntity implements Serializable {


    private String address;

    private LatLng location;


    public ToiletEntity() {
    }


    public ToiletEntity(String address, LatLng location) {
        this.address = address;
        this.location = location;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
