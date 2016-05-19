package com.example.cameron.caloriecounter;

public class BarStackSegment implements Cloneable {
    public float Value;
    public int Color;
    public BarStackSegment(int val, int color){
        Value = val;
        Color = color;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}