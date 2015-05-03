package com.H2TFC.H2T_DMS_MANAGER.controllers.employee_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.H2TFC.H2T_DMS_MANAGER.MyMainApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Employee;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.ImageUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils.hasConnectToInternet;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class EmployeeNewActivity extends Activity {
    //Keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;

    // UI initial
    BootstrapButton btnXong, btnHuy;
    BootstrapEditText etUsername, etPassword, etPasswordConfirm, etHoVaTen, etSoDienThoai, etDiaChi, etCMND;
    TextView tvNgaySinh;
    RadioButton rbNam, rbNu;
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

    private String mImagePathToBeAttached;
    private Bitmap mImageToBeAttached;
    boolean hasImage = false;
    private static final int THUMBNAIL_SIZE_PX = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_new);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.employeeNewTitle));
        InitializeComponent();
        SetUpEvent();
    }

    private void SetUpEvent() {
        // Photo click listener
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items;
                if (mImageToBeAttached != null)
                    items = new CharSequence[]{getString(R.string.takePhoto), getString(R.string.choosePhoto), getString(R.string.removePhoto)};
                else
                    items = new CharSequence[]{getString(R.string.takePhoto), getString(R.string.choosePhoto)};

                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeNewActivity.this);
                builder.setTitle(getString(R.string.addPhoto));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            dispatchTakePhotoIntent();
                        } else if (item == 1) {
                            dispatchChoosePhotoIntent();
                        } else {
                            deleteCurrentPhoto();
                        }
                    }
                });
                builder.show();
            }
        });


        // Textview ngay sinh -> Show a Date picker dialog
        tvNgaySinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EmployeeNewActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Button xong -> add new employee
        btnXong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if can't connect to internet then display error
                // else continue
                if (!hasConnectToInternet(EmployeeNewActivity.this)) {
                    Toast.makeText(EmployeeNewActivity.this, getString(R.string.cannotConnectInternetPleaseCheck),
                            Toast
                                    .LENGTH_LONG).show();
                } else {
                    // Progress dialog
                    final ProgressDialog progressDialog = new ProgressDialog(EmployeeNewActivity.this);
                    progressDialog.setTitle(getString(R.string.dialogNewEmployee));
                    progressDialog.setMessage(getString(R.string.dialogPleaseWaitForAddEmployee));
                    progressDialog.show();

                    // Get field input
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    String confirm_password = etPasswordConfirm.getText().toString();
                    String ho_va_ten = etHoVaTen.getText().toString();
                    String so_dien_thoai = etSoDienThoai.getText().toString();
                    String dia_chi = etDiaChi.getText().toString();
                    String cmnd = etCMND.getText().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date convertedDate = new Date();

                    boolean gioi_tinh = rbNam.isChecked(); // true = nam - false = nu

                    boolean error_exist = false;
                    String error_msg = "";

                    if (cmnd.trim().length() <= 0) {
                        error_exist = true;
                        error_msg = getString(R.string.errorBlankCMND);
                    }

                    try {
                        convertedDate = dateFormat.parse(tvNgaySinh.getText().toString());
                    } catch (java.text.ParseException e) {
                        error_exist = true;
                        error_msg = getString(R.string.errorBlankBirthDate);
                    }

                    if (dia_chi.trim().length() <= 0) {
                        error_exist = true;
                        error_msg = getString(R.string.errorBlankAddress);
                    }

                    if (so_dien_thoai.trim().length() <= 0) {
                        error_exist = true;
                        error_msg = getString(R.string.errorBlankPhoneNumber);
                    }

                    if (!hasImage) {
                        error_exist = true;
                        error_msg = getString(R.string.errorBlankEmployeePhoto);
                    }

                    // Blank first name and last name
                    if (ho_va_ten.trim().length() <= 0) {
                        error_exist = true;
                        error_msg = getString(R.string.errorBlankFirstNameLastName);
                    }

                    // Blank password or mismatch password or passworn length not match
                    if (password.trim().length() < 6 || password.trim().length() > 50 || !confirm_password.equals
                            (password)) {
                        error_exist = true;
                        error_msg = getString(R.string.errorMismatchPassword);
                    }

                    // Blank username or password length of username not match
                    if (username.trim().length() < 4 || username.trim().length() > 50) {
                        error_exist = true;
                        error_msg = getString(R.string.errorBlankUsername);
                    }


                    // if error exist then display error message
                    // else add new employee
                    if (error_exist) {
                        progressDialog.dismiss();
                        Toast.makeText(EmployeeNewActivity.this, error_msg, Toast.LENGTH_LONG).show();
                    } else {
                        final Employee employee = new Employee();
                        employee.setUsername(username);
                        employee.setPassword(password);
                        employee.setName(ho_va_ten);
                        employee.setPhoneNumber(so_dien_thoai);
                        employee.setGender(gioi_tinh ? getString(R.string.male) : getString(R.string.female));
                        employee.setAddress(dia_chi);
                        employee.setIdCard(cmnd);
                        employee.setLock(false);
                        employee.setDateOfBirth(convertedDate);

                        // Set role name
                        String roleName = ParseUser.getCurrentUser().get("role_name").toString();
                        if (roleName.equals("NVQL")) {
                            employee.setRoleName("NVKD");
                        }
                        if (roleName.equals("NVQL_V")) {
                            employee.setRoleName("NVQL");
                        }
                        if (roleName.equals("GDKD")) {
                            employee.setRoleName("NVQL_V");
                        }

                        // Set photo
                        if (ivPhoto.getDrawable() != null) {
                            // Get bytedata from bitmap
                            Bitmap bitmap = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] bitmapdata = stream.toByteArray();
                            // upload photo
                            final ParseFile file = new ParseFile(username + "_photo.png", bitmapdata);
                            try {
                                file.save();
                                employee.setPhoto(file);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        employee.setManagerId(ParseUser.getCurrentUser().getObjectId());

                        // Sign up a new user
                        employee.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                // if no error exist then continue add employee
                                // else display error message
                                if (e == null) {
                                    Toast.makeText(EmployeeNewActivity.this, getString(R.string.notifyAddNewEmployeeSuccess),
                                            Toast.LENGTH_LONG).show();
                                    employee.pinInBackground(DownloadUtils.PIN_EMPLOYEE, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e == null) {
                                                // log out sign up user
                                                ParseUser.logOut();

                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
                                                        (EmployeeNewActivity.this);
                                                String username = preferences.getString("Username", "");
                                                String password = preferences.getString("Password", "");
                                                ParseUser.logInInBackground(username, password, new LogInCallback() {
                                                    @Override
                                                    public void done(ParseUser parseUser, ParseException e) {
                                                        if (e == null) {
                                                            //
                                                            progressDialog.dismiss();

                                                            setResult(RESULT_OK);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(EmployeeNewActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                }
            }
        });

        // Button huy -> close the activity
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void InitializeComponent() {
        // Edittext
        etUsername = (BootstrapEditText) findViewById(R.id.activity_employee_new_et_tendangnhap);
        etPassword = (BootstrapEditText) findViewById(R.id.activity_employee_new_et_matkhau);
        etPasswordConfirm = (BootstrapEditText) findViewById(R.id.activity_employee_new_et_matkhaulan2);
        etHoVaTen = (BootstrapEditText) findViewById(R.id.activity_employee_new_et_hovaten);
        etSoDienThoai = (BootstrapEditText) findViewById(R.id.activity_employee_new_et_sodienthoai);
        etDiaChi = (BootstrapEditText) findViewById(R.id.activity_employee_new_et_diachi);
        etCMND = (BootstrapEditText) findViewById(R.id.activity_employee_new_et_cmnd);

        // Radio button
        rbNam = (RadioButton) findViewById(R.id.activity_employee_new_rb_nam);
        rbNu = (RadioButton) findViewById(R.id.activity_employee_new_rb_nu);

        // Button
        btnXong = (BootstrapButton) findViewById(R.id.activity_employee_new_btnxong);
        btnHuy = (BootstrapButton) findViewById(R.id.activity_employee_new_btnhuy);

        // Textview
        tvNgaySinh = (TextView) findViewById(R.id.activity_employee_new_et_ngaysinh);

        // ParseImageView
        ivPhoto = (ParseImageView) findViewById(R.id.activity_employee_new_iv_photome);
        ivPhoto.setPlaceholder(getResources().getDrawable(R.drawable.ic_contact_empty));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap thumbnail;
        if (requestCode == MyMainApplication.REQUEST_TAKE_PHOTO) {
            if(resultCode == RESULT_OK) {
                mImageToBeAttached = BitmapFactory.decodeFile(mImagePathToBeAttached);
                thumbnail = ImageUtils.thumbmailFromFile(mImagePathToBeAttached,
                        THUMBNAIL_SIZE_PX, THUMBNAIL_SIZE_PX);

                // Delete the temporary image file
                File file = new File(mImagePathToBeAttached);
                file.delete();
                mImagePathToBeAttached = null;
                ivPhoto.setImageBitmap(thumbnail);
                hasImage = true;
            }
        } else if (requestCode == MyMainApplication.REQUEST_CHOOSE_PHOTO) {
            try {
                Uri uri = data.getData();
                mImageToBeAttached = MediaStore.Images.Media.getBitmap(
                        EmployeeNewActivity.this.getContentResolver(), uri);
                AssetFileDescriptor descriptor =
                        EmployeeNewActivity.this.getContentResolver().openAssetFileDescriptor(uri, "r");
                thumbnail = ImageUtils.thumbmailFromDescriptor(descriptor.getFileDescriptor(),
                        THUMBNAIL_SIZE_PX, THUMBNAIL_SIZE_PX);
                ivPhoto.setImageBitmap(thumbnail);
                hasImage = true;
            } catch (IOException e) {
                Log.e("EmployeeNewActivity->OnActivityResult", "Cannot get a selected photo from the gallery.", e);
            } catch (Exception e) {
                Log.e("EmployeeNewActivity->OnActivityResult", "Cannot get a selected photo from the gallery.", e);
            }
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void dispatchChoosePhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"),
                MyMainApplication.REQUEST_CHOOSE_PHOTO);
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "H2T_DMS_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        mImagePathToBeAttached = image.getAbsolutePath();
        return image;
    }
    private void deleteCurrentPhoto() {
        if (mImageToBeAttached != null) {
            mImageToBeAttached.recycle();
            mImageToBeAttached = null;

            ivPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera));
        }
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(EmployeeNewActivity.this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e("EmployeeNewActivity->dispatchTakePhotoIntent()", "Cannot create a temp image file", e);
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, MyMainApplication.REQUEST_TAKE_PHOTO);
            }
        }
    }

}