package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Invoice;
import com.H2TFC.H2T_DMS_MANAGER.models.Store;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.widget.AutofitTextView;
import com.parse.*;

import java.text.SimpleDateFormat;
import java.util.Locale;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class InvoiceAdapter extends ParseQueryAdapter<Invoice> {

    public InvoiceAdapter(Context context, QueryFactory<Invoice> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final Invoice object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_invoice, null);
        }
        super.getItemView(object, v, parent);

        final TextView tvName = (TextView) v.findViewById(R.id.list_invoice_tv_name);
        ParseQuery<Store> storeQuery = Store.getQuery();
        storeQuery.whereEqualTo("objectId", object.getStoreId());
        storeQuery.fromPin(DownloadUtils.PIN_STORE);
        final View finalV = v;
        storeQuery.getFirstInBackground(new GetCallback<Store>() {
            @Override
            public void done(Store store, ParseException e) {
                if (store != null) {
                    tvName.setText(finalV.getContext().getString(R.string.invoice) + " " + finalV.getContext().getString(R.string.of) +
                            " " + store.getName());
                }
            }
        });


        final TextView tvEmployee = (TextView) v.findViewById(R.id.list_invoice_tv_employee);
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereEqualTo("objectId", object.getEmployeeId());
        userParseQuery.fromPin(DownloadUtils.PIN_EMPLOYEE);
        final View finalV1 = v;
        userParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    String name = parseUser.getString("name");
                    if (name.trim().length() == 0) {
                        name = parseUser.getUsername();
                    }
                    tvEmployee.setText(finalV1.getContext().getString(R.string.invoiceOwner) + ": " + name);
                }
            }
        });

        TextView tvStatus = (TextView) v.findViewById(R.id.list_invoice_tv_status);
        String status = object.getInvoiceStatus();
        if (status.equals(Invoice.MOI_TAO)) {
            status = v.getContext().getString(R.string.MOI_TAO);
        }
        if (status.equals(Invoice.DANG_XU_LY)) {
            status = v.getContext().getString(R.string.DANG_XU_LY);
        }
        if (status.equals(Invoice.DA_THANH_TOAN)) {
            status = v.getContext().getString(R.string.DA_THANH_TOAN);
        }
        tvStatus.setText(v.getContext().getString(R.string.invoiceStatus) + ": " + status);

        TextView tvDateOrder = (TextView) v.findViewById(R.id.list_invoice_tv_date_order);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tvDateOrder.setText(v.getContext().getString(R.string.invoiceCreateDate) + ": " + dateFormat.format(object
                .getCreatedAt()));

        LinearLayout llProduct = (LinearLayout) v.findViewById(R.id.list_invoice_ll_product);
        llProduct.setBackgroundColor(Invoice.getStatusColor(object.getInvoiceStatus()));

        TextView tvPrice = (TextView) v.findViewById(R.id.list_invoice_tv_price);
        tvPrice.setText(v.getContext().getString(R.string.invoiceTotalPrice) + ": " + String.format(Locale.CHINESE,
                "%1$,.0f", object.getInvoicePrice()) + " " + v.getContext().getString(R.string
                .VND));

        return v;
    }
}

