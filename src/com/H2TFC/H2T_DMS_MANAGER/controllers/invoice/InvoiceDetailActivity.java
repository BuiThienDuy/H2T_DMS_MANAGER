package com.H2TFC.H2T_DMS_MANAGER.controllers.invoice;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.InvoiceDetailAdapter;
import com.H2TFC.H2T_DMS_MANAGER.models.Invoice;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.models.ProductPurchase;
import com.H2TFC.H2T_DMS_MANAGER.models.Store;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class InvoiceDetailActivity extends Activity {
    String invoiceId;
    Invoice myInvoice;

    InvoiceDetailAdapter productListAdapter;

    ListView lvProduct;
    TextView tvEmptyProduct;

    TextView tvEmployeeName, tvStoreName, tvTotalPrice, tvDate, tvStatus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);
        setTitle(getString(R.string.invoiceDetailTitle));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("EXTRAS_INVOICE_ID")) {
            invoiceId = getIntent().getStringExtra("EXTRAS_INVOICE_ID");
        }

        if (ConnectUtils.hasConnectToInternet(InvoiceDetailActivity.this)) {
            DownloadUtils.DownloadParseProduct(InvoiceDetailActivity.this,new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    tvEmptyProduct.setVisibility(View.VISIBLE);
                    productListAdapter.loadObjects();
                }
            });

            DownloadUtils.DownloadParseProductPurchase(InvoiceDetailActivity.this,new SaveCallback() {
                @Override
                public void done(ParseException e) {

                }
            });

            DownloadUtils.DownloadParsePromotion(InvoiceDetailActivity.this,new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    productListAdapter.loadObjects();
                }
            });
        }

        InitializeComponent();
        try {
            SetupListView();
        } catch(IllegalStateException ex) {
            DownloadUtils.DownloadParseProductPurchase(InvoiceDetailActivity.this, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    SetupListView();
                }
            });
        }
        SetupEvent();

        ParseQuery<Invoice> invoiceParseQuery = Invoice.getQuery();
        invoiceParseQuery.whereEqualTo("objectId", invoiceId);
        invoiceParseQuery.fromPin(DownloadUtils.PIN_INVOICE);
        invoiceParseQuery.getFirstInBackground(new GetCallback<Invoice>() {
            @Override
            public void done(Invoice invoice, ParseException e) {
                if (e == null) {
                    myInvoice = invoice;

                    tvEmployeeName.setText(invoice.getEmployee().getString("name"));
                    tvTotalPrice.setText(String.format(Locale.CHINESE, "%1$,.0f", invoice.getInvoicePrice()) + " " + getString(R.string
                            .VND));

                    String status = invoice.getInvoiceStatus();
                    if (status.equals(Invoice.MOI_TAO)) {
                        status = getString(R.string.MOI_TAO);
                    }
                    if (status.equals(Invoice.DANG_XU_LY)) {
                        status = getString(R.string.DANG_XU_LY);
                    }
                    if (status.equals(Invoice.DA_THANH_TOAN)) {
                        status = getString(R.string.DA_THANH_TOAN);
                    }
                    tvStatus.setText(status);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    tvDate.setText(simpleDateFormat.format(invoice.getCreatedAt()));

                    ParseQuery<Store> storeParseQuery = Store.getQuery();
                    storeParseQuery.whereEqualTo("objectId", invoice.getStoreId())
                            .fromPin(DownloadUtils.PIN_STORE).getFirstInBackground(new GetCallback<Store>() {
                        @Override
                        public void done(Store store, ParseException e) {
                            if (e == null) {
                                tvStoreName.setText(store.getName());
                            }
                        }
                    });
                }
            }
        });
    }

    private void SetupListView() {
        ParseQueryAdapter.QueryFactory<Product> factory = new ParseQueryAdapter.QueryFactory<Product>() {
            @Override
            public ParseQuery<Product> create() {
                final String[] searchProduct = {""};
                ParseQuery<Invoice> invoiceParseQuery = Invoice.getQuery();
                invoiceParseQuery.whereEqualTo("objectId", invoiceId);
                invoiceParseQuery.fromPin(DownloadUtils.PIN_INVOICE);
                Invoice invoice = null;
                try {
                    invoice = invoiceParseQuery.getFirst();
                    List<ProductPurchase> productPurchaseList = invoice.getProductPurchases();
                    for(int i = 0 ; i < productPurchaseList.size(); i++) {
                        if(i == productPurchaseList.size()-1) {
                            searchProduct[0] += "(" + productPurchaseList.get(i).getProductRelate().getObjectId() + ")";
                        } else {
                            searchProduct[0] += "(" + productPurchaseList.get(i).getProductRelate().getObjectId() + ")|";
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ParseQuery<Product> query = Product.getQuery();
                query.whereNotEqualTo("status", Product.ProductStatus.KHOA.name());
                query.orderByDescending("createdAt");
                query.whereMatches("objectId", searchProduct[0]);
                query.fromPin(DownloadUtils.PIN_PRODUCT);
                return query;
            }
        };
        productListAdapter = new InvoiceDetailAdapter(this, factory, invoiceId);
        lvProduct.setEmptyView(tvEmptyProduct);
        lvProduct.setAdapter(productListAdapter);
    }

    private void SetupEvent() {
        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


            }
        });


    }

    private void InitializeComponent() {
        lvProduct = (ListView) findViewById(R.id.activity_invoice_detail_listview);
        tvEmptyProduct = (TextView) findViewById(R.id.activity_invoice_detail_tv_empty_product);
        tvEmptyProduct.setVisibility(View.INVISIBLE);


        tvDate = (TextView) findViewById(R.id.activity_invoice_detail_date_created);
        tvEmployeeName = (TextView) findViewById(R.id.activity_invoice_detail_employee_name);
        tvStoreName = (TextView) findViewById(R.id.activity_invoice_detail_store_name);
        tvTotalPrice = (TextView) findViewById(R.id.activity_invoice_detail_invoice_price);
        tvStatus = (TextView) findViewById(R.id.activity_invoice_detail_invoice_status);
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