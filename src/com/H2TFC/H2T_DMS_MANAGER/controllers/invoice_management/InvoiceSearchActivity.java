package com.H2TFC.H2T_DMS_MANAGER.controllers.invoice_management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Invoice;
import com.H2TFC.H2T_DMS_MANAGER.utils.MultiSpinner;
import com.H2TFC.H2T_DMS_MANAGER.widget.MyEditDatePicker;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class InvoiceSearchActivity extends Activity {
    BootstrapEditText etFromDate,etToDate;
    MultiSpinner spinnerStatus;
    BootstrapButton btnSearch;
    ArrayList<String> statusSelected;
    MyEditDatePicker edpFromDate,edpToDate;
    SimpleDateFormat simpleDateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.invoiceSearchTitle));
        this.setContentView(R.layout.dialog_invoice_management);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        etFromDate = (BootstrapEditText) findViewById(R.id.dialog_invoice_management_et_from_date);
        etToDate = (BootstrapEditText) findViewById(R.id.dialog_invoice_management_et_to_date);
        spinnerStatus = (MultiSpinner) findViewById(R.id.dialog_invoice_management_spinner_status);

        btnSearch = (BootstrapButton) findViewById(R.id.dialog_invoice_management_btn_search);

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        edpFromDate = new MyEditDatePicker(InvoiceSearchActivity.this, R.id
                .dialog_invoice_management_et_from_date,day,month,
                year);
        c.add(Calendar.DATE,1);
        day = c.get(Calendar.DATE);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        edpToDate =new MyEditDatePicker(InvoiceSearchActivity.this, R.id
                .dialog_invoice_management_et_to_date,day,month,
                year);
        edpFromDate.updateDisplay();
        edpToDate.updateDisplay();


        List<String> items = Arrays.asList(getString(R.string.MOI_TAO), getString(R.string.DANG_XU_LY),
                getString(R
                        .string.DA_THANH_TOAN));
        final List<String> stringArrayList = Arrays.asList(Invoice.MOI_TAO,Invoice.DANG_XU_LY,Invoice.DA_THANH_TOAN);

        statusSelected=  new ArrayList<String>();
        statusSelected.add(Invoice.MOI_TAO);
        statusSelected.add(Invoice.DANG_XU_LY);
        statusSelected.add(Invoice.DA_THANH_TOAN);
        spinnerStatus.setItems(items, getString(R.string.for_all), new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                statusSelected.clear();
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {
                        String status = null;
                        switch (i) {
                            case 0:
                                status = Invoice.MOI_TAO;
                                break;
                            case 1:
                                status = Invoice.DANG_XU_LY;
                                break;
                            case 2:
                                status = Invoice.DA_THANH_TOAN;
                                break;
                        }
                        //Toast.makeText(InvoiceManagementActivity.this,"Adding " + status,Toast.LENGTH_SHORT).show();
                        statusSelected.add(status);
                    }
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sFromDate = etFromDate.getText().toString();
                final String sToDate = etToDate.getText().toString();

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date fromDate = null;
                    Date toDate = null;
                    fromDate = dateFormat.parse(etFromDate.getText().toString());
                    toDate = dateFormat.parse(etToDate.getText().toString());

                    String sToSearch = "";
                    for (int i = 0; i < statusSelected.size(); i++) {
                        if (i == statusSelected.size() - 1) {
                            sToSearch += "(" + statusSelected.get(i) + ")";
                        } else {
                            sToSearch += "(" + statusSelected.get(i) + ")|";
                        }
                    }

                    Intent intent = new Intent();
                    intent.putExtra("EXTRAS_RETURN_FROM_DATE", sFromDate);
                    intent.putExtra("EXTRAS_RETURN_TO_DATE", sToDate);
                    intent.putExtra("EXTRAS_RETURN_STATUS", sToSearch);
                    setResult(RESULT_OK,intent);
                    finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etToDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Date fromDate = simpleDateFormat.parse(etFromDate.getText().toString());
                    Date toDate = simpleDateFormat.parse(etToDate.getText().toString());
                    edpFromDate.setMaxDate(fromDate);
                    if (fromDate.after(toDate) || fromDate.equals(toDate)) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(toDate);
                        cal.add(Calendar.DATE, -1);
                        toDate = cal.getTime();
                        etFromDate.setText(simpleDateFormat.format(toDate));
                    }
                } catch(java.text.ParseException ex) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
