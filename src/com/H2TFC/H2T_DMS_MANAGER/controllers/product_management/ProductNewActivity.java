package com.H2TFC.H2T_DMS_MANAGER.controllers.product_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;
import com.H2TFC.H2T_DMS_MANAGER.MyApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.ImageUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class ProductNewActivity extends Activity {
    private static final int THUMBNAIL_SIZE_PX = 150;
    BootstrapEditText etName, etUnit, etPrice;
    BootstrapButton btnDone, btnCancel;
    ParseImageView ivPhoto;
    private String mImagePathToBeAttached;
    private Bitmap mImageToBeAttached;
    boolean hasImage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_new);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        InitializeComponent();

        String productId = "";
        if (getIntent().hasExtra("EDIT")) {
            productId = getIntent().getExtras().getString("EDIT");
        }

        if (productId == null || productId.isEmpty()) {
            setTitle(getString(R.string.productNewTitle));
            NewProduct_OnCreate();
        } else {
            setTitle(getString(R.string.productNewUpdateTitle));
            EditProduct_OnCreate(productId);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void EditProduct_OnCreate(String productId) {
        btnDone.setText(getString(R.string.save));
        ParseQuery<Product> productParseQuery = Product.getQuery()
                .whereEqualTo("objectId", productId)
                .fromPin(DownloadUtils.PIN_PRODUCT);

        productParseQuery.getFirstInBackground(new GetCallback<Product>() {
            @Override
            public void done(Product product, ParseException e) {
                if(e== null) {
                    if(product != null) {
                        etName.setText(product.getProductName());
                        etUnit.setText(product.getUnit());
                        etPrice.setText( Double.toString(product.getPrice()));
                        ivPhoto.setParseFile(product.getPhoto());
                        ivPhoto.loadInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                // nothing to do
                            }
                        });
                    }
                }
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items;
                if (mImageToBeAttached != null)
                    items = new CharSequence[]{getString(R.string.takePhoto), getString(R.string.choosePhoto), getString(R.string.removePhoto)};
                else
                    items = new CharSequence[]{getString(R.string.takePhoto), getString(R.string.choosePhoto)};

                AlertDialog.Builder builder = new AlertDialog.Builder(ProductNewActivity.this);
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

        final String finalProductId = productId;
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String unit = etUnit.getText().toString();
                String price = etPrice.getText().toString();

                ParseQuery<Product> productParseQuery = Product.getQuery()
                        .whereEqualTo("objectId", finalProductId)
                        .fromPin(DownloadUtils.PIN_PRODUCT);
                final Product product;

                final ProgressDialog progressDialog = new ProgressDialog(ProductNewActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle(getString(R.string.productNewUpdateTitle));
                progressDialog.setMessage(getString(R.string.PleaseWait));
                progressDialog.show();
                try {
                    product = productParseQuery.getFirst();
                    product.setName(name);
                    product.setUnit(unit);
                    product.setPrice(Double.parseDouble(price));
                    product.setStatus("");

                    if(hasImage) {
                        byte[] bitmapdata  = ImageUtils.bitmapToByteArray(((BitmapDrawable) ivPhoto.getDrawable()).getBitmap());

                        final ParseFile file = new ParseFile(ParseUser.getCurrentUser().getUsername() + "photo"  + "" +
                                ".png",bitmapdata);
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    product.setPhoto(file);
                                    product.saveEventually();
                                    progressDialog.dismiss();
                                    product.pinInBackground(DownloadUtils.PIN_PRODUCT, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Toast.makeText(ProductNewActivity.this, getString(R.string.updateProductInfomationSuccess), Toast.LENGTH_LONG)
                                                    .show();
                                            finish();
                                        }
                                    });
                                } else {
                                    e.printStackTrace();
                                }

                            }
                        }, new ProgressCallback() {
                            @Override
                            public void done(Integer integer) {
                               if(integer == 100) {
                                   progressDialog.dismiss();
                               } else {
                                   progressDialog.setMessage("Ðang t?i: " + integer);
                               }
                            }
                        });


                    } else {
                        product.saveEventually();
                        progressDialog.dismiss();
                        product.pinInBackground(DownloadUtils.PIN_PRODUCT, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(ProductNewActivity.this, getString(R.string.updateProductInfomationSuccess), Toast.LENGTH_LONG)
                                        .show();
                                finish();
                            }
                        });
                    }


                } catch (ParseException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }


            }
        });
    }

    private void NewProduct_OnCreate() {
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items;
                if (mImageToBeAttached != null)
                    items = new CharSequence[]{getString(R.string.takePhoto), getString(R.string.choosePhoto), getString(R.string.removePhoto)};
                else
                    items = new CharSequence[]{getString(R.string.takePhoto), getString(R.string.choosePhoto)};

                AlertDialog.Builder builder = new AlertDialog.Builder(ProductNewActivity.this);
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
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String unit = etUnit.getText().toString();
                String price = etPrice.getText().toString();

                if(name.length() == 0) {
                    Toast.makeText(ProductNewActivity.this,getString(R.string.errorBlankProductName),Toast.LENGTH_LONG).show();
                    return;
                }

                if(unit.length() == 0) {
                    Toast.makeText(ProductNewActivity.this,getString(R.string.errorBlankProductUnit),Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                if(price.length() == 0) {
                    Toast.makeText(ProductNewActivity.this,getString(R.string.errorBlankProductPrice),Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                if(!hasImage){
                    Toast.makeText(ProductNewActivity.this,getString(R.string.errorEmptyProductPhoto),Toast.LENGTH_LONG)
                            .show();
                    return;
                }


                Product productToAdd = new Product();
                productToAdd.setName(name);
                productToAdd.setUnit(unit);
                productToAdd.setPrice(Double.parseDouble(price));

                byte[] bitmapdata  = ImageUtils.bitmapToByteArray(((BitmapDrawable) ivPhoto.getDrawable()).getBitmap());

                ParseFile file = new ParseFile(ParseUser.getCurrentUser().getUsername() + "photo" + ".png",bitmapdata);
                try {
                    file.save();
                    productToAdd.setPhoto(file);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                productToAdd.saveEventually();
                productToAdd.pinInBackground(DownloadUtils.PIN_PRODUCT, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(ProductNewActivity.this, getString(R.string.addNewProductSuccess), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });
    }

    private void deleteCurrentPhoto() {
        if (mImageToBeAttached != null) {
            mImageToBeAttached.recycle();
            mImageToBeAttached = null;

            ivPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera));
        }
    }

    private void dispatchChoosePhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"),
                MyApplication.REQUEST_CHOOSE_PHOTO);
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "H2T_DMS_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        mImagePathToBeAttached = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(ProductNewActivity.this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e("ProductNewActivity->dispatchTakePhotoIntent()", "Cannot create a temp image file", e);
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, MyApplication.REQUEST_TAKE_PHOTO);
            }
        }
    }


    private void InitializeComponent() {
        btnDone = (BootstrapButton) findViewById(R.id.activity_product_new_btn_done);
        btnCancel = (BootstrapButton) findViewById(R.id.activity_product_new_btn_cancel);

        etName = (BootstrapEditText) findViewById(R.id.activity_product_new_et_name);
        etPrice = (BootstrapEditText) findViewById(R.id.activity_product_new_et_price);
        etUnit = (BootstrapEditText) findViewById(R.id.activity_product_new_et_unit);

        ivPhoto = (ParseImageView) findViewById(R.id.activity_product_new_iv_photo);
        ivPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_light));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap thumbnail;
        if (requestCode == MyApplication.REQUEST_TAKE_PHOTO) {
            mImageToBeAttached = BitmapFactory.decodeFile(mImagePathToBeAttached);
            thumbnail = ImageUtils.thumbmailFromFile(mImagePathToBeAttached,
                        THUMBNAIL_SIZE_PX, THUMBNAIL_SIZE_PX);

            // Delete the temporary image file
            File file = new File(mImagePathToBeAttached);
            file.delete();
            mImagePathToBeAttached = null;
            ivPhoto.setImageBitmap(thumbnail);
            hasImage = true;
        } else if (requestCode == MyApplication.REQUEST_CHOOSE_PHOTO) {
            try {
                Uri uri = data.getData();
                mImageToBeAttached = MediaStore.Images.Media.getBitmap(
                        ProductNewActivity.this.getContentResolver(), uri);
                    AssetFileDescriptor descriptor =
                            ProductNewActivity.this.getContentResolver().openAssetFileDescriptor(uri, "r");
                    thumbnail = ImageUtils.thumbmailFromDescriptor(descriptor.getFileDescriptor(),
                            THUMBNAIL_SIZE_PX, THUMBNAIL_SIZE_PX);
                ivPhoto.setImageBitmap(thumbnail);
                hasImage = true;
            } catch (IOException e) {
                Log.e("ProductNewActivity->OnActivityResult", "Cannot get a selected photo from the gallery.", e);
            } catch (Exception e) {
                Log.e("ProductNewActivity->OnActivityResult", "Cannot get a selected photo from the gallery.", e);
            }
        }

    }
}