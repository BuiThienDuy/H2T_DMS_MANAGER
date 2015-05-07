package com.H2TFC.H2T_DMS_MANAGER.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class MyEditDatePicker implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    EditText _editText;
    private int _day;
    private int _month;
    private int _birthYear;
    private Context _context;
    private Date _maxDate;

    public MyEditDatePicker(Context context, int editTextViewID, int _day, int _month, int _birthYear)
    {
        Activity act = (Activity)context;
        this._editText = (EditText)act.findViewById(editTextViewID);
        this._editText.setOnClickListener(this);
        this._context = context;

        this._day = _day;
        this._month = _month;
        this._birthYear = _birthYear;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _birthYear = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        updateDisplay();
    }
    @Override
    public void onClick(View v) {
        DatePickerDialog dialog =  new DatePickerDialog(_context, this, _birthYear, _month, _day);
        try {
            dialog.getDatePicker().setMaxDate(_maxDate.getTime());
        } catch(Exception ex) {}
        dialog.show();

    }

    public void setMaxDate(Date date) {
        _maxDate = date;
    }

    // updates the date in the birth date EditText
    public void updateDisplay() {

        _editText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(_day).append("/").append(_month + 1).append("/").append(_birthYear).append(" "));
    }
}