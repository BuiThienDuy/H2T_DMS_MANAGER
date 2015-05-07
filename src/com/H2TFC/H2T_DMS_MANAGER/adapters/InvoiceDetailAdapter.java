package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Invoice;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.models.ProductPurchase;
import com.H2TFC.H2T_DMS_MANAGER.models.Promotion;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.widget.FlowLayout;
import com.parse.*;

import java.util.List;
import java.util.Locale;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class InvoiceDetailAdapter extends ParseQueryAdapter<Product> {
    Invoice myInvoice = null;

    public InvoiceDetailAdapter(Context context, QueryFactory<Product> queryFactory, String invoiceId) {
        super(context, queryFactory);
        try {
            myInvoice = Invoice.getQuery().whereEqualTo("objectId",invoiceId).fromPin(DownloadUtils.PIN_INVOICE).getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getItemView(final Product object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_product_purchase, null);

            // Link to promotion
            final FlowLayout flPromotion = (FlowLayout) v.findViewById(R.id.list_product_fl_promotion);

            EditText etAmount = (EditText) v.findViewById(R.id.list_product_et_amount);


            List<ProductPurchase> productPurchaseList = myInvoice.getProductPurchases();
            for (ProductPurchase productPurchase : productPurchaseList) {
                List<Promotion> promotionRelate = productPurchase.getPromotionRelate();
                if(object.getObjectId().equals(productPurchase.getProductRelate().getObjectId())) {
                    etAmount.setEnabled(false);
                    etAmount.setText("" + productPurchase.getQuantity());
                }

                if (promotionRelate.size() > 0) {
                    for (final Promotion promotion : promotionRelate) {
                        Button button = new Button(v.getContext());
                        button.setText(promotion.getPromotionName());
                        button.setTextColor(Color.parseColor("#292929"));
                        button.setTextSize((12 / v.getContext().getApplicationContext().getResources()
                                .getDisplayMetrics().scaledDensity));
                        button.setBackgroundResource(R.drawable.buttonshape);
                        button.setShadowLayer(Color.parseColor("#5E8AA8"), 0, 0, 5);
                        FlowLayout.LayoutParams layout_button = new FlowLayout.LayoutParams(ViewGroup
                                .LayoutParams.WRAP_CONTENT, 20);
                        layout_button.setMargins(0, 5, 5, 0);
                        button.setLayoutParams(layout_button);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                                dialog.setTitle(v.getContext().getString(R.string.prmotionDialogTitle));
                                if (promotion.getDiscount() <= 0) {
                                    // Product gift
                                    dialog.setMessage(v.getContext().getString(R.string.promotionType1)
                                            + v.getContext().getString(R.string.promotionBuyMsg) +
                                            promotion.getQuantityGift() + " " + promotion.getProductGift()
                                            .getUnit
                                                    () + "" +
                                            " " + promotion
                                            .getProductGift().getProductName() + " " +
                                            v.getContext().getString(R.string.prmotionGiftMsg) + " " +
                                            promotion.getQuantityGifted() + " " + promotion.getProductGifted()
                                            .getUnit() + " " +
                                            promotion.getProductGifted().getProductName() + ".");
                                } else {
                                    // Discount
                                    dialog.setMessage(v.getContext().getString(R.string.promotionType2)
                                            + v.getContext().getString(R.string.promotionBuyMsg) +
                                            promotion.getQuantityGift() + " " + promotion.getProductGift()
                                            .getUnit
                                                    () + " " + promotion
                                            .getProductGift().getProductName() + v.getContext().getString(R.string
                                            .willBeDiscountedMsg) + promotion
                                            .getDiscount() + "%.");
                                }

                                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();
                            }
                        });
                        flPromotion.addView(button);
                    }
                }
            }
        }

        super.getItemView(object, v, parent);
        TextView tvName = (TextView) v.findViewById(R.id.list_product_tv_name);
        tvName.setText(object.getProductName());
        TextView tvUnit = (TextView) v.findViewById(R.id.list_product_tv_unit);
        tvUnit.setText(object.getUnit());
        TextView tvPrice = (TextView) v.findViewById(R.id.list_product_tv_price);
        tvPrice.setText(String.format(Locale.CHINESE, "%1$,.0f", object.getPrice()) + " " + v.getContext().getString(R.string.VND));


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



        return v;
    }
}

