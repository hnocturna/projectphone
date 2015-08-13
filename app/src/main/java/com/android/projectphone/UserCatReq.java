package com.android.projectphone;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hnocturna on 5/11/2015.
 */
public class UserCatReq {
    String category;
    String spec;
    String operator;
    List<String> choice = new ArrayList<String>();

    Map<String, List> carrierMap = null;
    Map<String, Object> linkReqMap = null;              // Used to link this requirement with other categorical requirements so that a phone can have either-or to meet the requirements.

    public UserCatReq(String category, String spec, List<String> choice, String operator) {
        // Only one constructor required because all categorical specs are string values.
        this.category = category;
        this.spec = spec;
        this.choice = choice;
        if (operator.equals("and") || operator.equals("or")) {
            this.operator = operator;
        }
    }

    public UserCatReq(String category, String spec, Map<String, List> carrierMap) {
        // Used hold the supported frequencies of the carrier selected.
        this.category = category;
        this.spec = spec;
        this.carrierMap = new LinkedHashMap<>(carrierMap);

    }

    public void linkNumReq(UserNumReq userNumReq) {
        String mapTitle = userNumReq.getCategory() + userNumReq.getSpec();
        this.linkReqMap.put(mapTitle, userNumReq);
    }

    public void linkCatReq(UserCatReq userCatReq) {
        String mapTitle = userCatReq.getCategory() + userCatReq.getSpec();
        this.linkReqMap.put(mapTitle, userCatReq);
    }

    public Map<String, Object> getLinkReqMap() {
        return linkReqMap;
    }

    public String getReqType() {
        return "cat";
    }

    public String getCategory() {
        return category;
    }

    public String getSpec() {
        return spec;
    }

    public List<String> getChoice() {
        return choice;
    }

    public Map<String, List> getCarrierMap() {
        return carrierMap;
    }

    public String getOperator() {
        return operator;
    }


}
