package com.H2TFC.H2T_DMS_MANAGER.controllers.promotion_management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.controllers.product_management.ProductManagementActivity;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.models.Promotion;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.widget.MyEditDatePicker;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.*;
import com.parse.ParseException;

import java.text.*;
import java.util.*;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class PromotionNewActivity extends Activity {
    BootstrapButton btnOk, btnCancel;
    BootstrapEditText etTitle, etAmount1, etAmount2, etDiscount, etApplyFromDate, etApplyToDate;
    Spinner spnProductName1, spnProductName2, spnPromotionType;
    MyEditDatePicker edpFromDate,edpToDate;
    SimpleDateFormat simpleDateFormat;

    int selectedPromotiontypePosition = 0;

    // Hideable component
    LinearLayout llSanPham, llChietKhau;
    TextView tvSanPham, tvChietKhau;

    String promotionIdEdit;
    Promotion promotionToEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_new);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.addNewPromotionTitle));

        InitializeComponent();
        SetupEvent();

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if(getIntent().hasExtra("EXTRAS_PROMOTION_ID")) {
            setTitle(getString(R.string.updatePromotionTitle));
            promotionIdEdit = getIntent().getStringExtra("EXTRAS_PROMOTION_ID");
            btnOk.setText(getString(R.string.update));

            ParseQuery<Promotion> promotionParseQuery = Promotion.getQuery();
            promotionParseQuery.whereEqualTo("objectId", promotionIdEdit);
            promotionParseQuery.fromPin(DownloadUtils.PIN_PROMOTION);
            try {
                promotionToEdit = promotionParseQuery.getFirst();
                int discount = 0;
                try {
                    discount = promotionToEdit.getDiscount();
                    spnPromotionType.setSelection(1);
                } catch(Exception ex) {

                }
                int amount1 = 0;
                try {
                    amount1 = promotionToEdit.getQuantityGift();
                } catch(Exception ex) {

                }
                int amount2 = 0;
                try {
                    amount2 = promotionToEdit.getQuantityGifted();
                } catch(Exception ex) {

                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                etApplyFromDate.setText(dateFormat.format(promotionToEdit.getPromotionApplyFrom()));
                etApplyToDate.setText(dateFormat.format(promotionToEdit.getPromotionApplyTo()));
                etDiscount.setText("" + discount);
                etAmount1.setText("" + amount1);
                etAmount2.setText("" + amount2);
                etTitle.setText(promotionToEdit.getPromotionName());



            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    private void SetupEvent() {
        etApplyToDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Date fromDate = simpleDateFormat.parse(etApplyFromDate.getText().toString());
                    Date toDate = simpleDateFormat.parse(etApplyToDate.getText().toString());
                    edpFromDate.setMaxDate(fromDate);
                    if (fromDate.after(toDate) || fromDate.equals(toDate)) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(toDate);
                        cal.add(Calendar.DATE, -1);
                        toDate = cal.getTime();
                        etApplyFromDate.setText(simpleDateFormat.format(toDate));
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                final String productName1 = spnProductName1.getSelectedItem().toString();
                final String productName2 = spnProductName2.getSelectedItem().toString();
                int amount1 = 0;
                int amount2 = 0;
                try {
                    amount1 = Integer.parseInt(etAmount1.getText().toString());
                    amount2 = Integer.parseInt(etAmount2.getText().toString());
                } catch (Exception ex) {
                }
                ;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date applyFromDate = null;
                Date applyToDate = null;
                try {
                    applyFromDate = simpleDateFormat.parse(etApplyFromDate.getText().toString());
                    applyToDate = simpleDateFormat.parse(etApplyToDate.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // check error
                boolean error_existed = false;
                StringBuilder error_msg = new StringBuilder("");
                if (productName1.trim().length() == 0 || productName2.trim().length() == 0) {
                    Toast.makeText(PromotionNewActivity.this, getString(R.string.emptyProductPromotion), Toast
                            .LENGTH_LONG).show();
                    Intent intent = new Intent(PromotionNewActivity.this, ProductManagementActivity.class);
                    startActivity(intent);
                }

                if (selectedPromotiontypePosition == 0) {
                    if (title.trim().length() == 0) {
                        error_existed = true;
                        error_msg.append(getString(R.string.blankPromotionTitle));
                    }
                    if (amount1 < 1) {
                        if (!error_existed) {
                            error_existed = true;
                            error_msg.append(getString(R.string.blankPromotionAmount1));
                        }
                    }
                    if (amount2 < 1) {
                        if (!error_existed) {
                            error_existed = true;
                            error_msg.append(getString(R.string.blankPromotionAmount2));
                        }
                    }

                    if (error_existed) {
                        Toast.makeText(PromotionNewActivity.this, error_msg.toString(), Toast.LENGTH_SHORT).show();
                    } else {

                        Promotion promotion = null;
                        if (promotionIdEdit == null) {
                            promotion = new Promotion();
                        } else {
                            promotion = promotionToEdit;
                        }
                        assert promotion != null;
                        promotion.setPromotionName(title);
                        promotion.setQuantityGift(amount1);
                        promotion.setQuantityGifted(amount2);
                        promotion.setPromotionApplyFrom(applyFromDate);
                        promotion.setPromotionApplyTo(applyToDate);

                        final ParseQuery<Product> productParseQuery = Product.getQuery();
                        productParseQuery.whereEqualTo("name", productName1);
                        productParseQuery.fromPin(DownloadUtils.PIN_PRODUCT);
                        final Promotion finalPromotion = promotion;
                        productParseQuery.getFirstInBackground(new GetCallback<Product>() {
                            @Override
                            public void done(Product product, ParseException e) {
                                if (e == null) {
                                    finalPromotion.setProductGift(product);
                                    ParseQuery<Product> productParseQuery2 = Product.getQuery();
                                    productParseQuery2.whereEqualTo("name", productName2);
                                    productParseQuery2.fromPin(DownloadUtils.PIN_PRODUCT);
                                    productParseQuery2.getFirstInBackground(new GetCallback<Product>() {
                                        @Override
                                        public void done(Product product, ParseException e) {
                                            finalPromotion.setProductGifted(product);

                                            finalPromotion.saveEventually();
                                            finalPromotion.pinInBackground(DownloadUtils.PIN_PROMOTION, new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        if (promotionIdEdit == null) {
                                                            Toast.makeText(PromotionNewActivity.this, getString(R.string
                                                                    .addNewPromotionSuccess), Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(PromotionNewActivity.this, getString(R.string.updatePromotionSuccess),
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                } else {
                    int discount = Integer.parseInt(etDiscount.getText().toString());
                    if (title.trim().length() == 0) {
                        error_existed = true;
                        error_msg.append(getString(R.string.blankPromotionTitle));
                    }
                    if (amount1 < 1) {
                        if (!error_existed) {
                            error_existed = true;
                            error_msg.append(getString(R.string.blankPromotionAmount1));
                        }
                    }
                    if (discount < 1) {
                        if (!error_existed) {
                            error_existed = true;
                            error_msg.append(getString(R.string.blankDiscountPromotion));
                        }
                    }

                    if (error_existed) {
                        Toast.makeText(PromotionNewActivity.this, error_msg.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                    Promotion promotion = null;
                    if (promotionIdEdit == null) {
                        promotion = new Promotion();
                    } else {
                        promotion = promotionToEdit;
                    }
                    promotion.setPromotionName(title);
                    promotion.setQuantityGift(amount1);
                    promotion.setDiscount(discount);
                    promotion.setPromotionApplyFrom(applyFromDate);
                    promotion.setPromotionApplyTo(applyToDate);

                    final ParseQuery<Product> productParseQuery = Product.getQuery();
                    productParseQuery.whereEqualTo("name", productName1);
                    productParseQuery.fromPin(DownloadUtils.PIN_PRODUCT);
                    final Promotion finalPromotion = promotion;
                    productParseQuery.getFirstInBackground(new GetCallback<Product>() {
                        @Override
                        public void done(Product product, ParseException e) {
                            if (e == null) {
                                finalPromotion.setProductGift(product);

                                finalPromotion.saveEventually();
                                finalPromotion.pinInBackground(DownloadUtils.PIN_PROMOTION, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            if (promotionIdEdit == null) {
                                                Toast.makeText(PromotionNewActivity.this, getString(R.string
                                                        .addNewPromotionSuccess), Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(PromotionNewActivity.this, getString(R.string.updatePromotionSuccess),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            finish();

                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    });

            btnCancel.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                finish();
            }
        });

        spnPromotionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

            {
                @Override
                public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){
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
                public void onNothingSelected (AdapterView < ? > parent) {

            }
        });
    }

    private void InitializeComponent() {
        etTitle = (BootstrapEditText) findViewById(R.id.activity_promotion_new_et_title);
        etTitle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etAmount1 = (BootstrapEditText) findViewById(R.id.activity_promotion_new_et_1_product_amount);
        etAmount2 = (BootstrapEditText) findViewById(R.id.activity_promotion_new_et_2_product_amount);
        etDiscount = (BootstrapEditText) findViewById(R.id.activity_promotion_new_et_chiet_khau);

        btnOk = (BootstrapButton) findViewById(R.id.activity_promotion_new_btn_ok);
        btnCancel = (BootstrapButton) findViewById(R.id.activity_promotion_new_btn_cancel);

        spnProductName1 = (Spinner) findViewById(R.id.activity_promotion_new_spn_1_product_name);
        spnProductName2 = (Spinner) findViewById(R.id.activity_promotion_new_spn_2_product_name);
        spnPromotionType = (Spinner) findViewById(R.id.activity_promotion_new_spn_promotion_type);

        etApplyFromDate = (BootstrapEditText) findViewById(R.id.activity_promotion_management_et_from_date);
        etApplyToDate = (BootstrapEditText) findViewById(R.id.activity_promotion_management_et_to_date);

        // Hideable component
        llChietKhau = (LinearLayout) findViewById(R.id.activity_promotion_new_ll_chiet_khau);
        llSanPham = (LinearLayout) findViewById(R.id.activity_promotion_new_ll_san_pham_khuyen_mai);
        tvChietKhau = (TextView) findViewById(R.id.activity_promotion_tv_chiet_khau);
        tvSanPham = (TextView) findViewById(R.id.activity_promotion_new_tv_san_pham_khuyen_mai);

        llChietKhau.setVisibility(View.GONE);
        tvChietKhau.setVisibility(View.GONE);

        // Set up calendar
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        edpFromDate = new MyEditDatePicker(PromotionNewActivity.this, R.id
                .activity_promotion_management_et_from_date,day,month,
                year);

        c.add(Calendar.DATE,10);
        day = c.get(Calendar.DATE);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        edpToDate =new MyEditDatePicker(PromotionNewActivity.this, R.id
                .activity_promotion_management_et_to_date,day,month,
                year);

        edpFromDate.updateDisplay();
        edpToDate.updateDisplay();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}