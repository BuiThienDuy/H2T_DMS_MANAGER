package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.parse.*;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class EmployeeListAdapter extends ParseQueryAdapter<ParseUser> {
    public EmployeeListAdapter(Context context, QueryFactory<ParseUser> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(ParseUser object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_employee, null);
        }
        super.getItemView(object, v, parent);
        TextView tvTitle = (TextView) v.findViewById(R.id.list_employee_tvTitle);
        tvTitle.setText(object.getUsername());

        ParseImageView userPhoto = (ParseImageView) v.findViewById(R.id.list_employee_ivPhoto);
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
        return v;
    }

}
