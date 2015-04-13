package com.H2TFC.H2T_DMS_MANAGER.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.H2TFC.H2T_DMS_MANAGER.R;

/**
 * Created by User on 02/03/2015.
 */
public class ViewImageFullScreen extends Activity {
    String imageId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.viewImage));

        ImageView imageView = (ImageView) findViewById(R.id.activity_image_full_screen_iv);
        if(getIntent().hasExtra("EXTRA_IMAGE")) {
            imageView.setImageBitmap((Bitmap) getIntent().getExtras().get("EXTRA_IMAGE"));
        }

        if(getIntent().hasExtra("EXTRA_IMAGE_ID")) {
            imageId = getIntent().getStringExtra("EXTRA_IMAGE_ID");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.action_bar_image_full_screen,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            /*case R.id.action_bar_image_trungbay_delete: {
                // delete online
                ParseQuery<StoreImage> query = StoreImage.getQuery();
                query.whereEqualTo("objectId",imageId);
                query.fromPin(DownloadUtils.PIN_STORE_IMAGE);
                query.getFirstInBackground(new GetCallback<StoreImage>() {
                    @Override
                    public void done(StoreImage storeImage, ParseException e) {
                        storeImage.deleteEventually();
                        // delete offline
                        ParseQuery<StoreImage> query2 = StoreImage.getQuery();
                        query2.whereEqualTo("objectId",imageId);
                        query2.fromPin("PIN_DRAFT_PHOTO");
                        query2.getFirstInBackground(new GetCallback<StoreImage>() {
                            @Override
                            public void done(StoreImage storeImage, ParseException e) {
                                if (storeImage != null) {
                                    storeImage.unpinInBackground("PIN_DRAFT_PHOTO", new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                Toast.makeText(ViewImageFullScreen.this,getString(R.string.deleteImageSuccess),Toast.LENGTH_LONG).show();
                finish();

                break;
            }*/
        }
        return super.onOptionsItemSelected(item);
    }
}