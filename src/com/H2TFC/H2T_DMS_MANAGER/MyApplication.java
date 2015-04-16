package com.H2TFC.H2T_DMS_MANAGER;

import android.app.Application;
import com.H2TFC.H2T_DMS_MANAGER.controllers.LoginActivity;
import com.H2TFC.H2T_DMS_MANAGER.models.*;
import com.parse.*;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class MyApplication extends Application {
    public static final int REQUEST_ADD_NEW = 100;
    public static final int REQUEST_EDIT = 200;
    public static final int REQUEST_DELETE = 300;
    public static final int REQUEST_TAKE_PHOTO = 400;
    public static final int REQUEST_CHOOSE_PHOTO = 401;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register Parse subclass
        ParseObject.registerSubclass(Area.class);
        ParseObject.registerSubclass(Store.class);
        ParseObject.registerSubclass(StoreType.class);
        ParseObject.registerSubclass(StoreImage.class);
        ParseUser.registerSubclass(Employee.class);
        ParseObject.registerSubclass(Product.class);
        ParseObject.registerSubclass(Feedback.class);
        ParseObject.registerSubclass(Invoice.class);
        ParseObject.registerSubclass(Promotion.class);
        ParseObject.registerSubclass(Attendance.class);

        // Parse local data store
        Parse.enableLocalDatastore(this);

        // Parse initialize
        Parse.initialize(this, "GDqwS7Njj2jrD3CmbfrCZmFJ5X2Dw9yvTZVt6pEb", "kfor4yEebgGlrKQGBP12HM5KNlPyb7suo3aGacTT");
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        // Register push notification
        PushService.setDefaultPushCallback(this, LoginActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
