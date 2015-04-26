package com.H2TFC.H2T_DMS_MANAGER.controllers.store_management;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.StoreType;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.math.BigDecimal;
import java.util.Locale;


/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class StoreTypeNewDialog extends Dialog {
    Context context;
    BootstrapEditText etName,etDefaultDebt;
    BootstrapButton btnAdd, btnCancel;

    StoreType MyStoreType;

    public StoreTypeNewDialog(Context context, StoreType storeType) {
        super(context);
        this.context = context;
        MyStoreType = storeType;
    }

    public StoreTypeNewDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_store_type_new);

        InitializeComponent();
        SetupEvent();
    }

    private void SetupEvent() {
        if(MyStoreType == null) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String error_msg = "";
                    boolean error_exist = false;

                    String name = etName.getText().toString().trim();
                    String sDefaultDebt = etDefaultDebt.getText().toString().trim();

                    if (name.length() == 0) {
                        error_exist = true;
                        error_msg = context.getString(R.string.errorBlankStoreTypeName);
                    }

                    if (sDefaultDebt.length() == 0) {
                        error_exist = true;
                        error_msg = context.getString(R.string.errorBlankDefaultDebt);
                    }

                    if (error_exist) {
                        Toast.makeText(context, error_msg, Toast.LENGTH_LONG).show();
                    } else {
                        StoreType storeType = new StoreType();
                        storeType.setStoreTypeName(name);
                        storeType.setDefaultDebt(Double.parseDouble(sDefaultDebt));

                        storeType.saveEventually();
                        storeType.pinInBackground(DownloadUtils.PIN_STORE_TYPE, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(context, context.getString(R.string.addNewStoreTypeSuccess), Toast.LENGTH_LONG).show();
                                    StoreTypeManagementActivity activity = (StoreTypeManagementActivity) context;
                                    activity.storeTypeAdapter.loadObjects();
                                    dismiss();
                                } else {
                                    Toast.makeText(context, context.getString(R.string.addNewStoreTypeFailed) + e.getMessage(), Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
                    }
                }
            });
        } else {
            etName.setText(MyStoreType.getStoreTypeName());
            etDefaultDebt.setText(new BigDecimal(MyStoreType.getDefaultDebt()).toPlainString());


            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String error_msg = "";
                    boolean error_exist = false;

                    String name = etName.getText().toString().trim();
                    String sDefaultDebt = etDefaultDebt.getText().toString().trim();
                    if (name.length() == 0) {
                        error_exist = true;
                        error_msg = context.getString(R.string.errorBlankStoreTypeName);
                    }

                    if (sDefaultDebt.length() == 0) {
                        error_exist = true;
                        error_msg = context.getString(R.string.errorBlankDefaultDebt);
                    }

                    if (error_exist) {
                        Toast.makeText(context, error_msg, Toast.LENGTH_LONG).show();
                    } else {
                        MyStoreType.setStoreTypeName(name);
                        MyStoreType.setDefaultDebt(Double.parseDouble(sDefaultDebt));

                        MyStoreType.saveEventually();
                        MyStoreType.pinInBackground(DownloadUtils.PIN_STORE_TYPE, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(context, context.getString(R.string.updateStoreTypeSuccess), Toast
                                            .LENGTH_LONG)
                                            .show();
                                    StoreTypeManagementActivity activity = (StoreTypeManagementActivity) context;
                                    activity.storeTypeAdapter.loadObjects();
                                    dismiss();
                                } else {
                                    Toast.makeText(context, context.getString(R.string.updateStoreTypeFailed) + e
                                                    .getMessage
                                                    (), 
                                            Toast
                                            .LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
                    }
                }
            });




        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void InitializeComponent() {
        etName = (BootstrapEditText) findViewById(R.id.dialog_store_type_new_et_name);
        etDefaultDebt = (BootstrapEditText) findViewById(R.id.dialog_store_type_new_et_default_debt);

        btnAdd = (BootstrapButton) findViewById(R.id.dialog_store_type_new_btn_done);
        btnCancel = (BootstrapButton) findViewById(R.id.dialog_store_type_new_btn_cancel);
    }
}