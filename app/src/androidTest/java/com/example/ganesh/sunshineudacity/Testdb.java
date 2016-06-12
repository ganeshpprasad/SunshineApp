package com.example.ganesh.sunshineudacity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.ganesh.sunshineudacity.data.WeatherdbHelper;


/**
 * Created by Ganesh Prasad on 12-06-2016.
 */
public class Testdb extends AndroidTestCase {

    public void testCreateDb() throws Throwable {

        mContext.deleteDatabase(WeatherdbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherdbHelper(this.mContext).getWritableDatabase();
        assertEquals ( true , db.isOpen());
        db.close();
    }

    public ContentValues getLocationValues(){

        String

    }

}
