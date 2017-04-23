package com.fangyini.yiniweather.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.fangyini.yiniweather.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUpdateService extends Service implements LocationListener {

    private static final String TAG = "LocationUpdateService";

    private static final long LOCATION_TIMEOUT_IN_MS = 30000l;

    private LocationManager locationManager;

    private long lastLocationUpdateTime;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocationUpdateTime = System.currentTimeMillis();
        String latitude = String.format("%1$.2f", location.getLatitude());
        String longitude = String.format("%1$.2f", location.getLongitude());
        SharedPreferences mSharedPreferences = getSharedPreferences(Constants.APP_SETTINGS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.APP_SETTINGS_LATITUDE, latitude);
        editor.putString(Constants.APP_SETTINGS_LONGITUDE, longitude);
        getAndWriteAddressFromGeocoder(latitude, longitude, editor);
        editor.apply();

        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        locationManager.removeUpdates(this);
    }

    private void getAndWriteAddressFromGeocoder(String latitude, String longitude, SharedPreferences.Editor editor) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            String latitudeEn = latitude.replace(",", ".");
            String longitudeEn = longitude.replace(",", ".");
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitudeEn), Double.parseDouble(longitudeEn), 1);
            if ((addresses != null) && (addresses.size() > 0)) {
                editor.putString(Constants.APP_SETTINGS_GEO_CITY, addresses.get(0).getLocality());
                editor.putString(Constants.APP_SETTINGS_GEO_DISTRICT_OF_CITY, addresses.get(0).getSubLocality());
                editor.putString(Constants.APP_SETTINGS_GEO_COUNTRY_NAME, addresses.get(0).getCountryName());
            }
        } catch (IOException | NumberFormatException ex) {
            Log.e(TAG, "Unable to get address from latitude and longitude", ex);
        }
    }
}
