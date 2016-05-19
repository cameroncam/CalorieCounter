package com.example.cameron.caloriecounter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class InputFood extends AppCompatActivity {
    ArrayList<HashMap<String, String>> feedList;
    ListView items;
    SimpleAdapter simpleAdapter;
    Date currentDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_food);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        feedList = new ArrayList<HashMap<String, String>>();
        simpleAdapter = new SimpleAdapter(this, feedList, R.layout.view_item, new String[]{"foodName", "calories"}, new int[]{R.id.foodName, R.id.calories});
        items = (ListView) findViewById(R.id.listView1);
        items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, final int position, long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                feedList.remove(position);
                                simpleAdapter.notifyDataSetChanged();
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(InputFood.this);
                builder.setMessage("Remove " + feedList.get(position).get("foodName") + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            }
        });
        currentDay = new Date();
        setGoalText();
    }
    private void setGoalText() {
        TextView goalText = (TextView) findViewById(R.id.textView16);
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        int tdee = userInfo.getInt("tdee", 0);
        goalText.setText("Your goal calories per day is: " + tdee);
    }
    // Check to see if day has changed
    protected void onResume() {
        super.onResume();
        Date day = new Date();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(this.currentDay);
        cal2.setTime(day);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        if(!sameDay) {
            feedList.clear();
            simpleAdapter.notifyDataSetChanged();
            currentDay = day;
        }
    }

    public void viewStats(View view) {
        Intent statistics = new Intent(this, Statistics.class);
        startActivityForResult(statistics, 0);
    }
    public void search(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        String food = ((EditText) findViewById(R.id.editText2)).getText().toString();
        if(food.isEmpty()) {
            return;
        }
        EditText calories = (EditText) findViewById(R.id.editText4);
        AlertDialog foodNotFound = new AlertDialog.Builder(InputFood.this).create();
        foodNotFound.setTitle("Error");
        foodNotFound.setMessage("Food not found :(");
        foodNotFound.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        FoodSearch search = new FoodSearch(food, calories, foodNotFound);
        search.execute();
    }
    public void addItem(View view) {
        if(((EditText)findViewById(R.id.editText2)).getText().toString().isEmpty() || ((EditText)findViewById(R.id.editText4)).getText().toString().isEmpty()) {
            AlertDialog dialog = new AlertDialog.Builder(InputFood.this).create();
            dialog.setTitle("Error");
            dialog.setMessage("Please enter the food name and number of calories.");
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        String foodName = ((EditText)findViewById(R.id.editText2)).getText().toString();
        String calories = ((EditText)findViewById(R.id.editText4)).getText().toString();
        map.put("foodName", foodName);
        map.put("calories", calories);
        feedList.add(map);
        ListView items = (ListView) findViewById(R.id.listView1);
        items.setAdapter(simpleAdapter);
        ((EditText)findViewById(R.id.editText2)).setText("");
        ((EditText)findViewById(R.id.editText4)).setText("");
    }
    private boolean fileExists() {
        return new File(getFilesDir() + "foodData.xml").exists();
    }

    public void saveData(View view) {
        if(feedList.isEmpty()) {
            AlertDialog dialog = new AlertDialog.Builder(InputFood.this).create();
            dialog.setTitle("Error");
            dialog.setMessage("Enter some data to save.");
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
            return;
        }
        String write = "";
        if(fileExists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(getFilesDir() + "foodData")));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                write = text + "\n";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(getFilesDir() + "foodData"));
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy");
                outputStream.write((write + "Day " + format.format(date) + "\n").getBytes());
                for(HashMap<String, String> map: feedList) {
                    outputStream.write((map.get("foodName") + "\n").getBytes());
                    outputStream.write((map.get("calories") + "\n").getBytes());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getApplicationContext(), "Data saved.", Toast.LENGTH_SHORT).show();
    }
}
