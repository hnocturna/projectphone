package com.android.projectphone;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hnoct on 7/18/2015.
 */
public class UserReq {

    double min;
    double max;

    int specGroup;
    int colorGroup;

    String type;
    String category;
    String spec;
    String operator;                                    // Used to select whether the phone must contain ALL specs or ANY of the specs.
    String unit;                                        // Holds the unit to display to the user. Not used for parsing database.
    String resolutionChoice;                            // Holds the resolution choice to display to the user. Not used for parsing the database.

    List choice = null;                                 // List of all selections made by the user for one particular spec (e.g. Display > Technology can select IPS & OLED with the "or" operator).
    List<String> resolutions = null;                    // List of all resolutions of the user's selection and higher.

    Map<String, List> carrierMap = null;
    Map<String, UserReq> linkReqMap = null;             // Used to link this requirement with other requirements so that a phone can have either-or to meet the requirements.

    public UserReq(String category, String spec, List choice, String operator) {
        // Only one constructor required because all categorical specs are string values.
        this.category = category;
        this.spec = spec;
        this.choice = choice;
        if (operator.equals("and") || operator.equals("or")) {
            this.operator = operator;
            this.type = "cat";
        } else {
            this.unit = operator;
            this.type = "num";
        }

    }

    public UserReq(String category, String spec, Map<String, List> carrierMap) {
        // Used hold the supported frequencies of the carrier selected.
        this.category = category;
        this.spec = spec;
        this.carrierMap = new LinkedHashMap<>(carrierMap);
        this.type = "cat";
        this.operator = "and";
    }

    public UserReq(String category, String spec, double min, double max, String unit) {
        // Constructor for specs that have a min and max value
        this.category = category;
        this.spec = spec;
        this.min = min;
        this.max = max;
        this.unit = unit;
        this.type = "num";
    }

    public UserReq(String category, String spec, String resolutionChoice, List<String> resolutions) {
        // Constructor for the resolution spec because nonstandard resolutions are rounded to the nearest standard resolution.
        this.category = category;
        this.spec = spec;
        this.resolutionChoice = resolutionChoice;
        this.resolutions = resolutions;
        this.type = "num";
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getSpec() {
        return spec;
    }

    public String getOperator() {
        return operator;
    }

    public String getUnit() {
        return unit;
    }

    public String getResolutionChoice() {
        return resolutionChoice;
    }

    public List getChoice() {
        return choice;
    }

    public List<String> getResolutions() {
        return resolutions;
    }

    public void setSpecGroup(int specGroup) {
        this.specGroup = specGroup;
        Log.d("UserReq", "Spec group for " + spec + ": " + specGroup);
        if (SpecsActivity.specReqMap.get(specGroup) != null) {
            if (SpecsActivity.specReqMap.get(specGroup).size() > 1 && !SpecsActivity.specColorGroupMap.keySet().contains(specGroup)) {
                SpecsActivity.specColorGroupMap.put(specGroup, SpecsActivity.availableColorGroups.get(0));
                SpecsActivity.availableColorGroups.remove(0);
                this.colorGroup = SpecsActivity.specColorGroupMap.get(this.specGroup);
                Log.d("UserReq", "Color group set for " + spec + ": " + SpecsActivity.specColorGroupMap.get(specGroup));
            } else if (SpecsActivity.specReqMap.get(specGroup).size() > 1 && SpecsActivity.specColorGroupMap.keySet().contains(specGroup)) {
                this.colorGroup = SpecsActivity.specColorGroupMap.get(this.specGroup);
            } else if (SpecsActivity.specReqMap.get(specGroup).size() <= 1 && SpecsActivity.specColorGroupMap.keySet().contains(specGroup)) {
                SpecsActivity.availableColorGroups.add(SpecsActivity.specColorGroupMap.get(specGroup));
                SpecsActivity.specColorGroupMap.remove(specGroup);
                this.colorGroup = 6;
            } else {
                this.colorGroup = 6;
            }
        } else {
            this.colorGroup = 6;
        }



    }

    public int getSpecGroup() {
        return specGroup;
    }

    public void setColorGroup(int colorGroup) {

        this.colorGroup = colorGroup;
    }

    public int getColorGroup() {
        return colorGroup;
    }
}
