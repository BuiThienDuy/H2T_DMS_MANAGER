package com.H2TFC.H2T_DMS_MANAGER.models;

import com.parse.*;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
@ParseClassName("Attendance")
public class Attendance extends ParseObject {
    public void setPhoto(ParseFile photo) {
        put("photo",photo);
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhotoTitle(String photo) {
        put("photo_title",photo);
    }

    public String getPhotoTitle() {
        return getString("photo_title");
    }

    public boolean getPhotoSynched() {
        return getBoolean("photo_synched");
    }

    public void setPhotoSynched(boolean photoSynched) {
        put("photo_synched",photoSynched);
    }

    public void setLocation(ParseGeoPoint location) {
        put("location",location);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setEmployeeId(String employeeId) {
        put("employee_id",employeeId);
    }

    public String getEmployeeId() {
        return getString("employee_id");
    }

    public void setStoreId(String storeId) {
        put("store_id",storeId);
    }

    public String getStoreId() {
        return getString("store_id");
    }

    public static ParseQuery<Attendance> getQuery() {
        return ParseQuery.getQuery(Attendance.class);
    }

    public String getManagerId() {
        return getString("manager_id");
    }

    public void setManagerId(String managerId) {
        put("manager_id",managerId);
    }
}
