package com.example.cameron.caloriecounter;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.junit.Test;
/**
 * Created by Cameron on 4/16/2016.
 */
public class TDEECalcTest extends ApplicationTestCase<Application> {
    public TDEECalcTest() {
        super(Application.class);
    }
    @Test
    public static boolean TDEETest() {
        MainActivity main = new MainActivity();
        boolean result = main.calculateTDEE(5,8,140,2,20,0,1) == 2567;
        String pass = result ? "Success" : "Failure";
        Log.d("TDEE Test: ", pass);
        return result;
    }
}
