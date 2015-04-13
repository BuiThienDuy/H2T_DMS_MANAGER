package com.H2TFC.H2T_DMS_MANAGER.controllers.store_management;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.MyApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.TrungBayAdapter;
import com.H2TFC.H2T_DMS_MANAGER.models.StoreImage;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by c4sau on 28/03/2015.
 */
public class TrungBayActivity extends Activity {
    GridView gv_StoreImage;
    TrungBayAdapter storeImageAdapter;
    TextView tvEmpty;
    ProgressBar progressBarStoreImage;

    String store_id;
    String store_image_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trung_bay);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("EXTRAS_STORE_IMAGE_ID")) {
            store_image_id = getIntent().getStringExtra("EXTRAS_STORE_IMAGE_ID");
        }

        if (getIntent().hasExtra("EXTRAS_STORE_ID")) {
            store_id = getIntent().getStringExtra("EXTRAS_STORE_ID");
        }
        InitializeComponent();
        SetUpListView();
    }

    private void SetUpListView() {
        // Query data from local data store
        ParseQueryAdapter.QueryFactory<StoreImage> factory = new ParseQueryAdapter.QueryFactory<StoreImage>() {
            @Override
            public ParseQuery<StoreImage> create() {
                ParseQuery<StoreImage> query = StoreImage.getQuery();
                if (store_id != null) {
                    query.fromPin(DownloadUtils.PIN_STORE_IMAGE);
                    query.whereEqualTo("store_id", store_image_id);
                } else {
                    query.fromPin("PIN_DRAFT_PHOTO");
                }
                query.orderByDescending("createdAt");
                return query;
            }
        };

        // Set list adapter
        storeImageAdapter = new TrungBayAdapter(TrungBayActivity.this, factory);

        storeImageAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<StoreImage>() {
            @Override
            public void onLoading() {
                progressBarStoreImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<StoreImage> list, Exception e) {
                progressBarStoreImage.setVisibility(View.GONE);
            }
        });
        gv_StoreImage.setEmptyView(tvEmpty);
        gv_StoreImage.setAdapter(storeImageAdapter);
    }

    private void InitializeComponent() {
        gv_StoreImage = (GridView) findViewById(R.id.activity_trungbay_grid);
        tvEmpty = (TextView) findViewById(R.id.activity_trungbay_tv_empty);
        progressBarStoreImage = (ProgressBar) findViewById(R.id.activity_trungbay_management_progressbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.action_bar_trungbay,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
//            case R.id.action_bar_trungbay_new: {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent,  100);
//                break;
//            }
//            case R.id.action_bar_trungbay_done: {
//                finish();
//            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == MyApplication.REQUEST_ADD_NEW && data != null) {
                Bitmap bm = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();

                final ParseFile photo = new ParseFile("parse_photo.png", bitmapdata);
                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            StoreImage storeImage = new StoreImage();
                            storeImage.setName("");
                            storeImage.setPhoto(photo);

                            if (store_id != null) {
                                storeImage.setEmployeeId(ParseUser.getCurrentUser().getObjectId());
                                storeImage.setStoreId(store_image_id);
                                storeImage.pinInBackground(DownloadUtils.PIN_STORE_IMAGE, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        storeImageAdapter.loadObjects();
                                        storeImageAdapter.notifyDataSetChanged();
                                        gv_StoreImage.invalidateViews();
                                    }
                                });
                                storeImage.saveEventually();
                            } else {
                                storeImage.pinInBackground("PIN_DRAFT_PHOTO", new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            storeImageAdapter.loadObjects();
                                            storeImageAdapter.notifyDataSetChanged();
                                            gv_StoreImage.invalidateViews();
                                        } else {
                                            Log.d("TrungBayActivity", e.getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

            }
            if (requestCode == MyApplication.REQUEST_DELETE) {
                storeImageAdapter.loadObjects();
                storeImageAdapter.notifyDataSetChanged();
                gv_StoreImage.invalidateViews();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        storeImageAdapter.loadObjects();
        storeImageAdapter.notifyDataSetChanged();
        gv_StoreImage.invalidateViews();
    }


}