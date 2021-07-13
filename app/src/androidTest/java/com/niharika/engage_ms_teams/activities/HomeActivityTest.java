package com.niharika.engage_ms_teams.activities;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.niharika.engage_ms_teams.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> homeActivityActivityTestRule=new ActivityTestRule<HomeActivity>(HomeActivity.class);
    private HomeActivity mActivity=null;
    @Before
    public void setUp() throws Exception {
        mActivity=homeActivityActivityTestRule.getActivity();
    }

    //Testing for dark mode
    @Test
    public void TestLaunch()
    {
        View view=mActivity.findViewById(R.id.home_bar_layout);
        assertNotNull(view);
    }

    @Test
    public void isUserAuthenticated()
    {

    }
    @After
    public void tearDown() throws Exception
    {
        mActivity=null;
    }
}