package com.H2TFC.H2T_DMS_MANAGER.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.controllers.employee_management.EmployeeManagementActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.product_management.ProductManagementActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.promotion_management.PromotionManagementActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.store_management.StoreManagementActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.street_divide.EmployeeChooseActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.street_divide.StreetDivideActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.user_information_management.UserInformationManagementActivity;
import com.parse.ParseUser;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class DashBoardActivity extends Activity {
    Button btnEmployeeManagement, btnStreetDivide, btnProductManagement, btnStoreManagement,
           btnPromotionMangement, btnUserInformation, btnLogOut, btnViewReport, btnAttendance,btnViewFeedBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setTitle(getString(R.string.dashboardTitle));

        btnEmployeeManagement = (Button) findViewById(R.id.dashboard_btn_employee_management);
        btnStreetDivide = (Button) findViewById(R.id.dashboard_btn_street_divide);
        btnProductManagement = (Button) findViewById(R.id.dashboard_btn_product_management);
        btnStoreManagement = (Button) findViewById(R.id.dashboard_btn_store_management);
        btnPromotionMangement = (Button) findViewById(R.id.dashboard_btn_promotion_management);
        btnUserInformation = (Button) findViewById(R.id.dashboard_btn_user_information);
        btnLogOut = (Button) findViewById(R.id.dashboard_btn_log_out);
        btnViewReport = (Button) findViewById(R.id.dashboard_btn_view_report);
        btnAttendance = (Button) findViewById(R.id.dashboard_btn_employee_attendance);
        btnViewFeedBack = (Button) findViewById(R.id.dashboard_btn_view_feedback);

        btnEmployeeManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateIntent(EmployeeManagementActivity.class);
            }
        });

        btnStreetDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateIntent(EmployeeChooseActivity.class);
            }
        });

        btnProductManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashBoardActivity.this,getString(R.string.currentFeatureNotImplement),Toast.LENGTH_LONG)
                        .show();
                //NavigateIntent(ProductManagementActivity.class);
            }
        });

        btnStoreManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateIntent(StoreManagementActivity.class);
            }
        });

        btnPromotionMangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavigateIntent(PromotionManagementActivity.class);
                Toast.makeText(DashBoardActivity.this,getString(R.string.currentFeatureNotImplement),Toast.LENGTH_LONG)
                        .show();
            }
        });

        btnUserInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateIntent(UserInformationManagementActivity.class);
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(DashBoardActivity.this);
                confirmDialog.setMessage(getString(R.string.confirmLogOut));
                confirmDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(DashBoardActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                confirmDialog.setNegativeButton(getString(R.string.cancel),null);

                confirmDialog.show();
            }
        });

        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashBoardActivity.this,getString(R.string.currentFeatureNotImplement),Toast.LENGTH_LONG)
                        .show();
                //NavigateIntent(StoreManagementActivity.class);
            }
        });

        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashBoardActivity.this,getString(R.string.currentFeatureNotImplement),Toast.LENGTH_LONG)
                        .show();
            }
        });

        btnViewFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashBoardActivity.this,getString(R.string.currentFeatureNotImplement),Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void NavigateIntent(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.confirmExit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}