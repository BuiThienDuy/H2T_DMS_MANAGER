package com.H2TFC.H2T_DMS_MANAGER.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import com.H2TFC.H2T_DMS_MANAGER.models.*;
import com.parse.*;

import java.util.Date;
import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class DownloadUtils {
    public static final String PIN_AREA = "PIN_AREA_DOWNLOAD";
    public static final String PIN_EMPLOYEE = "PIN_EMPLOYEE_DOWNLOAD";
    public static final String PIN_STORE = "PIN_STORE_DOWNLOAD";
    public static final String PIN_STORE_IMAGE = "PIN_STORE_IMAGE_DOWNLOAD";
    public static final String PIN_STORE_TYPE = "PIN_STORE_TYPE_DOWNLOAD";
    public static final String PIN_PRODUCT = "PIN_PRODUCT_DOWNLOAD";
    public static final String PIN_INVOICE = "PIN_INVOICE_DOWNLOAD";

    public static void DownloadParseArea(final Context context, final SaveCallback saveCallback) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_AREA, 0));
        long diff = Math.abs(myDate.getTime() - new Date().getTime());
        if(diff/1000 > 5) {
            ParseQuery<Area> query = Area.getQuery();
            query.findInBackground(new FindCallback<Area>() {
                @Override
                public void done(List<Area> list, ParseException e) {
                    if (e == null) {
                        ParseObject.unpinAllInBackground(PIN_AREA);
                        ParseObject.pinAllInBackground(PIN_AREA, list, saveCallback);

                        // Save last download time
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();
                        Date dt = new Date();
                        editor.putLong("LAST_DOWNLOAD" + PIN_AREA, dt.getTime());
                        editor.apply();
                    }
                }
            });
        }
    }

    public static void DownloadParseEmployee(final SaveCallback saveCallback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if(e==null) {
                    ParseObject.unpinAllInBackground(PIN_EMPLOYEE);
                    ParseObject.pinAllInBackground(PIN_EMPLOYEE, list, saveCallback);
                }
                }
        });
    }

    public static void DownloadParseStore(final SaveCallback saveCallback) {
        ParseQuery<Store> query = Store.getQuery();
        query.findInBackground(new FindCallback<Store>() {
            @Override
            public void done(List<Store> list, ParseException e) {
                if(e == null) {
                    ParseObject.unpinAllInBackground(PIN_STORE);
                    ParseObject.pinAllInBackground(PIN_STORE, list, saveCallback);
                }
            }
        });
    }

    public static void DownloadParseStoreImage(final SaveCallback saveCallback) {
        ParseQuery<StoreImage> query = StoreImage.getQuery();
        query.findInBackground(new FindCallback<StoreImage>() {
            @Override
            public void done(List<StoreImage> list, ParseException e) {
                if(e == null) {
                    ParseObject.unpinAllInBackground(PIN_STORE_IMAGE);
                    ParseObject.pinAllInBackground(PIN_STORE_IMAGE, list, saveCallback);
                }
                }
        });
    }

    public static void DownloadParseStoreType(final SaveCallback saveCallback) {
        ParseQuery<StoreType> query = StoreType.getQuery();
        query.findInBackground(new FindCallback<StoreType>() {
            @Override
            public void done(List<StoreType> list, ParseException e) {
                if(e == null) {
                    ParseObject.unpinAllInBackground(PIN_STORE_TYPE);
                    ParseObject.pinAllInBackground(PIN_STORE_TYPE, list, saveCallback);
                }
            }
        });
    }

    public static void DownloadParseProduct(final SaveCallback saveCallback) {
        ParseQuery<Product> query = Product.getQuery();
        query.findInBackground(new FindCallback<Product>() {
            @Override
            public void done(List<Product> list, ParseException e) {
                if(e == null) {
                    ParseObject.unpinAllInBackground(PIN_PRODUCT);
                    ParseObject.pinAllInBackground(PIN_PRODUCT, list, saveCallback);
                }
            }
        });
    }

    public static void DownloadParseInvoice(final SaveCallback saveCallback) {
        ParseQuery<Invoice> query = Invoice.getQuery();
        query.findInBackground(new FindCallback<Invoice>() {
            @Override
            public void done(List<Invoice> list, ParseException e) {
                if(e == null) {
                    ParseObject.unpinAllInBackground(PIN_INVOICE);
                    ParseObject.pinAllInBackground(PIN_INVOICE, list, saveCallback);
                }
                }
        });
    }
}
