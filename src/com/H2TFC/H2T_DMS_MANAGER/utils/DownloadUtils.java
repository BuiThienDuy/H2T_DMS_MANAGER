package com.H2TFC.H2T_DMS_MANAGER.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.view.ContextThemeWrapper;
import com.H2TFC.H2T_DMS_MANAGER.controllers.invoice.InvoiceDetailActivity;
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
    public static final String PIN_ATTENDANCE = "PIN_ATTENDANCE_DOWNLOAD";
    public static final String PIN_FEEDBACK = "PIN_FEEDBACK_DOWNLOAD";
    public static final String PIN_PROMOTION = "PIN_PROMOTION_DOWNLOAD";
    public static final String PIN_PRODUCT_PURCHASE = "PIN_PRODUCT_PURCHASE_DOWNLOAD";


    public static void DownloadParseArea(final Context context, final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_AREA, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
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
    }

    public static void DownloadParseEmployee(final Context context,final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_EMPLOYEE, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground(PIN_EMPLOYEE);
                            ParseObject.pinAllInBackground(PIN_EMPLOYEE, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_EMPLOYEE, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public static void DownloadParseStore(final Context context,final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_STORE, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<Store> query = Store.getQuery();
                query.findInBackground(new FindCallback<Store>() {
                    @Override
                    public void done(List<Store> list, ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground(PIN_STORE);
                            ParseObject.pinAllInBackground(PIN_STORE, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_STORE, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public static void DownloadParseStoreImage(final Context context,final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_STORE_IMAGE, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<StoreImage> query = StoreImage.getQuery();
                query.findInBackground(new FindCallback<StoreImage>() {
                    @Override
                    public void done(List<StoreImage> list, ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground(PIN_STORE_IMAGE);
                            ParseObject.pinAllInBackground(PIN_STORE_IMAGE, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_STORE_IMAGE, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public static void DownloadParseStoreType(final Context context,final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_STORE_TYPE, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<StoreType> query = StoreType.getQuery();
                query.findInBackground(new FindCallback<StoreType>() {
                    @Override
                    public void done(List<StoreType> list, ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground(PIN_STORE_TYPE);
                            ParseObject.pinAllInBackground(PIN_STORE_TYPE, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_STORE_TYPE, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public static void DownloadParseProduct(final Context context, final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_PRODUCT, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<Product> query = Product.getQuery();
                query.findInBackground(new FindCallback<Product>() {
                    @Override
                    public void done(List<Product> list, ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground(PIN_PRODUCT);
                            ParseObject.pinAllInBackground(PIN_PRODUCT, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_PRODUCT, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public static void DownloadParseInvoice(final Context context,final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_INVOICE, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<Invoice> query = Invoice.getQuery();
                query.findInBackground(new FindCallback<Invoice>() {
                    @Override
                    public void done(List<Invoice> list, ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground(PIN_INVOICE);
                            ParseObject.pinAllInBackground(PIN_INVOICE, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_INVOICE, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }


    public static void DownloadParseAttendance(final Context context,final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_ATTENDANCE, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<Attendance> query = Attendance.getQuery();
                query.findInBackground(new FindCallback<Attendance>() {
                    @Override
                    public void done(List<Attendance> list, ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground(PIN_ATTENDANCE);
                            ParseObject.pinAllInBackground(PIN_ATTENDANCE, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_ATTENDANCE, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public static void DownloadParseFeedback(final Context context,final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_FEEDBACK, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<Feedback> query = Feedback.getQuery();
                query.findInBackground(new FindCallback<Feedback>() {
                    @Override
                    public void done(List<Feedback> list, ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground(PIN_FEEDBACK);
                            ParseObject.pinAllInBackground(PIN_FEEDBACK, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_FEEDBACK, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public static void DownloadParsePromotion(final Context context,final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_PROMOTION, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<Promotion> query = Promotion.getQuery();
                query.findInBackground(new FindCallback<Promotion>() {
                    @Override
                    public void done(List<Promotion> list, ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground(PIN_PROMOTION);
                            ParseObject.pinAllInBackground(PIN_PROMOTION, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_PROMOTION, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public static void DownloadParseProductPurchase(final Context context,final SaveCallback saveCallback) {
        if(ConnectUtils.hasConnectToInternet(context)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Date myDate = new Date(preferences.getLong("LAST_DOWNLOAD" + PIN_PRODUCT_PURCHASE, 0));
            long diff = Math.abs(myDate.getTime() - new Date().getTime());
            if (diff / 1000 > 5) {
                ParseQuery<ProductPurchase> query = ProductPurchase.getQuery();
                query.findInBackground(new FindCallback<ProductPurchase>() {
                    @Override
                    public void done(List<ProductPurchase> list, ParseException e) {
                        if(e==null) {
                            ParseObject.unpinAllInBackground(PIN_PRODUCT_PURCHASE);
                            ParseObject.pinAllInBackground(PIN_PRODUCT_PURCHASE, list, saveCallback);

                            // Save last download time
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            Date dt = new Date();
                            editor.putLong("LAST_DOWNLOAD" + PIN_PRODUCT_PURCHASE, dt.getTime());
                            editor.apply();
                        }
                    }
                });
            }
        }
    }
}
