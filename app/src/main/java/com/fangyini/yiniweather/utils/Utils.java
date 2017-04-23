package com.fangyini.yiniweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import com.fangyini.yiniweather.R;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {


    public static String getStrIcon(Context context, String iconId) {
        String icon;
        switch (iconId) {
            case "01d":
                icon = context.getString(R.string.icon_clear_sky_day);
                break;
            case "01n":
                icon = context.getString(R.string.icon_clear_sky_night);
                break;
            case "02d":
                icon = context.getString(R.string.icon_few_clouds_day);
                break;
            case "02n":
                icon = context.getString(R.string.icon_few_clouds_night);
                break;
            case "03d":
                icon = context.getString(R.string.icon_scattered_clouds);
                break;
            case "03n":
                icon = context.getString(R.string.icon_scattered_clouds);
                break;
            case "04d":
                icon = context.getString(R.string.icon_broken_clouds);
                break;
            case "04n":
                icon = context.getString(R.string.icon_broken_clouds);
                break;
            case "09d":
                icon = context.getString(R.string.icon_shower_rain);
                break;
            case "09n":
                icon = context.getString(R.string.icon_shower_rain);
                break;
            case "10d":
                icon = context.getString(R.string.icon_rain_day);
                break;
            case "10n":
                icon = context.getString(R.string.icon_rain_night);
                break;
            case "11d":
                icon = context.getString(R.string.icon_thunderstorm);
                break;
            case "11n":
                icon = context.getString(R.string.icon_thunderstorm);
                break;
            case "13d":
                icon = context.getString(R.string.icon_snow);
                break;
            case "13n":
                icon = context.getString(R.string.icon_snow);
                break;
            case "50d":
                icon = context.getString(R.string.icon_mist);
                break;
            case "50n":
                icon = context.getString(R.string.icon_mist);
                break;
            default:
                icon = context.getString(R.string.icon_weather_default);
        }

        return icon;
    }

    public static String getTemperatureUnit(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(
                Constants.KEY_PREF_TEMPERATURE, "metric");
    }
    public static String getSpeedScale(Context context) {
        String unitPref = getTemperatureUnit(context);
        return unitPref.equals("metric") ?
                context.getString(R.string.wind_speed_meters) :
              context.getString(R.string.wind_speed_miles);
    }

    public static String unixTimeToFormatTime(Context context, long unixTime) {
        long unixTimeToMillis = unixTime * 1000;
        return DateFormat.getTimeFormat(context).format(unixTimeToMillis);
    }

    public static URL getWeatherForecastUrl(String endpoint, String lat, String lon) throws
                                                                                         MalformedURLException {
        String url = Uri.parse(endpoint)
                        .buildUpon()
                        .appendQueryParameter("appid", "4503154adfe31641e669231ae64b0e14")
                        .appendQueryParameter("lat", lat)
                        .appendQueryParameter("lon", lon)
                        .build()
                        .toString();
        return new URL(url);
    }

    private static String getCityAndCountryFromGeolocation(SharedPreferences preferences) {
        String geoCountryName = preferences.getString(Constants.APP_SETTINGS_GEO_COUNTRY_NAME, "United Kingdom");
        String geoCity = preferences.getString(Constants.APP_SETTINGS_GEO_CITY, "London");
        if("".equals(geoCity)) {
            return geoCountryName;
        }
        String geoDistrictOfCity = preferences.getString(Constants.APP_SETTINGS_GEO_DISTRICT_OF_CITY, "");
        if ("".equals(geoDistrictOfCity) || geoCity.equalsIgnoreCase(geoDistrictOfCity)) {
            return geoCity + ", " + geoCountryName;
        }
        return geoCity + " - " + geoDistrictOfCity + ", " + geoCountryName;
    }
    public static String getCityAndCountry(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.APP_SETTINGS_NAME, 0);

            return getCityAndCountryFromGeolocation(preferences);
    }
}
