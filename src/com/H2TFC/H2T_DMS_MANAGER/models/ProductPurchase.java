package com.H2TFC.H2T_DMS_MANAGER.models;

import com.parse.ParseClassName;
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
@ParseClassName("ProductPurchase")
public class ProductPurchase extends ParseObject {
    // 1. Product name
    public String getProductName() {
        return getString("name");
    }

    public void setName(String productName) {
        put("name", productName);
    }

    // 2. Product unit
    public String getUnit() {
        return getString("unit");
    }

    public void setUnit(String unit) {
        put("unit", unit);
    }

    // 3. Product price
    public double getPrice() {
        return getDouble("price");
    }

    public void setPrice(double price) {
        put("price", price);
    }

    // 4. Product purchase quantity
    public void setQuantity(int quantity) {
        put("quantity",quantity);
    }

    public int getQuantity() {
        return getInt("quantity");
    }

    // 5. Product relation
    public void setProductRelate(Product product) {
        put("product_relate",product);
    }

    public Product getProductRelate() {
        return (Product) getParseObject("product_relate");
    }

    // 6. Promotion relation
    public void setPromotionRelate(List<Promotion> promotionRelate) {
        put("promotion_relate",promotionRelate);
    }

    public List<Promotion> getPromotionRelate() {
        return getList("promotion_relate");
    }


    // 7. Query
    public static ParseQuery<ProductPurchase> getQuery() {
        return ParseQuery.getQuery(ProductPurchase.class);
    }
}
