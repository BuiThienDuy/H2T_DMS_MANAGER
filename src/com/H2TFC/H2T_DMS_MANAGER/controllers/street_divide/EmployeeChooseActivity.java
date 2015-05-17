package com.H2TFC.H2T_DMS_MANAGER.controllers.street_divide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.EmployeeListAdapter;
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
public class EmployeeChooseActivity extends Activity {
    EmployeeListAdapter employeeListAdapter;

    ListView lvEmployee;
    TextView tvEmptyView;

    ProgressBar progressBar;

    int selectedItemIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_choose);
        getActionBar().setDisplayHomeAsUpEnabled(true);


            DownloadUtils.DownloadParseEmployee(getApplicationContext(),new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    employeeListAdapter.notifyDataSetChanged();
                    employeeListAdapter.loadObjects();
                }
            });



        InitializeComponent();
        SetupListView();

        // Event
        lvEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                selectedItemIndex = position;
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
                query.orderByDescending("createdAt");
                query.whereEqualTo("locked",false);
                query.fromPin(DownloadUtils.PIN_EMPLOYEE);
                return query;
            }
        };

        lvEmployee.setEmptyView(tvEmptyView);

        // Set list adapter
        employeeListAdapter = new EmployeeListAdapter(this, factory);
        lvEmployee.setEmptyView(tvEmptyView);
        lvEmployee.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        employeeListAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseUser>() {
            @Override
            public void onLoading() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<ParseUser> list, Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
        lvEmployee.setAdapter(employeeListAdapter);
    }

    public void InitializeComponent() {
        lvEmployee = (ListView) findViewById(R.id.activity_choose_employee_lvemployee);
        progressBar = (ProgressBar) findViewById(R.id.activity_choose_employee_progressbar);
        tvEmptyView = (TextView) findViewById(R.id.activity_choose_employee_tv_empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_employee_choose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

                case android.R.id.home: {
                    finish();
                    break;
                }

            case R.id.action_bar_choose_employee_chon: {
                if (selectedItemIndex >= 0) {
                    Intent intent = new Intent(EmployeeChooseActivity.this, StreetDivideActivity.class);
                    ParseUser employee = (ParseUser) lvEmployee.getItemAtPosition(selectedItemIndex);
                    intent.putExtra("EMPLOYEE_ID", employee.getObjectId());
                    startActivity(intent);
                } else {
                    Toast.makeText(EmployeeChooseActivity.this, getString(R.string.pleaseChooseOneOfEmployeeInList), Toast
                            .LENGTH_LONG)
                            .show();
                }
                break;
            }
            case R.id.action_bar_choose_employee_huy: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}