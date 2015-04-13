package com.H2TFC.H2T_DMS_MANAGER.controllers.employee_management;

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
import com.H2TFC.H2T_DMS_MANAGER.adapters.EmployeeListAdapter;
import com.H2TFC.H2T_DMS_MANAGER.adapters.EmployeeViewLockedListAdapter;
import com.H2TFC.H2T_DMS_MANAGER.controllers.LoginActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.street_divide.StreetDivideActivity;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created by c4sau on 25/03/2015.
 */
public class EmployeeViewLockedActivity extends Activity {
    EmployeeViewLockedListAdapter employeeListViewLockedAdapter;

    ListView lvEmployee;
    TextView tvEmptyView;

    ProgressBar progressBar;

    int selectedItemIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_view_locked);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        InitializeComponent();
        SetupListView();

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
                query.whereEqualTo("locked",true);
                query.fromPin(DownloadUtils.PIN_EMPLOYEE);
                return query;
            }
        };

        lvEmployee.setEmptyView(tvEmptyView);

        // Set list adapter
        employeeListViewLockedAdapter = new EmployeeViewLockedListAdapter(this, factory);
        lvEmployee.setEmptyView(tvEmptyView);
        lvEmployee.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        employeeListViewLockedAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseUser>() {
            @Override
            public void onLoading() {
                tvEmptyView.setText("");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<ParseUser> list, Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
        lvEmployee.setAdapter(employeeListViewLockedAdapter);
    }

    public void InitializeComponent() {
        lvEmployee = (ListView) findViewById(R.id.activity_employee_locked_lv_employee);
        progressBar = (ProgressBar) findViewById(R.id.activity_employee_locked_progressbar);
        tvEmptyView = (TextView) findViewById(R.id.activity_employee_locked_tv_empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_employee_view_locked, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                finish();
                break;
            }

            case R.id.action_bar_employee_view_locked_lock_unlock: {
                if (selectedItemIndex >= 0) {
                    final ParseUser employee = (ParseUser) lvEmployee.getItemAtPosition(selectedItemIndex);

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("username", employee.getUsername());
                    params.put("locked", false);

                    ParseCloud.callFunctionInBackground("modifyUser", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object o, ParseException e) {
                            if (e == null) {
                                Toast.makeText(EmployeeViewLockedActivity.this,getString(R.string.unlockEmployeeSuccess),Toast.LENGTH_LONG)
                                        .show();
                                employee.put("locked", false);
                                employee.pinInBackground(DownloadUtils.PIN_EMPLOYEE, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            employeeListViewLockedAdapter.loadObjects();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(EmployeeViewLockedActivity.this,e.getMessage(),Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(EmployeeViewLockedActivity.this, getString(R.string.pleaseChooseOneOfEmployeeInList), Toast
                            .LENGTH_LONG)
                            .show();
                }
                break;
            }

            case R.id.action_bar_employee_view_locked_log_out: {
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(EmployeeViewLockedActivity.this);
                confirmDialog.setMessage(getString(R.string.confirmLogOut));
                confirmDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(EmployeeViewLockedActivity.this, LoginActivity.class);
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

}