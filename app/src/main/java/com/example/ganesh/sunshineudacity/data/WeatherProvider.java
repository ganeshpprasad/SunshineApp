package com.example.ganesh.sunshineudacity.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Ganesh Prasad on 13-06-2016.
 */
public class WeatherProvider extends ContentProvider {

    public static final int WEATHER = 100;
    public static final int WEATHER_WITH_LOCATION = 101;
    public static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    public static final int LOCATION = 300;
    public static final int LOCATION_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public WeatherdbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sWeatherFromLocationQueryBuilder;

    static {
        sWeatherFromLocationQueryBuilder = new SQLiteQueryBuilder();
        sWeatherFromLocationQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME + "." +
                        WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " = " +
                        WeatherContract.LocationEntry.TABLE_NAME + "." +
                        WeatherContract.LocationEntry._ID);
    }

    private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME + "." +
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME + "." +
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";

    private static final String sLocationSettingWithDaySelection =
            WeatherContract.LocationEntry.TABLE_NAME + "." +
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.TABLE_NAME + "." +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";

    private Cursor getWeatherByLocationSettingWithDate ( Uri uri , String[] projection , String sortOrder ) {

        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String day = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return sWeatherFromLocationQueryBuilder.query( mOpenHelper.getReadableDatabase() ,
                projection ,
                sLocationSettingWithDaySelection ,
                new String[]{ locationSetting , day } ,
                null ,
                null ,
                sortOrder
                );
    }

    private Cursor getWeatherByLocationSetting( Uri uri , String[] projection ,String sortOrder ) {

        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String selection;
        String[] selectionArgs;

        if( startDate == null ) {

            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};

        } else {
            selectionArgs = new String[] { locationSetting , startDate };
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherFromLocationQueryBuilder.query( mOpenHelper.getReadableDatabase() ,
                projection ,
                selection ,
                selectionArgs ,
                null ,
                null ,
                sortOrder
                );
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority , WeatherContract.PATH_WEATHER , WEATHER);
        uriMatcher.addURI(authority , WeatherContract.PATH_WEATHER + "/*" , WEATHER_WITH_LOCATION);
        uriMatcher.addURI(authority , WeatherContract.PATH_WEATHER + "/*/*" , WEATHER_WITH_LOCATION_AND_DATE);

        uriMatcher.addURI(authority , WeatherContract.PATH_LOCATION , LOCATION);
        uriMatcher.addURI(authority , WeatherContract.PATH_LOCATION + "/#" , LOCATION_ID);

        return uriMatcher;

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherdbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {

            case WEATHER:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME ,
                        projection,
                        selection ,
                        selectionArgs ,
                        null ,
                        null ,
                        sortOrder);
                break;
            }

            case WEATHER_WITH_LOCATION:
                retCursor = getWeatherByLocationSettingWithDate(uri , projection ,sortOrder);
                break;

            case WEATHER_WITH_LOCATION_AND_DATE:
                retCursor = getWeatherByLocationSetting( uri , projection , sortOrder );
                break;

            case LOCATION:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME ,
                        projection,
                        selection ,
                        selectionArgs ,
                        null ,
                        null ,
                        sortOrder);
                break;
            }

            case LOCATION_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME ,
                        projection,
                        WeatherContract.LocationEntry._ID + "=" + ContentUris.parseId(uri),
                        null ,
                        null ,
                        null ,
                        sortOrder);
                break;
            }

            default:
                retCursor = null;
        }

        retCursor.setNotificationUri(getContext().getContentResolver() , uri);
        return retCursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
