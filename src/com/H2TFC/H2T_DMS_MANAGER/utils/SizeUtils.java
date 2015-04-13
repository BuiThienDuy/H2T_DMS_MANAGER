package com.H2TFC.H2T_DMS_MANAGER.utils;

import android.content.Context;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class SizeUtils {
    public static float distance(Context context, float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx, context);
    }

    public static float pxToDp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;
    }
}
