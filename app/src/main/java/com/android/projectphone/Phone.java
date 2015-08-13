package com.android.projectphone;

import android.graphics.Bitmap;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nocturna on 5/17/2015.
 */
public class Phone {
    static Map<String, Bitmap> phoneThumbnailMap = new LinkedHashMap<>();
    String phone;
    Map<String, List<String>> categorySpecMap = new LinkedHashMap<>();
    // Map<String, List<String>> specPhoneSpecMap = new LinkedHashMap<>();
    Map<String, String> specPhoneSpecMap = new LinkedHashMap<>();
    /* public Phone(String phone, Map<String, List<String>> categorySpecMap, Map <String, List<String>> specPhoneSpecMap) {
        this.phone = phone;
        this.categorySpecMap = new LinkedHashMap<>(categorySpecMap);
        this.specPhoneSpecMap = new LinkedHashMap<>(specPhoneSpecMap);
    } */
    Bitmap thumbnail = null;

    public Phone(String phone, Map<String, List<String>> categorySpecMap, Map<String, String> specPhoneSpecMap) {
        this.phone = phone;
        this.categorySpecMap = categorySpecMap;
        this.specPhoneSpecMap = specPhoneSpecMap;
    }

    public String getPhone() {
        return phone;
    }

    public Map<String, List<String>> getCategorySpecMap() {
        return categorySpecMap;
    }

    public Map<String, String> getSpecPhoneSpecMap() {
        return specPhoneSpecMap;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
