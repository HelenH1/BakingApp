package com.example.android.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity implements RecipeStepAdapter.StepAdapterOnClickHandler,
        RecipeIngredientAdapter.IngredientAdapterOnClickHandler{

    String recipeId;
    ContentValues[] recipeValues;
    ContentValues basicValues;
    @BindView(R.id.recipe_instructions) RecyclerView stepRecyclerView;
    @BindView(R.id.recipe_ingredients) RecyclerView ingredRecyclerView;
    @BindView(R.id.tv_detail_title) TextView nameView;
    @BindView(R.id.tv_recipe_servings) TextView servingsView;
    ContentValues ingredValues;
    ContentValues instrucValues;
    RecipeStepAdapter stepAdapter;
    RecipeIngredientAdapter ingredAdapter;
    Context mContext;
    String mName;
    String mServings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);


        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                recipeId = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                mName = intentThatStartedThisActivity.getStringExtra("name");
                mServings = intentThatStartedThisActivity.getStringExtra("servings");
            }
        }
        Timber.d("ID is "+recipeId);

        nameView.setText(mName);
        servingsView.setText(getString(R.string.recipe_serves)+mServings);

        LinearLayoutManager stepLayoutManager =
                new LinearLayoutManager(this);

        stepRecyclerView.setLayoutManager(stepLayoutManager);

        LinearLayoutManager ingredLayoutManager =
                new LinearLayoutManager(this);

        ingredRecyclerView.setLayoutManager(ingredLayoutManager);
        mContext = this;

        stepAdapter = new RecipeStepAdapter(mContext, this);
        ingredAdapter = new RecipeIngredientAdapter(mContext, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        // mRecyclerView.setAdapter(mForecastAdapter);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        //getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);
        //System.out.println("OnCreate working");
        //System.out.println();

        new FetchWeatherTask().execute(recipeId);

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

            String id = params[0];

            URL dataRequestUrl = NetworkUtils.getUrlForMainActivity();
            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(dataRequestUrl);
                //System.out.println("Got HTTP Response");

                ContentValues[] jsonMovieData = OpenWeatherJsonUtils
                        .getFullMovieInfoFromJson(jsonMovieResponse, id);
                System.out.println("Returning JSON MOVIE DATA");

                return jsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ContentValues[] jsonMovieData) {
            stepRecyclerView.setAdapter(stepAdapter);
            ingredRecyclerView.setAdapter(ingredAdapter);

            stepRecyclerView.setVisibility(View.VISIBLE);
            ingredRecyclerView.setVisibility(View.VISIBLE);

            if (jsonMovieData != null) {
                recipeValues = jsonMovieData;
                setOtherData();


            } else {
                Timber.d("Weather data null. Showing error message. No movie data assigned to ForecastAdapter.");
                //System.out.println();
            }
        }
    }

    public void setOtherData() {

        Timber.d("Setting data");
        System.out.println("Setting data (println)");

        ingredValues = recipeValues[1];
        instrucValues = recipeValues[2];
        basicValues = recipeValues[0];

        System.out.println("Is instruc values false?" +instrucValues.getAsString("count"));

        System.out.println("Is ingred values false?" +ingredValues.getAsString("count"));

        stepAdapter.setBasicMovieInfo(instrucValues);
        ingredAdapter.setBasicMovieInfo(ingredValues);
        Timber.d("Setting basic movie info in ForecastAdapter");
        //System.out.println();

    }

    public void onClick(ContentValues movieKeyValues) {
        System.out.println("Ingredient clicked");
    }

    @Override
    public void onClick(int position, ContentValues movieKeyValues) {
        Context context = this;

        System.out.println("Step Recycler View Item clicked!");
        String vidURL = movieKeyValues.getAsString("vidURL"+position);
        //System.out.println("Movie key is: "+movieKey);


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

        if(vidURL.length()>0) { //won't launch exoplayer if this step doesn't have a video
            Bundle arguments = new Bundle();
            arguments.putString(RecipeDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(RecipeDetailFragment.ARG_ITEM_ID));
            RecipeDetailFragment fragment = new RecipeDetailFragment();
            arguments.putString("vidURL", vidURL);
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail, fragment)
                    .commit();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
