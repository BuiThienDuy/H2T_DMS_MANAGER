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
@ParseClassName("StoreType")
public class StoreType extends ParseObject {
    public static ParseQuery<StoreType> getQuery() {
        return ParseQuery.getQuery(StoreType.class);
    }

    public String getStoreTypeName() {
        return getString("store_type_name");
    }

    public void setStoreTypeName(String storeTypeName) {
        put("store_type_name", storeTypeName);
    }

    public Double getDefaultDebt() {
        return getDouble("default_debt");
    }

    public void setDefaultDebt(Double defaultDebt) {
        put("default_debt",defaultDebt);
    }

    public void setLocked(boolean locked) {
        put("locked",locked);
    }

    public boolean getLocked() {
        return getBoolean("locked");
    }
}
