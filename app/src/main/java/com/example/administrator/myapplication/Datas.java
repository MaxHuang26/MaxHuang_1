package com.example.administrator.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Datas implements Serializable {

    private List<Double> valuelist1 = new ArrayList<>();
    private List<Double> valuelist2 = new ArrayList<>();
    private List<Double> valuelist3 = new ArrayList<>();
    private List<Double> valuelist4 = new ArrayList<>();
    private List<Double> valuelist5 = new ArrayList<>();
    private List<Double> valuelist6 = new ArrayList<>();

    public List<Double> getValuelist(int i) {
        switch (i) {
            case 1:
                return valuelist1;
            case 2:
                return valuelist2;
            case 3:
                return valuelist3;
            case 4:
                return valuelist4;
            case 5:
                return valuelist5;
            case 6:
                return valuelist6;
            default:
                return null;
        }
    }
    public void addvalue(int i,Double value){
        switch (i) {
            case 1:

                valuelist1.add(value);
                break;
            case 2:
                valuelist2.add(value);
                break;
            case 3:
                valuelist3.add(value);
                break;
            case 4:
                valuelist4.add(value);
                break;
            case 5:
                valuelist5.add(value);
                break;
            case 6:
                valuelist6.add(value);
                break;
            default:
                break;
        }
    }
}
