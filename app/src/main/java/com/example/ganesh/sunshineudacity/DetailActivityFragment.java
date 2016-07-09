package com.example.ganesh.sunshineudacity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.example.ganesh.sunshineudacity.data.WeatherContract;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;

    //DATE_KEY allows to decide the date we'll be querying for
    public static final String DATE_KEY = "date";
    public static final String LOCATION_KEY = "location";

    private String mLocation;
    private String mForecastStr;
    private ShareActionProvider mShareActionProvider;
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    //columns to query in cursor
    String[] COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID ,
            WeatherContract.WeatherEntry.COLUMN_DATE ,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC ,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP ,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP ,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY ,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE ,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED ,
            WeatherContract.WeatherEntry.COLUMN_DEGREES ,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    public DetailActivityFragment() {
    }

    private String mDateStr;

    TextView description_tv;
    TextView date_tv ;
    TextView max_tv ;
    TextView min_tv ;
    ImageView weather_iv;

    TextView humidity_tv ;
    TextView wind_speed_tv ;
    TextView direction_tv ;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail , menu);
        MenuItem shareItem = menu.findItem(R.id.share_detail);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (mForecastStr != null) {
            mShareActionProvider.setShareIntent(createshareForecastIntent());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Bundle arguments = getArguments();
//        if (arguments != null) {
//            Log.v("Detail" , "not null");
//            mDateStr = arguments.getString(DetailActivityFragment.DATE_KEY);
//        }

        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        mDateStr = intent.getStringExtra(DATE_KEY);

        description_tv = (TextView) rootView.findViewById(R.id.detail_forecast_tv);
        date_tv = (TextView) rootView.findViewById(R.id.detail_date_tv);
        max_tv = (TextView) rootView.findViewById(R.id.detail_high_tv);
        min_tv = (TextView) rootView.findViewById(R.id.detail_low_tv);
        weather_iv = (ImageView) rootView.findViewById(R.id.weather_icon_detail_iv);

        humidity_tv = (TextView) rootView.findViewById(R.id.detail_humidity_tv);
        wind_speed_tv = (TextView) rootView.findViewById(R.id.detail_wind_speed_tv);
        direction_tv = (TextView) rootView.findViewById(R.id.detail_wind_direction_tv);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER , null , this);
    }

    private Intent createshareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT , mForecastStr + FORECAST_SHARE_HASHTAG );
        return shareIntent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LOCATION_KEY , mLocation);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mLocation && !mLocation.equals(Utility.getPreferredLocation(getActivity()))){
            getLoaderManager().restartLoader(DETAIL_LOADER , null , this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatheruri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate( mLocation , mDateStr );

        return new CursorLoader(
                getActivity() ,
                weatheruri ,
                COLUMNS ,
                null ,
                null ,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        double low = 0;
        double high = 0;
        String date = null;
        String description = null;

        Double humidity = 0d;
        String wind_speed = null;
        Double wind_direction = 0d;

        int weatherId = 0;

        if (data.moveToFirst()) {
            description = data.getString(data.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
            date = data.getString(data.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_DATE));
            high = data.getDouble(data.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
            low = data.getDouble(data.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
            weatherId = data.getInt(data.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));

            humidity = data.getDouble(data.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
            wind_speed = data.getString(data.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
            wind_direction = data.getDouble(data.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_DEGREES));
        }

        boolean isMetric = Utility.isMetric(getActivity());

        Log.d("Detail" , " " + humidity);
        Log.d("Detail" , " " + wind_direction);
        Log.d("Detail" , " " + high);
        Log.d("Detail" , " " + low);

        description_tv.setText(description);
        date_tv.setText(Utility.formatDate(date));
        max_tv.setText(Utility.formatTemperature(getActivity() ,high  ,isMetric));
        min_tv.setText(Utility.formatTemperature(getActivity() , low , isMetric));

        weather_iv.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        humidity_tv.setText(Double.toString(humidity));
        wind_speed_tv.setText(wind_speed);
        direction_tv.setText(Double.toString(wind_direction));

        mForecastStr = String.format("%s - %s , %s / %s" , date_tv.getText() , description_tv.getText() ,
                max_tv.getText() , min_tv.getText());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
