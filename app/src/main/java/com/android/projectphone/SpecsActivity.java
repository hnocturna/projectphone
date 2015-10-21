package com.android.projectphone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.projectphone.data.PhoneDbHelperTest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SpecsActivity extends ActionBarActivity {
    // Displays the requirements the user has chosen and allows a launching point for adding more requirements and searching for a phone that meets those requirements.
    RelativeLayout activitySpecsLayout;
    PhoneDbHelperTest db = new PhoneDbHelperTest(this);
    static Map<Integer, List<UserReq>> specReqMap = new LinkedHashMap<>();          // Used to hold all user selected requirements;
    static List<Integer> availableColorGroups = new LinkedList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
    static Map<Integer, Integer> specColorGroupMap = new LinkedHashMap<>();
    List<Map<Integer, UserReq>> cardSpecList = new LinkedList<>();                  // List of specs to pass to the Required Spec Card Adapter
    static int specGroup = 0;

    boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specs);
        activitySpecsLayout = (RelativeLayout) findViewById(R.id.activity_specs_layout);
    }

    @Override
    protected void onStart() {

        Log.d("SpecsActivity", "onStart()");
        super.onStart();

        // Buttons for adding required specs and searching phones.
        final com.melnykov.fab.FloatingActionButton addSpecButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.add_spec_button);
        final com.melnykov.fab.FloatingActionButton searchButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.search_phone_button);

        // Search button is hidden unless a requirement is added by the user.
        if (!specReqMap.isEmpty()) {
            searchButton.setVisibility(View.VISIBLE);
        } else {
            searchButton.setVisibility(View.GONE);
        }

        addSpecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starts the new activity to select specs.
                Intent intent = new Intent(SpecsActivity.this, SelectSpec.class);
                finish();
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starts the activity that displays a list of phones that meet requirements.
                Intent intent = new Intent(SpecsActivity.this, KnownActivity.class);

                // Pass on the list of phones that meet the specifications as an Extra to the next activity.
                intent.putStringArrayListExtra("phoneList", new ArrayList(db.getCandidatePhones(specReqMap, null)));
                intent.putExtra("activity", "specs");
                startActivity(intent);
            }
        });

        displaySpecs();

    }

    private void displaySpecs() {
        // Log.d("Specs.displayRequiredSpecs", "List of requirements" + specReqMap.values().toString());
        for (int group : specReqMap.keySet()) {
            List<UserReq> tempList = specReqMap.get(group);
            Log.d("Specs.displaySpecs", "List of requirements" + specReqMap.values().toString());
        }
        // Displays a CardView of the requirements the user has selected.

        activitySpecsLayout = (RelativeLayout) findViewById(R.id.activity_specs_layout);

        // Set up the Recycler View and Layout Manager.
        final RecyclerView reqRecyclerView = (RecyclerView) findViewById(R.id.requirement_card_view);
        LinearLayoutManager llm = new LinearLayoutManager(SpecsActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        reqRecyclerView.setHasFixedSize(true);
        reqRecyclerView.setLayoutManager(llm);

        // Initialize the list to populate the Require Spec Card Adapter
        List<CardInfo> reqCardList = new LinkedList<>();

        // Initialize the Map that will be used to populate the CardView.
        Map<String, List<Map<UserReq, String>>>  cardReqSpecMap = new LinkedHashMap<>();        // Each category will link to a List of a Map of the UserReq and card text.

        for (int cardSpecGroup : specReqMap.keySet()) {
            List<UserReq> specReqList = specReqMap.get(cardSpecGroup);
            Log.d("Specs.displaySpecs", "Size of list: " + specReqList.size());
            for (UserReq userReq : specReqList) {
                // Converts the user's requirements to a readable text.

                userReq.setSpecGroup(cardSpecGroup);
                // Map to store UserReq and the associated card text. Will be used to remove/edit the userReq in case the edit/link button is pressed.
                Map<UserReq, String> specCardTextMap = new LinkedHashMap<>();

                String category = userReq.getCategory();
                String cardText = generateCardText(userReq);
                Log.d("Specs.displaySpecs", "Card text: " + cardText);

                // Populate the Map matching categories to its List of card text.
                if (cardReqSpecMap.get(category) == null) {
                    cardReqSpecMap.put(category, new LinkedList<Map<UserReq, String>>());
                }

                // Get the existing List of the cardReqSpecMap and add the current requirement to the list and then replace it in the map.
                List<Map<UserReq, String>> cardTextMapList = cardReqSpecMap.get(category);
                specCardTextMap.put(userReq, cardText);
                cardTextMapList.add(specCardTextMap);
                cardReqSpecMap.put(category, cardTextMapList);
            }
        }
        Log.d("Specs.displaySpecs", "Available color groups: " + availableColorGroups.toString());

        for (String cardCategory : cardReqSpecMap.keySet()) {
            reqCardList.add(new CardInfo(cardCategory, cardReqSpecMap.get(cardCategory)));
        }

        ReqSpecCardAdapter reqSpecCardAdapter = new ReqSpecCardAdapter(reqCardList);
        reqRecyclerView.setAdapter(reqSpecCardAdapter);

        Context context = getApplicationContext();

    }

    private String generateCardText(UserReq userReq) {
        // Converts the user's requirements to a readable text.
        String cardText = null;

        // Initialize the information to be passed onto the Card Adapter
        String unit = null;

            // Variables for user choice.
        String resolutionChoice = null;

        Double min = 0.0;
        Double max = 0.0;
        List<String> choiceList = null;
        // Map<String, List> carrierMap = null;         // Currently unused. Might implement at a later time.

        String type = userReq.getType();
        String spec = userReq.getSpec();
        String specText = spec.replaceAll("_", " ");    // Separate String for user-readable text of the spec.

        if (type.equals("num")) {
            max = userReq.getMax();
            min = userReq.getMin();
            resolutionChoice = userReq.getResolutionChoice();
            unit = userReq.getUnit();
            choiceList = userReq.getChoice();

            if (max > 0) {
                // If max is greater than zero, then the requirement is a range.
                if (spec.contains("Dimensions")) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    cardText = specText + " between " + min + "-" + max + "inches (" + df.format(min * 25.4) + "-" + df.format(max * 25.4) + " mm)";
                } else {
                    cardText = specText + " between " + min + "-" + max + " " + unit + ".";
                }

            } else if (resolutionChoice != null) {
                // Special requirements that include entire groups of resolutions.
                cardText = specText + " of at least " + resolutionChoice + " resolution.";
            } else {
                // Any requirement with only a minimum value.
                Log.d("Specs.generateCardText", "Choices: " + choiceList.toString());
                cardText = specText + " with at least " + choiceList.get(0) + " " + unit + ".";
            }
        } else if (type.equals("cat")) {
            choiceList = userReq.getChoice();
            cardText = specText + " with " + choiceList.toString() + " features.";
        } else {
            Log.d("Specs.getCardText", "Unknown requirement type: " + type);
        }

        return cardText;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_specs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.reset_specs) {
            SpecsActivity.specReqMap.clear();
            specGroup = 0;
            onStart();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
