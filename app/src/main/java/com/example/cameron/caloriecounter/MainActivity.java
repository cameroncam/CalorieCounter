package com.example.cameron.caloriecounter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public boolean initialize() {
        // Get preferences if already entered
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        int tdee = userInfo.getInt("tdee", -1);
        if(tdee != -1) {
            exit();
        }

        Spinner feet = (Spinner) findViewById(R.id.height_feet);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.height_feet, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feet.setAdapter(adapter);

        Spinner inches = (Spinner) findViewById(R.id.height_in);
        ArrayAdapter<CharSequence> adapter_inch = ArrayAdapter.createFromResource(this,
                R.array.height_in, android.R.layout.simple_spinner_item);
        adapter_inch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inches.setAdapter(adapter_inch);

        Spinner activityLevel = (Spinner) findViewById(R.id.activity_level);
        ArrayAdapter<CharSequence> adapter_activity = ArrayAdapter.createFromResource(this,
                R.array.activity_level, android.R.layout.simple_spinner_item);
        adapter_activity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevel.setAdapter(adapter_activity);

        Spinner goal = (Spinner) findViewById(R.id.goal);
        ArrayAdapter<CharSequence> adapter_goal = ArrayAdapter.createFromResource(this,
                R.array.goal, android.R.layout.simple_spinner_item);
        adapter_goal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goal.setAdapter(adapter_goal);

        Spinner gender = (Spinner) findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter_gender);

        return true;
    }

    public void saveAction(View view) {
        if(((EditText) findViewById(R.id.editText)).getText().toString().isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please complete the information.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }

        int heightFt, heightIn, weight, activityLevel, goal, gender, age;
        // Get preferences
        heightFt = Integer.parseInt(((Spinner) findViewById(R.id.height_feet)).getSelectedItem().toString());
        heightIn = Integer.parseInt(((Spinner) findViewById(R.id.height_in)).getSelectedItem().toString());
        weight = Integer.parseInt(((EditText) findViewById(R.id.editText)).getText().toString());
        activityLevel = ((Spinner) findViewById(R.id.activity_level)).getSelectedItemPosition();
        goal = ((Spinner) findViewById(R.id.goal)).getSelectedItemPosition();
        gender = ((Spinner) findViewById(R.id.gender)).getSelectedItemPosition();
        age = Integer.parseInt(((EditText) findViewById(R.id.editText3)).getText().toString());
        int tdee = calculateTDEE(heightFt,heightIn,weight,activityLevel,age,gender, goal);

        // Save preferences in XML
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putInt("tdee", tdee);
        editor.commit();
        try {
            FileOutputStream fileos = new FileOutputStream ( new File(getFilesDir() + "userData.xml"));
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "TDEE");
            xmlSerializer.text(Integer.toString(tdee));
            xmlSerializer.endTag(null, "TDEE");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Exit activity
        exit();
    }
    public int calculateTDEE(int heightFt, int heightIn, int weight, int activityLevel, int age, int gender, int goal) {
        int height = heightFt * 12 + heightIn;
        double kg = weight * .453592;
        double m = height * .0254;
        double cm = height * 2.54;
        double bmr = (10 * kg) + (6.25 * cm) - (5 * age) + 5;
        if(gender == 1) {
            bmr = (10* kg) + (6.25 * cm) - (5*age) - 161;
        }
        double activity = 1.25;
        switch(activityLevel) {
            case 1:
                activity = 1.375;
                break;
            case 2:
                activity = 1.55;
                break;
            case 3:
                activity = 1.725;
                break;
            case 4:
                activity = 1.9;
                break;
        }
        double tdee = bmr * activity;
        switch(goal) {
            case 0:
                tdee -=500;
                break;
            case 2:
                tdee +=500;
                break;
        }
        tdee += .5;
        return (int) tdee;
    }

    private void exit() {
        // Launch input activity
        Intent inputData = new Intent(this, InputFood.class);
        startActivity(inputData);

        // Close this one
        finish();
    }
}
