package com.H2TFC.H2T_DMS_MANAGER.controllers.invoice_management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.InvoiceAdapter;
import com.H2TFC.H2T_DMS_MANAGER.models.Invoice;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;

import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class InvoiceHistoryActivity extends Activity {
    InvoiceAdapter invoiceAdapter;
    ListView lvInvoice;
    TextView tvEmptyInvoice;

    String storeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_history);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.invoiceHistoryTitle));


        if(getIntent().hasExtra("EXTRAS_STORE_ID")) {
            storeId = getIntent().getStringExtra("EXTRAS_STORE_ID");
        }

        if(ConnectUtils.hasConnectToInternet(InvoiceHistoryActivity.this)) {
            ParseQuery<Invoice> query = Invoice.getQuery();
            query.whereEqualTo("storeId",storeId);
            query.findInBackground(new FindCallback<Invoice>() {
                @Override
                public void done(List<Invoice> list, ParseException e) {
                    ParseObject.unpinAllInBackground(DownloadUtils.PIN_INVOICE);
                    ParseObject.pinAllInBackground(DownloadUtils.PIN_INVOICE,list);
                }
            });
        }

        InitializeComponent();
        SetUpListView();

        lvInvoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position >= 0) {
                    Invoice invoice = invoiceAdapter.getItem(position);

                    Intent intent = new Intent(InvoiceHistoryActivity.this,InvoiceDetailActivity.class);
                    intent.putExtra("EXTRAS_INVOICE_ID",invoice.getObjectId());
                    startActivity(intent);
                }
            }
        });
    }

    private void SetUpListView() {
        ParseQueryAdapter.QueryFactory<Invoice> factory = new ParseQueryAdapter.QueryFactory<Invoice>() {
            @Override
            public ParseQuery<Invoice> create() {
                ParseQuery<Invoice> query = Invoice.getQuery();
                query.orderByDescending("updatedAt");
                query.whereEqualTo("storeId", storeId);
                query.fromPin(DownloadUtils.PIN_INVOICE);
                return query;
            }
        };

        invoiceAdapter = new InvoiceAdapter(this,factory);
        lvInvoice.setAdapter(invoiceAdapter);
        lvInvoice.setEmptyView(tvEmptyInvoice);
    }

    private void InitializeComponent() {
        tvEmptyInvoice = (TextView) findViewById(R.id.activity_invoice_history_tv_empty);
        lvInvoice = (ListView) findViewById(R.id.activity_invoice_history_lv_invoice);

        // Default setting
        tvEmptyInvoice.setVisibility(View.INVISIBLE);
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