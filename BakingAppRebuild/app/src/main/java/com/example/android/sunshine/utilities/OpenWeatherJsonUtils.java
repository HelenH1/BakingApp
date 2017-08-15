/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.utilities;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class OpenWeatherJsonUtils {

    /* Location information */
    private static final String OWM_CITY = "city";
    private static final String OWM_COORD = "coord";

    private static final String OWM_MOVIE_ID = "movie_id";
    private static final String OWM_POSTER_PATH = "movie_poster_path";
    private static final String OWM_TITLE = "movie_title";
    private static final String OWM_SYNOPSIS = "movie_synopsis";
    private static final String OWM_RATING = "movie_rating";
    private static final String OWM_RELEASE_DATE = "movie_release_date";
    private static final String OWM_REVIEWS = "movie_reviews";
    private static final String OWM_TRAILERS = "movie_trailers";

    /* Location coordinate */
    private static final String OWM_LATITUDE = "lat";
    private static final String OWM_LONGITUDE = "lon";

    /* Weather information. Each day's forecast info is an element of the "list" array */
    private static final String OWM_LIST = "list";

    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    /* All temperatures are children of the "temp" object */
    private static final String OWM_TEMPERATURE = "temp";

    /* Max temperature for the day */
    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";

    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_ID = "id";

    private static final String OWM_MESSAGE_CODE = "cod";
    private static final int MAX_RECIPES = 4;

    public static ContentValues[] getFullMovieInfoFromJson(String mainJsonStr, String id)
            throws JSONException {

        /* String array to hold each day's weather String */
        ContentValues[] movieContentValues = new ContentValues[3];

        JSONArray forecastJson = new JSONArray(mainJsonStr);
        int idInt = Integer.parseInt(id);
        JSONObject recipe = forecastJson.getJSONObject(idInt);

        String recID = recipe.getString("id");
        String name = recipe.getString("name");
        String servings = recipe.getString("servings");
        String image = recipe.getString("image");


        //System.out.println("MovieContentValues and forecastJson now created.");
        /* Is there an error? */


        ContentValues basicInfo = new ContentValues();
        basicInfo.put("id", recID);
        basicInfo.put("name", name);
        basicInfo.put("servings", servings);
        basicInfo.put("image", image);



        JSONArray ingredients = recipe.getJSONArray("ingredients");

        int ingredCount = ingredients.length();
        ContentValues ingredValues = new ContentValues();
        ingredValues.put("count", ingredCount);

        for(int i = 0; i<ingredCount; i++) {
            JSONObject ingredient = ingredients.getJSONObject(i);
            String quantity = ingredient.getString("quantity");
            String measure = ingredient.getString("measure");
            String inged = ingredient.getString("ingredient");
            ingredValues.put("quantity"+i, quantity);
            ingredValues.put("measure"+i, measure);
            ingredValues.put("ingredient"+i, inged);
        }


        System.out.println("Is ingred values false?" +Boolean.toString(ingredValues == null));


        JSONArray instructions = recipe.getJSONArray("steps");
        int stepCount = instructions.length();
        ContentValues instructVals = new ContentValues();
        instructVals.put("count", stepCount);

        for(int i = 0; i<stepCount; i++) {
            JSONObject aStep = instructions.getJSONObject(i);
            String stepID = aStep.getString("id");
            String shortDescrip = aStep.getString("shortDescription");
            String descrip = aStep.getString("description");
            String vidurl = aStep.getString("videoURL");
            String thumburl = aStep.getString("thumbnailURL");

            instructVals.put("id"+i, stepID);
            instructVals.put("shortDescrip"+i, shortDescrip);
            instructVals.put("description"+i, descrip);
            instructVals.put("videoURL"+i, vidurl);
            instructVals.put("thumbnailURL"+i, thumburl);
        }
        movieContentValues[0] = basicInfo;
        Timber.d("zzzz Is basic info null? "+Boolean.toString(basicInfo == null));
        movieContentValues[1] = ingredValues;
        Timber.d("zzzz Is ingred values null? "+Boolean.toString(ingredValues == null));

        movieContentValues[2] = instructVals;
        Timber.d("zzzz Is instruct vals null? "+Boolean.toString(instructVals == null));



        return movieContentValues;
    }


    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getBasicMovieInfoFromJson(String forecastJsonStr)
            throws JSONException {

        //System.out.println("Getting basic movie info from JSON");
        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String OWM_LIST = "results";

        final String OWM_MESSAGE_CODE = "cod";

        /* String array to hold each day's weather String */
        ContentValues[] movieContentValues = new ContentValues[MAX_RECIPES];

        JSONArray forecastJson = new JSONArray(forecastJsonStr);
        //System.out.println("MovieContentValues and forecastJson now created.");
        /* Is there an error? */


        for (int i = 0; i < movieContentValues.length; i++) {
            ContentValues movieValues = new ContentValues();

            /* Get the JSON object representing the day */
            JSONObject dayForecast = forecastJson.getJSONObject(i);

            movieValues.put(OWM_MOVIE_ID, dayForecast.getString("id"));
            String recipeName = dayForecast.getString("name");
            String servings = dayForecast.getString("servings");
            String id = Integer.toString(i);

            Timber.d("zzz Recipe name is "+recipeName);
            movieValues.put("name", recipeName);
            Timber.d("zzz Servings: "+servings);
            movieValues.put("servings", servings);
            movieValues.put("id", id);

            movieContentValues[i] = movieValues;
        }

        return movieContentValues;
    }
}