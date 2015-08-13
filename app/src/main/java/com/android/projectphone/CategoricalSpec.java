package com.android.projectphone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hnocturna on 5/11/2015.
 */
public class CategoricalSpec {
    String category;
    String spec;

    List<String> choices = new ArrayList<>();

    public CategoricalSpec(String category, String spec, List<String> choices) {
        this.category = category;
        this.spec = spec;
        this.choices = choices;
    }


    public String getCategory() {
        return category;
    }

    public String getSpec() {
        return spec;
    }

    public List<String> getChoices() {
        return choices;
    }


}
