package com.H2TFC.H2T_DMS_MANAGER.controllers.attendance_management;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.AttendanceDetailAdapter;
import com.H2TFC.H2T_DMS_MANAGER.adapters.EmployeeListAttendanceAdapter;
import com.H2TFC.H2T_DMS_MANAGER.models.Attendance;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class AttendanceDetailActivity extends Activity {
    GridView gvAttendance;
    TextView tvEmptyView;
    ProgressBar progressBar;
    AttendanceDetailAdapter attendanceAdapter;
    private String employeeId;
    Date FromDate,ToDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.employeeAttendanceDetail));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if(getIntent().hasExtra("EXTRAS_EMPLOYEE_ID")) {
            employeeId = getIntent().getStringExtra("EXTRAS_EMPLOYEE_ID");
        }
        if(getIntent().hasExtra("EXTRAS_FROM_DATE")) {
            try {
                FromDate = dateFormat.parse(getIntent().getStringExtra("EXTRAS_FROM_DATE"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(getIntent().hasExtra("EXTRAS_TO_DATE")) {
            try {
                ToDate = dateFormat.parse(getIntent().getStringExtra("EXTRAS_TO_DATE"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        InitializeComponent();
        SetupListView();
    }

    private void InitializeComponent() {
        gvAttendance = (GridView) findViewById(R.id.activity_attendance_detail_grid);
        tvEmptyView = (TextView) findViewById(R.id.activity_attendance_detail_tv_empty);
        progressBar = (ProgressBar) findViewById(R.id.activity_attendance_detail_progressbar);
    }

    private void SetupListView() {
        // Query data from local data store
        ParseQueryAdapter.QueryFactory<Attendance> factory = new ParseQueryAdapter.QueryFactory<Attendance>() {
            @Override
            public ParseQuery<Attendance> create() {
                ParseQuery<Attendance> query = Attendance.getQuery();
                query.whereEqualTo("employee_id", employeeId);
                query.whereGreaterThan("createdAt", FromDate);
                query.whereLessThan("createdAt", ToDate);
                query.orderByDescending("createdAt");
                query.fromPin(DownloadUtils.PIN_ATTENDANCE);
                return query;
            }
        };

        gvAttendance.setEmptyView(tvEmptyView);

        // Set list adapter
        attendanceAdapter = new AttendanceDetailAdapter(this, factory);
        gvAttendance.setEmptyView(tvEmptyView);
        gvAttendance.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        attendanceAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Attendance>() {
            @Override
            public void onLoading() {
                tvEmptyView.setText("");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<Attendance> list, Exception e) {
                if(list.size() == 0) {
                    tvEmptyView.setText(getString(R.string.noAttendancePictureFoundOnListView));
                }
                progressBar.setVisibility(View.GONE);
            }
        });
        gvAttendance.setAdapter(attendanceAdapter);
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