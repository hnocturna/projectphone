package com.android.projectphone;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hnocturna on 5/11/2015.
 */
public class UserNumReq {
    // Class contains all the specs and information required to find phones with matching requirements within the database
    double min;
    double max;
    String category;
    String spec;
    String unit;                        // Holds the unit to display to the user. Not used for parsing database.
    String resolutionChoice;            // Holds the resolution choice to display to the user. Not used for parsing the database.
    List<String> choiceList = null;
    List<String> resolutions = null;
    Map<String, Object> linkReqMap = null;

    public UserNumReq(String category, String spec, double min, double max, String unit) {
        // Constructor for specs that have a min and max value
        this.category = category;
        this.spec = spec;
        this.min = min;
        this.max = max;
        this.unit = unit;

    }

    public UserNumReq(String category, String spec, double min, double max, String unit, Map<String, Object> linkMap, String type) {
        // Constructor for specs that have a min and max value
        this.category = category;
        this.spec = spec;
        this.min = min;
        this.max = max;
        this.unit = unit;
        if (type.equals("cat")) {
            this.linkReqMap = linkMap;
        } else if (type.equals("num")) {
            this.linkReqMap = linkMap;
        }
    }

    public UserNumReq(String category, String spec, List<Integer> integerList, String unit) {
        // Constructor for specs that only have a minimum value
        this.category = category;
        this.spec = spec;
        this.choiceList = new LinkedList<>();
        for (int integer : integerList) {
            // Convert Integer choices to string for simplified parsing through the database
            this.choiceList.add(Integer.toString(integer));
        }
        this.unit = unit;

    }

    public UserNumReq(String category, String spec, List<Integer> integerList, String unit, Map<String, Object> linkMap, String type) {
        // Constructor for specs that only have a minimum value
        this.category = category;
        this.spec = spec;
        this.choiceList = new LinkedList<>();
        for (int integer : integerList) {
            // Convert Integer choices to string for simplified parsing through the database
            this.choiceList.add(Integer.toString(integer));
        }
        this.unit = unit;
        if (type.equals("cat")) {
            this.linkReqMap = linkMap;
        } else if (type.equals("num")) {
            this.linkReqMap = linkMap;
        }

    }

    public UserNumReq(String category, String spec, List<String> choiceList) {
        this.category = category;
        this.spec = spec;
        this.choiceList = new LinkedList<>(choiceList);
    }

    public UserNumReq(String category, String spec, String resolutionChoice, List<String> resolutions) {
        // Constructor for the resolution spec because nonstandard resolutions are rounded to the nearest standard resolution.
        this.category = category;
        this.spec = spec;
        this.resolutionChoice = resolutionChoice;
        this.resolutions = resolutions;
    }

    public UserNumReq(String category, String spec, String resolutionChoice, List<String> resolutions, Map<String, Object> linkMap, String type) {
        // Constructor for the resolution spec because nonstandard resolutions are rounded to the nearest standard resolution.
        this.category = category;
        this.spec = spec;
        this.resolutionChoice = resolutionChoice;
        this.resolutions = resolutions;
        if (type.equals("cat")) {
            this.linkReqMap = linkMap;
        } else if (type.equals("num")) {
            this.linkReqMap = linkMap;
        }
    }

    public void linkNumReq(UserNumReq userNumReq) {
        if (this.linkReqMap == null) {
            this.linkReqMap = new LinkedHashMap<>();
        }
        String mapTitle = userNumReq.getCategory() + userNumReq.getSpec();
        this.linkReqMap.put(mapTitle, userNumReq);
    }

    public void linkCatReq(UserCatReq userCatReq) {
        if (this.linkReqMap == null) {
            this.linkReqMap = new LinkedHashMap<>();
        }
        String mapTitle = userCatReq.getCategory() + userCatReq.getSpec();
        this.linkReqMap.put(mapTitle, userCatReq);
    }

    public Map<String, Object> getLinkReqMap() {
        return linkReqMap;
    }

    public String getReqType() {
        return "num";
    }

    public String getCategory() {
        return category;
    }

    public String getSpec() {
        return spec;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public String getUnit() {
        return unit;
    }

    public List<String> getChoiceList() {
        return choiceList;
    }

    public String getResolutionChoice() {
        return resolutionChoice;
    }

    public List<String> getResolutions() {
        return resolutions;
    }
}
