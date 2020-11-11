package com.example.mymap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MarkerMapActivity extends FragmentActivity implements OnMapReadyCallback, OnMyLocationButtonClickListener,
        OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerDragListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        createMarkers();
        createShapes();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                moveToCurrentPosition();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, String.format("Current location\nLatitude: %s\nLongitude: %s", location.getLatitude(),
                location.getLongitude()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng = marker.getPosition();
        Toast.makeText(this, String.format("Dragged marker location\nLatitude: %s\nLongitude: %s", latLng.latitude,
                latLng.longitude), Toast.LENGTH_LONG).show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            Toast.makeText(this, "No location permission granted", Toast.LENGTH_SHORT).show();
            permissionDenied = true;
        }
    }

    private void moveToCurrentPosition() {
        try {
            if (!permissionDenied) {
                FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 11));
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void createMarkers() {
        MarkerOptions[] markers = new MarkerOptions[]{
                new MarkerOptions().position(new LatLng(50, 20)).title("Marker nr 1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)),
                new MarkerOptions().position(new LatLng(50.055745, 19.892726)).title("Marker nr 2, draggable").draggable(true),
                new MarkerOptions().position(new LatLng(50.075787, 19.906205)).title("Marker nr 3").alpha(0.5f),
                new MarkerOptions().position(new LatLng(50.091375, 19.939234)).title("Marker nr 4").flat(true),
                new MarkerOptions().position(new LatLng(50.065849, 19.959365)).title("Marker nr 5, rotated").rotation(180.0f),
                new MarkerOptions().position(new LatLng(50.024384, 19.910597)).title("Marker nr 5, with extra info").snippet("extra info"),
        };

        for (MarkerOptions marker : markers) {
            this.mMap.addMarker(marker);
        }

        mMap.setOnMarkerDragListener(this);
    }

    private void createShapes() {
        mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(52.199611, 20.835813),
                        new LatLng(52.302322, 21.046410),
                        new LatLng(52.229550, 21.124648),
                        new LatLng(52.139891, 21.014490))
                .strokeColor(Color.BLUE)
                .fillColor(0x15FF0000));

        mMap.addPolyline(new PolylineOptions().add(
                new LatLng(50.175747, 19.987861),
                new LatLng(50.219867, 20.031006),
                new LatLng(50.235812, 20.080532),
                new LatLng(50.252589, 20.087759),
                new LatLng(50.291865, 20.045465),
                new LatLng(50.296471, 20.033181),
                new LatLng(50.310546, 20.039975))
                .color(Color.GREEN));
    }

}
