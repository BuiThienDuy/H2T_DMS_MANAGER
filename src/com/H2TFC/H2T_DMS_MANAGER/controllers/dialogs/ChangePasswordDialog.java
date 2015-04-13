package com.H2TFC.H2T_DMS_MANAGER.controllers.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.controllers.user_information_management.UserInformationManagementActivity;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class ChangePasswordDialog extends Dialog {
    Context context;
    BootstrapEditText etCurrentPassword,etNewPassword,etNewPasswordAgain;
    BootstrapButton btnDone,btnCancel;

    public ChangePasswordDialog(Activity a) {
        super(a);
        this.context = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(context.getString(R.string.changePassword));
        this.setContentView(R.layout.dialog_change_password);

        btnDone = (BootstrapButton) findViewById(R.id.dialog_user_information_management_btn_done);
        btnCancel = (BootstrapButton) findViewById(R.id.dialog_user_information_management_btn_cancel);

        etCurrentPassword = (BootstrapEditText) findViewById(R.id.dialog_change_password_et_current_password);
        etNewPassword = (BootstrapEditText) findViewById(R.id.dialog_change_password_et_new_password);
        etNewPasswordAgain= (BootstrapEditText) findViewById(R.id.dialog_change_password_et_new_password_again);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = etCurrentPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String newPasswordAgain = etNewPasswordAgain.getText().toString();
                boolean errorExisted = false;
                String errorMessage = "";

                if(newPassword.trim().length() < 6 || !newPasswordAgain.equals(newPassword)) {
                    errorExisted = true;
                    if(newPassword.trim().length() < 6) {
                        errorMessage = context.getString(R.string.errorChangePassword_NewPasswordLength);
                    }
                    if(!newPasswordAgain.equals(newPassword)) {
                        errorMessage = context.getString(R.string.errorChangePassword_PasswordMismatch);
                    }
                }
                if(currentPassword.trim().length() < 6) {
                    errorExisted = true;
                    errorMessage = context.getString(R.string.errorChangePassword_CurrentPasswordLength);
                }

                if(errorExisted) {
                    Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
                } else {
                    if(CheckOldPassword(currentPassword)) {

                        ParseUser userToEdit = ParseUser.getCurrentUser();
                        userToEdit.setPassword(newPassword);
                        userToEdit.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    dismiss();
                                    Toast.makeText(context, context.getString(R.string.changePasswordSuccess), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(context,context.getString(R.string.errorChangePassword_WrongCurrentPassword),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private boolean CheckOldPassword(String oldPassword) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        String username = preferences.getString("Username", "");
        String password = preferences.getString("Password", "");

        // First logout current user
        ParseUser.logOut();

        // Second login to check correct password
        try {
            ParseUser.logIn(username, oldPassword);
            // Re-login
            if(ParseUser.getCurrentUser() != null) {
                ParseUser.logOut();
                ParseUser.logIn(username, password);
            }
            return true;
        } catch (ParseException e) {
            // Re-login
            if(ParseUser.getCurrentUser() != null) {
                ParseUser.logOut();
                try {
                    ParseUser.logIn(username, password);
                } catch (ParseException e1) {
                    return false;
                }
            }
            return false;
        }



    }
}