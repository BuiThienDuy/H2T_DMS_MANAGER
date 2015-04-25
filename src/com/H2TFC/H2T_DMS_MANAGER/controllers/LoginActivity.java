package com.H2TFC.H2T_DMS_MANAGER.controllers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.*;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */

public class LoginActivity extends Activity {
    // Declare variables - UI
    BootstrapEditText etTenDangNhap, etMatKhau;
    BootstrapButton btnDangNhap;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.loginTitle));

        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        // Assign UI via id

        etTenDangNhap = (BootstrapEditText) findViewById(R.id.login_et_tendangnhap);
        etMatKhau = (BootstrapEditText) findViewById(R.id.login_et_matkhau);
        btnDangNhap = (BootstrapButton) findViewById(R.id.login_btn_dangnhap);


        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tenDangNhap = etTenDangNhap.getText().toString();                      //username
                final String matKhau = etMatKhau.getText().toString();                              //password

                boolean loiXuatHien = false;                                                        //error_exist
                StringBuilder noiDungLoi = new StringBuilder("");     //error_msg

                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage(getString(R.string.PleaseWait));
                progressDialog.show();

                // if username blank
                if (tenDangNhap.trim().equals("")) {
                    loiXuatHien = true;
                    noiDungLoi.append(getString(R.string.errorBlankUsername));
                }

                // if password blank
                if (matKhau.trim().equals("")) {
                    if (!loiXuatHien) {
                        loiXuatHien = true;
                        noiDungLoi.append(getString(R.string.errorBlankPassword));
                    }
                }

                noiDungLoi.append(".");

                // if error exist then show error message
                if (loiXuatHien) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, noiDungLoi.toString(), Toast.LENGTH_LONG).show();
                }else if(!ConnectUtils.hasConnectToInternet(LoginActivity.this)) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, getString(R.string.cannotConnectInternetPleaseCheck), Toast.LENGTH_LONG).show();
                } else {
                    // Check user is locked or correct role
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username",tenDangNhap);
                    try {
                        ParseUser user = query.getFirst();
                        if(user.getString("role_name").equals("NVKD")) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, getString(R.string.onlyManagerAreAllowedToLogin),
                                    Toast
                                    .LENGTH_LONG)
                                    .show();
                            return;
                        }
                        if(user.getBoolean("locked")) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, getString(R.string.errorLogin_UserIsLocked),
                                    Toast
                                            .LENGTH_LONG)
                                    .show();
                            return;
                        }

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Username", tenDangNhap);
                        editor.putString("Password", matKhau);
                        editor.apply();

                        ParseUser.logInInBackground(tenDangNhap, matKhau, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (e == null) {
                                    ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
                                    parseInstallation.put("username", parseUser.getUsername());
                                    parseInstallation.put("userId", parseUser.getObjectId());
                                    parseInstallation.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {

                                            } else {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    // Yay no error found while login
                                    // -> move to dashboard activity
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } else {
                                    // Oops something went wrong
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, getString(R.string.userNameOrPasswordIncorrect), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } catch (ParseException e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, getString(R.string.userNameOrPasswordIncorrect), Toast
                                .LENGTH_LONG).show();
                    }
                }
            }
        });
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
