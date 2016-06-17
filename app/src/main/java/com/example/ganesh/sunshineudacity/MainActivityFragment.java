package com.example.ganesh.sunshineudacity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.ganesh.sunshineudacity.data.WeatherContract;
import com.example.ganesh.sunshineudacity.data.WeatherContract.WeatherEntry;
import com.example.ganesh.sunshineudacity.data.WeatherContract.LocationEntry;

import java.util.Date;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mForecastAdapter;
    private static final int FORECAST_LOADER = 0;
    private String mLocation;
    private ListView mListView;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
//        updateWeather();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if( mLocation != null && !Utility.getPreferredLocation(getActivity()).equals(mLocation) ){
            getLoaderManager().restartLoader(FORECAST_LOADER ,null , this );
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.refresh) {
            updateWeather();
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity( new Intent(getActivity() , SettingsActivity.class) );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mForecastAdapter = new SimpleCursorAdapter(
                getActivity() ,
                R.layout.fragment_item ,
                null ,
                new String[] {
                        WeatherEntry.COLUMN_DATE ,
                        WeatherEntry.COLUMN_SHORT_DESC ,
                        WeatherEntry.COLUMN_MAX_TEMP ,
                        WeatherEntry.COLUMN_MIN_TEMP
                } ,
                new int[] {
                        R.id.list_item_date_tv,
                        R.id.list_item_forecast_tv ,
                        R.id.list_item_high_tv ,
                        R.id.list_item_low_tv
                } ,
                0
        );

        mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (columnIndex) {
                    case COL_WEATHER_MAX_TEMP:
                    case COL_WEATHER_MIN_TEMP:{
                        boolean isMetric = Utility.isMetric(getActivity());
                        ((TextView) view).setText(Utility.formatTemperature(getActivity() ,
                                cursor.getDouble(columnIndex) , isMetric));
                        return true;
                    }
                    case COL_WEATHER_DATE: {
                        String date = cursor.getString(columnIndex);
                        TextView dateView = (TextView) view;
                        dateView.setText(Utility.formatDate(date));
                        return true;
                    }
                }
                return false;
            }
        });

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(mForecastAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity() , DetailActivity.class);
                SimpleCursorAdapter adapter = (SimpleCursorAdapter) parent.getAdapter();
                Cursor cursor = adapter.getCursor();
                if( cursor != null && cursor.moveToPosition(position) ) {
                    Boolean isMetric = Utility.isMetric(getActivity());
                    String forecast = String.format("%s - %s , %s / %s",
                            Utility.formatDate(cursor.getString(COL_WEATHER_DATE)) ,
                            cursor.getString(COL_WEATHER_DESC) ,
                            Utility.formatTemperature(getActivity() , cursor.getDouble(COL_WEATHER_MAX_TEMP) , isMetric) ,
                            Utility.formatTemperature(getActivity() , cursor.getDouble(COL_WEATHER_MIN_TEMP) , isMetric)
                    );

                    intent.putExtra(Intent.EXTRA_TEXT , forecast);
                    startActivity(intent);

                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather() {
        FetchForecastTask fetchForecastTask = new FetchForecastTask(getActivity());
        mLocation = Utility.getPreferredLocation(getActivity());
        fetchForecastTask.execute(mLocation);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getdbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Log.v("MAIN" , mLocation);
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }
}