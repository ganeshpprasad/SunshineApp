package com.example.ganesh.sunshineudacity;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ganesh.sunshineudacity.data.WeatherContract;

import org.w3c.dom.Text;

/**
 * Created by Ganesh Prasad on 16-06-2016.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int TYPE_TODAY = 0;
    private static final int TYPE_FUTURE_DAY = 1;
    public static final String LOG_TAG = "Forecast Adapter";

    public ForecastAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return ( position == 0 ) ? TYPE_TODAY : TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int view = getItemViewType(cursor.getPosition());
        int layoutId;

        if (view == 0) {
            layoutId = R.layout.fragment_item_today;
        } else {
            layoutId = R.layout.fragment_item;
        }

        return LayoutInflater.from(context).inflate(layoutId , parent , false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int viewType = getItemViewType( cursor.getPosition() );

        TextView date = (TextView) view.findViewById(R.id.list_item_date_tv);
        TextView forecast = (TextView) view.findViewById(R.id.list_item_forecast_tv);
        TextView high = (TextView) view.findViewById(R.id.list_item_high_tv);
        TextView low = (TextView) view.findViewById(R.id.list_item_low_tv);
        ImageView imageView = (ImageView) view.findViewById(R.id.weather_icon_iv);

        switch (viewType) {

            case TYPE_TODAY : imageView.setImageResource( Utility.getArtResourceForWeatherCondition(
                    cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID))
            ) );
                break;
            case TYPE_FUTURE_DAY : imageView.setImageResource(Utility.getIconResourceForWeatherCondition(
                    cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID))
            ));

        }

        Long date_str = cursor.getLong(MainActivityFragment.COL_WEATHER_DATE);

        String forecast_str = cursor.getString(MainActivityFragment.COL_WEATHER_DESC);
        Double max_str = cursor.getDouble(MainActivityFragment.COL_WEATHER_MAX_TEMP);
        Double min_str = cursor.getDouble(MainActivityFragment.COL_WEATHER_MIN_TEMP);

        boolean isMetric = Utility.isMetric(context);

        String date_format = Utility.getFriendlyDayString(context,date_str);


        date.setText(date_format);
        forecast.setText(forecast_str);
        high.setText(Utility.formatTemperature(context , max_str , isMetric));
        low.setText(Utility.formatTemperature(context , min_str , isMetric));

    }
}
