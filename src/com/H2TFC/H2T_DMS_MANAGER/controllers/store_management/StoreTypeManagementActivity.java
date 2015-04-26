package com.H2TFC.H2T_DMS_MANAGER.controllers.store_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.StoreTypeListAdapter;
import com.H2TFC.H2T_DMS_MANAGER.controllers.LoginActivity;
import com.H2TFC.H2T_DMS_MANAGER.models.StoreType;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;

import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class StoreTypeManagementActivity extends Activity {
    public ListView lvStoreType;
    TextView tvEmpty;
    ProgressBar progressBar;

    StoreTypeListAdapter storeTypeAdapter;
    int selectionIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_type_management);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.storeTypeManagementTitle));

            DownloadUtils.DownloadParseStoreType(StoreTypeManagementActivity.this,new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    storeTypeAdapter.loadObjects();
                }
            });

        InitializeComponent();
        SetupListView();
        SetupEvent();
    }

    private void SetupEvent() {
        lvStoreType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectionIndex = position;
                view.setSelected(true);
            }
        });
    }

    public void InitializeComponent(){
        lvStoreType = (ListView) findViewById(R.id.activity_store_type_management_listview);
        tvEmpty = (TextView) findViewById(R.id.activity_store_type_management_tv_empty);
        progressBar = (ProgressBar) findViewById(R.id.activity_store_type_management_progressbar);


    }

    public void SetupListView() {
        // Query data from local data store
        ParseQueryAdapter.QueryFactory<StoreType> factory = new ParseQueryAdapter.QueryFactory<StoreType>() {
            @Override
            public ParseQuery<StoreType> create() {
                ParseQuery<StoreType> query = StoreType.getQuery();
                query.orderByDescending("createdAt");
                query.fromPin(DownloadUtils.PIN_STORE_TYPE);
                return query;
            }
        };

        lvStoreType.setEmptyView(tvEmpty);

        // Set list adapter
        storeTypeAdapter = new StoreTypeListAdapter(this, factory);
        lvStoreType.setEmptyView(tvEmpty);
        lvStoreType.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        storeTypeAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<StoreType>() {
            @Override
            public void onLoading() {
                tvEmpty.setText("");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<StoreType> list, Exception e) {
                if (list.size() == 0) {
                    tvEmpty.setText(getString(R.string.emptyPromotionList));
                }
                progressBar.setVisibility(View.GONE);

            }
        });
        lvStoreType.setAdapter(storeTypeAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_store_type_management, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_bar_store_type_management_add: {
                AddNewStoreType();
                break;
            }

            case R.id.action_bar_store_type_management_edit: {
                if(selectionIndex >= 0) {
                    StoreType storeTypeToEdit = storeTypeAdapter.getItem(selectionIndex);
                    EditStoreType(storeTypeToEdit);
                } else {
                    Toast.makeText(StoreTypeManagementActivity.this,getString(R.string.errorNoSelectStoreTypeToEdit),Toast
                            .LENGTH_LONG).show();
                }
                break;
            }

            case R.id.action_bar_store_type_management_delete: {
                if(selectionIndex >= 0) {
                    StoreType storeTypeToDelete = storeTypeAdapter.getItem(selectionIndex);
                    DeleteStoreType(storeTypeToDelete);
                } else {
                    Toast.makeText(StoreTypeManagementActivity.this,getString(R.string.errorNoSelectStoreTypeToDelete),Toast
                            .LENGTH_LONG).show();
                }
                break;
            }
            case R.id.action_bar_store_type_management_log_out: {
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(StoreTypeManagementActivity.this);
                confirmDialog.setMessage(getString(R.string.confirmLogOut));
                confirmDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(StoreTypeManagementActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                confirmDialog.setNegativeButton(getString(R.string.cancel),null);

                confirmDialog.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void AddNewStoreType() {
        StoreTypeNewDialog dialog = new StoreTypeNewDialog(StoreTypeManagementActivity.this);
        dialog.show();
    }

    public void EditStoreType(StoreType storeType) {
        StoreTypeNewDialog dialog = new StoreTypeNewDialog(StoreTypeManagementActivity.this,storeType);
        dialog.show();
    }

    public void DeleteStoreType(final StoreType storeType) {
        final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(StoreTypeManagementActivity.this);
        confirmDialog.setTitle(getString(R.string.confirmDeleteStoreTypeTitle));
        confirmDialog.setMessage(getString(R.string.confirmDeleteStoreTypeMessage));
        confirmDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                storeType.deleteEventually();
                storeType.unpinInBackground(DownloadUtils.PIN_STORE_TYPE, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(StoreTypeManagementActivity.this, getString(R.string.removeStoreTypeSuccess),Toast
                                    .LENGTH_LONG).show();
                            storeTypeAdapter.loadObjects();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        confirmDialog.setNegativeButton(getString(R.string.cancel), null);

        confirmDialog.show();
    }
}