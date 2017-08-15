package com.example.android.sunshine;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {
    ContentValues ingredValues;

    String text;
    Context mContext;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        mContext = context;

        new FetchWeatherTask().execute();


    }

    public class FetchWeatherTask extends AsyncTask<String, Void, ContentValues[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //System.out.println("On Pre Execute");
            //System.out.println();
        }

        @Override
        protected ContentValues[] doInBackground(String... params) {
            String sortBy;

    /* There will always be some sort method assigned.*/
            sortBy = params[0];

            URL dataRequestUrl = NetworkUtils.getUrlForMainActivity();
            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(dataRequestUrl);
                //System.out.println("Got HTTP Response");

                ContentValues[] jsonMovieData = OpenWeatherJsonUtils
                        .getFullMovieInfoFromJson(jsonMovieResponse, "1");
                System.out.println("Returning JSON MOVIE DATA");

                return jsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ContentValues[] jsonMovieData) {

            if (jsonMovieData != null) {
                ingredValues = jsonMovieData[1];

                int count = ingredValues.getAsInteger("count");

                for (int i=0; i<count; i++) {

                    String quantity = ingredValues.getAsString("quantity"+i);
                    String measure = ingredValues.getAsString("measure"+i);
                    String ingred = ingredValues.getAsString("ingredient"+i);

                    text.concat(quantity + " " + measure + " " + ingred + "%n");

                }

                Timber.d("Setting basic movie info in ForecastAdapter");
                RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget);
                views.setTextViewText(R.id.appwidget_text, text);
                //System.out.println();
            } else {
                Timber.d("Weather data null. Showing error message. No movie data assigned to ForecastAdapter.");
                //System.out.println();

            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

