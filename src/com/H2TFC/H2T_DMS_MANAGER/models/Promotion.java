package com.H2TFC.H2T_DMS_MANAGER.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
@ParseClassName("Promotion")
public class Promotion extends ParseObject {
    public static ParseQuery<Promotion> getQuery() {
        return ParseQuery.getQuery(Promotion.class);
    }

    // 1. Promotion name
    public String getPromotionName() {
        return getString("promotion_name");
    }

    public void setPromotionName(String promotionName) {
        put("promotion_name", promotionName);
    }

    // 2. Promotion apply from
    public Date getPromotionApplyFrom() {
        return getDate("promotion_apply_from");
    }

    public void setPromotionApplyFrom(Date fromDate) {
        put("promotion_apply_from", fromDate);
    }

    // 3. Promotion apply to
    public Date getPromotionApplyTo() {
        return getDate("promotion_apply_to");
    }

    public void setPromotionApplyTo(Date toDate) {
        put("promotion_apply_to", toDate);
    }


    // 4. Discount
    public int getDiscount() {
        return getInt("promotion_discount");
    }

    public void setDiscount(int discount) {
        put("promotion_discount", discount);
    }

    // 5. Product gift
    public Product getProductGift() {
        return (Product) getParseObject("promotion_product_gift");
    }

    public void setProductGift(Product productGift) {
        put("promotion_product_gift", productGift);
    }

    // 6. Quantity gift
    public int getQuantityGift() {
        return getInt("promotion_quantity_gift");
    }
    public void setQuantityGift(int quantity) {
        put("promotion_quantity_gift", quantity);
    }

    // 7. Product gifted
    public Product getProductGifted() {
        return (Product) getParseObject("promotion_product_gifted");
    }

    public void setProductGifted(Product productGifted) {
        put("promotion_product_gifted", productGifted);
    }

    // 8. Quantity gifted
    public int getQuantityGifted() {
        return getInt("promotion_quantity_gifted");
    }
    public void setQuantityGifted(int quantity) {
        put("promotion_quantity_gifted", quantity);
    }
}
