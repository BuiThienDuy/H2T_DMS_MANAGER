package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Attendance;
import com.H2TFC.H2T_DMS_MANAGER.models.Store;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;

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
public class AttendanceDetailAdapter extends ParseQueryAdapter<Attendance> {
    public AttendanceDetailAdapter(Context context, QueryFactory<Attendance> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(Attendance object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_attendance_detail, null);
        }
        super.getItemView(object, v, parent);

        // Set photo
        ParseImageView storePhoto = (ParseImageView) v.findViewById(R.id.list_attendance_detail_iv_photo);
        ParseFile photoFile = object.getParseFile("photo");

        storePhoto.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_action_picture));

        if (photoFile != null) {
            storePhoto.setParseFile(photoFile);
            storePhoto.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }

        // Set store name, photo created date
        final TextView tvStoreName = (TextView) v.findViewById(R.id.list_attendance_detail_tv_store_name);
        ParseQuery<Store> storeParseQuery = Store.getQuery();
        storeParseQuery.whereEqualTo("objectId", object.getStoreId());
        storeParseQuery.fromPin(DownloadUtils.PIN_STORE);
        final View finalV = v;
        storeParseQuery.getFirstInBackground(new GetCallback<Store>() {
            @Override
            public void done(Store store, ParseException e) {
                if( e == null) {
                    tvStoreName.setText(finalV.getContext().getString(R.string.store)+ ": " + store.getName());
                }
            }
        });

        TextView tvCreatedDate = (TextView) v.findViewById(R.id.list_attendance_detail_tv_created_date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tvCreatedDate.setText(v.getContext().getString(R.string.takePhotoAt) +": " + dateFormat.format(object.getCreatedAt()) );
                
        return v;


    }
}
