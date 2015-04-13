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
public class EmployeeViewLockedListAdapter extends ParseQueryAdapter<ParseUser> {
    public EmployeeViewLockedListAdapter(Context context, ParseQueryAdapter.QueryFactory<ParseUser> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(ParseUser object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_employee_locked, null);
        }
        super.getItemView(object, v, parent);

        TextView tvName = (TextView) v.findViewById(R.id.list_employee_locked_tv_name);
        tvName.setText(object.getString("name"));
        TextView tvUsername = (TextView) v.findViewById(R.id.list_employee_locked_tv_user_name);
        tvUsername.setText(object.getUsername());

        ParseImageView userPhoto = (ParseImageView) v.findViewById(R.id.list_employee_locked_iv_photo);
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

        ParseImageView userPhotoLocked = (ParseImageView) v.findViewById(R.id.list_employee_locked_iv_locked);
        if(object.getBoolean("locked")) {
            userPhotoLocked.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_action_secure));
        } else {
            userPhotoLocked.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_action_not_secure));
        }

        return v;
    }
}
