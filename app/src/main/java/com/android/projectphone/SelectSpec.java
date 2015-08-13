package com.android.projectphone;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nocturna on 5/10/2015.
 */
public class SelectSpec extends ActionBarActivity {
    MySQLiteHelper db = new MySQLiteHelper(this);

    List<UserReq> userReqList = new LinkedList<>();

    Double minInput = 0.0;
    Double maxInput = 0.0;

    int screenState = -1;
    int passedSpecGroup = -1;

    String chosenCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SelectSpec", "onCreate");
    }

    @Override
    protected void onStart() {
        Log.d("SelectSpec", "onStart");
        super.onStart();
        setContentView(R.layout.activity_select_spec);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String category = extras.getString("category");
            String spec = extras.getString("spec");
            String type = extras.getString("type");
            Log.d("SelectSpec.onStart", "Category: " + category + " | Spec: " + spec);
            chosenCategory = category;
            passedSpecGroup = extras.getInt("specGroup");
            if (type.equals("num")) {
                inputNumericalSpec(category, spec);
            } else if (type.equals("cat")) {
                inputCategoricalSpec(category, spec);
            } else {
                Log.d("SelectSpec.getExtras", "Unknown type: " + type);
            }
        } else {
            loadCategories();
        }
    }

    private void loadCategories() {
        Log.d("SelectSpec", "Loading Categories!");
        screenState = 1;
        setContentView(R.layout.activity_select_spec);
        LinearLayout specCategoryLayout = (LinearLayout) findViewById(R.id.activity_select_specs);

        specCategoryLayout.addView(new Button(this));
        specCategoryLayout.removeAllViews();

        ArrayList<String> categoryTitles = new ArrayList<String>(db.returnSectionTitles(getApplication())); // Get a list of all the tables from the database.
        for (final String category : categoryTitles) {
            // For each table, create a new button that will load a new layout with a button of all specs within that category/table.

            if (category.equals("availability") || category.equals("internet_browsing") || category.equals("multimedia")) {
                // Ignore aforementioned tables because there are no real values to select.
                continue;
            }

            Button categoryButton = new Button(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonParams.setMargins(30, 4, 30, 4);
            categoryButton.setLayoutParams(buttonParams);
            categoryButton.setText(category.replace("_", " "));
            categoryButton.setTextColor(getResources().getColor(R.color.white));
            specCategoryLayout.addView(categoryButton, buttonParams);
            categoryButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    chosenCategory = category;
                    loadSpecs(category);
                }
            });
        }
    }

    private void loadSpecs(final String category) {
        // Loads a layout containing a button for each spec within the chosen category/table.
        setContentView(R.layout.activity_select_spec);
        screenState = 2;

        // Clean the slate! Make sure buttons from the loadCategory method are removed.
        LinearLayout specLayout = (LinearLayout) findViewById(R.id.activity_select_specs);;
        specLayout.removeAllViews();

        ArrayList<String> specs = new ArrayList<String>(db.returnColumns(category));

        if (category.equals("technology")) {
            specs = new ArrayList<String>(Arrays.asList("Positioning", "Multiple_SIM_cards", "HD_Voice"));
        }

        for (final String spec : specs) {
            if (spec.equals("Processor") || spec.equals("Graphics_processor") || spec.equals("Maximum_User_Storage") || spec.equals("Talk_time") || spec.equals("Stand_by_time") ||
                    spec.equals("Stand_by_time_3G") || spec.equals("Stand_by_time_4G") || spec.equals("Music_playback") || spec.equals("Video_playback") || spec.equals("Talk_time_3G") ||
                    spec.equals("Flash") || spec.equals("Mobile_hotspot") || (category.equals("Connectivity") && spec.equals("Features")) || spec.equals("Rugged") || spec.equals("Colors") ||
                    spec.equals("Touchscreen") || spec.equals("Navigation") || spec.equals("GSM") || spec.equals("UMTS") || spec.equals("FDD_LTE") || spec.equals("Micro_SIM") || spec.equals("nano_SIM") ||
                    spec.equals("CDMA") || spec.equals("TDD_LTE") || spec.equals("UMTS") || spec.equals("Colors")  || spec.equals("Touchscreen") || spec.equals("Device_type") ||
                    spec.equals("MIL_STD_810_certified") || spec.equals("Video_call_time")) {
                // Skip these specs because there isn't much to choose from or the table values are incomplete.
                continue;
            }
            Button specButton = new Button(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Adds a button for each spec. <-- Consider using ListView to clean it up.
            buttonParams.setMargins(30, 4, 30, 4);
            specButton.setLayoutParams(buttonParams);
            specButton.setText(spec.replace("_", " "));
            specButton.setTextColor(getResources().getColor(R.color.white));
            specLayout.addView(specButton, buttonParams);

            specButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.input_spec);
                    if (spec.equals("Resolution") || spec.equals("Screen_to_body_ratio") || spec.equals("Physical_size") || spec.equals("Pixel_density") || spec.equals("Camera") || spec.equals("Aperture_size")
                            || spec.equals("Pixel_size") || spec.equals("Front_facing_camera") || spec.equals("Capacity") || spec.equals("System_memory") || spec.equals("Built_in_storage") ||
                            spec.equals("Camcorder") || spec.equals("Aperture_size") || spec.equals("Endurance_Rating") || spec.equals("Focal_length_35mm_equivalent") || spec.equals("Camera_sensor_size") || spec.equals("Weight")) {

                        // These specs have numerical values that require sliders to allow the user select the values.

                        inputNumericalSpec(category, spec);
                    }
                    else if (spec.equals("IP_certified") || spec.equals("USB" ) || spec.equals("HDMI") || spec.equals("Storage_expansion") || spec.equals("Features") ||
                            spec.equals("Connector")  || spec.equals("Other") || spec.equals("Settings") || spec.equals("CC_Features") || spec.equals("FFC_Features") ||
                            spec.equals("Not_user_replaceable") || spec.equals("Materials") || spec.equals("Technology") || spec.equals("System_chip") || spec.equals("Connector") ||
                            (category.equals("connectivity") && (spec.equals("Features") || spec.equals("Other"))) || (category.equals("other_features") && (spec.equals("Notifications") ||
                            spec.equals("Additional_microphones") || spec.equals("Sensors") || spec.equals("Hearing_aid_compatibility"))) || spec.equals("Positioning") ||
                            spec.equals("Multiple_SIM_cards") || spec.equals("HD_Voice") || spec.equals("Carrier") || spec.equals("Wireless_charging") || spec.equals("Flash") ||
                            spec.equals("Shooting_Modes") || (category.equals("camera") && (spec.equals("Features") || spec.equals("Settings"))) || spec.equals("Wi_Fi") ||
                            spec.equals("Bluetooth") || spec.equals("Rugged") || (category.equals("display") && spec.equals("Features") || spec.equals("Radio") ||
                            category.equals("other_features") || spec.equals("nano_SIM") || spec.equals("Micro_SIM") || spec.equals("Multiple_SIM_cards") || spec.equals("OS")) ) {

                        // These specs have categorical values that can be selected with checkboxes.

                        Log.d("SelectSpec.loadSpecs", "Spec: " + spec);
                        inputCategoricalSpec(category, spec);
                    } else if (spec.equals("Dimensions")) {
                        final Dialog dialog = new Dialog(SelectSpec.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.hwd_popup);

                        Button hButton = (Button) dialog.findViewById(R.id.height_button);
                        Button wButton = (Button) dialog.findViewById(R.id.width_button);
                        Button dButton = (Button) dialog.findViewById(R.id.depth_button);

                        hButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                inputNumericalSpec(category, spec + " height");
                                dialog.hide();
                            }
                        });

                        wButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                inputNumericalSpec(category, spec + " width");
                                dialog.hide();
                            }
                        });

                        dButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                inputNumericalSpec(category, spec + " depth");
                                dialog.hide();
                            }
                        });

                        dialog.show();
                    } else {
                        Log.d("SelectSpec.loadSpecs", "Spec: " + spec);
                    }
                }
            });
        }
    }

    private void inputNumericalSpec(final String category, final String spec) {
        // Layout that allows the user to select numerical specs - either with or without a maximum.
        setContentView(R.layout.input_spec);
        screenState = 3;
        chosenCategory = category;

        com.melnykov.fab.FloatingActionButton addSpecButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.confirm_spec_button);
        com.melnykov.fab.FloatingActionButton linkSpecButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.link_spec_button);
        NumericalSpec ns = db.getMinMax(category, spec);

        final String unit = ns.getUnit();

        final Integer min = ns.getMin();
        final Integer max = ns.getMax();
        final Integer magnitude = ns.getMagnitude();                                                // Magnitude aka power of ten that the original value was in before it was converted to an integer for the seekBar.

        final Map<Integer, Integer> choiceMap = ns.getChoiceMap();                                  // An ordered HashMap of choices for easy selection using seekBar becase the seekBar can only return
                                                                                                    // integer values which can be used to get the actual value in the choiceMap.
        final Map<String, List<String>> resolutions = ns.getResolutions();
        final List<String> resolutionKeyList = new ArrayList(resolutions.keySet());                 // keySet holds the name of the standard resolution.
        final List<String> choiceList = ns.getChoiceList();

        final TextView maxTv = (TextView) findViewById(R.id.max_value);
        final TextView minTv = (TextView) findViewById(R.id.min_value);

        SeekBar minSeek = (SeekBar) findViewById(R.id.min_seek);
        SeekBar maxSeek = (SeekBar) findViewById(R.id.max_seek);

        if (min != null) {
            // Used for when this spec has a minimum & maximum value, but because these come as a couple, checking for one is as good as checking for both.
            minInput = (double) min / (Math.pow(10, magnitude));    // Convert the min-integer back to original double-value using the magnitude stored above.
            maxInput = (double) max / (Math.pow(10, magnitude));
            // Set the corresponding TextViews to the min/max values of the spec
            if (magnitude == 0) {
                minTv.setText("Minimum: " + Integer.toString(min) + " " + unit);
                maxTv.setText("Maximum: " + Integer.toString(max) + " " + unit);
            } else if (spec.contains("Dimensions")) {
                DecimalFormat df = new DecimalFormat("#.#");
                // Truncate the decimal for the min/max values.
                minInput = Math.floor(minInput * 100) / 100;
                maxInput = Math.floor(maxInput * 100) / 100;

                // Set the TextViews for the min/max including the metric system values that are calculated on the fly. (Consider doing it in reverse as mm is more precise).
                minTv.setText("Minimum: " + Double.toString(minInput) + " inches (" + df.format(minInput * 25.4) + " mm)");
                maxTv.setText("Minimum: " + Double.toString(maxInput) + " inches (" + df.format(maxInput * 25.4) + " mm)");
            } else if (spec.equals("Weight")) {
                DecimalFormat df = new DecimalFormat("#");

                // Truncate the decimal for the min/max values.
                minInput = Math.floor(minInput * 100) / 100;
                maxInput = Math.floor(maxInput * 100) / 100;

                minTv.setText("Minimum: " + Double.toString(minInput) + " oz (" + df.format(minInput * 28.3495) + " g)");
                maxTv.setText("Minimum: " + Double.toString(maxInput) + " oz (" + df.format(maxInput * 28.3495) + " g)");
            }
            else {
                minTv.setText("Minimum: " + Double.toString(min / (Math.pow(10,magnitude))) + " " + unit);
                maxTv.setText("Maximum: " + Double.toString(max / (Math.pow(10,magnitude))) + " " + unit);

            }
            // Set max values for each SeekBar.
            minSeek.setMax(max - min);
            maxSeek.setMax(max - min);
            // Set progress of max SeekBar to the max value.
            maxSeek.setProgress(max);

        } else if (spec.equals("Resolution") || spec.equals("Camcorder")) {
            // Used if the spec is resolution because of the non-standard resolutions being rounded to the nearest standard resolution value.
            minInput = (double) 0;
            maxInput = (double) (resolutions.size() - 1);       // No reason to allow user to set max resolution, so this is set by default.
            minSeek.setMax(maxInput.intValue());
            maxSeek.setVisibility(View.GONE);                   // Hidden from user - see above.
            minTv.setText(resolutionKeyList.get(0));
        } else if (spec.contains("Dimensions")) {

        } else {
            // Used for all values that have a min, but no max. Like resolution, but these only have a single value so there is no reason for a hashed list.
            minInput = (double) 0;
            maxInput = (double) (choiceMap.size() - 1);         // No reason to allow user to set max, so this is set by default.
            minSeek.setMax(maxInput.intValue());
            maxSeek.setVisibility(View.GONE);                   // Hidden from user - see above.
            minTv.setText("Minimum: " + Integer.toString(choiceMap.get(0)) + " " + unit);
        }

        minSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (min != null) {
                    // Used if min value exists (not minInput, but the actual value returned form the database)
                    int value = (min + progress);       // Actual minimum value from the SeekBar will have to add the int progress to min
                    if (magnitude != 0) {
                        // Convert the input min to a double including the correct magnitude of the value.

                        minInput = value / (Math.pow(10, magnitude));
                        if (spec.contains("Dimensions")) {
                            DecimalFormat df = new DecimalFormat("#.#");
                            minInput = Math.floor(minInput * 100) / 100;
                            minTv.setText("Minimum: " + Double.toString(minInput) + " inches (" + df.format(minInput * 25.4) + "mm)");
                        } else if (spec.equals("Weight")) {
                            DecimalFormat df = new DecimalFormat("#");
                            minInput = Math.floor(minInput * 100) /100;
                            minTv.setText("Minimum: " + Double.toString(minInput) + " oz (" + df.format(minInput * 28.3495) + " g)");
                        } else {
                            minTv.setText("Minimum: " + Double.toString(minInput) + " " + unit);
                        }

                    } else {
                        minInput = (double) value;
                        minTv.setText("Minimum: " + Integer.toString(value));
                    }
                } else if (spec.equals("Resolution") || spec.equals("Camcorder")) {
                    // Used if the spec if resolution.
                    minInput = (double) progress;                                   // Not entirely sure I need this as minInput is not used for the resolution
                    minTv.setText(resolutionKeyList.get(progress));                 // Use progress to iterate through the resolution map keySet and find corresponding values.
                } else {
                    // Used for any spec that doesn't require a max.
                    minInput = (double) progress;
                    minTv.setText("Minimum: " + Integer.toString(choiceMap.get(progress)) + " " + unit);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        maxSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Only used for the specs with user-selectable max-values.
                int value = (min + progress);
                if (magnitude != 0) {
                    maxInput = value / (Math.pow(10, magnitude));
                    if (spec.contains("Dimensions")) {
                        DecimalFormat df = new DecimalFormat("#.#");
                        maxInput = Math.floor(maxInput * 100) / 100;
                        maxTv.setText("Minimum: " + Double.toString(maxInput) + " inches (" + df.format(maxInput * 25.4) + "mm)");
                    } else if (spec.equals("Weight")) {
                        DecimalFormat df = new DecimalFormat("#");
                        maxInput = Math.floor(maxInput * 100) / 100;
                        maxTv.setText("Minimum: " + Double.toString(maxInput) + " oz (" + df.format(maxInput * 28.3495) + " g)");
                    } else {
                        maxTv.setText("Maximum: " + Double.toString(maxInput) + " " + unit);
                    }

                } else {
                    maxInput = (double) value;
                    maxTv.setText("Maximum: " + Integer.toString(value) + " " + unit);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        CharSequence text = "Minimum value cannot be higher than maximum value!";   // Toast to be shown if the max < min.
        int duration = Toast.LENGTH_SHORT;
        final Toast toast = Toast.makeText(getApplicationContext(), text, duration);

        addSpecButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("SelectSpecs.AddSpec", "Add spec button clicked!");
                Log.d("SelectSpecs.AddSpec", "Min: " + minInput + " | Max: " + maxInput);
                if (minInput > maxInput) {
                    // Invalid input. Show error toast.
                    toast.show();
                }
                else {
                    UserReq userReq = null;
                    if (min != null) {
                        // Create new UserReq object containing user selected values for specs w/ user-selectable max values.
                        userReq = new UserReq(category, spec, minInput, maxInput, unit);

                    } else if (spec.equals("Resolution") || spec.equals("Camcorder")) {
                        // Create UserReq object storing users preferred min resolution
                        List<String> choiceList = new LinkedList<String>();
                        for (int i = minInput.intValue(); i < resolutionKeyList.size(); i++) {
                            for (String resolution : resolutions.get(resolutionKeyList.get(i))) {
                                // Add all resolutions in the selected standard resolution subcategories and higher to a new list.


                                choiceList.add(resolution);
                            }
                        }
                        userReq = new UserReq(category, spec, resolutionKeyList.get(minInput.intValue()), choiceList);

                    } else {
                        // Create UserReq object for all other specs.
                        List<String> choiceList = new LinkedList<String>();
                        for (int i = minInput.intValue(); i < choiceMap.size(); i++) {
                            // Add all selected values and higher to the list by iterating through the choiceMap.
                            choiceList.add(String.valueOf(choiceMap.get(i)));
                        }
                        userReq = new UserReq(category, spec, choiceList, unit);
                    }

                    if (passedSpecGroup != -1) {
                        userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(passedSpecGroup, userReqList);
                        passedSpecGroup = -1;
                    } else {
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(SpecsActivity.specGroup, userReqList);
                    }

                    Intent intent = new Intent(SelectSpec.this, SpecsActivity.class);
                    startActivity(intent);
                    SpecsActivity.specGroup++;
                    finish();
                }
            }
        });

        linkSpecButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                UserReq userReq = null;
                if (min != null) {
                    // Create new UserReq object containing user selected values for specs w/ user-selectable max values.
                    userReq = new UserReq(category, spec, minInput, maxInput, unit);

                } else if (spec.equals("Resolution") || spec.equals("Camcorder")) {
                    // Create UserReq object storing users preferred min resolution
                    List<String> choiceList = new LinkedList<String>();
                    for (int i = minInput.intValue(); i < resolutionKeyList.size(); i++) {
                        for (String resolution : resolutions.get(resolutionKeyList.get(i))) {
                            // Add all resolutions in the selected standard resolution subcategories and higher to a new list.
                            choiceList.add(resolution);
                        }
                    }
                    userReq = new UserReq(category, spec, resolutionKeyList.get(minInput.intValue()), choiceList);

                } else {
                    // Create UserReq object for all other specs.
                    List<Integer> choiceList = new LinkedList<Integer>();
                    for (int i = minInput.intValue(); i < choiceMap.size(); i++) {
                        // Add all selected values and higher to the list by iterating through the choiceMap.
                        choiceList.add(choiceMap.get(i));
                    }
                    userReq = new UserReq(category, spec, choiceList, unit);
                }
                if (passedSpecGroup != -1) {
                    userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                    userReqList.add(userReq);
                } else {
                    userReqList.add(userReq);
                }
                loadCategories();
            }
        });
    }

    String select;
    String operator;

    private void inputCategoricalSpec(final String category, final String spec) {
        // Used for all specs that are categorical and can use checkboxes to hold values.
        screenState = 3;

        setContentView(R.layout.input_spec);

        LinearLayout inputSpecLayout = (LinearLayout) findViewById(R.id.input_spec_num_layout);
        ListView checklistListView = (ListView) findViewById(R.id.input_spec_listview);           // Displays the choices available to the user.

        final com.melnykov.fab.FloatingActionButton addSpecButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.confirm_spec_button);     // See the OnClickListener below for function.
        final com.melnykov.fab.FloatingActionButton linkSpecButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.link_spec_button);       // See the OnClickListener below for function.

        if (spec.equals("Carrier")) {
            // Hide button for carrier because each choice will be a button that will include its function.
            addSpecButton.setVisibility(View.GONE);
        }

        // Hide unused elements.
        SeekBar minSeek = (SeekBar) findViewById(R.id.min_seek);
        SeekBar maxSeek = (SeekBar) findViewById(R.id.max_seek);
        minSeek.setVisibility(View.GONE);
        maxSeek.setVisibility(View.GONE);

        TextView minText = (TextView) findViewById(R.id.min_value);
        TextView maxText = (TextView) findViewById(R.id.max_value);
        minText.setVisibility(View.GONE);
        maxText.setVisibility(View.GONE);

        // Retrieve the choices from the db column.
        CategoricalSpec cs = db.getChoices(category, spec);

        final List<String> userChoice = new ArrayList<String>();        // Holds the list of choices the user has selected.

        int i = 1;
        if (spec.equals("IP_certified") || spec.equals("USB" ) || spec.equals("HDMI") || spec.equals("Storage_expansion") || spec.equals("Features") ||
                spec.equals("Connector")  || spec.equals("Other") || spec.equals("Settings") || spec.equals("CC_Features") || spec.equals("FFC_Features") ||
                spec.equals("Not_user_replaceable") || spec.equals("Materials") || spec.equals("Technology") || spec.equals("System_chip") || spec.equals("Connector") ||
                (category.equals("connectivity") && (spec.equals("Features") || spec.equals("Other"))) || (category.equals("other_features") && (spec.equals("Notifications") ||
                spec.equals("Additional_microphones") || spec.equals("Sensors") || spec.equals("Hearing_aid_compatibility"))) || spec.equals("Positioning") ||
                spec.equals("Multiple_SIM_cards") || spec.equals("HD_Voice") || spec.equals("Carrier") | spec.equals("Wireless_charging") || spec.equals("Flash") || spec.equals("Shooting_Modes") ||
                (category.equals("camera") && (spec.equals("Features") || spec.equals("Settings"))) || spec.equals("Wi_Fi") || spec.equals("Bluetooth") || spec.equals("Rugged") ||
                (category.equals("display") && spec.equals("Features") || spec.equals("Radio") || category.equals("other_features") || spec.equals("nano_SIM") || spec.equals("Micro_SIM")) ) {

            final List<String> choices = cs.getChoices();
            if (spec.equals("Carrier")) {
                // Incomplete. Need a way to hold all carrier variants.

                for (final String choice : choices) {
                    RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    if (i != 1) {
                        buttonParams.addRule(RelativeLayout.BELOW, i - 1);
                    }

                    Button button = new Button(this);
                    button.setText(choice);
                    button.setId(i);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String, List> carrierMap = new LinkedHashMap<>();
                            if (choice.equals("T-Mobile")) {
                                carrierMap.put("GSM", new ArrayList(Arrays.asList("850", "900", "1800", "1900")));
                                carrierMap.put("UMTS", new ArrayList(Arrays.asList("850", "900", "1700/2100", "1900", "2100")));
                                carrierMap.put("FDD_LTE", new ArrayList(Arrays.asList("2100 (band 1)", "1900 (band 2)", "1700/2100 (band 4)", "700 (band 12)")));
                            }
                            UserReq userReq = new UserReq(category, spec, carrierMap);
                            Intent intent = new Intent(SelectSpec.this, SpecsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    inputSpecLayout.addView(button, buttonParams);
                    i++;
                }
            } else {
                // Attach the list of options to the Adapter for the ListView.
                ChecklistAdapter checkListAdapter = new ChecklistAdapter(this, choices, userChoice);
                checklistListView.setAdapter(checkListAdapter);

            }
        }

        // Set up a dialog window that forces the user to select whether the phone should contain ALL requirements or ANY requirement selected.
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.all_or_any_popup);

        Button allButton = (Button) dialog.findViewById(R.id.all_button);
        Button anyButton = (Button) dialog.findViewById(R.id.any_button);

        anyButton.setOnClickListener(new View.OnClickListener() {
            // Allows the user to find phones that contain at least one of the selected options.
            @Override
            public void onClick(View view) {
                operator = "or";
                UserReq userReq = new UserReq(category, spec, userChoice, operator);
                if (select.equals("add")) {
                    Intent intent = new Intent(SelectSpec.this, SpecsActivity.class);
                    if (passedSpecGroup != -1) {
                        userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(passedSpecGroup, userReqList);
                        passedSpecGroup = -1;
                    } else {
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(SpecsActivity.specGroup, userReqList);
                        SpecsActivity.specGroup++;
                    }
                    startActivity(intent);
                    finish();
                } else if (select.contains("link")) {
                    if (passedSpecGroup != -1) {
                        userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                    }
                    userReqList.add(userReq);
                    loadCategories();
                }
            }
        });

        allButton.setOnClickListener(new View.OnClickListener() {
            // Allows the user to find only phones tha contain ALL selected options.
            @Override
            public void onClick(View view) {
                operator = "and";
                UserReq userReq = new UserReq(category, spec, userChoice, operator);
                if (select.equals("add")) {
                    Intent intent = new Intent(SelectSpec.this, SpecsActivity.class);
                    if (passedSpecGroup != -1) {
                        userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(passedSpecGroup, userReqList);
                        passedSpecGroup = -1;
                    } else {
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(SpecsActivity.specGroup, userReqList);
                        SpecsActivity.specGroup++;
                    }
                    startActivity(intent);
                    finish();
                } else if (select.contains("link")) {
                    if (passedSpecGroup != -1) {
                        userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                    }
                    userReqList.add(userReq);
                    loadCategories();
                }
            }
        });

        CharSequence text = "Must select at least one requirement!";   // Toast to be shown if operator is null.
        int duration = Toast.LENGTH_SHORT;
        final Toast toast = Toast.makeText(getApplicationContext(), text, duration);

        addSpecButton.setOnClickListener(new View.OnClickListener() {
            // Adds the requirement to the list of requirements.
            @Override
            public void onClick(View v) {
                select = "add";

                if (userChoice.size() == 1) {
                    operator = "and";
                    UserReq userReq = new UserReq(category, spec, userChoice, operator);

                    if (passedSpecGroup != -1) {
                        userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(passedSpecGroup, userReqList);
                        passedSpecGroup = -1;
                    } else {
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(SpecsActivity.specGroup, userReqList);
                        SpecsActivity.specGroup++;
                    }

                    Intent intent = new Intent(SelectSpec.this, SpecsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (spec.equals("System_chip") && userChoice.size() >= 1) {
                    operator = "or";
                    UserReq userReq = new UserReq(category, spec, userChoice, operator);

                    if (passedSpecGroup != -1) {
                        userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(passedSpecGroup, userReqList);
                        passedSpecGroup = -1;
                    } else {
                        userReqList.add(userReq);
                        SpecsActivity.specReqMap.put(SpecsActivity.specGroup, userReqList);
                        SpecsActivity.specGroup++;
                    }

                    Intent intent = new Intent(SelectSpec.this, SpecsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (userChoice.size() < 1) {
                    // Must select at least one requirement.
                    toast.show();
                } else {
                    // Shows the popup dialog described above.
                    dialog.show();
                }
            }
        });


        linkSpecButton.setOnClickListener(new View.OnClickListener() {
            // Links the requirement to another requirement to search for phones that contain either requirement that is linked.
            @Override
            public void onClick(View view) {
                select = "link";
                if (userChoice.size() == 1) {
                    operator = "and";
                    UserReq userReq = new UserReq(category, spec, userChoice, operator);

                    if (passedSpecGroup != -1) {
                        userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                    }
                    userReqList.add(userReq);
                    loadCategories();
                } else if (spec.equals("System_chip") && userChoice.size() >= 1) {
                    // Phones cannot contain more than one of this spec.
                    operator = "or";
                    UserReq userReq = new UserReq(category, spec, userChoice, operator);

                    if (passedSpecGroup != -1) {
                        userReqList = SpecsActivity.specReqMap.get(passedSpecGroup);
                    }
                    userReqList.add(userReq);
                    loadCategories();
                } else if (userChoice.size() < 1) {
                    // Must select at least one requirement.
                    toast.show();
                } else {
                    // Shows the dialog described above.
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Used to for switching between the different layouts without changing activities.
        if (screenState == 1) {
            Intent intent = new Intent(this, SpecsActivity.class);
            startActivity(intent);
            finish();
        } else if (screenState == 2) {
            loadCategories();
        } else if (screenState == 3) {
            loadSpecs(chosenCategory);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_specs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
