package com.H2TFC.H2T_DMS_MANAGER.controllers.street_divide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.*;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.MyMainApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.controllers.dialogs.ColorPickerDialog;
import com.H2TFC.H2T_DMS_MANAGER.models.Area;
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
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;
import com.mobisys.android.autocompletetextviewcomponent.ClearableAutoTextView;
import com.mobisys.android.autocompletetextviewcomponent.SelectionListener;
import com.parse.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class StreetDivideActivity extends Activity {
    /**
     * Max allowed duration for a "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 150;
    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;
    public Button btn_colorPicker_fill;
    public Button btn_colorPicker_stroke;
    ClearableAutoTextView tvSearchMap;
    String employee_id = null;
    //Marker markerClicked = null;
    Polygon polygonStore;
    ImageView ivCrosshair;
    ImageButton btn_undo, btn_redo, btn_discard, btn_search;
    Button btn_tao;
    private GoogleMap map;
    private Stack<Marker> markerOptionsList;
    private Stack<Marker> markerOptionsList_redo;
    private long pressStartTime;
    private float pressedX;
    private float pressedY;

    ArrayList<Polygon> myPolygon;

    ArrayList<Polygon> mapPolygon;
    ArrayList<Marker> mapMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_divide);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (ConnectUtils.hasConnectToInternet(StreetDivideActivity.this)) {
            DownloadUtils.DownloadParseArea(StreetDivideActivity.this,new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    drawAllEmployeePolygon();
                }
            });
        }

        InitializeComponent();
        SetupEvent();
        SetupMap();

        if (getIntent().hasExtra("EMPLOYEE_ID")) {
            employee_id = getIntent().getExtras().getString("EMPLOYEE_ID");
        }

        String roleName = ParseUser.getCurrentUser().get("role_name").toString();

        if (roleName.equals("NVQL")) {
            setTitle(getString(R.string.streetDivide_NVQL_Title));
        }
        if (roleName.equals("NVQL_V")) {
            setTitle(getString(R.string.streetDivide_NVQL_V_Title));
        }
        if (roleName.equals("GDKD")) {
            setTitle(getString(R.string.streetDivide_GDKD_Title));
        }

        mapPolygon = new ArrayList<Polygon>();
        mapMarker = new ArrayList<Marker>();
    }

    private static float distance(Context context, float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx, context);
    }

    private static float pxToDp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    private void SetupMap() {
        // Gets the MapView from the XML layout and creates it
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.activity_street_divide_map)).getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        map.setMyLocationEnabled(true);

        try {
            MapsInitializer.initialize(StreetDivideActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /////----------------------------------Zooming camera to position user-----------------

        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.canGetLocation())
        {
            try {
                Location location = gpsTracker.getLocation();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                                                                 // Sets the zoom
                        .build();                                                                 // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } catch(NullPointerException ex) {

            }
        } else {
            gpsTracker.showSettingsAlert();
        }


    }

    private void SetupEvent() {
        btn_colorPicker_fill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(StreetDivideActivity.this);
                colorPickerDialog.show();
            }
        });

        btn_colorPicker_stroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(StreetDivideActivity.this);
                colorPickerDialog.show();
            }
        });

        btn_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markerOptionsList.isEmpty()) {
                    Toast.makeText(StreetDivideActivity.this, getString(R.string.couldNotUnDoAction), Toast.LENGTH_LONG)
                            .show();
                } else {
                    Marker marker = markerOptionsList.pop();
                    markerOptionsList_redo.add(marker);
                    marker.remove();
                    connectAllMarker();
                }
            }
        });

        btn_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markerOptionsList_redo.isEmpty()) {
                    Toast.makeText(StreetDivideActivity.this, getString(R.string.couldNotRedoAction), Toast.LENGTH_LONG).show();
                } else {
                    Marker marker = markerOptionsList_redo.pop();
                    IconGenerator iconFactory = new IconGenerator(StreetDivideActivity.this);
                    Bitmap iconBitmap = iconFactory.makeIcon(Integer.toString(markerOptionsList.size() + 1));
                    markerOptionsList.add(map.addMarker(new MarkerOptions()
                            .position(marker.getPosition()).title(marker.getPosition().toString()).icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))));
                }
                connectAllMarker();
            }
        });

        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(StreetDivideActivity.this);
                alertDialog.setTitle(getString(R.string.deleteAllStorePointAndArea));
                alertDialog.setMessage(getString(R.string.areYouSureYouWantToRemoveAllArea));
                alertDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        while (!markerOptionsList.isEmpty()) {
                            markerOptionsList.pop().remove();
                        }
                        while (!markerOptionsList_redo.isEmpty()) {
                            markerOptionsList_redo.pop().remove();
                        }
                        connectAllMarker();
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
                if (markerOptionsList.size() < 2) {
                    Toast.makeText(StreetDivideActivity.this, getString(R.string.errorRequire2PointForStreetDivide), Toast
                            .LENGTH_SHORT).show();
                } else {
                    // First confirm user to add new area
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(StreetDivideActivity.this);
                    alertDialog.setTitle(getString(R.string.createArea));
                    alertDialog.setMessage(getString(R.string.areYouSureYouWantToCreateArea));
                    alertDialog.setPositiveButton(getString(R.string.approve), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Area employeeArea = new Area();
                            employeeArea.setEmployeeId(employee_id);

                            ColorDrawable buttonFillColor = (ColorDrawable) btn_colorPicker_fill.getBackground();

                            String currentRole = ParseUser.getCurrentUser().getString("role_name");
                            if (currentRole.equals("NVQL")) {
                                buttonFillColor.setAlpha(128);
                            }
                            if (currentRole.equals("NVQL_V")) {
                                buttonFillColor.setAlpha(64);
                            }
                            if (currentRole.equals("GDKD")) {
                                buttonFillColor.setAlpha(32);
                            }
                            employeeArea.setFillColor(buttonFillColor.getColor());

                            employeeArea.setStatus(Area.AreaStatus.MOI_TAO.name());

                            // Convert Stack<Marker> to List<GeoPoint>
                            ArrayList<ParseGeoPoint> geoPointList = new ArrayList<ParseGeoPoint>();
                            for (Marker marker : markerOptionsList) {
                                ParseGeoPoint geoPoint = new ParseGeoPoint();
                                geoPoint.setLatitude(marker.getPosition().latitude);
                                geoPoint.setLongitude(marker.getPosition().longitude);

                                geoPointList.add(geoPoint);
                            }
                            employeeArea.setNodeList(geoPointList);
                            employeeArea.saveEventually();
                            Toast.makeText(StreetDivideActivity.this, getString(R.string.streetDivideToEmployeeSuccess),
                                    Toast.LENGTH_LONG).show();

                            // Remove current polygon
                            while (!markerOptionsList.isEmpty()) {
                                markerOptionsList.pop().remove();
                            }
                            while (!markerOptionsList_redo.isEmpty()) {
                                markerOptionsList_redo.pop().remove();
                            }
                            connectAllMarker();

                            // Pinning
                            employeeArea.pinInBackground(DownloadUtils.PIN_AREA, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (isFinishing()) {
                                        return;
                                    }
                                    if (e != null) {
                                        Log.e("StreetDivideActivity",
                                                "Error saving: " + e.getMessage());
                                    } else {
                                        drawAllEmployeePolygon();
                                    }
                                }
                            });


                            // Send push notification to NVKD
                            String managerName = (String) ParseUser.getCurrentUser().get("name");
                            CustomPushUtils.sendMessageToEmployee(employee_id, managerName + getString(R.string.pushNotificationStreetDivide));

                            dialog.dismiss();
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

        ivCrosshair.setOnTouchListener(new View.OnTouchListener() {
            public float offsetX;
            public float offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int theAction = event.getAction();
                switch (theAction) {
                    case MotionEvent.ACTION_DOWN:
                        // Button down
                        offsetX = ivCrosshair.getX() - event.getRawX();
                        offsetY = ivCrosshair.getY() - event.getRawY();

                        pressStartTime = System.currentTimeMillis();
                        pressedX = event.getX();
                        pressedY = event.getY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved
                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;
                        v.setX(newX);
                        v.setY(newY);

                        break;
                    case MotionEvent.ACTION_UP:
                        long pressDuration = System.currentTimeMillis() - pressStartTime;
                        if (pressDuration < MAX_CLICK_DURATION && distance(getApplicationContext(), pressedX, pressedY, event.getX(),
                                event
                                        .getY
                                                ()) < MAX_CLICK_DISTANCE) {
                            // Click event has occurred
                            // Button up
                            MarkerOptions markerOptions = new MarkerOptions();
                            Projection projection = map.getProjection();
                            int centerX = (int) (ivCrosshair.getX() + ivCrosshair.getWidth() / 2);
                            int centerY = (int) (ivCrosshair.getY() + ivCrosshair.getHeight() / 2);

                            LatLng centerImagePoint = projection.fromScreenLocation(new Point(centerX, centerY));

                            boolean isInPolygon = false;
                            for(Polygon polygon : myPolygon) {
                                ArrayList<LatLng> polygonList =new ArrayList<LatLng>();
                                for(LatLng latLng : polygon.getPoints()) {
                                    polygonList.add(latLng);
                                }
                                if (PolyUtil.containsLocation(centerImagePoint,polygonList,true)) {
                                    isInPolygon = true;
                                }
                            }
                            if(isInPolygon || ParseUser.getCurrentUser().getString("role_name").equals("GDKD")) {
                                IconGenerator iconFactory = new IconGenerator(StreetDivideActivity.this);
                                Bitmap iconBitmap = iconFactory.makeIcon(Integer.toString(markerOptionsList.size() + 1));

                                markerOptions.position(centerImagePoint);
                                markerOptions.title(centerImagePoint.toString());
                                markerOptions.draggable(false);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));

                                markerOptionsList.add(map.addMarker(markerOptions));

                                connectAllMarker();
                            } else {
                                Toast.makeText(StreetDivideActivity.this,getString(R.string
                                        .streetDivideOutsideOfArea),Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
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



    private void InitializeComponent() {
        markerOptionsList = new Stack<Marker>();
        markerOptionsList_redo = new Stack<Marker>();

        // Button
        btn_colorPicker_fill = (Button) findViewById(R.id.activity_street_btn_fillcolor);
        btn_colorPicker_stroke = (Button) findViewById(R.id.activity_street_btn_strokecolor);
        btn_undo = (ImageButton) findViewById(R.id.activity_street_btn_undo);
        btn_redo = (ImageButton) findViewById(R.id.activity_street_btn_redo);
        btn_discard = (ImageButton) findViewById(R.id.activity_street_btn_discard);
        btn_tao = (Button) findViewById(R.id.activity_street_divide_btn_tao);
        btn_search = (ImageButton) findViewById(R.id.activity_street_divide_btn_search);

        // Crosshair
        ivCrosshair = (ImageView) findViewById(R.id.activity_street_iv_crosshair);

        // AutoCompleteTextView
        tvSearchMap = (ClearableAutoTextView) findViewById(R.id.activity_street_divide_tv_search);

        myPolygon = new ArrayList<Polygon>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        drawAllEmployeePolygon();
    }

    private void drawAllEmployeePolygon() {
        // clear all marker and polygon
        for(Marker marker : mapMarker) {
            marker.remove();
        }
        for(Polygon polygon : mapPolygon) {
            polygon.remove();
        }

        for(Polygon polygon : myPolygon) {
            polygon.remove();
        }

        // Draw current area survey
        ParseQuery<Area> areaParseQuery = Area.getQuery();
        areaParseQuery.whereEqualTo("employee_id",ParseUser.getCurrentUser().getObjectId());
        areaParseQuery.fromPin(DownloadUtils.PIN_AREA);
        areaParseQuery.findInBackground(new FindCallback<Area>() {
            @Override
            public void done(List<Area> list, ParseException e) {
                for(Area area : list) {
                    PolygonOptions polygonOptions = new PolygonOptions();
                    polygonOptions.fillColor(Color.argb(16, 155, 48, 255));
                    polygonOptions.strokeColor(Color.YELLOW);
                    polygonOptions.strokeWidth(5);
                    for(ParseGeoPoint point : area.getNodeList()) {
                        polygonOptions.add(new LatLng(point.getLatitude(),point.getLongitude()));
                    }

                    myPolygon.add(map.addPolygon(polygonOptions));
                }
            }
        });

        ParseQuery<ParseUser> queryEmployee = ParseUser.getQuery();
        queryEmployee.whereEqualTo("manager_id", ParseUser.getCurrentUser().getObjectId());
        queryEmployee.fromPin(DownloadUtils.PIN_EMPLOYEE);

        queryEmployee.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    // Get area of each employee
                    for (final ParseUser employee : list) {
                        final String employeeName = employee.getString("name");
                        ParseQuery<Area> queryEmployeeArea = Area.getQuery();
                        queryEmployeeArea.whereEqualTo("employee_id", employee.getObjectId());
                        queryEmployeeArea.fromPin(DownloadUtils.PIN_AREA);
                        queryEmployeeArea.findInBackground(new FindCallback<Area>() {
                            @Override
                            public void done(List<Area> list, ParseException e) {
                                if (e == null) {
                                    // Get all area of that employee
                                    if (list.size() == 0) return;

                                    for (Area employeeArea : list) {
                                        try {
                                            ArrayList<ParseGeoPoint> listGeoPoint = (ArrayList<ParseGeoPoint>) employeeArea.getNodeList();
                                            // get list of Parse geopoint then make a polygon
                                            PolygonOptions polygonOptions = new PolygonOptions();
                                            for (ParseGeoPoint geoPoint : listGeoPoint) {
                                                // Init marker to indicate area employee name
                                                MarkerOptions markerOptions = new MarkerOptions();

                                                // Generate marker text equal employee name
                                                IconGenerator iconGenerator = new IconGenerator(StreetDivideActivity.this);
                                                iconGenerator.setColor(Area.getStatusColor(Area.AreaStatus
                                                        .valueOf(employeeArea
                                                                .getStatus())));
                                                if(employeeArea.getStatus().equals(Area.AreaStatus.MOI_TAO.name
                                                        ())) {
                                                    iconGenerator.setTextAppearance(R.style.iconGenText_WHITE);
                                                }
                                                Bitmap bitmap = iconGenerator.makeIcon(employeeName);
                                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                                                double lat = geoPoint.getLatitude();
                                                double lng= geoPoint.getLongitude();
                                                markerOptions.position(new LatLng(lat, lng));

                                                Marker marker = map.addMarker(markerOptions);
                                                mapMarker.add(marker);

                                                polygonOptions.add(new LatLng(lat, lng));
                                            }
                                            polygonOptions.fillColor(employeeArea.getFillColor());
                                            //  polygonOptions.strokeColor(AreaUtils.getStatusColor(AreaUtils.AreaStatus
                                            //                                                    .valueOf(employeeArea.getStatus())));
                                            polygonOptions.strokeWidth(4);

                                            if (employeeArea.getNodeList().size() > 0 ) {
                                                mapPolygon.add(map.addPolygon(polygonOptions));
                                            }
                                        } catch(NullPointerException ex) {

                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    public void connectAllMarker() {
        if (markerOptionsList.size() > 1) {
            if (polygonStore != null) {
                polygonStore.remove();
            }

            // Declare polygon option
            PolygonOptions polygonOptions = new PolygonOptions();

            // Loop through the marker list to add polygon based on marker
            for (Marker marker : markerOptionsList) {
                polygonOptions.add(marker.getPosition());

                // Set fill color
                ColorDrawable buttonFillColor = (ColorDrawable) btn_colorPicker_fill.getBackground();
                String currentRole = ParseUser.getCurrentUser().getString("role_name");
                if(currentRole.equals("NVQL")) {
                    buttonFillColor.setAlpha(128);
                }
                if(currentRole.equals("NVQL_V")) {
                    buttonFillColor.setAlpha(64);
                }
                if(currentRole.equals("GDKD")) {
                    buttonFillColor.setAlpha(32);
                }
                polygonOptions.fillColor(buttonFillColor.getColor());

                // Set stroke color
                ColorDrawable buttonStrokeColor = (ColorDrawable) btn_colorPicker_stroke.getBackground();
                polygonOptions.strokeColor(buttonStrokeColor.getColor());

                polygonOptions.strokeWidth(4);

            }
            polygonStore = map.addPolygon(polygonOptions);
        } else {
            if (polygonStore != null) {
                polygonStore.remove();
            }
        }
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