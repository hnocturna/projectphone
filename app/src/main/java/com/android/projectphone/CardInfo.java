package com.android.projectphone;

import java.util.List;
import java.util.Map;

/**
 * Created by Hnocturna on 6/4/2015.
 */
public class CardInfo {
    String title;
    String imageURL;
    String text;

    boolean clicked;

    List<String> specList;
    List<Map<UserReq, String>> reqSpecList;

    Map<String, String> phoneSpecMap;

    int icon;

    public CardInfo(String title, String text, int icon) {
        this.title = title;
        this.text = text;
        this.icon = icon;
    }

    public CardInfo(String title, String text, int icon, boolean clicked) {
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.clicked = clicked;
    }

    public CardInfo(String section, List<String> specList, Map<String, String> phoneSpecMap) {
        this.title = section;
        this.specList = specList;
        this.phoneSpecMap = phoneSpecMap;
    }

    public CardInfo(String section, List<Map<UserReq, String>> reqSpecList) {
        this.title = section;
        this.reqSpecList = reqSpecList;
    }

    public CardInfo(String title, String imageURL) {
        this.title = title;
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getIcon() {
        return icon;
    }

    public List<String> getSpecList() {
        return specList;
    }

    public List<Map<UserReq, String>> getReqSpecList() {
        return reqSpecList;
    }

    public Map<String, String> getPhoneSpecMap() {
        return phoneSpecMap;
    }

    public boolean getClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}
