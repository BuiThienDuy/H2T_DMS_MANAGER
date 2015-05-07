package com.H2TFC.H2T_DMS_MANAGER.controllers.attendance_management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.EmployeeListAttendanceAdapter;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.widget.MyEditDatePicker;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.*;
import com.parse.ParseException;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class AttendanceManagementActivity extends Activity {
    public BootstrapEditText etFromDate, etToDate;
    EmployeeListAttendanceAdapter attendanceAdapter;
    ListView lvEmployee;
    ProgressBar progressBar;
    TextView tvEmptyView;
    MyEditDatePicker edpFromDate,edpToDate;
    SimpleDateFormat simpleDateFormat;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_management);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.attendanceManagementTitle));

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.fromPin(DownloadUtils.PIN_EMPLOYEE);
        try {
            if(userQuery.count() == 0) {
                DownloadUtils.DownloadParseEmployee(AttendanceManagementActivity.this, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        DownloadUtils.DownloadParseAttendance(AttendanceManagementActivity.this, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                attendanceAdapter.loadObjects();
                            }
                        });
                    }
                });
            } else {
                DownloadUtils.DownloadParseAttendance(AttendanceManagementActivity.this, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        attendanceAdapter.loadObjects();
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



        InitializeComponent();
        SetupListView();
        SetupEvent();
    }

    private void SetupEvent() {
        etFromDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                attendanceAdapter.loadObjects();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etToDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Date fromDate = simpleDateFormat.parse(etFromDate.getText().toString());
                    Date toDate = simpleDateFormat.parse(etToDate.getText().toString());
                    edpFromDate.setMaxDate(fromDate);
                    if(fromDate.after(toDate) || fromDate.equals(toDate)) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(toDate);
                        cal.add(Calendar.DATE, -1);
                        toDate = cal.getTime();
                        etFromDate.setText(simpleDateFormat.format(toDate));
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                attendanceAdapter.loadObjects();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lvEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(AttendanceManagementActivity.this, AttendanceDetailActivity.class);
                intent.putExtra("EXTRAS_EMPLOYEE_ID", attendanceAdapter.getItem(position).getObjectId());
                intent.putExtra("EXTRAS_FROM_DATE", etFromDate.getText().toString());
                intent.putExtra("EXTRAS_TO_DATE", etToDate.getText().toString());
                startActivity(intent);

            }
        });
    }

    private void InitializeComponent() {
        lvEmployee = (ListView) findViewById(R.id.activity_attendance_management_lv_employee);

        progressBar = (ProgressBar) findViewById(R.id.activity_attendance_management_progressbar);
        tvEmptyView = (TextView) findViewById(R.id.activity_attendance_management_tv_empty);

        etFromDate = (BootstrapEditText) findViewById(R.id.activity_attendance_management_et_from_date);
        etToDate = (BootstrapEditText) findViewById(R.id.activity_attendance_management_et_to_date);

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        edpFromDate = new MyEditDatePicker(AttendanceManagementActivity.this, R.id
                .activity_attendance_management_et_from_date, day, month,
                year);

        c.add(Calendar.DATE, 1);
        day = c.get(Calendar.DATE);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        edpToDate = new MyEditDatePicker(AttendanceManagementActivity.this, R.id
                .activity_attendance_management_et_to_date, day, month,
                year);


        edpFromDate.updateDisplay();
        edpToDate.updateDisplay();

        try {
            edpFromDate.setMaxDate(simpleDateFormat.parse(etToDate.getText().toString()));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

    }

    private void SetupListView() {
        // Query data from local data store
        ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>() {
            @Override
            public ParseQuery<ParseUser> create() {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
                query.orderByDescending("createdAt");
                query.whereEqualTo("locked", false);
                query.fromPin(DownloadUtils.PIN_EMPLOYEE);
                return query;
            }
        };

        lvEmployee.setEmptyView(tvEmptyView);

        // Set list adapter
        attendanceAdapter = new EmployeeListAttendanceAdapter(this, factory);
        lvEmployee.setEmptyView(tvEmptyView);
        lvEmployee.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        attendanceAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseUser>() {
            @Override
            public void onLoading() {
                tvEmptyView.setText("");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<ParseUser> list, Exception e) {
                if (list.size() == 0) {
                    tvEmptyView.setText(getString(R.string.lvEmptyEmployee));
                }
                progressBar.setVisibility(View.GONE);

            }
        });
        lvEmployee.setAdapter(attendanceAdapter);
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