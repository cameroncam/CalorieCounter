package com.example.cameron.caloriecounter;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Statistics extends AppCompatActivity {
    private int tdee;
    private ArrayList<Pair> data;
    private Calendar currentViewingWeek;
    private Calendar currentWeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        data = new ArrayList<Pair>();
        currentViewingWeek = Calendar.getInstance();
        currentViewingWeek.add(Calendar.DAY_OF_WEEK, -(currentViewingWeek.get(Calendar.DAY_OF_WEEK) - 1));
        currentWeek = Calendar.getInstance();
        currentWeek.set(Calendar.DATE, currentViewingWeek.get(Calendar.DATE));
        setSupportActionBar(toolbar);
        getUserInfo();
        initComponents();
    }
    private void getUserInfo() {
        // Get preferences if already entered
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        tdee = userInfo.getInt("tdee", -1);
        File file = new File(getFilesDir() + "foodData");
        ArrayList<String> userData = new ArrayList<String>();
        if(file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(getFilesDir() + "foodData")));
                String line;
                while ((line = br.readLine()) != null) {
                    userData.add(line);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(userData.isEmpty()) {
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy");
        for(int i = 0; i < userData.size(); ++i) {
            String day = userData.get(i).substring(5,userData.get(i).length());

            try {
                Date date = format.parse(day);
                double sum = 0;
                for(int j = i+2; j < userData.size(); j+=2) {
                    if(userData.get(j).contains("Day")) {
                        break;
                    }
                    sum += Double.parseDouble(userData.get(j));
                    i = j;
                }
                data.add(new Pair(date, sum));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void initComponents() {
        Date date = currentViewingWeek.getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Calendar saturday = Calendar.getInstance();
        if(saturday.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY); {
            saturday.add(Calendar.DAY_OF_YEAR, (Calendar.SATURDAY - saturday.get(Calendar.DAY_OF_WEEK)) % 7);
        }
        Date sat = saturday.getTime();
        String weekPeriod = format.format(date) + " - " + format.format(sat);
        TextView weekText = (TextView) findViewById(R.id.textView14);
        weekText.setText(weekPeriod);
        createGraph();
        createGoalText();
    }

    private void createGoalText() {
        TextView goalText = (TextView) findViewById(R.id.textView15);
        if(data.size() == 0) {
            goalText.setVisibility(View.INVISIBLE);
            return;
        }
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        int tdee = userInfo.getInt("tdee", 0);
        int sum = 0;
        int count = 0;
        for(Pair p: data) {
            sum+= p.calories;
            count++;
        }
        int average = sum/count;
        if(average < tdee) {
            goalText.setText("Your average calories per day is: " + average + "\nwhich is below your goal of " + tdee +  "\n\nGood job!");
        }
        if(average == tdee) {
            goalText.setText("Your average calories per day is: " + average + "\nwhich is equal to your goal of " + tdee +  "\n\nGood job!");
        }
        if(average > tdee) {
            goalText.setText("Your average calories per day is: " + average + "\nwhich is above your goal of " + tdee +  "\n\nGet it together.");
        }
    }

    public void rightClick(View view) {
        Calendar calendar = (Calendar) currentViewingWeek.clone();
        calendar.add(Calendar.DATE, 7);
        Date prev = calendar.getTime();
        Calendar calendar2 = (Calendar) calendar.clone();
        calendar2.add(Calendar.DATE, 6);
        Date next = calendar2.getTime();
        currentViewingWeek = calendar;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String weekPeriod = format.format(prev) + " - " + format.format(next);
        TextView weekText = (TextView) findViewById(R.id.textView14);
        weekText.setText(weekPeriod);
        createGraph();
        if(currentViewingWeek.get(Calendar.DAY_OF_YEAR) == currentWeek.get(Calendar.DAY_OF_YEAR) &&
                currentViewingWeek.get(Calendar.YEAR) == currentWeek.get(Calendar.YEAR)) {
            Button rightClick = (Button) view;
            rightClick.setEnabled(false);
            rightClick.setVisibility(View.INVISIBLE);
        }
    }
    public void leftClick(View view) {
        Calendar calendar = (Calendar) currentViewingWeek.clone();
        calendar.add(Calendar.DATE, -7);
        Date prev = calendar.getTime();
        Calendar calendar2 = (Calendar) currentViewingWeek.clone();
        calendar2.add(Calendar.DATE, -1);
        Date next = calendar2.getTime();
        currentViewingWeek = calendar;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String weekPeriod = format.format(prev) + " - " + format.format(next);
        TextView weekText = (TextView) findViewById(R.id.textView14);
        weekText.setText(weekPeriod);
        createGraph();
        Button rightClick = (Button) findViewById(R.id.button8);
        rightClick.setEnabled(true);
        rightClick.setVisibility(View.VISIBLE);
    }
    private void createGraph() {
        int color = Color.parseColor("#9ac2e5");
        ArrayList<Bar> bars = new ArrayList<Bar>();
        Bar sun = new Bar();
        sun.setColor(color);
        sun.setName("Sun");
        sun.setValue(0);
        bars.add(sun);

        Bar mon = new Bar();
        mon.setColor(color);
        mon.setName("Mon");
        mon.setValue(0);
        bars.add(mon);

        Bar tues = new Bar();
        tues.setColor(color);
        tues.setName("Tues");
        tues.setValue(0);
        bars.add(tues);

        Bar wed = new Bar();
        wed.setColor(color);
        wed.setName("Wed");
        wed.setValue(0);
        bars.add(wed);

        Bar thurs = new Bar();
        thurs.setColor(color);
        thurs.setName("Thurs");
        thurs.setValue(0);
        bars.add(thurs);

        Bar fri = new Bar();
        fri.setColor(color);
        fri.setName("Fri");
        fri.setValue(0);
        bars.add(fri);

        Bar sat = new Bar();
        sat.setColor(color);
        sat.setName("Sat");
        sat.setValue(0);
        bars.add(sat);

        for(Pair p: data) {
            if(inThisWeek(p.day)) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(p.day);
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                switch(day) {
                    case Calendar.SUNDAY:
                        sun.setValue((float)p.calories);
                        break;
                    case Calendar.MONDAY:
                        mon.setValue((float)p.calories);
                        break;
                    case Calendar.TUESDAY:
                        tues.setValue((float) p.calories);
                        break;
                    case Calendar.WEDNESDAY:
                        wed.setValue((float) p.calories);
                        break;
                    case Calendar.THURSDAY:
                        thurs.setValue((float) p.calories);
                        break;
                    case Calendar.FRIDAY:
                        fri.setValue((float) p.calories);
                        break;
                    case Calendar.SATURDAY:
                        sat.setValue((float) p.calories);
                        break;
                }
            }
        }
        BarGraph g = (BarGraph)findViewById(R.id.graph);
        g.setBars(bars);
        g.setUnit("");
    }
    private boolean inThisWeek(Date date) {
        int week = currentViewingWeek.get(Calendar.WEEK_OF_YEAR);
        int year = currentViewingWeek.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }
    public void inputFood(View view) {
        finish();
    }
}
