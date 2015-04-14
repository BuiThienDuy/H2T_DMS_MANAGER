package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.controllers.attendance_management.AttendanceManagementActivity;
import com.H2TFC.H2T_DMS_MANAGER.models.Attendance;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;
import com.parse.ParseException;

import java.text.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class EmployeeListAttendanceAdapter extends ParseQueryAdapter<ParseUser> {
    public  int imageCount = 0;

    public EmployeeListAttendanceAdapter(Context context, ParseQueryAdapter.QueryFactory<ParseUser> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(ParseUser object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_employee_attendance, null);
        }
        super.getItemView(object, v, parent);
        TextView tvTitle = (TextView) v.findViewById(R.id.list_employee_attendance_tv_name);
        tvTitle.setText(object.getUsername());

        ParseImageView userPhoto = (ParseImageView) v.findViewById(R.id.list_employee_attendance_iv_photo);
        ParseFile photoFile = object.getParseFile("photo");

        userPhoto.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_contact_empty));

        if (photoFile != null) {
            userPhoto.setParseFile(photoFile);
            userPhoto.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }

        AttendanceManagementActivity activity = (AttendanceManagementActivity) v.getContext();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date fromDate = null;
        Date toDate = null;
        final TextView tvSoAnhChup = (TextView) v.findViewById(R.id.list_employee_attendance_tv_so_anh_chup);
        final TextView tvSoCuaHang = (TextView) v.findViewById(R.id.list_employee_attendance_tv_so_cua_hang_vieng_tham);
        try {
            fromDate = dateFormat.parse(activity.etFromDate.getText().toString());
            toDate = dateFormat.parse(activity.etToDate.getText().toString());


            ParseQuery<Attendance> attendanceParseQuery = Attendance.getQuery();
            attendanceParseQuery.whereEqualTo("employee_id", object.getObjectId());
            attendanceParseQuery.whereGreaterThan("createdAt", fromDate);
            attendanceParseQuery.whereLessThan("createdAt", toDate);
            attendanceParseQuery.fromPin(DownloadUtils.PIN_ATTENDANCE);

            final View finalV = v;
            attendanceParseQuery.findInBackground(new FindCallback<Attendance>() {
                @Override
                public void done(List<Attendance> list, ParseException e) {
                    if (e == null) {
                        ArrayList<String> distinctStoreList = new ArrayList<String>();
                        for (Attendance attendance : list) {
                            if (!distinctStoreList.contains(attendance.getStoreId())) {
                                distinctStoreList.add(attendance.getStoreId());
                            }
                        }
                        imageCount = list.size();
                        tvSoAnhChup.setText(finalV.getContext().getString(R.string.amountOfImageCapture) + list.size());
                        tvSoCuaHang.setText(finalV.getContext().getString(R.string.amountOfVisitedStore) + distinctStoreList.size());
                    }
                }
            });


        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return v;
    }

}
