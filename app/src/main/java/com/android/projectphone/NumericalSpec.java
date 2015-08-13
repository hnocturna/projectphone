package com.android.projectphone;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hnocturna on 5/11/2015.
 */
public class NumericalSpec {
    // Used to hold the values from the database to display to the user while selecting the requirements of the specification.
    String category;
    String spec;
    Integer min;
    Integer max;
    Integer magnitude;
    String unit;
    List<String> choiceList = new LinkedList<String>();
    Map<Integer, Integer> choiceMap = new LinkedHashMap<>();
    Map<String, List<String>> resolutions = new LinkedHashMap<>();

    public NumericalSpec(String category, String spec, Integer min, Integer max, Integer magnitude, String unit) {
        // For specs that can have a minimum and maximum value.
        this.category = category;
        this.spec = spec;
        this.min = min;
        this.max = max;
        this.magnitude = magnitude;
        this.unit = unit;
    }
    public NumericalSpec(String category, String spec, HashMap<Integer, Integer> choiceMap, String unit) {
        // For specs with a clear ranking. <- Ranking is determined by numerical value and stored in the getMinMax() method from MySQLiteHelper class.
        this.category = category;
        this.spec = spec;
        this.choiceMap = choiceMap;
        this.unit = unit;
    }

    public NumericalSpec(String category, String spec, HashMap<String, List<String>> resolutions) {
        // Special constructor only for resolutions due to non-standard values being rounded to the nearest standard resolution.
        this.category = category;
        this.spec = spec;
        this.resolutions = resolutions;
    }

    public NumericalSpec(String category, String spec, List<String> choiceList) {
        this.category = category;
        this.spec = spec;
        this.choiceList = choiceList;
    }


    public String getCategory() {
        return category;
    }

    public String getSpec() {
        return spec;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public Integer getMagnitude() {
        return magnitude;
    }

    public String getUnit() {
        return unit;
    }

    public Map<Integer, Integer> getChoiceMap() {
        return choiceMap;
    }

    public Map<String, List<String>> getResolutions() {
        return resolutions;
    }

    public List<String> getChoiceList() {
        return choiceList;
    }
}
