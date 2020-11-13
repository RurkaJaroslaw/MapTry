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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class DirectionsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerDragListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private GoogleMap googleMap;
    String apiKey = "PUT_YOUR_API_KEY_HERE";
    Polyline currentRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMyLocationButtonClickListener(this);
        this.googleMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        setupDestinationMarker();
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
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
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
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

    private void navigateToDestination(final LatLng destination) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        LatLng origin = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        sendRequestToDirectionsApi(origin, destination);
                    }
                }
            }
        });
    }

    private void sendRequestToDirectionsApi(LatLng origin, LatLng destination) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%.6f,%.6f&destination=%.6f,%.6f&key=%s", origin.latitude, origin.longitude, destination.latitude, destination.longitude, apiKey);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reader = new JSONObject(response);
                            Object points = reader.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").get("points");
                            addRoute((String) points);
                        } catch (JSONException e) {
                            System.err.println("Error: creating JSONObject");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.println(1, "HERE", "Request error");
            }
        });

        queue.add(stringRequest);
    }

    private void addRoute(String encodedPolyfill) {
        if (currentRoute != null) currentRoute.remove();

        List<LatLng> pointsDecoded = PolyUtil.decode(encodedPolyfill);
        currentRoute = googleMap.addPolyline(new PolylineOptions().addAll(pointsDecoded).color(Color.BLUE));
    }

    private void setupDestinationMarker() {
        this.googleMap.addMarker(new MarkerOptions().position(new LatLng(50.055745, 19.892726))
                .title("Marker nr 2, draggable").draggable(true));
        googleMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        navigateToDestination(marker.getPosition());
    }
}
