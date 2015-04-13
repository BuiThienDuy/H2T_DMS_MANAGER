package com.H2TFC.H2T_DMS_MANAGER.controllers.user_information_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.MyApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.controllers.LoginActivity;
import com.H2TFC.H2T_DMS_MANAGER.controllers.dialogs.ChangePasswordDialog;
import com.H2TFC.H2T_DMS_MANAGER.models.Employee;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.*;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.H2TFC.H2T_DMS_MANAGER.utils.ImageUtils.getResizedBitmap;

/**
 * Created by User on 23/03/2015.
 */
public class UserInformationManagementActivity extends Activity {
    BootstrapEditText etHoVaTen,etDiaChi,etCMND,etSoDienThoai;
    TextView tvNgaySinh;
    RadioButton rbNam, rbNu;
    BootstrapButton btnDone,btnCancel;
    ParseImageView ivPhoto;

    Calendar myCalendar = Calendar.getInstance();
    // Create datetime listener to pick ngaysinh
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information_management);
        setTitle(getString(R.string.userInformationManagementTitle));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        InitializeComponent();
        SetupEvent();

        if(!ConnectUtils.hasConnectToInternet(UserInformationManagementActivity.this)) {
            DownloadUtils.DownloadParseEmployee(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                }
            });
        }
    }

    private void SetupEvent() {
        // Photo click listener
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, MyApplication.REQUEST_TAKE_PHOTO);
                } catch (ActivityNotFoundException ex) {
                    //display an error message
                    String errorMessage = getString(R.string.error_dont_have_camera);
                    Toast toast = Toast.makeText(UserInformationManagementActivity.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        tvNgaySinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UserInformationManagementActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ho_va_ten = etHoVaTen.getText().toString();
                String so_dien_thoai = etSoDienThoai.getText().toString();
                String dia_chi = etDiaChi.getText().toString();
                String cmnd = etCMND.getText().toString();

                boolean gioi_tinh = rbNam.isChecked(); // true = nam - false = nu

                boolean error_exist = false;
                String error_msg = "";


                if (cmnd.trim().length() <= 0) {
                    error_exist = true;
                    error_msg = getString(R.string.errorBlankCMND);
                }

                if (dia_chi.trim().length() <= 0) {
                    error_exist = true;
                    error_msg = getString(R.string.errorBlankAddress);
                }

                if (so_dien_thoai.trim().length() <= 0) {
                    error_exist = true;
                    error_msg = getString(R.string.errorBlankPhoneNumber);
                }

                if (ho_va_ten.trim().length() <= 0) {
                    error_exist = true;
                    error_msg = getString(R.string.errorBlankFirstNameLastName);
                }

                // if error exist then display error message
                // else add new employee
                if (error_exist) {
                    Toast.makeText(UserInformationManagementActivity.this, error_msg, Toast.LENGTH_LONG).show();
                } else {
                    ParseUser employee = ParseUser.getCurrentUser();
                    employee.put("name",ho_va_ten);
                    employee.put("phone_number",so_dien_thoai);
                    employee.put("gender",gioi_tinh ? getString(R.string.male) : getString(R.string.female));
                    employee.put("address",dia_chi);
                    employee.put("identify_card_number",cmnd);
                    //
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date convertedDate = new Date();
                    try {
                        convertedDate = dateFormat.parse(tvNgaySinh.getText().toString());
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                    employee.put("date_of_birth",convertedDate);
                    employee.put("locked",false);

                    // Set photo
                    if (ivPhoto.getDrawable() != null) {
                        // Get bytedata from bitmap
                        Bitmap bitmap = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bitmapdata = stream.toByteArray();
                        // upload photo
                        final ParseFile file = new ParseFile(ParseUser.getCurrentUser().getUsername() + "_photo.png",
                                bitmapdata);
                        try {
                            file.save();
                            employee.put("photo",file);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    employee.pinInBackground(DownloadUtils.PIN_EMPLOYEE, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if( e != null) {
                                e.printStackTrace();
                            }
                        }
                    });
                    employee.saveEventually();
                    Toast.makeText(UserInformationManagementActivity.this,getString(R.string.updateUserInformationSuccess),
                            Toast.LENGTH_LONG).show();

                    finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void InitializeComponent() {
        // EditText
        etHoVaTen = (BootstrapEditText) findViewById(R.id.activity_user_information_management_et_hovaten);
        etCMND = (BootstrapEditText) findViewById(R.id.activity_user_information_management_et_cmnd);
        etDiaChi = (BootstrapEditText) findViewById(R.id.activity_user_information_management_et_diachi);
        etSoDienThoai = (BootstrapEditText) findViewById(R.id.activity_user_information_management_et_sodienthoai);

        // TextView
        tvNgaySinh = (TextView) findViewById(R.id.activity_user_information_management_et_ngaysinh);

        // RadioButton
        rbNam = (RadioButton) findViewById(R.id.activity_user_information_management_rb_nam);
        rbNu = (RadioButton) findViewById(R.id.activity_user_information_management_rb_nu);

        // Button
        btnDone = (BootstrapButton) findViewById(R.id.activity_user_information_management_btn_done);
        btnCancel = (BootstrapButton) findViewById(R.id.activity_user_information_management_btn_cancel);

        // ParseImageView
        ivPhoto = (ParseImageView) findViewById(R.id.activity_user_information_management_iv_photome);
        ivPhoto.setPlaceholder(getResources().getDrawable(R.drawable.ic_contact_empty));

        // Load current user
        ParseUser curUser = ParseUser.getCurrentUser();
        ivPhoto.setParseFile(curUser.getParseFile("photo"));
        ivPhoto.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if(e != null) {

                }
            }
        });
        etHoVaTen.setText(curUser.getString("name"));
        etSoDienThoai.setText(curUser.getString("phone_number"));
        etDiaChi.setText(curUser.getString("address"));
        if (curUser.getString("gender").equals("Nam")) {
            rbNam.setChecked(true);
            rbNu.setChecked(false);
        } else {
            rbNam.setChecked(false);
            rbNu.setChecked(true);
        }

        if (curUser.getDate("date_of_birth") != null) {
            String myFormat = "dd/MM/yy"; //Format for date time
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            tvNgaySinh.setText(sdf.format(curUser.getDate("date_of_birth")));
        }

        etCMND.setText(curUser.getString("identify_card_number"));
    }

    /**
     * Method to update ngaysinh after choosing from dialog
     */
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //Format for date time
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tvNgaySinh.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_user_information_management, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_bar_user_information_management_change_password: {
                ChangePasswordDialog passwordDialog = new ChangePasswordDialog(UserInformationManagementActivity.this);
                passwordDialog.show();
                break;
            }
            case R.id.action_bar_user_information_management_log_out: {
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(UserInformationManagementActivity.this);
                confirmDialog.setMessage(getString(R.string.confirmLogOut));
                confirmDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(UserInformationManagementActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                confirmDialog.setNegativeButton(getString(R.string.cancel),null);

                confirmDialog.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApplication.REQUEST_TAKE_PHOTO && data != null) {
                //get the Uri for the captured image
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                bm = getResizedBitmap(bm, 100, 100);
                ivPhoto.setImageBitmap(bm);
            }
        }
    }
}