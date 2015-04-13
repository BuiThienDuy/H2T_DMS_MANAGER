package com.H2TFC.H2T_DMS_MANAGER.controllers.employee_management;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.MyApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.EmployeeListAdapter;
import com.H2TFC.H2T_DMS_MANAGER.controllers.LoginActivity;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.*;

import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class EmployeeManagementActivity extends Activity {
    final int REQUEST_ADD_NEW_EMPLOYEE = 100;
    EmployeeListAdapter employeeListAdapter;
    int selectedItemIndex = -1;

    // UI initial
    BootstrapButton btnXong, btnHuy;
    BootstrapEditText etUsername, etPassword, etPasswordConfirm, etHoVaTen, etSoDienThoai, etDiaChi, etCMND,etSearch;
    ListView lvEmployee;
    TextView tvEmptyView,tvNgaySinh;
    RadioButton rbNam, rbNu;
    ParseImageView ivPhoto;
    ProgressBar progressBar;
    LinearLayout layout_employee_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);
        InitializeComponent();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.employeeManagementTitle));

        // Download and save to local datastore
        if (ConnectUtils.hasConnectToInternet(EmployeeManagementActivity.this)) {
            DownloadUtils.DownloadParseEmployee(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    employeeListAdapter.loadObjects();
                }
            });
        }
        SetupListView();
        SetupEvent();
    }

    private void SetupEvent() {
        lvEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layout_employee_detail.setVisibility(View.VISIBLE);

                view.setSelected(true);
                //lvEmployee.setItemChecked(position,true);
                selectedItemIndex = position;
                ParseUser selectedFromList = (ParseUser) (lvEmployee.getItemAtPosition(position));
                ivPhoto.setParseFile(selectedFromList.getParseFile("photo"));
                ivPhoto.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {

                    }
                });
                etHoVaTen.setText(selectedFromList.getString("name"));
                etSoDienThoai.setText(selectedFromList.getString("phone_number"));
                etDiaChi.setText(selectedFromList.getString("address"));
                if (selectedFromList.getString("gender").equals("Nam")) {
                    rbNam.setChecked(true);
                    rbNu.setChecked(false);
                } else {
                    rbNam.setChecked(false);
                    rbNu.setChecked(true);
                }

                if (selectedFromList.getDate("date_of_birth") != null) {
                    String myFormat = "dd/MM/yy"; //Format for date time
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    tvNgaySinh.setText(sdf.format(selectedFromList.getDate("date_of_birth")));
                }

                etCMND.setText(selectedFromList.getString("identify_card_number"));

            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etSearch.getText().toString().length() != 0) {
                    ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>() {
                        @Override
                        public ParseQuery<ParseUser> create() {
                            ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                            query1.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId())
                                    .whereEqualTo("locked",false)
                                 .whereContains("username", etSearch.getText().toString());
                            query1.orderByDescending("createdAt");
                            query1.fromPin(DownloadUtils.PIN_EMPLOYEE);


                            return query1;
                        }
                    };
                    employeeListAdapter = new EmployeeListAdapter(EmployeeManagementActivity.this,factory);
                    lvEmployee.setAdapter(employeeListAdapter);
                    employeeListAdapter.notifyDataSetChanged();
                    layout_employee_detail.setVisibility(View.INVISIBLE);
                } else {
                    ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>() {
                        @Override
                        public ParseQuery<ParseUser> create() {
                            ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                            query1.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
                            query1.whereEqualTo("locked",false);
                            query1.orderByDescending("createdAt");
                            query1.fromPin(DownloadUtils.PIN_EMPLOYEE);
                            return query1;
                        }
                    };
                    employeeListAdapter = new EmployeeListAdapter(EmployeeManagementActivity.this,factory);
                    lvEmployee.setAdapter(employeeListAdapter);
                    employeeListAdapter.notifyDataSetChanged();
                    layout_employee_detail.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void SetupListView() {
        // Query data from local data store
        ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>() {
            @Override
            public ParseQuery<ParseUser> create() {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("locked",false);
                query.orderByDescending("createdAt");
                query.fromPin(DownloadUtils.PIN_EMPLOYEE);
                return query;
            }
        };

        // Set list adapter
        employeeListAdapter = new EmployeeListAdapter(this, factory);

        employeeListAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseUser>() {
            @Override
            public void onLoading() {
                tvEmptyView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<ParseUser> list, Exception e) {
                if (list.size() == 0) {
                    ParseObject.fetchAllIfNeededInBackground(list);
                    tvEmptyView.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        lvEmployee.setEmptyView(tvEmptyView);
        lvEmployee.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvEmployee.setAdapter(employeeListAdapter);
    }

    private void InitializeComponent() {
        // Edittext
        etHoVaTen = (BootstrapEditText) findViewById(R.id.activity_employee_management_et_hovaten);
        etSoDienThoai = (BootstrapEditText) findViewById(R.id.activity_employee_management_et_sodienthoai);
        etDiaChi = (BootstrapEditText) findViewById(R.id.activity_employee_management_et_diachi);
        etCMND = (BootstrapEditText) findViewById(R.id.activity_employee_management_et_cmnd);
        etSearch = (BootstrapEditText) findViewById(R.id.activity_employee_management_search);

        // Textview
        tvNgaySinh = (TextView) findViewById(R.id.activity_employee_management_et_ngaysinh);
        tvEmptyView = (TextView) findViewById(R.id.activity_employee_management_tv_empty);

        // Radio button
        rbNam = (RadioButton) findViewById(R.id.activity_employee_management_rb_nam);
        rbNu = (RadioButton) findViewById(R.id.activity_employee_management_rb_nu);

        // Imageview
        ivPhoto = (ParseImageView) findViewById(R.id.activity_employee_management_iv_photo);

        // Layout
        layout_employee_detail = (LinearLayout) findViewById(R.id.activity_employee_management_employee_detail);

        // Listview
        lvEmployee = (ListView) findViewById(R.id.activity_employee_management_lvemployee);

        // ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.activity_employee_management_progressbar);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_employee_management, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.actionbar_employee_add: {
                Intent intent = new Intent(EmployeeManagementActivity.this, EmployeeNewActivity.class);
                startActivityForResult(intent, REQUEST_ADD_NEW_EMPLOYEE);
                break;
            }

            case R.id.actionbar_employee_logout: {
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(EmployeeManagementActivity.this);
                confirmDialog.setMessage(getString(R.string.confirmLogOut));
                confirmDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(EmployeeManagementActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                confirmDialog.setNegativeButton(getString(R.string.cancel),null);

                confirmDialog.show();
                break;
            }

            case R.id.actionbar_employee_lock: {
                AlertDialog.Builder confirmDialog= new AlertDialog.Builder(EmployeeManagementActivity.this);
                confirmDialog.setTitle(getString(R.string.lockThisEmployee));
                confirmDialog.setMessage(getString(R.string.confirmLockUser));
                confirmDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ParseUser selectedEmployeeFromList = (ParseUser) (lvEmployee.getItemAtPosition(selectedItemIndex));

                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("username", selectedEmployeeFromList.getUsername());
                        params.put("locked", true);

                        ParseCloud.callFunctionInBackground("modifyUser", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object o, ParseException e) {
                                if (e == null) {
                                    Toast.makeText(EmployeeManagementActivity.this,getString(R.string.lockEmployeeSuccess),Toast
                                            .LENGTH_LONG)
                                            .show();
                                    selectedEmployeeFromList.put("locked", true);
                                    selectedEmployeeFromList.pinInBackground(DownloadUtils.PIN_EMPLOYEE, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e == null) {
                                                employeeListAdapter.loadObjects();
                                                layout_employee_detail.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(EmployeeManagementActivity.this,e.getMessage(),Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
                    }
                });
                confirmDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                confirmDialog.show();

                break;
            }
            case R.id.actionbar_employee_lock_view: {
                Intent intent = new Intent(EmployeeManagementActivity.this,EmployeeViewLockedActivity.class);
                startActivityForResult(intent, MyApplication.REQUEST_EDIT);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_NEW_EMPLOYEE) {
                employeeListAdapter.notifyDataSetChanged();
                employeeListAdapter.loadObjects();

                lvEmployee.invalidateViews();
            }
        }
        employeeListAdapter.loadObjects();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}