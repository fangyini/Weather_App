package com.fangyini.yiniweather;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fangyini.yiniweather.R;
import com.fangyini.yiniweather.adapter.WeatherForecastAdapter;
import com.fangyini.yiniweather.model.WeatherForecast;
import com.fangyini.yiniweather.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.fangyini.yiniweather.utils.Utils.getWeatherForecastUrl;

public class WeatherForecastActivity extends BaseActivity {

    private final String TAG = "WeatherForecastActivity";

    private List<WeatherForecast> mWeatherForecastList;
    private RecyclerView mRecyclerView;
    private static Handler mHandler;
    private ProgressDialog mGetWeatherProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        mWeatherForecastList = new ArrayList<>();
        mGetWeatherProgress = getProgressDialog();

        mRecyclerView = (RecyclerView) findViewById(R.id.forecast_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateUI();
        getWeather();

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case Constants.TASK_RESULT_ERROR:
                        Toast.makeText(WeatherForecastActivity.this,
                                       R.string.toast_parse_error,
                                       Toast.LENGTH_SHORT).show();
                        setVisibleUpdating(false);
                        break;
                    case Constants.PARSE_RESULT_ERROR:
                        Toast.makeText(WeatherForecastActivity.this,
                                       R.string.toast_parse_error,
                                       Toast.LENGTH_SHORT).show();
                        setVisibleUpdating(false);
                        break;
                    case Constants.PARSE_RESULT_SUCCESS:
                        setVisibleUpdating(false);
                        updateUI();
                        break;
                }
            }
        };
    }

    private void updateUI() {
        ImageView android = (ImageView) findViewById(R.id.android);
        if (mWeatherForecastList.size() < 5) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            android.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            android.setVisibility(View.GONE);
        }
        WeatherForecastAdapter adapter = new WeatherForecastAdapter(this,
                                                                    mWeatherForecastList,
                                                                    getSupportFragmentManager());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setVisibleUpdating(boolean visible) {
        if (visible) {
            mGetWeatherProgress.show();
        } else {
            mGetWeatherProgress.cancel();
        }
    }

    private void getWeather() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences(Constants.APP_SETTINGS_NAME, 0);
                String latitude = pref.getString(Constants.APP_SETTINGS_LATITUDE, "51.51");
                String longitude = pref.getString(Constants.APP_SETTINGS_LONGITUDE, "-0.13");

                String requestResult = "";
                HttpURLConnection connection = null;
                try {
                    URL url = getWeatherForecastUrl(Constants.WEATHER_FORECAST_ENDPOINT, latitude, longitude);
                    connection = (HttpURLConnection) url.openConnection();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                        InputStream inputStream = connection.getInputStream();

                        int bytesRead;
                        byte[] buffer = new byte[1024];
                        while ((bytesRead = inputStream.read(buffer)) > 0) {
                            byteArray.write(buffer, 0, bytesRead);
                        }
                        byteArray.close();
                        requestResult = byteArray.toString();
                    }
                } catch (IOException e) {
                    mHandler.sendEmptyMessage(Constants.TASK_RESULT_ERROR);
                    Log.e(TAG, "IOException: " + requestResult);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                parseWeatherForecast(requestResult);
            }
        });
        t.start();
    }

    private void parseWeatherForecast(String data) {
        try {
            if (!mWeatherForecastList.isEmpty()) {
                mWeatherForecastList.clear();
            }

            JSONObject jsonObject = new JSONObject(data);
            JSONArray listArray = jsonObject.getJSONArray("list");

            int listArrayCount = listArray.length();
            for (int i = 0; i < listArrayCount; i++) {
                WeatherForecast weatherForecast = new WeatherForecast();
                JSONObject resultObject = listArray.getJSONObject(i);
                weatherForecast.setDateTime(resultObject.getLong("dt"));
                JSONObject temperatureObject = resultObject.getJSONObject("temp");
                weatherForecast.setTemperatureMin(
                        Float.parseFloat(temperatureObject.getString("min")));
                weatherForecast.setTemperatureMax(
                        Float.parseFloat(temperatureObject.getString("max")));
                JSONArray weatherArray = resultObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                weatherForecast.setDescription(weatherObject.getString("description"));
                weatherForecast.setIcon(weatherObject.getString("icon"));

                mWeatherForecastList.add(weatherForecast);
                mHandler.sendEmptyMessage(Constants.PARSE_RESULT_SUCCESS);
            }
        } catch (JSONException e) {
            mHandler.sendEmptyMessage(Constants.TASK_RESULT_ERROR);
            e.printStackTrace();
        }
    }
}
