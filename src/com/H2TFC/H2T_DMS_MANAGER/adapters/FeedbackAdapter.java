package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Feedback;
import com.H2TFC.H2T_DMS_MANAGER.models.Store;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.text.SimpleDateFormat;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class FeedbackAdapter extends ParseQueryAdapter<Feedback> {
    public FeedbackAdapter(Context context, QueryFactory<Feedback> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(Feedback object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_feedback, null);
        }
        super.getItemView(object, v, parent);

        TextView tvTitle = (TextView) v.findViewById(R.id.list_feedback_tv_title);
        TextView tvStatus = (TextView) v.findViewById(R.id.list_feedback_tv_status);
        final TextView tvStore = (TextView) v.findViewById(R.id.list_feedback_tv_store);
        TextView tvCreatedDate = (TextView) v.findViewById(R.id.list_feedback_tv_created_date);
        TextView tvContent = (TextView) v.findViewById(R.id.list_feedback_tv_content);

        tvTitle.setText(" " + object.getTitle());

        String status = object.getStatus();
        if (status.equals(Feedback.MOI_TAO)) {
            status = v.getContext().getString(R.string.haveCreated);
        }
        if (status.equals(Feedback.DANG_XEM_XET)) {
            status = v.getContext().getString(R.string.onPending);
        }
        if (status.equals(Feedback.DA_XU_LY)) {
            status = v.getContext().getString(R.string.feedbackCompleted);
        }

        tvStatus.setText(" " + status);

        ParseQuery<Store> query = Store.getQuery()
                .whereEqualTo("objectId", object.getStoreId());
        query.getFirstInBackground(new GetCallback<Store>() {
            @Override
            public void done(Store store, ParseException e) {
                if (e == null) {
                    tvStore.setText(" " + store.getName());
                }
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tvCreatedDate.setText(" " + dateFormat.format(object.getCreatedAt()));

        tvContent.setText(object.getDescription());

        return v;
    }
}
