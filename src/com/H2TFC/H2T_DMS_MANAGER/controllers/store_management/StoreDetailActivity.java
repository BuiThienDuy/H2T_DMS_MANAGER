package com.H2TFC.H2T_DMS_MANAGER.controllers.store_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Invoice;
import com.H2TFC.H2T_DMS_MANAGER.models.Store;
import com.H2TFC.H2T_DMS_MANAGER.models.StoreImage;
import com.H2TFC.H2T_DMS_MANAGER.models.StoreType;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
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
public class StoreDetailActivity extends Activity {
    BootstrapButton btnTrungBay, btnGiaHan, btnQuayVe;
    BootstrapEditText etCongNo;
    BootstrapEditText etTenCuaHang, etTenChuCuaHang, etDiaChi, etSDT, etDoanhThu, etMatHangDoiThu;
    ImageView ivCongNo;
    TextView tvBucAnhDaChup;

    Spinner spnLoaiCuaHang;

    String storeID, store_image_id;
    LinearLayout layoutTitleCongNo, layoutEditCongNo, layoutButton;
    ArrayAdapter<String> storeTypeAdapter;
    private double[] congNoMax;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        congNoMax = new double[1];
        congNoMax[0] = 0;


        InitializeComponent();
        GetAndShowStoreDetail();
        SetupEvent();

