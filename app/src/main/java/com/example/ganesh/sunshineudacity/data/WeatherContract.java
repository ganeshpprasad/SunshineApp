package com.example.ganesh.sunshineudacity.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ganesh Prasad on 06-06-2016.
 */
public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.example.ganesh.sunshineudacity.app";

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_LOC_KEY = "location_id";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_WEATHER_ID = "weather_id";

        public static final String COLUMN_SHORT_DESC = "short_desc";

        public static final String COLUMN_MIN_TEMP = "min";

        public static final String COLUMN_MAX_TEMP = "max";

        public static final String COLUMN_HUMIDITY = "humidity";

        public static final String COLUMN_PRESSURE = "pressure";

        public static final String COLUMN_WIND_SPEED = "wind_speed";

        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildWeatherUri( long id ){
            return ContentUris.withAppendedId(CONTENT_URI , id);
        }

        public static Uri buildWeatherLocation( String locationSetting ) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate( String locationSetting ,String startDate ) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendQueryParameter(COLUMN_DATE , startDate).build();
        }

        public static Uri buildWeatherLocationWithDate ( String locationSetting , String date ) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {
            if ( uri.getPathSegments().get(2) != null ) {
                return uri.getPathSegments().get(2);
            } else {
                return null;
            }
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATE);
        }

    }

    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        public static final String COLUMN_CITY_NAME = "city_name";

        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri( long _id ){
            return ContentUris.withAppendedId(CONTENT_URI , _id);
        }

    }

    static String DATE_FORMAT = "ddMMyyyy";

    public static String getdbDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static Date getDateFromDb(String date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

}
