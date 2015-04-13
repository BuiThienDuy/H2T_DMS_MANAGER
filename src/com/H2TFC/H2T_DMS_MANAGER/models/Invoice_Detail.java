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
@ParseClassName("Invoice_Detail")
public class Invoice_Detail extends ParseObject {
    public static ParseQuery<Invoice_Detail> getQuery() {
        return ParseQuery.getQuery(Invoice_Detail.class);
    }

    // 1. Invoice detail quantity
    public int getQuantity() {
        return getInt("invoice_detail_quantity");
    }

    public void setQuantity(int quantity) {
        put("invoice_detail_quantity", quantity);
    }

    // 2. Invoice detail price
    public double getPrice() {
        return getDouble("invoice_detail_price");
    }

    public void setPrice(double price) {
        put("invoice_detail_price", price);
    }

    // 3. Invoice detail productId
    public String getProductId() {
        return getString("invoice_detail_productId");
    }

    public void setProductId(String productId) {
        put("invoice_detail_productId", productId);
    }

    // 4. Invoice detail invoiceId
    public String getInvoiceId() {
        return getString("invoice_detail_invoiceId");
    }

    public void setInvoiceId(String invoiceId) {
        put("invoice_detail_invoiceId", invoiceId);
    }
}
