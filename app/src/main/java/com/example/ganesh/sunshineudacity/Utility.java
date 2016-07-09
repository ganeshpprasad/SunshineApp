package com.example.ganesh.sunshineudacity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.example.ganesh.sunshineudacity.data.WeatherContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {

    private static final String LOG_TAG = "Utility";

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_general_location_key),
                context.getString(R.string.pref_default_edit_location));
    }

    public static String formatTemperature(Context context, double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return context.getString(R.string.format_temperature, temp);
    }

    static String formatDate(String dateString) {
        Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }

    /**
     * Returns true if metric unit should be used, or false if
     * imperial units should be used.
     */
    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric)).equals(
                context.getString(R.string.pref_units_metric));
    }

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return a user-friendly representation of the date.
     */
    public static String getFriendlyDayString(Context context, long dateInMillis) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

//        Date todayDate = new Date();
//        String todayStr = WeatherContract.getdbDateString(todayDate);
//        Date inputDate = WeatherContract.getDateFromDb(dateStr);
//        Log.d(LOG_TAG , inputDate.toString());
//
//        // If the date we're building the String for is today's date, the format
//        // is "Today, June 24"
//        if (todayStr.equals(dateStr)) {
//            String today = context.getString(R.string.today);
//            return context.getString(
//                    R.string.format_full_friendly_date,
//                    today,
//                    getFormattedMonthDay(context, dateStr));
//        } else {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(todayDate);
//            cal.add(Calendar.DATE, 7);
//            String weekFutureString = WeatherContract.getdbDateString(cal.getTime());
//
//            if (dateStr.compareTo(weekFutureString) < 0) {
//                // If the input date is less than a week in the future, just return the day name.
//                return getDayName(context, dateStr);
//            } else {
//                // Otherwise, use the form "Mon Jun 3"
//                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//                return shortenedDateFormat.format(inputDate);
//            }
//        }

        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(context, dateInMillis)));
        } else if ( julianDay < currentJulianDay + 7 ) {
            // If the input date is less than a week in the future, just return the day name.
            return getDayName(context, dateInMillis);
        } else {
            // Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(dateInMillis);
        }


    }


//    public static String getDayName(Context context, String dateStr) {
//        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
//        try {
//            Date inputDate = dbDateFormat.parse(dateStr);
//            Date todayDate = new Date();
//            // If the date is today, return the localized version of "Today" instead of the actual
//            // day name.
//            if (WeatherContract.getdbDateString(todayDate).equals(dateStr)) {
//                return context.getString(R.string.today);
//            } else {
//                // If the date is set for tomorrow, the format is "Tomorrow".
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(todayDate);
//                cal.add(Calendar.DATE, 1);
//                Date tomorrowDate = cal.getTime();
//                if (WeatherContract.getdbDateString(tomorrowDate).equals(
//                        dateStr)) {
//                    return context.getString(R.string.tomorrow);
//                } else {
//                    // Otherwise, the format is just the day of the week (e.g "Wednesday".
//                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
//                    return dayFormat.format(inputDate);
//                }
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//            // It couldn't process the date correctly.
//            return "";
//        }
//    }

//    public static String getFormattedMonthDay(Context context, String dateStr) {
//        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
//        try {
//            Date inputDate = dbDateFormat.parse(dateStr);
//            SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
//            String monthDayString = monthDayFormat.format(inputDate);
//            return monthDayString;
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    public static String getFormattedMonthDay(Context context, long dateInMillis ) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }

    public static int getIconResourceForWeatherCondition( int weatherId ) {
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }

}