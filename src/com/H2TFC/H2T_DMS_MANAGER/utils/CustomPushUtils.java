package com.H2TFC.H2T_DMS_MANAGER.utils;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class CustomPushUtils {
    public static void sendMessageToEmployee(String employeeId, String message) {
        ParsePush parsePush = new ParsePush();
        ParseQuery pQuery = ParseInstallation.getQuery();
        pQuery.whereEqualTo("userId", employeeId);

        if (message.length() > 140) {
            message = message.substring(0, 137) + "...";
        }

        ParsePush.sendMessageInBackground(message, pQuery);
    }
}
