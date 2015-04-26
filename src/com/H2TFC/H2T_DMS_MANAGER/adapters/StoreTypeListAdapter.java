package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.StoreType;
import com.parse.ParseQueryAdapter;

import java.util.Locale;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class StoreTypeListAdapter extends ParseQueryAdapter<StoreType> {
    public StoreTypeListAdapter(Context context, QueryFactory<StoreType> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(StoreType object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_store_type, null);
        }
        super.getItemView(object, v, parent);

        TextView tvName = (TextView) v.findViewById(R.id.list_store_type_name);
        TextView tvDefaultDebt = (TextView) v.findViewById(R.id.list_store_type_default_debt);

        tvName.setText(object.getStoreTypeName());
        tvDefaultDebt.setText(String.format(Locale.CHINESE, "%1$,.0f", object.getDefaultDebt()) + " " + v.getContext().getString(R
                .string.VND));

        return v;
    }
}
