package com.H2TFC.H2T_DMS_MANAGER.controllers.promotion_management;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import com.H2TFC.H2T_DMS_MANAGER.R;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class PromotionManagementActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_management);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.promotionManagemenTitle));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}