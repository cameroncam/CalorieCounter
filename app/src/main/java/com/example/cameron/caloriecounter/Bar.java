package com.example.cameron.caloriecounter;

import android.graphics.Path;
import android.graphics.Region;

import java.util.ArrayList;

public class Bar {

    private int color;
    private String name;
    private float value;
    private Path path;
    private Region region;
    private boolean isStackedBar;
    private ArrayList<BarStackSegment> values = new ArrayList<BarStackSegment>();

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public float getValue() {
        return value;
    }
    public void setValue(float value) {
        this.value = value;
    }
    public Path getPath() {
        return path;
    }
    public void setPath(Path path) {
        this.path = path;
    }
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    public void setStackedBar(boolean stacked){
        isStackedBar = stacked;
    }
    public boolean getStackedBar(){
        return isStackedBar;
    }
    public void AddStackValue(BarStackSegment val){
        values.add(val);
    }
    public ArrayList<BarStackSegment> getStackedValues(){
        return values;
    }

}