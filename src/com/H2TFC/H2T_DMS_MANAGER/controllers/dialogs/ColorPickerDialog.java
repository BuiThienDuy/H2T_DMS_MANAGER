package com.H2TFC.H2T_DMS_MANAGER.controllers.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.controllers.street_divide.StreetDivideActivity;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class ColorPickerDialog extends Dialog {
    Context context;
    Button btnFill, btnStroke;

    Button vRed, vLime, vBlue, vYellow,
            vCyan, vMagenta, vMaroon, vOlive,
            vGreen, vPurple, vTeal, vNavy;

    public ColorPickerDialog(Activity a) {
        super(a);
        this.context = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(context.getString(R.string.chooseFillColor));
        this.setContentView(R.layout.dialog_colorpicker);

        vRed = (Button) findViewById(R.id.dialog_colorpicker_red);
        vLime = (Button) findViewById(R.id.dialog_colorpicker_Lime);
        vBlue = (Button) findViewById(R.id.dialog_colorpicker_Blue);
        vYellow = (Button) findViewById(R.id.dialog_colorpicker_Yellow);
        vCyan = (Button) findViewById(R.id.dialog_colorpicker_Cyan);
        vMagenta = (Button) findViewById(R.id.dialog_colorpicker_Magenta);
        vMaroon = (Button) findViewById(R.id.dialog_colorpicker_Maroon);
        vOlive = (Button) findViewById(R.id.dialog_colorpicker_Olive);
        vGreen = (Button) findViewById(R.id.dialog_colorpicker_Green);
        vPurple = (Button) findViewById(R.id.dialog_colorpicker_Purple);
        vTeal = (Button) findViewById(R.id.dialog_colorpicker_Teal);
        vNavy = (Button) findViewById(R.id.dialog_colorpicker_Navy);



        // Initial color background
        final ColorPicker colorPicker_fill = (ColorPicker) findViewById(R.id.dialog_colorpicker_picker_fill);

        btnFill = (Button) findViewById(R.id.dialog_colorpicker_btn_fillcolor);
        btnStroke = (Button) findViewById(R.id.dialog_colorpicker_btn_strokecolor);

        // fill
        StreetDivideActivity current_activity = (StreetDivideActivity) context;
        ColorDrawable buttonFillColor = (ColorDrawable) current_activity.btn_colorPicker_fill.getBackground();
        colorPicker_fill.setOldCenterColor(buttonFillColor.getColor());
        colorPicker_fill.setColor(buttonFillColor.getColor());
        //opacityBar_fill.setOpacity(buttonFillColor.getAlpha());

        // button
        BootstrapButton btnXong = (BootstrapButton) findViewById(R.id.dialog_colorpicker_btnxong);
        BootstrapButton btnHuy = (BootstrapButton) findViewById(R.id.dialog_colorpicker_btnhuy);

        btnXong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StreetDivideActivity activity = (StreetDivideActivity) context;
                activity.btn_colorPicker_fill.setBackgroundColor(colorPicker_fill.getColor());
                activity.connectAllMarker();
                dismiss();
            }
        });

        //
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }

        });

        colorPicker_fill.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                btnFill.setBackgroundColor(color);
            }
        });

        // Change color on click color set
        View.OnClickListener changeColorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonFillColor = (ColorDrawable) v.getBackground();
                colorPicker_fill.setOldCenterColor(buttonFillColor.getColor());
                colorPicker_fill.setColor(buttonFillColor.getColor());
            }
        };

        vRed.setOnClickListener(changeColorClickListener);
        vLime.setOnClickListener(changeColorClickListener);
        vBlue.setOnClickListener(changeColorClickListener);
        vYellow.setOnClickListener(changeColorClickListener);
        vCyan.setOnClickListener(changeColorClickListener);
        vMagenta.setOnClickListener(changeColorClickListener);
        vMaroon.setOnClickListener(changeColorClickListener);
        vOlive.setOnClickListener(changeColorClickListener);
        vGreen.setOnClickListener(changeColorClickListener);
        vPurple.setOnClickListener(changeColorClickListener);
        vTeal.setOnClickListener(changeColorClickListener);
        vNavy.setOnClickListener(changeColorClickListener);
    }


}