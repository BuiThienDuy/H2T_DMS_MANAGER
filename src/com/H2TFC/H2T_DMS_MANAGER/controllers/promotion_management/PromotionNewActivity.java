package com.H2TFC.H2T_DMS_MANAGER.controllers.promotion_management;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.models.Promotion;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.*;

import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class PromotionNewActivity extends Activity {
    BootstrapButton btnOk, btnCancel;
    BootstrapEditText etTitle, etAmount1, etAmount2, etDiscount;
    Spinner spnProductName1, spnProductName2, spnPromotionType;

    int selectedPromotiontypePosition = 0;

    // Hideable component
    LinearLayout llSanPham, llChietKhau;
    TextView tvSanPham, tvChietKhau;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_new);

        InitializeComponent();

        SetupEvent();
    }

    private void SetupEvent() {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                final String productName1 = spnProductName1.getSelectedItem().toString();
                final String productName2 = spnProductName2.getSelectedItem().toString();
                int amount1 = Integer.parseInt(etAmount1.getText().toString());

                if (selectedPromotiontypePosition == 0) {
                    int amount2 = Integer.parseInt(etAmount2.getText().toString());
                    final Promotion promotion = new Promotion();
                    promotion.setPromotionName(title);
                    promotion.setQuantityGift(amount1);
                    promotion.setQuantityGifted(amount2);

                    final ParseQuery<Product> productParseQuery = Product.getQuery();
                    productParseQuery.whereEqualTo("name", productName1);
                    productParseQuery.fromPin(DownloadUtils.PIN_PRODUCT);
                    productParseQuery.getFirstInBackground(new GetCallback<Product>() {
                        @Override
                        public void done(Product product, ParseException e) {
                            if (e == null) {
                                promotion.setProductGift(product);
                                ParseQuery<Product> productParseQuery2 = Product.getQuery();
                                productParseQuery2.whereEqualTo("name", productName2);
                                productParseQuery2.fromPin(DownloadUtils.PIN_PRODUCT);
                                productParseQuery2.getFirstInBackground(new GetCallback<Product>() {
                                    @Override
                                    public void done(Product product, ParseException e) {
                                        promotion.setProductGifted(product);

                                        promotion.saveEventually();
                                        promotion.pinInBackground(DownloadUtils.PIN_PROMOTION, new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Toast.makeText(PromotionNewActivity.this, getString(R.string
                                                            .addNewPromotionSuccess), Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                } else {
                    int discount = Integer.parseInt(etDiscount.getText().toString());

                    final Promotion promotion = new Promotion();
                    promotion.setPromotionName(title);
                    promotion.setQuantityGift(amount1);
                    promotion.setDiscount(discount);

                    final ParseQuery<Product> productParseQuery = Product.getQuery();
                    productParseQuery.whereEqualTo("name", productName1);
                    productParseQuery.fromPin(DownloadUtils.PIN_PRODUCT);
                    productParseQuery.getFirstInBackground(new GetCallback<Product>() {
                        @Override
                        public void done(Product product, ParseException e) {
                            if (e == null) {
                                promotion.setProductGift(product);

                                promotion.saveEventually();
                                promotion.pinInBackground(DownloadUtils.PIN_PROMOTION, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(PromotionNewActivity.this, getString(R.string
                                                    .addNewPromotionSuccess), Toast.LENGTH_LONG).show();
                                            finish();

                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spnPromotionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPromotiontypePosition = position;
                if (position == 0) {
                    llChietKhau.setVisibility(View.GONE);
                    tvChietKhau.setVisibility(View.GONE);

                    llSanPham.setVisibility(View.VISIBLE);
                    tvSanPham.setVisibility(View.VISIBLE);
                } else {
                    llChietKhau.setVisibility(View.VISIBLE);
                    tvChietKhau.setVisibility(View.VISIBLE);

                    llSanPham.setVisibility(View.GONE);
                    tvSanPham.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void InitializeComponent() {
        etTitle = (BootstrapEditText) findViewById(R.id.activity_promotion_new_et_title);
        etAmount1 = (BootstrapEditText) findViewById(R.id.activity_promotion_new_et_1_product_amount);
        etAmount2 = (BootstrapEditText) findViewById(R.id.activity_promotion_new_et_2_product_amount);
        etDiscount = (BootstrapEditText) findViewById(R.id.activity_promotion_new_et_chiet_khau);

        btnOk = (BootstrapButton) findViewById(R.id.activity_promotion_new_btn_ok);
        btnCancel = (BootstrapButton) findViewById(R.id.activity_promotion_new_btn_cancel);

        spnProductName1 = (Spinner) findViewById(R.id.activity_promotion_new_spn_1_product_name);
        spnProductName2 = (Spinner) findViewById(R.id.activity_promotion_new_spn_2_product_name);
        spnPromotionType = (Spinner) findViewById(R.id.activity_promotion_new_spn_promotion_type);

        // Hideable component
        llChietKhau = (LinearLayout) findViewById(R.id.activity_promotion_new_ll_chiet_khau);
        llSanPham = (LinearLayout) findViewById(R.id.activity_promotion_new_ll_san_pham_khuyen_mai);
        tvChietKhau = (TextView) findViewById(R.id.activity_promotion_tv_chiet_khau);
        tvSanPham = (TextView) findViewById(R.id.activity_promotion_new_tv_san_pham_khuyen_mai);

        llChietKhau.setVisibility(View.GONE);
        tvChietKhau.setVisibility(View.GONE);

        // Set up spinner
        String[] items = new String[]{
                getString(R.string.tangPham), getString(R.string.chietKhau)
        };

        ArrayAdapter promotionTypeAdapter = new ArrayAdapter<String>(PromotionNewActivity.this, android.R.layout
                .simple_spinner_item, items);
        spnPromotionType.setAdapter(promotionTypeAdapter);

        ParseQuery<Product> productNameParseQuery = Product.getQuery();
        productNameParseQuery.whereNotEqualTo("status", Product.ProductStatus.KHOA.name());
        productNameParseQuery.fromPin(DownloadUtils.PIN_PRODUCT);
        productNameParseQuery.findInBackground(new FindCallback<Product>() {
            @Override
            public void done(List<Product> list, ParseException e) {
                if (e == null) {
                    String[] items = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        items[i] = list.get(i).getProductName();
                    }

                    ArrayAdapter<String> productNameAdapter = new ArrayAdapter<String>(PromotionNewActivity.this,
                            android.R
                                    .layout
                                    .simple_spinner_item, items);
                    spnProductName1.setAdapter(productNameAdapter);

                    ArrayAdapter<String> productNameAdapter2 = new ArrayAdapter<String>(PromotionNewActivity.this, android.R.layout
                            .simple_spinner_item, items);
                    spnProductName2.setAdapter(productNameAdapter2);
                }
            }
        });
    }
}