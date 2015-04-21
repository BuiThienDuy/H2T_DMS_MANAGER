package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Promotion;
import com.H2TFC.H2T_DMS_MANAGER.models.Store;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;
import org.apache.http.impl.cookie.DateParseException;

import java.text.SimpleDateFormat;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class PromotionListAdapter extends ParseQueryAdapter<Promotion> {

    public PromotionListAdapter(Context context, QueryFactory<Promotion> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(Promotion object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_promotion, null);
        }
        super.getItemView(object, v, parent);

        TextView tvFromDate = (TextView) v.findViewById(R.id.list_promotion_tv_from_date);
        TextView tvToDate = (TextView) v.findViewById(R.id.list_promotion_tv_to_date);
        TextView tvTitle = (TextView) v.findViewById(R.id.list_promotion_tv_title);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tvTitle.setText(object.getPromotionName());

        try {
            tvToDate.setText(v.getContext().getString(R.string.applyFromDateDescription) + " " + dateFormat.format(object
                    .getPromotionApplyTo
                            ()));
            tvFromDate.setText(v.getContext().getString(R.string.appyToDateDescription)+ " " + dateFormat.format
                    (object.getPromotionApplyFrom()));
        } catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return v;
    }
}
