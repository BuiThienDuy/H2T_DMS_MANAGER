package com.H2TFC.H2T_DMS_MANAGER.controllers.feed_back_management;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.EmployeeListAttendanceAdapter;
import com.H2TFC.H2T_DMS_MANAGER.adapters.FeedbackAdapter;
import com.H2TFC.H2T_DMS_MANAGER.models.Feedback;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.widget.MyEditDatePicker;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.*;

import java.util.Calendar;
import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class FeedbackManagementActivity extends Activity {
    FeedbackAdapter feedbackAdapter;
    ListView lvFeedback;
    ProgressBar progressBar;
    TextView tvEmptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_management);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.viewFeedbackTitle));


            DownloadUtils.DownloadParseFeedback(FeedbackManagementActivity.this,new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    feedbackAdapter.loadObjects();
                }
            });

        InitializeComponent();
        SetupListView();
    }

    private void InitializeComponent() {
        lvFeedback = (ListView) findViewById(R.id.activity_feedback_management_lv_feedback);

        progressBar = (ProgressBar) findViewById(R.id.activity_feedback_management_progressbar);
        tvEmptyView = (TextView) findViewById(R.id.activity_feedback_management_tv_empty);
    }

    private void SetupListView() {
        // Query data from local data store
        ParseQueryAdapter.QueryFactory<Feedback> factory = new ParseQueryAdapter.QueryFactory<Feedback>() {
            @Override
            public ParseQuery<Feedback> create() {
                ParseQuery<Feedback> query = Feedback.getQuery();
                query.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
                query.orderByDescending("createdAt");
                query.fromPin(DownloadUtils.PIN_FEEDBACK);
                return query;
            }
        };

        lvFeedback.setEmptyView(tvEmptyView);

        // Set list adapter
        feedbackAdapter = new FeedbackAdapter(this, factory);
        lvFeedback.setEmptyView(tvEmptyView);
        lvFeedback.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        feedbackAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Feedback>() {
            @Override
            public void onLoading() {
                tvEmptyView.setText("");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<Feedback> list, Exception e) {
                if (list.size() == 0) {
                    tvEmptyView.setText(getString(R.string.emptyFeedback));
                }
                progressBar.setVisibility(View.GONE);

            }
        });
        lvFeedback.setAdapter(feedbackAdapter);
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