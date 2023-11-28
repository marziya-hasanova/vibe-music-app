package com.example.vibe

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vibe.presentation.ui.activities.SettingsActivity
import com.example.vibe.utils.THEME_PREFERENCES
import com.example.vibe.utils.THEME_PREFERENCES_KEY
import junit.framework.TestCase.assertTrue
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SettingsActivity::class.java)

    @Test
    fun testActivityLaunch() {
        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()))
    }

    @Test
    fun testActionBarTitle() {
        onView(withText(R.string.settings))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testThemeSwitch() {

        onView(withId(R.id.theme_switch))
            .perform(click())

        val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences: SharedPreferences = targetContext.getSharedPreferences(
            THEME_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val isDarkMode = sharedPreferences.getBoolean(THEME_PREFERENCES_KEY, false)
        Assert.assertTrue(isDarkMode)
    }

    @Test
    fun testBackButtonFinishesActivity() {

        val scenario = ActivityScenario.launch(SettingsActivity::class.java)

        onView(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description)).perform(click())

        scenario.onActivity { activity ->
            assertTrue(activity.isFinishing)
        }
    }

}