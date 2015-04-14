package com.H2TFC.H2T_DMS_MANAGER.models;

import android.graphics.Color;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
@ParseClassName("Invoice")
public class Invoice extends ParseObject {
    public static final String MOI_TAO = "MOI_TAO";
    public static final String DANG_XU_LY = "DANG_XU_LY";
    public static final String DA_THANH_TOAN = "DA_THANH_TOAN";

    public static int getStatusColor(String status) {
        if (status.equals(MOI_TAO)) {
            return Color.RED;
        }
        if (status.equals(DANG_XU_LY)) {
            return Color.YELLOW;
        }
        if (status.equals(DA_THANH_TOAN)) {
            return Color.GREEN;
        }
        return 0;
    }

    public static ParseQuery<Invoice> getQuery() {
        return ParseQuery.getQuery(Invoice.class);
    }

    // 1. Invoice Price
    public double getInvoicePrice() {
        return getDouble("invoice_price");
    }

    public void setInvoicePrice(double price) {
        put("invoice_price", price);
    }

    // 2. Invoice Status
    public String getInvoiceStatus() {
        return getString("invoice_status");
    }

    public void setInvoiceStatus(String invoiceStatus) {
        put("invoice_status", invoiceStatus);
    }

    // 3. Employee
    public ParseUser getEmployee() {
        return getParseUser("employee");
    }

    public void setEmployee(ParseUser employee) {
        put("employee", employee);
    }

    // 4. Store id
    public String getStoreId() {
        return getString("storeId");
    }

    public void setStoreId(String storeId) {
        put("storeId", storeId);
    }
}
