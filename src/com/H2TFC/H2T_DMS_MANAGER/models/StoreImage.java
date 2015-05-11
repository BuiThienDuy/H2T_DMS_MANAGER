package com.H2TFC.H2T_DMS_MANAGER.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
@ParseClassName("StoreImage")
public class StoreImage extends ParseObject {
    public static ParseQuery<StoreImage> getQuery() {
        return ParseQuery.getQuery(StoreImage.class);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile photo) {
        put("photo", photo);
    }

    public String getEmployeeId() {
        return getString("employee_id");
    }

    public void setEmployeeId(String employee) {
        put("employee_id", employee);
    }

    public String getStoreId() {
        return getString("store_id");
    }

    public void setStoreId(String storeId) {
        put("store_id", storeId);
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

}
