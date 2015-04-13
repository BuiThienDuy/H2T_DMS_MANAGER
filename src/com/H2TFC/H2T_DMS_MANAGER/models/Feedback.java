package com.H2TFC.H2T_DMS_MANAGER.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
@ParseClassName("Feedback")
public class Feedback extends ParseObject {
    public static final String MOI_TAO = "Mới tạo";
    public static final String DANG_XEM_XET = "Đang xem xét";
    public static final String DA_XU_LY = "Đã xử lý";

    public void setTitle(String title) {
        put("title",title);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setDescription(String description) {
        put("description",description);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setStoreId(String storeId) {
        put("storeId",storeId);
    }

    public String getStoreId() {
        return getString("storeId");
    }

    public void setStatus(String status) {
        put("status",status);
    }

    public String getStatus() {
        return getString("status");
    }

    public static ParseQuery<Feedback> getQuery() {
        return ParseQuery.getQuery(Feedback.class);
    }
}
