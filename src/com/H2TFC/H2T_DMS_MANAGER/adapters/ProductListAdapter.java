package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.H2TFC.H2T_DMS_MANAGER.MyApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.controllers.product_management.ProductManagementActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.product_management.ProductNewActivity;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;

import java.text.NumberFormat;
import java.util.Locale;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class ProductListAdapter extends ParseQueryAdapter<Product> {
    public ProductListAdapter(Context context, QueryFactory<Product> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final Product object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_product, null);
        }
        super.getItemView(object, v, parent);
        TextView tvName = (TextView) v.findViewById(R.id.list_product_tv_name);
        tvName.setText(object.getProductName());
        TextView tvUnit = (TextView) v.findViewById(R.id.list_product_tv_unit);
        tvUnit.setText(v.getContext().getString(R.string.unitDesciption) + object.getUnit());
        TextView tvPrice = (TextView) v.findViewById(R.id.list_product_tv_price);

        tvPrice.setText(v.getContext().getString(R.string.proceDescripti) + String.format(Locale.CHINESE, "%1$,.0f", object.getPrice()) + " " + v.getContext().getString(R.string.VND));


        ParseImageView productPhoto = (ParseImageView) v.findViewById(R.id.list_product_iv_product);
        ParseFile photoFile = object.getParseFile("photo");

        productPhoto.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_action_picture));

        if (photoFile != null) {
            productPhoto.setParseFile(photoFile);
            productPhoto.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }

        ImageButton btnDelete = (ImageButton) v.findViewById(R.id.list_product_delete);
        ImageButton btnEdit = (ImageButton) v.findViewById(R.id.list_product_edit);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                object.setStatus(Product.ProductStatus.KHOA.name());
                object.pinInBackground(DownloadUtils.PIN_PRODUCT, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(v.getContext(),v.getContext().getString(R.string.deleteProductSuccess),Toast
                                .LENGTH_LONG)
                                .show();
                    }
                });
                object.saveEventually();
                notifyDataSetChanged();
                loadObjects();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProductNewActivity.class);
                intent.putExtra("EDIT", object.getObjectId());
                ((Activity)v.getContext()).startActivityForResult(intent, MyApplication.REQUEST_EDIT);
            }
        });
        return v;
    }
}
