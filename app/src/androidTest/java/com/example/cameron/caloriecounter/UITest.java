package com.example.cameron.caloriecounter;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;


/**
 * Created by Cameron on 4/11/2016.
 */
public class UITest extends ApplicationTestCase<Application> {
    public UITest() {
        super(Application.class);
    }
    @Test
    public static boolean initTest() {
        MainActivity main = new MainActivity();
        boolean result = main.initialize();
        return result;
    }
}
