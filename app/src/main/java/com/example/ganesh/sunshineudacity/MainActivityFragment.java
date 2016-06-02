package com.example.ganesh.sunshineudacity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivityFragment extends Fragment {

    String JSONforecast;
    BufferedReader reader;
    HttpURLConnection urlConnection = null;

    String format = "json";
    String units = "metric";

    FetchForecastTask fetchForecastTask;

    public void updateWeather(){

        fetchForecastTask = new FetchForecastTask();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String location = prefs.getString( "location" , "London" );

        fetchForecastTask.execute(location);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    int numOfDays = 7;

    public static final String ERROR_TAG = MainActivity.class.getSimpleName();

    private ArrayAdapter<String> adapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.refresh) {
            updateWeather();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1 ,
                new ArrayList<String>());

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, item);
                startActivity(intent);
            }
        });

        return rootView;

    }

    public class FetchForecastTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {

            String forecastResult = establishConnection(params[0]);

            Log.v(ERROR_TAG, forecastResult);

            try {
                return getWeatherDataFromJSON(forecastResult);
            } catch (JSONException j) {
                Log.v(ERROR_TAG, " Error in get weather from json ");

                j.printStackTrace();
            }

            return null;
        }

        private String[] getWeatherDataFromJSON(String forecastResult) throws JSONException {

            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DATETIME = "dt";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastResult);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            String[] daysArray = new String[numOfDays];

            for (int i = 0; i < weatherArray.length(); i++) {

                String day;
                String description;
                String highAndLow;

                JSONObject dayForecast = weatherArray.getJSONObject(i);

                long dateTime = dayForecast.getLong(OWM_DATETIME);
                day = getReadableDateString(dateTime);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                JSONObject tempObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = tempObject.getDouble(OWM_MAX);
                double low = tempObject.getDouble(OWM_MIN);

                highAndLow = formatHighAndLow(high, low);
                daysArray[i] = day + "-" + description + "-" + highAndLow;

            }

            for (String s : daysArray) {
                Log.v(ERROR_TAG, s);
            }

            return daysArray;

        }

        private String formatHighAndLow(double high, double low) {
            long h = Math.round(high);
            long l = Math.round(low);

            String highLowStr = h + "/" + l;
            return highLowStr;
        }

        private String establishConnection(String city) {

            try {

                final String baseURL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=json&units=metric&cnt=7&APPID=fde6e9891517ea4889424fe0a4b6c8ee");

//                Uri builtUri = Uri.parse(baseURL).buildUpon()
//                        .appendQueryParameter(QUERY_PARAM ,city )
//                        .appendQueryParameter(FORMAT_PARAM ,format )
//                        .appendQueryParameter(UNITS_PARAM , units)
//                        .appendQueryParameter(DAYS_PARAM , Integer.toString(numOfDays)).build();

//                URL url = new URL(builtUri.toString());

//                Log.v(ERROR_TAG , builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    return null;
                }

                JSONforecast = buffer.toString();

                return JSONforecast;

            } catch (Exception e) {
                Log.e(ERROR_TAG, " JSON main exception ");
            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(ERROR_TAG, " JSON exception ");
                    }
                }

            }

            return null;

        }

        private String getReadableDateString(long time) {

            Date date = new Date(time * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM d");
            return dateFormat.format(date);

        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                for (String f : result) {
                    adapter.add(f);
                }

            }
        }
    }

}
