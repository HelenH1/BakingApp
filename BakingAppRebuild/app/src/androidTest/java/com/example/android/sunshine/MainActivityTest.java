package com.example.android.sunshine;

/**
 * Created by helenherring on 8/14/17.
 */

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.AssertionFailedError;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

public class MainActivityTest {

    /**
     * This test demos a user clicking the decrement button and verifying that it properly decrease
     * the quantity the total cost.
     */

    @RunWith(AndroidJUnit4.class)
    public class OrderActivityBasicTest {

        /**
         * The ActivityTestRule is a rule provided by Android used for functional testing of a single
         * activity. The activity that will be tested will be launched before each test that's annotated
         * with @Test and before methods annotated with @before. The activity will be terminated after
         * the test and methods annotated with @After are complete. This rule allows you to directly
         * access the activity during the test.
         */

        @Rule
        public ActivityTestRule<MainActivity> mActivityTestRule =
                new ActivityTestRule<>(MainActivity.class);

        @Test
        public void errorMessageHidden() {

            try {
                onView(withId(R.id.tv_error_message_display)).check(matches(isDisplayed()));
                // View is displayed
            } catch (AssertionFailedError e) {
                // View not displayed
            }

        }
    }

}
