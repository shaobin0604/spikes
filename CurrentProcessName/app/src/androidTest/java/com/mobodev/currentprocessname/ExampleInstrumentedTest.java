package com.mobodev.currentprocessname;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.TimingLogger;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.mobodev.currentprocessname", appContext.getPackageName());
    }

    @Test
    public void testGetCurrentProcessName1() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        final long start = SystemClock.elapsedRealtime();

        assertEquals(appContext.getPackageName(), ProcessUtils.getCurrentProcessName1(appContext));

        System.out.println("testGetCurrentProcessName1 cost: " + (SystemClock.elapsedRealtime() - start));
    }

    @Test
    public void testGetCurrentProcessName2() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        final long start = SystemClock.elapsedRealtime();

        assertEquals(appContext.getPackageName(), ProcessUtils.getCurrentProcessName2());

        System.out.println("testGetCurrentProcessName2 cost: " + (SystemClock.elapsedRealtime() - start));
    }
}
