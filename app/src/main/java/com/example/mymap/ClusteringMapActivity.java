package com.example.mymap;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

public class ClusteringMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ClusterManager<ClusterMarker> clusterManager;

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(50.061625, 19.937153), 11));

        setupCluster();
    }

    private void setupCluster() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<ClusterMarker>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        createMarkers();
    }

    private void createMarkers() {
        ClusterMarker[] markers = new ClusterMarker[]{
                new ClusterMarker(50, 20, "Kraków 1", ""),
                new ClusterMarker(50.055745, 19.892726, "Kraków 2", ""),
                new ClusterMarker(50.075787, 19.906205, "Kraków 3", ""),
                new ClusterMarker(50.091375, 19.939234, "Kraków 4", ""),
                new ClusterMarker(50.065849, 19.959365, "Kraków 5", ""),
                new ClusterMarker(50.267455, 19.025338, "Katowice", ""),
                new ClusterMarker(50.811246, 19.126550, "Częstochowa", ""),
                new ClusterMarker(51.751637, 19.446662, "Łódź 1", ""),
                new ClusterMarker(51.765472, 19.473447, "Łódź 2", ""),
                new ClusterMarker(52.136892, 21.017029, "Warszawa 1", ""),
                new ClusterMarker(52.301432, 21.030681, "Warszawa 2", ""),
                new ClusterMarker(52.254955, 20.953518, "Warszawa 3", ""),
        };

        for (ClusterMarker marker : markers) {
            clusterManager.addItem(marker);
        }

    }

    public class ClusterMarker implements ClusterItem {
        private final LatLng position;
        private final String title;
        private final String snippet;

        public ClusterMarker(double lat, double lng, String title, String snippet) {
            position = new LatLng(lat, lng);
            this.title = title;
            this.snippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSnippet() {
            return snippet;
        }
    }

}
