package com.example.ganesh.sunshineudacity;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.ganesh.sunshineudacity.data.WeatherContract;

import org.w3c.dom.Text;

/**
 * Created by Ganesh Prasad on 16-06-2016.
 */
public class ForecastAdapter extends CursorAdapter {

    public ForecastAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v("ADAPTER" , "may be");
        return LayoutInflater.from(context).inflate(R.layout.fragment_item , parent , false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView date = (TextView) view.findViewById(R.id.list_item_date_tv);
        TextView forecast = (TextView) view.findViewById(R.id.list_item_forecast_tv);
        TextView high = (TextView) view.findViewById(R.id.list_item_high_tv);
        TextView low = (TextView) view.findViewById(R.id.list_item_low_tv);

        String date_str = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_DATE));
        String forecast_str = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
        String max_str = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
        String min_str = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));

        Log.v("ADAPTER" , date_str);
        Log.v("ADAPTER" , max_str);

        date.setText("Bullshit");
        forecast.setText(forecast_str);
        high.setText(max_str);
        low.setText(min_str);

    }
}
