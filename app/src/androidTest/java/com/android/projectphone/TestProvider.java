package com.android.projectphone;

import android.test.AndroidTestCase;
import android.util.Log;

import com.android.projectphone.data.PhoneProvider;

/**
 * Created by hnoct on 10/20/2015.
 */
public class TestProvider extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public String testQueryOutput() {
        Log.v("test", PhoneProvider.testString());
        int i = 1 + 2;
        return PhoneProvider.testString();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
