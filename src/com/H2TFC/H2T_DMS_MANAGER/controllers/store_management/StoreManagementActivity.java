package com.H2TFC.H2T_DMS_MANAGER.controllers.store_management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.MyMainApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.EmployeeListAdapter;
import com.H2TFC.H2T_DMS_MANAGER.controllers.LoginActivity;
import com.H2TFC.H2T_DMS_MANAGER.models.Area;
import com.H2TFC.H2T_DMS_MANAGER.models.Store;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.CustomPushUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.GPSTracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.ui.IconGenerator;
import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView;
import com.mobisys.android.autocompletetextviewcomponent.SelectionListener;
import com.parse.*;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.util.*;

import static com.H2TFC.H2T_DMS_MANAGER.utils.SizeUtils.distance;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class StoreManagementActivity extends Activity {
    /**
     * Max allowed duration for a "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 150;
    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;
    private static final double MOVE_SENSITIVITY = 1.25;
    EmployeeListAdapter employeeListAdapter;
    ListView lvEmployee;
    TextView tvEmptyView;
    ProgressBar progressBar;
    ImageButton btn_undo, btn_redo, btn_discard, btn_search;
    Button btn_tao;
    Polygon polygonStore;
    ImageView ivCrosshair;
    HashMap<Marker, Store> myMapMarker;
    ArrayList<Marker> selectedMarker;
    ClearableAutoTextView tvSearchMap;
    int employeeSelectedPosition = -1;
    private GoogleMap map;
    private Stack<Circle> circleOptionsList;
    private Stack<Circle> circleOptionsList_redo;
    private long pressStartTime;
    private float pressedX;
    private float pressedY;
    private boolean isPinchMode;
    private boolean isDoneResizing = true;
    private float lastCircleX;
    private float lastCircleY;
    SlidingLayer slidingLayer;
    Store currentStore = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_management);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.storeManagementTitle));

        DownloadUtils.DownloadParseEmployee(StoreManagementActivity.this, new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });

        DownloadUtils.DownloadParseStoreType(StoreManagementActivity.this, new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
        InitializeComponent();
        SetupMap();
        SetupListView();
        SetupEvent();

        DownloadUtils.DownloadParseStore(StoreManagementActivity.this, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                DrawStorePoint();
            }
        });

        DrawStorePoint();

        slidingLayer = (SlidingLayer) findViewById(R.id.activity_store_management_slidingLayer);

        slidingLayer.setShadowDrawable(R.drawable.sidebar_shadow);
        slidingLayer.setShadowSizeRes(R.dimen.shadow_size);
        slidingLayer.setOffsetDistanceRes(R.dimen.offset_distance);
        slidingLayer.setPreviewOffsetDistanceRes(R.dimen.preview_offset_distance);
        slidingLayer.setStickTo(SlidingLayer.STICK_TO_LEFT);
        slidingLayer.setChangeStateOnTap(true);
        slidingLayer.setSlidingFromShadowEnabled(true);

        if(getIntent().hasExtra("EXTRAS_STORE_ID")) {
            String storeId = getIntent().getStringExtra("EXTRAS_STORE_ID");
            ParseQuery<Store> storeParseQuery = Store.getQuery();
            storeParseQuery.whereEqualTo("objectId", storeId);
            storeParseQuery.fromPin(DownloadUtils.PIN_STORE);
            try {
                Store store = storeParseQuery.getFirst();
                if(store != null) {
                    ParseGeoPoint location = store.getLocationPoint();
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(lat, lng))      // Sets the center of the map to location user
                            .zoom(17)                                                                 // Sets the zoom
                            .build();                                                                 // Creates a CameraPosition from the builder
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    currentStore = store;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private void DrawStorePoint() {
        // clear map marker
        for (Marker marker : myMapMarker.keySet()) {
            marker.remove();
        }

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
        queryUser.fromPin(DownloadUtils.PIN_EMPLOYEE);
        queryUser.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                for (final ParseUser employee : list) {
                    ParseQuery<Store> queryStore = Store.getQuery();
                    queryStore.whereEqualTo("employee_id", employee.getObjectId());
                    queryStore.fromPin(DownloadUtils.PIN_STORE);
                    queryStore.findInBackground(new FindCallback<Store>() {
                        @Override
                        public void done(List<Store> list, ParseException e) {
                            for (final Store store : list) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(store.getLocationPoint().getLatitude(), store.getLocationPoint
                                        ().getLongitude()));
                                markerOptions.title(getString(R.string.store) + " " + store.getName());
                                markerOptions.snippet(getString(R.string.clickToViewStoreDetail));
                                IconGenerator iconGenerator = new IconGenerator(StoreManagementActivity.this);

                                iconGenerator.setColor(Store.getStatusColor(Store.StoreStatus.valueOf
                                        (store
                                                .getStatus())));
                                if (store.getStatus().equals(Store.StoreStatus.TIEM_NANG.name()) ||
                                        store.getStatus().equals(Store.StoreStatus.KHONG_DU_TIEU_CHUAN.name()) ||
                                        store.getStatus().equals(Store.StoreStatus.CHO_CAP_TREN.name())) {
                                    iconGenerator.setTextAppearance(R.style.iconGenText_WHITE);
                                }

                                Bitmap bitmap = iconGenerator.makeIcon((String) employee.get("name"));
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                                Marker marker = map.addMarker(markerOptions);
                                if(currentStore == store) {
                                    marker.showInfoWindow();
                                }
                                myMapMarker.put(marker, store);
                            }
                        }
                    });
                }
            }
        });


    }

    public void InitializeComponent() {
        lvEmployee = (ListView) findViewById(R.id.activity_store_management_lvemployee);
        progressBar = (ProgressBar) findViewById(R.id.activity_store_management_progressbar);
        tvEmptyView = (TextView) findViewById(R.id.activity_store_management_tv_empty);

        tvSearchMap = (ClearableAutoTextView) findViewById(R.id.activity_store_management_tv_search);

        // Map
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.activity_store_management_map)).getMap();

        // Image view
        ivCrosshair = (ImageView) findViewById(R.id.activity_store_management_iv_crosshair);

        circleOptionsList = new Stack<Circle>();
        circleOptionsList_redo = new Stack<Circle>();

        // Button
        btn_tao = (Button) findViewById(R.id.activity_store_management_btn_tao);
        btn_discard = (ImageButton) findViewById(R.id.activity_store_management_btn_discard);
        btn_redo = (ImageButton) findViewById(R.id.activity_store_management_btn_redo);
        btn_undo = (ImageButton) findViewById(R.id.activity_store_management_btn_undo);
        btn_search = (ImageButton) findViewById(R.id.activity_store_management_btn_search);

        // Other
        myMapMarker = new HashMap<Marker, Store>();
        selectedMarker = new ArrayList<Marker>();
    }

    public void SetupMap() {
        // Initialize The map
        try {
            MapsInitializer.initialize(StoreManagementActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setting up the button that show on the map
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);

        // Zooming camera to position user
        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.canGetLocation())
        {
            Location location = gpsTracker.getLocation();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                                                                 // Sets the zoom
                    .build();                                                                 // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            gpsTracker.showSettingsAlert();
        }


    }

    private class myTask extends AsyncTask<Void,Void,Void> {

        public myTask() {
            super();
        }

        protected Void doInBackground(Void... params) {

            //do stuff
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //do stuff
            //how to return a variable here?
        }
    }

    public void ShowMarkerSelected() {
        for(final Marker marker : myMapMarker.keySet()) {
            final Store store = myMapMarker.get(marker);
                ParseUser.getQuery()
                        .whereEqualTo("objectId", store.getEmployeeId())
                        .getFirstInBackground(new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser employee, ParseException e) {
                                if(e == null) {
                                    String title = "";
                                    if(selectedMarker.contains(marker)) {
                                        title = employee.get("name") + " - " + getString(R.string.selecting);
                                    } else {
                                        title = (String) employee.get("name");
                                    }
                                    IconGenerator iconGenerator = new IconGenerator(StoreManagementActivity.this);

                                    iconGenerator.setColor(Store.getStatusColor(Store.StoreStatus.valueOf
                                            (store
                                                    .getStatus())));
                                    if (store.getStatus().equals(Store.StoreStatus.TIEM_NANG.name()) ||
                                            store.getStatus().equals(Store.StoreStatus.KHONG_DU_TIEU_CHUAN.name()) ||
                                            store.getStatus().equals(Store.StoreStatus.CHO_CAP_TREN.name())) {
                                        iconGenerator.setTextAppearance(R.style.iconGenText_WHITE);
                                    }

                                    try {
                                        Bitmap bitmap = iconGenerator.makeIcon(title);
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                    } catch(IllegalArgumentException ex) {

                                    }
                                }
                            }
                        });
        }
    }

    public void SetupEvent() {
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Context context = getApplicationContext();
                try {
                    startActivityForResult(builder.build(context), MyMainApplication.REQUEST_GOOGLE_PLACES);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        lvEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                employeeSelectedPosition = position;
                view.setSelected(true);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!myMapMarker.get(marker).getStatus().equals(Store.StoreStatus.BAN_HANG.name())) {
                    // if store point are selected then remove from list
                    if (selectedMarker.contains(marker)) {
                        if (pointInCircle(marker.getPosition(), circleOptionsList)) {
                            selectedMarker.remove(marker);
                            ShowMarkerSelected();
                        }
                    } else {
                        // if store point are selected then add to list
                        if (pointInCircle(marker.getPosition(), circleOptionsList)) {
                            // check status of store
                            selectedMarker.add(marker);
                            ShowMarkerSelected();
                        }
                    }
                }

                return false;
            }
        });


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(StoreManagementActivity.this, StoreDetailActivity.class);
                Store store = myMapMarker.get(marker);
                intent.putExtra("EXTRAS_STORE_ID", store.getObjectId());
                intent.putExtra("EXTRAS_STORE_IMAGE_ID", store.getStoreImageId());
                if(store.getStatus().equals(Store.StoreStatus.BAN_HANG.name())) {
                    intent.putExtra("EXTRAS_STORE_POINT",true);
                }
                startActivity(intent);
            }
        });

        btn_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleOptionsList.isEmpty()) {
                    Toast.makeText(StoreManagementActivity.this, getString(R.string.couldNotUnDoAction), Toast.LENGTH_LONG)
                            .show();
                } else {
                    for(Marker marker : myMapMarker.keySet()) {
                        if(pointInCircle(marker.getPosition(),circleOptionsList) && !selectedMarker.contains
                                (marker)) {
                            selectedMarker.add(marker);
                        } else {
                            if(selectedMarker.contains(marker)) {
                                selectedMarker.remove(marker);
                            }
                        }
                    }

                    Circle circle = circleOptionsList.pop();
                    circleOptionsList_redo.add(circle);
                    circle.remove();

                    for(Marker marker : myMapMarker.keySet()) {
                        if(pointInCircle(marker.getPosition(),circleOptionsList) && !selectedMarker.contains
                                (marker)) {
                            selectedMarker.add(marker);
                        }
                    }

                    ShowMarkerSelected();

                }
            }
        });

        btn_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleOptionsList_redo.isEmpty()) {
                    Toast.makeText(StoreManagementActivity.this, getString(R.string.couldNotRedoAction), Toast.LENGTH_LONG).show();
                } else {
                    Circle circle = circleOptionsList_redo.pop();
                    circleOptionsList.add(map.addCircle(new CircleOptions()
                            .center(circle.getCenter())
                            .radius(circle.getRadius())
                            .fillColor(circle.getFillColor())
                            .strokeColor(circle.getStrokeColor())
                            .strokeWidth(circle.getStrokeWidth())));

                    for(Marker marker : myMapMarker.keySet()) {
                        if(pointInCircle(marker.getPosition(),circleOptionsList) && !selectedMarker.contains
                                (marker))
                            selectedMarker.add(marker);
                    }
                    ShowMarkerSelected();

                }
            }
        });

        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(StoreManagementActivity.this);
                alertDialog.setTitle(getString(R.string.deleteAllStorePointAndArea));
                alertDialog.setMessage(getString(R.string.areYouSureYouWantToRemoveAllArea));
                alertDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        while (!circleOptionsList.isEmpty()) {
                            circleOptionsList.pop().remove();
                        }
                        while (!circleOptionsList_redo.isEmpty()) {
                            circleOptionsList_redo.pop().remove();
                        }
                        ShowMarkerSelected();
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        btn_tao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Store> selectedStorePoint = new ArrayList<Store>();

                if (circleOptionsList == null || circleOptionsList.size() <= 0) {
                    Toast.makeText(StoreManagementActivity.this, getString(R.string.pleaseSelectAtLeastOneStorePoint), Toast
                            .LENGTH_SHORT).show();
                    return;
                }
                for (Marker marker : myMapMarker.keySet()) {
                    if (pointInCircle(marker.getPosition(), circleOptionsList) & !selectedStorePoint.contains(myMapMarker.get(marker))) {
                        Store store = myMapMarker.get(marker);
                        if (!store.getStatus().equals(Store.StoreStatus.BAN_HANG.name()))
                            selectedStorePoint.add(myMapMarker.get(marker));
                    }
                }


                ParseUser selectedEmployee = new ParseUser();
                if (employeeSelectedPosition >= 0) {
                    selectedEmployee = employeeListAdapter.getItem(employeeSelectedPosition);
                }

                if (employeeSelectedPosition < 0) {
                    if (!slidingLayer.isOpened()) {
                        slidingLayer.openLayer(true);
                    }
                    Toast.makeText(StoreManagementActivity.this, getString(R.string
                            .errorNotSelectEmployeeWhenDivideStore), Toast.LENGTH_SHORT).show();
                } else if (selectedStorePoint.size() <= 0) {
                    Toast.makeText(StoreManagementActivity.this, getString(R.string.pleaseSelectAtLeastOneStorePoint), Toast
                            .LENGTH_SHORT).show();
                } else {
                    // First confirm user to add new area
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(StoreManagementActivity.this);
                    alertDialog.setTitle(getString(R.string.divideStorePointTitle));
                    alertDialog.setMessage(getString(R.string.btnDivide) + " " + selectedStorePoint.size() + " " +
                            getString(R.string.pointForEmployee) + " " + selectedEmployee.get("name") + " " + getString(R.string.goSignContract));
                    final ParseUser finalSelectedEmployee = selectedEmployee;
                    alertDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int i = 1;
                            for (Store store : selectedStorePoint) {
                                store.setStatus(Store.StoreStatus.TIEM_NANG.name());
                                store.setEmployeeId(finalSelectedEmployee.getObjectId());
                                store.saveEventually();
                                if (i++ == selectedStorePoint.size()) {
                                    store.pinInBackground(DownloadUtils.PIN_STORE, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            DrawStorePoint();
                                            // Send push notification to NVKD
                                            String managerName = (String) ParseUser.getCurrentUser().get("name");
                                            CustomPushUtils.sendMessageToEmployee(finalSelectedEmployee.getObjectId()
                                                    , managerName + " " + getString(R.string.btnDivide) + " " + selectedStorePoint.size() + " " +
                                                    getString(R.string.pointForEmployee) + " " + finalSelectedEmployee.get("name") + " " + getString(R.string.goSignContract));
                                        }
                                    });
                                } else {
                                    store.pinInBackground(DownloadUtils.PIN_STORE, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                        }
                                    });
                                }

                            }
                        }

                    });

                    alertDialog.setNeutralButton(getString(R.string.storePointNotApproval), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int i = 1;
                            for (Store store : selectedStorePoint) {
                                store.setStatus(Store.StoreStatus.KHONG_DU_TIEU_CHUAN.name());
                                store.setEmployeeId(finalSelectedEmployee.getObjectId());
                                store.saveEventually();

                                // Send push notification to NVKD
                                String managerName = (String) ParseUser.getCurrentUser().get("name");
                                CustomPushUtils.sendMessageToEmployee(finalSelectedEmployee.getObjectId()
                                        , managerName + " " + getString(R.string.haveChangedStoreStatus) +
                                        " " + store.getName());

                                if (i++ == selectedStorePoint.size()) {
                                    store.pinInBackground(DownloadUtils.PIN_STORE, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            DrawStorePoint();
                                        }
                                    });
                                } else {
                                    store.pinInBackground(DownloadUtils.PIN_STORE, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                        }
                                    });
                                }

                            }
                        }
                    });

                    alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
            }
        });
        // Crosshair that help employee add new survey store point
        ivCrosshair.setOnTouchListener(new View.OnTouchListener() {
            public float offsetX;
            public float offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int historySize;
                double lastDistance;
                double oneBeforeLastDistance;

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        pressStartTime = System.currentTimeMillis();
                        pressedX = event.getX();
                        pressedY = event.getY();


                        offsetX = ivCrosshair.getX() - event.getRawX();
                        offsetY = ivCrosshair.getY() - event.getRawY();
                        lastCircleX = ivCrosshair.getX();
                        lastCircleY = ivCrosshair.getY();
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        isPinchMode = true;
                        isDoneResizing = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        ivCrosshair.setX(lastCircleX);
                        ivCrosshair.setY(lastCircleY);

                        if (!isPinchMode && isDoneResizing) {

                            historySize = event.getHistorySize();
                            if (historySize > 0) {

                                oneBeforeLastDistance = Math.sqrt((event.getX() - event.getHistoricalX(0, historySize - 1)) *
                                        (event.getX() - event.getHistoricalX(0, historySize - 1)) +
                                        (event.getY() - event.getHistoricalY(0, historySize - 1)) *
                                                (event.getY() - event.getHistoricalY(0, historySize - 1)));


                                if (oneBeforeLastDistance > MOVE_SENSITIVITY) {
                                    //circle.centerX = (int) event.getX();
                                    //circle.centerY = (int) event.getY();
                                    ivCrosshair.setX(event.getRawX() + offsetX);
                                    ivCrosshair.setY(event.getRawY() + offsetY);
                                    lastCircleX = ivCrosshair.getX();
                                    lastCircleY = ivCrosshair.getY();

                                }
                            }
                        }
                        if (isPinchMode) {
                            ivCrosshair.setX(lastCircleX);
                            ivCrosshair.setY(lastCircleY);

                            historySize = event.getHistorySize();
                            if (historySize > 0) {

                                lastDistance = Math.sqrt((event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1)) +
                                        (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1)));

                                oneBeforeLastDistance = Math.sqrt((event.getHistoricalX(0, historySize - 1) - event.getHistoricalX(1, historySize - 1)) *
                                        (event.getHistoricalX(0, historySize - 1) - event.getHistoricalX(1, historySize - 1)) +
                                        (event.getHistoricalY(0, historySize - 1) - event.getHistoricalY(1, historySize - 1)) *
                                                (event.getHistoricalY(0, historySize - 1) - event.getHistoricalY(1, historySize - 1)));


                                if (lastDistance < oneBeforeLastDistance) {
                                    ivCrosshair.getLayoutParams().height -= Math.abs(lastDistance - oneBeforeLastDistance);
                                    ivCrosshair.getLayoutParams().width -= Math.abs(lastDistance -
                                            oneBeforeLastDistance);
                                } else {
                                    ivCrosshair.getLayoutParams().height += Math.abs(lastDistance - oneBeforeLastDistance);
                                    ivCrosshair.getLayoutParams().width += Math.abs(lastDistance -
                                            oneBeforeLastDistance);
                                }
                            }
                        }
                        lastCircleX = ivCrosshair.getX();
                        lastCircleY = ivCrosshair.getY();
                        ivCrosshair.requestLayout();
                        break;

                    case MotionEvent.ACTION_POINTER_UP:


                        ivCrosshair.setX(lastCircleX);
                        ivCrosshair.setY(lastCircleY);
                        isPinchMode = false;
                        break;

                    case MotionEvent.ACTION_UP:
                        ivCrosshair.setX(lastCircleX);
                        ivCrosshair.setY(lastCircleY);
                        isPinchMode = false;
                        isDoneResizing = true;

                        long pressDuration = System.currentTimeMillis() - pressStartTime;
                        if (pressDuration < MAX_CLICK_DURATION && distance(getApplicationContext(), pressedX, pressedY, event.getX(),
                                event.getY()) < MAX_CLICK_DISTANCE) {
                            // Click event has occurred
                            Projection projection = map.getProjection();
                            int centerX = (int) (ivCrosshair.getX() + ivCrosshair.getWidth() / 2);
                            int centerY = (int) (ivCrosshair.getY() + ivCrosshair.getHeight() / 2);
                            LatLng centerImagePoitnt = projection.fromScreenLocation(new Point(centerX, centerY));

                            // Calculate the radius to match circle layout(imageview)
                            int x1 = (int) (ivCrosshair.getX());
                            int y1 = (int) (ivCrosshair.getY());
                            int x2 = (int) (ivCrosshair.getX() + ivCrosshair.getWidth() / 2 - 10); // -10 based on the image
                            int y2 = (int) (ivCrosshair.getY()); // height doesn't need to add cause: i + x = r
                            LatLng x1y1 = projection.fromScreenLocation(new Point(x1, y1));
                            LatLng x2y2 = projection.fromScreenLocation(new Point(x2, y2));

                            Location location_1 = new Location("");
                            location_1.setLatitude(x1y1.latitude);
                            location_1.setLongitude(x1y1.longitude);

                            Location location_2 = new Location("");
                            location_2.setLatitude(x2y2.latitude);
                            location_2.setLongitude(x2y2.longitude);

                            double radius = location_1.distanceTo(location_2);

                            Circle circle = map.addCircle(new CircleOptions()
                                    .center(centerImagePoitnt)
                                    .radius(radius)
                                    .fillColor(Color.argb(64, 0, 0, 0))
                                    .strokeColor(Color.argb(64, 0, 0, 0))
                                    .strokeWidth(1));
                            circleOptionsList.add(circle);

                            for(Marker marker : myMapMarker.keySet()) {
                                if(pointInCircle(marker.getPosition(),circleOptionsList) && !selectedMarker.contains
                                        (marker) && !myMapMarker.get(marker).getStatus().equals(Store.StoreStatus.BAN_HANG.name()))
                                selectedMarker.add(marker);
                            }
                            ShowMarkerSelected();

                            circleOptionsList_redo.clear();
                        }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    case MotionEvent.ACTION_HOVER_MOVE:
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        tvSearchMap.setSelectionListener(new SelectionListener() {
            @Override
            public void onItemSelection(ClearableAutoTextView.DisplayStringInterface selectedItem) {

            }

            @Override
            public void onReceiveLocationInformation(double lat, double lng) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lat, lng), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(lat, lng))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                                //.bearing(90)                // Sets the orientation of the camera to east
                                //.tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

    }

    public boolean pointInCircle(LatLng point, Stack<Circle> circleList) {
        double px = point.latitude;
        double py = point.longitude;

        for (Circle circle : circleList) {
            double center_x = circle.getCenter().latitude;
            double center_y = circle.getCenter().longitude;
            //(px - center_x) * (px - center_x) + (py - center_y) * (py - center_y) < circle.getRadius() * circle.getRadius()

            float[] distanceBetween = new float[1];
            Location.distanceBetween(px, py, center_x, center_y, distanceBetween);

            if (distanceBetween[0] < circle.getRadius()) {
                return true;
            }
        }
        return false;
    }

    public boolean pointInPolygon(LatLng point, Polygon polygon) {
        // ray casting alogrithm http://rosettacode.org/wiki/Ray-casting_algorithm
        int crossings = 0;
        List<LatLng> path = polygon.getPoints();
        path.remove(path.size() - 1); //remove the last point that is added automatically by getPoints()

        // for each edge
        for (int i = 0; i < path.size(); i++) {
            LatLng a = path.get(i);
            int j = i + 1;
            //to close the last edge, you have to take the first point of your polygon
            if (j >= path.size()) {
                j = 0;
            }
            LatLng b = path.get(j);
            if (rayCrossesSegment(point, a, b)) {
                crossings++;
            }
        }

        // odd number of crossings?
        return (crossings % 2 == 1);
    }

    public boolean rayCrossesSegment(LatLng point, LatLng a, LatLng b) {
        // Ray Casting algorithm checks, for each segment, if the point is 1) to the left of the segment and 2) not above nor below the segment. If these two conditions are met, it returns true
        double px = point.longitude,
                py = point.latitude,
                ax = a.longitude,
                ay = a.latitude,
                bx = b.longitude,
                by = b.latitude;
        if (ay > by) {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0 || ax < 0 || bx < 0) {
            px += 360;
            ax += 360;
            bx += 360;
        }
        // if the point has the same latitude as a or b, increase slightly py
        if (py == ay || py == by) py += 0.00000001;


        // if the point is above, below or to the right of the segment, it returns false
        if ((py > by || py < ay) || (px > Math.max(ax, bx))) {
            return false;
        }
        // if the point is not above, below or to the right and is to the left, return true
        else if (px < Math.min(ax, bx)) {
            return true;
        }
        // if the two above conditions are not met, you have to compare the slope of segment [a,b] (the red one here) and segment [a,p] (the blue one here) to see if your point is to the left of segment [a,b] or not
        else {
            double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.POSITIVE_INFINITY;
            double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.POSITIVE_INFINITY;
            return (blue >= red);
        }

    }

    private void SetupListView() {
        // Query data from local data store
        ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>() {
            @Override
            public ParseQuery<ParseUser> create() {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
                query.orderByDescending("createdAt");
                query.fromPin(DownloadUtils.PIN_EMPLOYEE);
                return query;
            }
        };

        lvEmployee.setEmptyView(tvEmptyView);

        // Set list adapter
        employeeListAdapter = new EmployeeListAdapter(StoreManagementActivity.this, factory);
        lvEmployee.setEmptyView(tvEmptyView);
        lvEmployee.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        employeeListAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseUser>() {
            @Override
            public void onLoading() {
                tvEmptyView.setText("");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<ParseUser> list, Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
        lvEmployee.setAdapter(employeeListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_store_management,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }

            case R.id.action_bar_store_management_store_type: {
                Intent intent = new Intent(StoreManagementActivity.this,StoreTypeManagementActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.action_bar_store_management_log_out: {
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(StoreManagementActivity.this);
                confirmDialog.setMessage(getString(R.string.confirmLogOut));
                confirmDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(StoreManagementActivity.this, LoginActivity.class);
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
        if (requestCode == MyMainApplication.REQUEST_GOOGLE_PLACES) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        place.getLatLng(), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(place.getLatLng())      // Sets the center of the map to location user
                        .zoom(17)                                                                 // Sets the zoom
                        .build();                                                                 // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }
}