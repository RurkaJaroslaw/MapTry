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
    private GoogleMap googleMap;
    private ClusterManager<ClusterMarker> clusterManager;

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
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(50.061625, 19.937153), 11));

        setupCluster();
    }

    private void setupCluster() {
        clusterManager = new ClusterManager<ClusterMarker>(this, googleMap);

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

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