        LoadImageCount();
        if (getIntent().hasExtra("EXTRAS_STORE_ID")) {
            storeID = getIntent().getStringExtra("EXTRAS_STORE_ID");
            DownloadUtils.DownloadParseStoreImage(StoreDetailActivity.this, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    LoadImageCount();
                }
            });
        }
        if (getIntent().hasExtra("EXTRAS_STORE_IMAGE_ID")) {
            store_image_id = getIntent().getStringExtra("EXTRAS_STORE_IMAGE_ID");
        }

        if(getIntent().hasExtra("EXTRAS_STORE_POINT")) {
            btnGiaHan.setVisibility(View.VISIBLE);
            btnQuayVe.setVisibility(View.VISIBLE);
        }
    }

    private void GetAndShowStoreDetail() {
        ParseQuery<StoreType> storeTypeParseQuery = StoreType.getQuery();
        storeTypeParseQuery.whereNotEqualTo("locked",true);
        storeTypeParseQuery.fromPin(DownloadUtils.PIN_STORE_TYPE);
        try {
            List<StoreType> storeTypeList = storeTypeParseQuery.find();
            String[] items = new String[storeTypeList.size()];
            for (int i = 0; i < storeTypeList.size(); i++) {
                items[i] = storeTypeList.get(i).getStoreTypeName();
            }
            storeTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
            spnLoaiCuaHang.setAdapter(storeTypeAdapter);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (storeID != null) {
            // Get the store image
            ParseQuery<StoreImage> imageQuery = StoreImage.getQuery();
            imageQuery.fromPin(DownloadUtils.PIN_STORE_IMAGE);
            imageQuery.whereEqualTo("store_id", storeID);

            ParseQuery<StoreImage> localImageQuery = StoreImage.getQuery();
            localImageQuery.fromPin("PIN_DRAFT_PHOTO");
            localImageQuery.whereEqualTo("store_id", storeID);
            try {
                int totalImage = imageQuery.count() + localImageQuery.count();
                tvBucAnhDaChup.setText(totalImage + getString(R.string.captureImage));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Get the store
            ParseQuery<Store> storeParseQuery = Store.getQuery();
            storeParseQuery.whereEqualTo("objectId", storeID);
            storeParseQuery.fromPin(DownloadUtils.PIN_STORE);
            storeParseQuery.getFirstInBackground(new GetCallback<Store>() {
                @Override
                public void done(Store store, ParseException e) {
                    if (e == null) {
                        try {
                            etTenCuaHang.setText(store.getName());
                            etTenChuCuaHang.setText(store.getStoreOwner());
                            etDiaChi.setText(store.getAddress());
                            etSDT.setText(store.getPhoneNumber());
                            etDoanhThu.setText(String.format(Locale.CHINESE, "%1$,.0f", store.getIncome()));
                            etMatHangDoiThu.setText(store.getCompetitor());
                            congNoMax[0] = store.getMaxDebt();
                            int itemPosition = -1;
                            for (int index = 0, count = storeTypeAdapter.getCount(); index < count; ++index) {
                                if (storeTypeAdapter.getItem(index).equals(store.getStoreType())) {
                                    itemPosition = index;
                                    break;
                                }
                            }
                            spnLoaiCuaHang.setSelection(itemPosition);
                            if (store.getStatus().equals(Store.StoreStatus.BAN_HANG.name())) {
                                layoutTitleCongNo.setVisibility(View.VISIBLE);
                                layoutEditCongNo.setVisibility(View.VISIBLE);
                                GetAndShowDebt(store);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {

                    }
                }
            });
        }
    }

    private void GetAndShowDebt(final Store store) {
        final double[] congNoHienTai = new double[1];
        congNoHienTai[0] = 0;

        ParseQuery<Invoice> invoiceParseQuery = Invoice.getQuery();
        invoiceParseQuery.whereEqualTo("storeId", store.getObjectId());
        invoiceParseQuery.fromPin(DownloadUtils.PIN_INVOICE);
        invoiceParseQuery.findInBackground(new FindCallback<Invoice>() {
            @Override
            public void done(List<Invoice> list, ParseException e) {
                if (e == null) {
                    for (Invoice invoice : list) {
                        congNoHienTai[0] += invoice.getInvoicePrice();
                    }


                    ParseQuery<StoreType> storeTypeParseQuery = StoreType.getQuery();
                    storeTypeParseQuery.whereEqualTo("store_type_name", store.getStoreType());
                    storeTypeParseQuery.fromPin(DownloadUtils.PIN_STORE_TYPE);
                    storeTypeParseQuery.getFirstInBackground(new GetCallback<StoreType>() {
                        @Override
                        public void done(StoreType storeType, ParseException e) {
                            if (e == null) {
                                if (congNoMax[0] == 0) {
                                    congNoMax[0] = storeType.getDefaultDebt();
                                }
                                if (congNoHienTai[0] >= congNoMax[0]) {
                                    ivCongNo.setBackgroundColor(Color.RED);
                                } else if (congNoHienTai[0] < congNoMax[0] && congNoHienTai[0] >= congNoMax[0] * 2 / 3) {
                                    ivCongNo.setBackgroundColor(Color.YELLOW);
                                } else {
                                    ivCongNo.setBackgroundColor(Color.GREEN);
                                }


                                etCongNo.setText(String.format(Locale.CHINESE, "%1$,.0f", congNoHienTai[0]) + "/" + String.format
                                        (Locale.CHINESE, "%1$,.0f", congNoMax[0]) + " " + getString(R.string.VND));
                            }
                        }
                    });


                }
            }
        });
    }

    private void SetupEvent() {
        btnTrungBay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreDetailActivity.this, TrungBayActivity.class);
                intent.putExtra("EXTRAS_STORE_ID", storeID);
                intent.putExtra("EXTRAS_STORE_IMAGE_ID", store_image_id);
                startActivity(intent);
            }
        });


        btnGiaHan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<Store> storeParseQuery = Store.getQuery();
                storeParseQuery.fromPin(DownloadUtils.PIN_STORE);
                storeParseQuery.whereEqualTo("objectId", storeID);
                storeParseQuery.getFirstInBackground(new GetCallback<Store>() {
                    @Override
                    public void done(final Store store, ParseException e) {
                        if (e == null) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(StoreDetailActivity.this);

                            alert.setTitle(getString(R.string.modifiedDebtTitle));
                            alert.setMessage(getString(R.string.modifiedDebtMessage));

                            // Set an EditText view to get user input
                            final EditText input = new EditText(StoreDetailActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            alert.setView(input);

                            alert.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    double value = Double.parseDouble(input.getText().toString());
                                    store.setMaxDebt(value);
                                    store.saveEventually();
                                    store.pinInBackground(DownloadUtils.PIN_STORE, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(StoreDetailActivity.this, getString(R.string
                                                        .modifyDebtSuccess), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });

                            alert.show();

                        }
                    }
                });
            }
        });

        btnQuayVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void InitializeComponent() {
        etTenCuaHang = (BootstrapEditText) findViewById(R.id.activity_store_detail_et_ten);
        etTenChuCuaHang = (BootstrapEditText) findViewById(R.id.activity_store_detail_et_ten_chu_cua_hang);
        etDiaChi = (BootstrapEditText) findViewById(R.id.activity_store_detail_et_dia_chi);
        etSDT = (BootstrapEditText) findViewById(R.id.activity_store_detail_et_sdt);
        etDoanhThu = (BootstrapEditText) findViewById(R.id.activity_store_detail_et_doanh_thu);
        etMatHangDoiThu = (BootstrapEditText) findViewById(R.id.activity_store_detail_et_mat_hang_doi_thu_canh_tranh);

        btnTrungBay = (BootstrapButton) findViewById(R.id.activity_store_detail_btn_trung_bay_quay_ke);
        btnGiaHan = (BootstrapButton) findViewById(R.id.activity_store_detail_btn_modifiy_debt);
        btnQuayVe = (BootstrapButton) findViewById(R.id.activity_store_detail_btn_quay_ve);

        tvBucAnhDaChup = (TextView) findViewById(R.id.activity_store_detail_tv_total_image);

        spnLoaiCuaHang = (Spinner) findViewById(R.id.activity_store_detail_spn_loai_cua_hang);

        etTenCuaHang.setEnabled(false);
        etTenChuCuaHang.setEnabled(false);
        etDiaChi.setEnabled(false);
        etSDT.setEnabled(false);
        etDoanhThu.setEnabled(false);
        etMatHangDoiThu.setEnabled(false);
        spnLoaiCuaHang.setEnabled(false);
        etCongNo = (BootstrapEditText) findViewById(R.id.activity_store_detail_et_cong_no);
        etCongNo.setEnabled(false);

        ivCongNo = (ImageView) findViewById(R.id.activity_store_detail_iv_cong_no);

        layoutEditCongNo = (LinearLayout) findViewById(R.id.activity_store_detail_layout_edit_cong_no);
        layoutTitleCongNo = (LinearLayout) findViewById(R.id.activity_store_detail_layout_title_cong_no);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ParseObject.unpinAllInBackground("PIN_DRAFT_PHOTO", new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                } else {

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void LoadImageCount() {
        ParseQuery<StoreImage> imageQuery = StoreImage.getQuery();
        imageQuery.fromPin(DownloadUtils.PIN_STORE_IMAGE);
        imageQuery.whereEqualTo("store_id", store_image_id);

        ParseQuery<StoreImage> localImageQuery = StoreImage.getQuery();
        localImageQuery.fromPin("PIN_DRAFT_PHOTO");
        localImageQuery.whereEqualTo("store_id", storeID);

        int imageCount = 0;
        int localImageCount = 0;

        try {
            imageCount = imageQuery.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            localImageCount = localImageQuery.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int totalImage = imageCount + localImageCount;
        tvBucAnhDaChup.setText(totalImage + getString(R.string.captureImage));
    }

    private String ValidateInput() {
        String error_msg = "";
        String tenCuaHang = etTenCuaHang.getText().toString();
        String tenChuCuaHang = etTenChuCuaHang.getText().toString();
        String diaChi = etDiaChi.getText().toString();
        String sdt = etSDT.getText().toString();
        String matHangDoiThuCanhTranh = etMatHangDoiThu.getText().toString();
        String soBucAnhCup = tvBucAnhDaChup.getText().toString();

        String sDoanhThu = etDoanhThu.getText().toString().replace(",", "").replace(".", "");
        double doanhThu;
        if (tenCuaHang.trim().length() <= 0 || tenCuaHang.trim().length() > 100) {
            error_msg = getString(R.string.errorInputStoreName);
            return error_msg;
        }

        if (tenChuCuaHang.trim().length() <= 0 || tenChuCuaHang.trim().length() > 100) {
            error_msg = getString(R.string.errorInputStoreOwner);
            return error_msg;
        }

        if (diaChi.trim().length() <= 0) {
            error_msg = getString(R.string.errorInputAddress);
            return error_msg;
        }

        if (sdt.trim().length() > 11) {
            error_msg = getString(R.string.errorInputPhoneNumber);
            return error_msg;
        }

        try {
            doanhThu = Double.parseDouble(sDoanhThu);
        } catch (NumberFormatException ex) {
            error_msg = getString(R.string.errorInputIncome);
            return error_msg;
        } finally {
            if (sDoanhThu.trim().length() <= 0) {
                error_msg = getString(R.string.errorInputIncome);
                return error_msg;
            }
        }

        if (soBucAnhCup.equals("0" + getString(R.string.captureImage))) {
            error_msg = getString(R.string.errorInputImage);
            return error_msg;
        }

        if (matHangDoiThuCanhTranh.trim().length() <= 0) {
            error_msg = getString(R.string.errorInputCompetitiveProduct);
            return error_msg;
        }

        return error_msg;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}