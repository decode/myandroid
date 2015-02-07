package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by home on 2/5/15.
 */
public class GeoActivity extends Activity {

    private LocationManager locationManager;
    private TextView text_latitude, text_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);

        processView();
    }

    private void processView() {
        text_latitude = (TextView) findViewById(R.id.txt_latitude);
        text_longitude = (TextView) findViewById(R.id.txt_longitude);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void updateView(Location location) {
        if (location != null) {
            text_latitude.setText(String.valueOf(location.getLatitude()));
            text_longitude.setText(String.valueOf(location.getLongitude()));
        } else {
            text_latitude.setText("");
            text_longitude.setText("");
        }

    }

    public void buttonClick(View view) {
        int id = view.getId();
        Intent result = getIntent();
        switch (id) {
            case R.id.btn_geo_cancel:
                finish();
                break;
            case R.id.btn_geo_ok:
                // put information back

                result.putExtra("geo_latitude", text_latitude.getText());
                result.putExtra("geo_longitude", text_longitude.getText());
                setResult(Activity.RESULT_OK, result);
                finish();
                break;
            case R.id.btn_geo_reset:
                result.putExtra("geo_latitude", "");
                result.putExtra("geo_longitude", "");
                setResult(Activity.RESULT_OK, result);
                finish();
                break;
            case R.id.btn_geo_get:
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateView(location);
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        updateView(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        updateView(locationManager.getLastKnownLocation(provider));
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        updateView(null);
                    }
                };
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                break;
        }
    }
}
