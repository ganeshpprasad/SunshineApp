package com.example.ganesh.sunshineudacity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.ganesh.sunshineudacity.data.WeatherContract.LocationEntry;
import com.example.ganesh.sunshineudacity.data.WeatherContract.WeatherEntry;
import com.example.ganesh.sunshineudacity.data.WeatherdbHelper;

import java.util.Map;
import java.util.Set;


/**
 * Created by Ganesh Prasad on 12-06-2016.
 */
public class TestProvider extends AndroidTestCase {

    public void testDeleteDb() throws Throwable {

        mContext.deleteDatabase(WeatherdbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherdbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public static String TEST_CITY_NAME = "North Pole";

    static public ContentValues getContentValues() {

        ContentValues values = new ContentValues();

        String testLocationSetting = "99705";
        double testLatitude = 64.755;
        double testLongitude = -147.355;

        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);

        return values;

    }

    static public ContentValues getWeatherValues(long rowId) {

        ContentValues weatherValues = new ContentValues();

        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY , rowId);
        weatherValues.put(WeatherEntry.COLUMN_DATE , "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES , 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY , 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE , 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP , 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP , 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED , 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID , 135);

        return weatherValues;

    }

    public static void validateCursor( ContentValues values , Cursor resultCursor ) {

        Set<Map.Entry<String , Object>> valueSet = values.valueSet();

        for( Map.Entry<String , Object> entries : valueSet ) {

            String columnName = entries.getKey();
            int index = resultCursor.getColumnIndex(columnName);
            assertFalse( -1 == index );
            String expectedValue = entries.getValue().toString();

            assertEquals( expectedValue , resultCursor.getString(index) );

        }

    }

    long rowId;

    public void testInsertDb() throws Throwable {

        WeatherdbHelper weatherdbHelper = new WeatherdbHelper(mContext);
        SQLiteDatabase db = weatherdbHelper.getWritableDatabase();

        ContentValues contentValues = getContentValues();

        rowId = db.insert(LocationEntry.TABLE_NAME , null , contentValues);

        assertTrue( rowId != -1 );
        Log.d("INSERT" , " " + rowId);

        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME ,
                null ,
                null ,
                null ,
                null ,
                null ,
                null
        );

        if( cursor.moveToFirst() ) {
            validateCursor(contentValues, cursor);

            ContentValues weatherValues = getWeatherValues(rowId);

            long weatherRowId;
            weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

            assertTrue(weatherRowId != -1);
            Log.d("TEST_INSERT", " " + weatherRowId);

            Cursor weatherCursor = db.query(
                    WeatherEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);
            } else {
                Log.d("INSERT", "Something wrong");
            }

        }

    }


}
