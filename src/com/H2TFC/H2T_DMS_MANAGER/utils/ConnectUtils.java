package com.H2TFC.H2T_DMS_MANAGER.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class ConnectUtils {
    public static boolean hasConnectToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Check the current state of the Network Information
        if (networkInfo == null)
            return false;
        if (networkInfo.isConnected() == false)
            return false;
        if (networkInfo.isAvailable() == false)
            return false;
        return true;
    }
}
