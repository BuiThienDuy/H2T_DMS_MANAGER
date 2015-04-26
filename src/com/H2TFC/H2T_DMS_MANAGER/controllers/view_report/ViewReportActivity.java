package com.H2TFC.H2T_DMS_MANAGER.controllers.view_report;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.BarChartItem;
import com.H2TFC.H2T_DMS_MANAGER.adapters.ChartItem;
import com.H2TFC.H2T_DMS_MANAGER.adapters.LineChartItem;
import com.H2TFC.H2T_DMS_MANAGER.adapters.PieChartItem;
import com.H2TFC.H2T_DMS_MANAGER.models.Attendance;
import com.H2TFC.H2T_DMS_MANAGER.models.Invoice;
import com.H2TFC.H2T_DMS_MANAGER.models.Store;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.widget.MyEditDatePicker;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.mikephil.charting.animation.AnimationEasing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import javax.xml.xpath.XPathVariableResolver;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class ViewReportActivity extends Activity {
    ListView lvChart;
    BootstrapEditText etFromDate,etToDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        InitializeComponent();
        LoadReport();

        DownloadUtils.DownloadParseInvoice(ViewReportActivity.this,new SaveCallback() {
            @Override
            public void done(ParseException e) {
                DownloadUtils.DownloadParseStore(getApplicationContext(), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        LoadReport();
                    }
                });

            }
        });

        SetupEvent();
    }

    private void InitializeComponent() {
        lvChart = (ListView) findViewById(R.id.activity_view_report_lv_chart);
        etFromDate = (BootstrapEditText) findViewById(R.id.activity_view_report_et_from_date);
        etToDate = (BootstrapEditText) findViewById(R.id.activity_view_report_et_to_date);

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        MyEditDatePicker edpFromDate = new MyEditDatePicker(ViewReportActivity.this, R.id
                .activity_view_report_et_from_date,day,month,
                year);

        c.add(Calendar.DATE,1);
        day = c.get(Calendar.DATE);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        MyEditDatePicker edpToDate =new MyEditDatePicker(ViewReportActivity.this, R.id
                .activity_view_report_et_to_date,day,month,
                year);

        edpFromDate.updateDisplay();
        edpToDate.updateDisplay();

    }

    private void SetupEvent() {
        etFromDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LoadReport();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etToDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LoadReport();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void LoadReport() {
        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date fromDate = new Date();
        Date toDate = new Date();

        try {
            fromDate = df.parse(etFromDate.getText().toString());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        try {
            toDate = df.parse(etToDate.getText().toString());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        list.add(new PieChartItem(generateDataPie_Tong_ThuNhap(fromDate, toDate), getApplicationContext(), getString(R.string
                .totalIncomeChart)));
        list.add(new BarChartItem(generateDataBar_DoanhThu_Nhanvien(fromDate, toDate), getApplicationContext(), getString(R
                .string
                .employeeIncomeChart)));
        list.add(new PieChartItem(generateDataPie_diem_cua_hang(fromDate, toDate),getApplicationContext()
                ,getString(R
                .string.storePointChart)));
        list.add(new PieChartItem(generateDataPie_TinhTrang_DonHang(fromDate, toDate),getApplicationContext()
                ,getString(R.string.invoiceStatusChart)));



        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lvChart.setAdapter(cda);
    }

    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position == 0) {

            }
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    private PieData generateDataPie_Tong_ThuNhap(Date fromDate,Date toDate) {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        int doanhThu = 0;
        int thucThu = 0;

        ParseQuery<Invoice> invoiceParseQuery = Invoice.getQuery();
        invoiceParseQuery.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
        invoiceParseQuery.whereGreaterThan("createdAt", fromDate);
        invoiceParseQuery.whereLessThan("createdAt", toDate);
        invoiceParseQuery.fromPin(DownloadUtils.PIN_INVOICE);

        try {
            List<Invoice> invoiceList = invoiceParseQuery.find();
            for(Invoice invoice : invoiceList) {
                doanhThu += invoice.getInvoicePrice();
                if(invoice.getInvoiceStatus().equals(Invoice.DA_THANH_TOAN)) {
                    thucThu += invoice.getInvoicePrice();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        entries.add(new Entry(doanhThu, 0));
        entries.add(new Entry(thucThu, 1));

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.JOYFUL_COLORS);

        ArrayList<String> q = new ArrayList<String>();
        q.add(getString(R.string.income));
        q.add(getString(R.string.realIncome));

        PieData cd = new PieData(q , d);
        return cd;
    }

    private BarData generateDataBar_DoanhThu_Nhanvien(Date fromDate,Date toDate) {
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
        userParseQuery.fromPin(DownloadUtils.PIN_EMPLOYEE);
        List<ParseUser> userList = null;
        ArrayList<String> userNameList = new ArrayList<String>();
        try {
            userList = userParseQuery.find();
            for (int i = 0; i < userList.size(); i++) {
                userNameList.add(userList.get(i).getString("name"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Generate chart
        ArrayList<BarEntry> doanhthu_entries = new ArrayList<BarEntry>();

        assert userList != null;
        for (int i = 0; i < userList.size(); i++) {
            double doanhThu = 0;
            ParseQuery<Invoice> invoiceParseQuery = Invoice.getQuery();
            invoiceParseQuery.whereEqualTo("employee_id", userList.get(i).getObjectId());
            invoiceParseQuery.whereGreaterThan("createdAt",fromDate);
            invoiceParseQuery.whereLessThan("createdAt", toDate);
            invoiceParseQuery.fromPin(DownloadUtils.PIN_INVOICE);
            try {
                List<Invoice> invoiceList = invoiceParseQuery.find();
                for(Invoice invoice : invoiceList) {
                    doanhThu += invoice.getInvoicePrice();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            doanhthu_entries.add(new BarEntry((int)doanhThu, i));
        }

        BarDataSet d = new BarDataSet(doanhthu_entries, getString(R.string.income));
        d.setBarSpacePercent(20f);
        d.setColor(Color.CYAN);
        //d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        ArrayList<BarEntry> thucthu_entries = new ArrayList<BarEntry>();

        for (int i = 0; i < userList.size(); i++) {
            double thucThu = 0;
            ParseQuery<Invoice> invoiceParseQuery = Invoice.getQuery();
            invoiceParseQuery.whereEqualTo("employee_id", userList.get(i).getObjectId());
            invoiceParseQuery.whereEqualTo("invoice_status", Invoice.DA_THANH_TOAN);
            invoiceParseQuery.whereGreaterThan("createdAt", fromDate);
            invoiceParseQuery.whereLessThan("createdAt", toDate);
            invoiceParseQuery.fromPin(DownloadUtils.PIN_INVOICE);
            try {
                List<Invoice> invoiceList = invoiceParseQuery.find();
                for(Invoice invoice : invoiceList) {
                    thucThu += invoice.getInvoicePrice();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            thucthu_entries.add(new BarEntry((int) thucThu, i));
        }

        BarDataSet t = new BarDataSet(thucthu_entries, getString(R.string.realIncome));
        t.setBarSpacePercent(20f);
        t.setColor(Color.YELLOW);
        //t.setColors(ColorTemplate.LIBERTY_COLORS);
        t.setHighLightAlpha(255);

        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        sets.add(d);
        sets.add(t);

        BarData cd = new BarData(userNameList, sets);
        return cd;
    }

    private PieData generateDataPie_diem_cua_hang(Date fromDate,Date toDate) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        int count_ban_hang = 0;
        int count_tiem_nang = 0;
        int count_khao_sat = 0;
        int count_khong_du_tieu_chuan = 0;
        int count_cho_cap_tren = 0;
        int count_dang_thoa_thuan = 0;

        // Get data first
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
        userParseQuery.fromPin(DownloadUtils.PIN_EMPLOYEE);
        List<ParseUser> userList = null;
        ArrayList<String> userNameList = new ArrayList<String>();
        try {
            userList = userParseQuery.find();
            for(ParseUser user : userList) {
                ParseQuery<Store> storeParseQuery = Store.getQuery();
                storeParseQuery.whereEqualTo("employee_id", user.getObjectId());
                storeParseQuery.whereGreaterThan("createdAt", fromDate);
                storeParseQuery.whereLessThan("createdAt", toDate);
                storeParseQuery.fromPin(DownloadUtils.PIN_STORE);
                List<Store> storeList = null;

                try {
                    storeList = storeParseQuery.find();
                    for(Store store : storeList) {
                        //storeNameList.add(store.getStoreType() + " " + store.getName());
                        String status = store.getStatus();
                        if(status.equals(Store.StoreStatus.BAN_HANG.name())) {
                            count_ban_hang++;
                        }
                        if(status.equals(Store.StoreStatus.TIEM_NANG.name())) {
                            count_tiem_nang++;
                        }
                        if(status.equals(Store.StoreStatus.KHAO_SAT.name())) {
                            count_khao_sat++;
                        }
                        if(status.equals(Store.StoreStatus.KHONG_DU_TIEU_CHUAN.name())) {
                            count_khong_du_tieu_chuan++;
                        }
                        if(status.equals(Store.StoreStatus.CHO_CAP_TREN.name())) {
                            count_cho_cap_tren++;
                        }
                        if(status.equals(Store.StoreStatus.DANG_THOA_THUAN.name())) {
                            count_dang_thoa_thuan++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> q = new ArrayList<String>();
        if(count_ban_hang > 0) {
            entries.add(new Entry(count_ban_hang, 0));
            q.add(getString(R.string.BAN_HANG));
        }
        if(count_tiem_nang > 0) {
            entries.add(new Entry(count_tiem_nang, 1));
            q.add(getString(R.string.TIEM_NANG));
        }
        if(count_khao_sat > 0) {
            entries.add(new Entry(count_khao_sat, 2));
            q.add(getString(R.string.KHAO_SAT));
        }
        if(count_khong_du_tieu_chuan > 0) {
            entries.add(new Entry(count_khong_du_tieu_chuan, 3));
            q.add(getString(R.string.KHONG_DU_TIEU_CHUAN));
        }
        if(count_cho_cap_tren > 0) {
            entries.add(new Entry(count_cho_cap_tren, 4));
            q.add(getString(R.string.CHO_CAP_TREN));
        }
        if(count_dang_thoa_thuan > 0) {
            entries.add(new Entry(count_dang_thoa_thuan, 5));
            q.add(getString(R.string.DANG_THOA_THUAN));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData cd = new PieData(q, d);
        return cd;
    }

    private PieData generateDataPie_TinhTrang_DonHang(Date fromDate, Date toDate) {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        // Get data first
        ParseQuery<Invoice> invoiceParseQuery = Invoice.getQuery();
        invoiceParseQuery.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
        invoiceParseQuery.whereGreaterThan("createdAt",fromDate);
        invoiceParseQuery.whereLessThan("createdAt", toDate);
        invoiceParseQuery.fromPin(DownloadUtils.PIN_INVOICE);
        List<Invoice> invoiceList = null;
        int count_moi_tao = 0;
        int count_dang_xu_ly = 0;
        int count_hoan_thanh = 0;

        try {
            invoiceList = invoiceParseQuery.find();
            for(Invoice invoice : invoiceList) {
                //storeNameList.add(store.getStoreType() + " " + store.getName());
                String status = invoice.getInvoiceStatus();
                if(status.equals(Invoice.MOI_TAO)) {
                    count_moi_tao++;
                }
                if(status.equals(Store.StoreStatus.TIEM_NANG.name())) {
                    count_dang_xu_ly++;
                }
                if(status.equals(Store.StoreStatus.KHAO_SAT.name())) {
                    count_hoan_thanh++;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> q = new ArrayList<String>();
        if(count_moi_tao > 0) {
            entries.add(new Entry(count_moi_tao, 0));
            q.add(getString(R.string.MOI_TAO));
        }
        if(count_dang_xu_ly > 0) {
            entries.add(new Entry(count_dang_xu_ly, 1));
            q.add(getString(R.string.DANG_XU_LY));
        }
        if(count_hoan_thanh > 0) {
            entries.add(new Entry(count_hoan_thanh, 2));
            q.add(getString(R.string.DA_THANH_TOAN));
        }


        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData cd = new PieData(q, d);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private LineData generateDataLine(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e1.add(new Entry((int) (Math.random() * 65) + 40, i));
        }

        LineDataSet d1 = new LineDataSet(e1, "New DataSet " + cnt + ", (1)");
        d1.setLineWidth(2.5f);
        d1.setCircleSize(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<Entry> e2 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e2.add(new Entry(e1.get(i).getVal() - 30, i));
        }

        LineDataSet d2 = new LineDataSet(e2, "New DataSet " + cnt + ", (2)");
        d2.setLineWidth(2.5f);
        d2.setCircleSize(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);
        sets.add(d2);

        LineData cd = new LineData(getMonths(), sets);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private BarData generateDataBar(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry((int) (Math.random() * 70) + 30, i));
        }

        BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);
        d.setBarSpacePercent(20f);
        d.setColors(ColorTemplate.JOYFUL_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(getMonths(), d);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private PieData generateDataPie(int cnt) {

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < 4; i++) {
            entries.add(new Entry((int) (Math.random() * 70) + 30, i));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData cd = new PieData(getQuarters(), d);
        return cd;
    }

    private ArrayList<String> getQuarters() {

        ArrayList<String> q = new ArrayList<String>();
        q.add("q1");
        q.add("q2");
        q.add("q3");
        q.add("q4");

        return q;
    }

    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Jan");
        m.add("Feb");
        m.add("Mar");
        m.add("Apr");
        m.add("May");
        m.add("Jun");
        m.add("Jul");
        m.add("Aug");
        m.add("Sep");
        m.add("Okt");
        m.add("Nov");
        m.add("Dec");

        return m;
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