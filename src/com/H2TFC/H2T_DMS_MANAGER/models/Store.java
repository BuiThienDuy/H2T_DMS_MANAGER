package com.H2TFC.H2T_DMS_MANAGER.models;

import android.graphics.Color;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
@ParseClassName("Store")
public class Store extends ParseObject {
    public static int getStatusColor(StoreStatus status) {
        switch (status) {
            case KHAO_SAT: {
                return Color.CYAN;
            }
            case TIEM_NANG: {
                return Color.BLUE;
            }

            case KHONG_DU_TIEU_CHUAN: {
                return Color.RED;
            }

            case CHO_CAP_TREN: {
                return Color.argb(255, 255, 105, 180);
            }

            case DANG_THOA_THUAN: {
                return Color.YELLOW;
            }

            case BAN_HANG: {
                return Color.GREEN;
            }
        }
        return 0;
    }

    public static enum StoreStatus {
        KHAO_SAT,

        TIEM_NANG,
        KHONG_DU_TIEU_CHUAN,

        CHO_CAP_TREN,
        DANG_THOA_THUAN,

        BAN_HANG,
    }


    // 1. Store name
    public String getName() {
        return getString("name");
    }

    public void setName(String storeName) {
        put("name", storeName);
    }

    // 2. Store owner
    public String getStoreOwner() {
        return getString("owner");
    }

    public void setStoreOwner(String owner) {
        put("owner", owner);
    }

    // 3. Store address
    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String storeAddress) {
        put("address", storeAddress);
    }

    // 4. Store Income
    public double getIncome() {
        return getDouble("income");
    }

    public void setIncome(double income) {
        put("income", income);
    }

    // 5. Store Image id
    public String getStoreImageId() {
        return getString("store_image");
    }

    public void setStoreImageId(String storeImageId) {
        put("store_image", storeImageId);
    }

    // 6. Store Phone number
    public String getPhoneNumber() {
        return getString("phone_number");
    }

    public void setPhoneNumber(String storePhoneNumber) {
        put("phone_number", storePhoneNumber);
    }

    // 7. Store competitor
    public String getCompetitor() {
        return getString("competitor");
    }

    public void setCompetitor(String storeCompetitor) {
        put("competitor", storeCompetitor);
    }

    // 8. Store status
    public String getStatus() {
        return getString("status");
    }

    public void setStatus(String storeStatus) {
        put("status", storeStatus);
    }

    // 9. Store type id
    public String getStoreType() {
        return getString("store_type");
    }

    public void setStoreType(String storeType) {
        put("store_type", storeType);
    }

    // 10. Employee
    public String getEmployeeId() {
        return getString("employee_id");
    }

    public void setEmployeeId(String employee) {
        put("employee_id", employee);
    }

    // 11. Location point
    public ParseGeoPoint getLocationPoint() {
        return getParseGeoPoint("location_point");
    }

    public void setLocationPoint(ParseGeoPoint locationPoint) {
        put("location_point", locationPoint);
    }

    // 12. Debt
    public double getMaxDebt() {
        return getDouble("max_debt");
    }

    public void setMaxDebt(double maxDebt) {
        put("max_debt",maxDebt);
    }

    // 12. Query
    public static ParseQuery<Store> getQuery() {
        return ParseQuery.getQuery(Store.class);
    }
}
