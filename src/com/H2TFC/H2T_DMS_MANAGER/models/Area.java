package com.H2TFC.H2T_DMS_MANAGER.models;

import android.graphics.Color;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
@ParseClassName("Area")
public class Area extends ParseObject {
    static int Red = Color.argb(64, 255, 0, 0);
    static int Lime = Color.argb(64, 0, 255, 0);
    static int Blue = Color.argb(64, 0, 0, 255);
    static int Yellow = Color.argb(64, 255, 255, 0);
    static int Cyan = Color.argb(64, 0, 255, 255);
    static int Magenta = Color.argb(64, 255, 0, 255);
    static int Maroon = Color.argb(64, 128, 0, 0);
    static int Olive = Color.argb(64, 128, 128, 0);
    static int Green = Color.argb(64, 0, 128, 0);
    static int Purple = Color.argb(64, 128, 0, 128);
    static int Teal = Color.argb(64, 0, 128, 128);
    static int Navy = Color.argb(64, 0, 0, 128);

    public static int getStatusColor(AreaStatus status) {
        switch (status) {
            case MOI_TAO: {
                return Red;
            }

            case DANG_KHAO_SAT: {
                return Yellow;
            }

            case HOAN_THANH: {
                return Green;
            }
        }
        return 0;
    }

    public static enum AreaStatus {
        MOI_TAO,

        DANG_KHAO_SAT,

        HOAN_THANH
    }

    // 1. Node list
    public List<ParseGeoPoint> getNodeList() {
        return getList("nodelist");
    }

    public void setNodeList(List<ParseGeoPoint> nodeList) {
        put("nodelist", nodeList);
    }

    // 2. Fill color
    public int getFillColor() {
        return getInt("fill_color");
    }

    public void setFillColor(int fillColor) {
        put("fill_color", fillColor);
    }

    // 3. Status
    public String getStatus() {
        return getString("status");
    }

    public void setStatus(String status) {
        put("status", status);
    }

    // 4. Employee id
    public String getEmployeeId() {
        return getString("employee_id");
    }

    public void setEmployeeId(String user) {
        put("employee_id", user);
    }

    public static ParseQuery<Area> getQuery() {
        return ParseQuery.getQuery(Area.class);
    }
}
