package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.H2TFC.H2T_DMS_MANAGER.MyMainApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.controllers.product_management.ProductNewActivity;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.daimajia.swipe.SwipeLayout;
import com.parse.*;

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

        final SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(R.id.list_product_swipe_layout);

        LinearLayout surfaceLayout = (LinearLayout) v.findViewById(R.id.list_product_surface_view);
        surfaceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swipeLayout.getOpenStatus() == SwipeLayout.Status.Close) {
                    swipeLayout.open(true);
                } else {
                    swipeLayout.close();
                }
            }
        });

        ImageButton btnDelete = (ImageButton) v.findViewById(R.id.list_product_delete);
        ImageButton btnEdit = (ImageButton) v.findViewById(R.id.list_product_edit);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle(v.getContext().getString(R.string.confirmDeleteProductTitle));
                dialog.setMessage(v.getContext().getString(R.string.confirmDeleteProduct));
                dialog.setPositiveButton(v.getContext().getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        object.setStatus(Product.ProductStatus.KHOA.name());
                        object.pinInBackground(DownloadUtils.PIN_PRODUCT, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(v.getContext(), v.getContext().getString(R.string.deleteProductSuccess), Toast
                                        .LENGTH_LONG)
                                        .show();
                            }
                        });
                        object.saveEventually();
                        notifyDataSetChanged();
                        loadObjects();
                    }
                });
                dialog.setNegativeButton(v.getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProductNewActivity.class);
                intent.putExtra("EDIT", object.getObjectId());
                ((Activity) v.getContext()).startActivityForResult(intent, MyMainApplication.REQUEST_EDIT);
            }
        });
        return v;
    }
}
