package com.H2TFC.H2T_DMS_MANAGER.controllers.dialogs;

import android.content.Context;
import android.content.Intent;
import com.H2TFC.H2T_DMS_MANAGER.controllers.DashBoardActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.LoginActivity;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class Receiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i;
        if(ParseUser.getCurrentUser() != null) {
            i = new Intent(context, DashBoardActivity.class);
        } else{
            i = new Intent(context, LoginActivity.class);
        }
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

    }
}
