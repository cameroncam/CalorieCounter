package com.example.cameron.caloriecounter;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Cameron on 4/16/2016.
 */
public class FoodSearch extends AsyncTask<Void, Void, String> {
    String food;
    EditText calories;
    AlertDialog foodNotFound;

    FoodSearch(String food, EditText calories, AlertDialog foodNotFound) {
        this.food = food;
        this.calories = calories;
        this.foodNotFound = foodNotFound;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            food = food.replaceAll(" ", "%20");
            URL url = new URL("http://api.nal.usda.gov/ndb/search/?format=JSON&q=" + food +
                    "&max=1&offset=0&sort=r&api_key=xMJV33vSmKsquFqcBwZ23oJ7DlL2abmfsrDUUx1l");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String result = stringBuilder.toString();
                if(result.contains("zero results")) {
                    String s = "empty";
                    return s;
                }
                JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
                JSONObject list = object.getJSONObject("list");
                JSONArray items = list.getJSONArray("item");
                String item = items.get(0).toString();
                int i = item.indexOf("ndbno\":\"") + 8;
                int f = item.indexOf("\"", i);
                String ndbno = item.substring(i,f);
                Log.d("DEBUG", ndbno);

                URL url2 = new URL("http://api.nal.usda.gov/ndb/reports/?ndbno=" + ndbno +
                        "&type=b&format=JSON&api_key=xMJV33vSmKsquFqcBwZ23oJ7DlL2abmfsrDUUx1l");
                HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(urlConnection2.getInputStream()));
                StringBuilder stringBuilder2 = new StringBuilder();
                String line2;
                while ((line2 = bufferedReader2.readLine()) != null) {
                    stringBuilder2.append(line2).append("\n");
                }
                bufferedReader2.close();
                String res = stringBuilder2.toString();
                int index = res.indexOf("\"unit\": \"kcal\",") + 46;
                int index2 = res.indexOf("\"", index);
                String calories = res.substring(index,index2);
                urlConnection2.disconnect();
                return calories;
            }
            finally{
                urlConnection.disconnect();
            }

        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            String s = "empty";
            return s;
        }
    }
    protected void onPostExecute(String response) {
        if(!response.isEmpty() && !response.equals("empty")) {
            calories.setText(response);
        } else {
            foodNotFound.show();
            return;
        }
    }
}
