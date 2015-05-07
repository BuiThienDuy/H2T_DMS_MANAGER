package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.models.Promotion;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.widget.FlowLayout;
import com.parse.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class InvoiceNewAdapter extends ParseQueryAdapter<Product> {
    public InvoiceNewAdapter(Context context, QueryFactory<Product> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final Product object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_product, null);

            // Link to promotion
            final FlowLayout flPromotion = (FlowLayout) v.findViewById(R.id.list_product_fl_promotion);

            ParseQuery<Product> queryProduct = Product.getQuery();
            queryProduct.whereEqualTo("objectId",object.getObjectId());
            queryProduct.fromPin(DownloadUtils.PIN_PRODUCT);


            ParseQuery<Promotion> queryPromotion = Promotion.getQuery();

            final Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            queryPromotion.whereMatchesQuery("promotion_product_gift", queryProduct);
            queryPromotion.fromPin(DownloadUtils.PIN_PROMOTION);
            final View finalV1 = v;
            queryPromotion.findInBackground(new FindCallback<Promotion>() {
                @Override
                public void done(List<Promotion> list, ParseException e) {
                    if (e == null) {
                        for (final Promotion promotion : list) {
                            try {
                            if (currentDate.after(promotion.getPromotionApplyFrom()) && currentDate.before(promotion
                                    .getPromotionApplyTo())) {
                                Button button = new Button(finalV1.getContext());
                                button.setText(promotion.getPromotionName());
                                button.setTextColor(Color.parseColor("#292929"));
                                button.setTextSize((12 / finalV1.getContext().getApplicationContext().getResources().getDisplayMetrics().scaledDensity));
                                button.setBackgroundResource(R.drawable.buttonshape);
                                button.setShadowLayer(Color.parseColor("#5E8AA8"), 0, 0, 5);
                                FlowLayout.LayoutParams layout_button = new FlowLayout.LayoutParams(ViewGroup
                                        .LayoutParams.WRAP_CONTENT, 20);
                                layout_button.setMargins(0, 5, 5, 0);
                                button.setLayoutParams(layout_button);

                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(finalV1.getContext());
                                        dialog.setTitle(finalV1.getContext().getString(R.string.prmotionDialogTitle));
                                        if (promotion.getDiscount() <= 0) {
                                            // Product gift
                                            dialog.setMessage(finalV1.getContext().getString(R.string.promotionType1)
                                                    + finalV1.getContext().getString(R.string.promotionBuyMsg) +
                                                    promotion.getQuantityGift() + " " + promotion.getProductGift()
                                                    .getUnit
                                                            () + "" +
                                                    " " + promotion
                                                    .getProductGift().getProductName() + " " +
                                                    finalV1.getContext().getString(R.string.prmotionGiftMsg) + " " +
                                                    promotion.getQuantityGifted() + " "  + promotion.getProductGifted()
                                                    .getUnit() + " " +
                                                    promotion.getProductGifted().getProductName() + ".");
                                        } else {
                                            // Discount
                                            dialog.setMessage(finalV1.getContext().getString(R.string.promotionType2)
                                                    + finalV1.getContext().getString(R.string.promotionBuyMsg) +
                                                    promotion.getQuantityGift() + " " + promotion.getProductGift()
                                                    .getUnit
                                                            () + " " + promotion
                                                    .getProductGift().getProductName() + finalV1.getContext().getString(R.string.willBeDiscountedMsg) + promotion
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
                            } catch (Exception ex) {

                            }
                        }
                    }
                }
            });
        }
        super.getItemView(object, v, parent);
        TextView tvName = (TextView) v.findViewById(R.id.list_product_tv_name);
        tvName.setText(object.getProductName());
        TextView tvUnit = (TextView) v.findViewById(R.id.list_product_tv_unit);
        tvUnit.setText(object.getUnit());
        TextView tvPrice = (TextView) v.findViewById(R.id.list_product_tv_price);
        tvPrice.setText(String.format(Locale.CHINESE,"%1$,.0f", object.getPrice()) + " " + v.getContext().getString(R.string.VND));


        ParseImageView productPhoto = (ParseImageView) v.findViewById(R.id.list_product_iv_product);
        ParseFile photoFile = object.getParseFile("photo");

        productPhoto.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_contact_empty));

        if (photoFile != null) {
            productPhoto.setParseFile(photoFile);
            productPhoto.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }

        final EditText etAmount = (EditText) v.findViewById(R.id.list_product_et_amount);
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.equals("")) {
                    etAmount.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        return v;
    }

}
