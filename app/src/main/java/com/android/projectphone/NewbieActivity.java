package com.android.projectphone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class NewbieActivity extends ActionBarActivity {
    List<CardInfo> cardInfoList = new LinkedList<CardInfo>();                      // Used to hold the list Cards to be

    int specGroup = 0;

    Map<Integer, List<UserReq>> inputSpecMap = new LinkedHashMap<>();       // Used to hold all user specs. Supersedes the following Maps.
    HashMap<String, UserNumReq> inputNumSpecMap = new HashMap<>();          // Used to hold all user selected numerical specs.
    HashMap<String, UserCatReq> inputCatSpecMap = new HashMap<>();          // Used to hold all user selected categorical specs.

    List<String> specificPhones = new LinkedList<String>();                      // Holds phones that are going to be included regardless of spec candidacy.
    List<String> tempSpecificPhones = new LinkedList<String>();                  // Temporarily holds phones and merges them with the full list to retain only the common phones.
    Double minInput;                                                        // Holds the minInput for the display size.
    Double maxInput;                                                        // Hold the maxInput for the display size
    MySQLiteHelper db = new MySQLiteHelper(this);
    List<CardInfo> multimediaCardList = new LinkedList<>();
    List<CardInfo> cameraCardList = new LinkedList<>();
    List<CardInfo> phoneCardList = new LinkedList<>();
    List<CardInfo> fitnessCardList = new LinkedList<>();
    List<CardInfo> productivityCardList = new LinkedList<>();
    List<CardInfo> displayCardList = new LinkedList<>();
    List<CardInfo> batteryCardList = new LinkedList<>();
    List<CardInfo> priorityCardList = new LinkedList<>();

    int screenState = 0;                                                    // Used to capture back button and move between screens.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(SplashScreen.myPrefs, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        boolean newbieSplashShown = prefs.getBoolean("newbieSplashShown", false);

        specificPhones = new LinkedList<>();

        screenState = 0;

        // General list of interests the user might have.
        cardInfoList.add(new CardInfo("Multimedia", "I use my phone for music and videos.", R.drawable.ic_headset_black_24dp));
        cardInfoList.add(new CardInfo("Camera", "My phone is my camera and my camera is how I show off to the world.", R.drawable.ic_camera_black_24dp));
        cardInfoList.add(new CardInfo("Phone", "It's a phone, damn it. I use it to make calls and text.", R.drawable.ic_call_black_24dp));
        cardInfoList.add(new CardInfo("Fitness", "I need a device that I can use as proof to show off how healthy I am.", R.drawable.ic_directions_run_black_24dp));
        cardInfoList.add(new CardInfo("Productivity", "Something that can help me earn more money for Mr. Man... and me.", R.drawable.ic_attach_money_black_24dp));

        // Populate each of the lists of cards
        CardInfo musicCard = new CardInfo("Music", "Music is my passion. I need it. WARNING: AUDIOPHILES ONLY", R.drawable.ic_audiotrack_black_24dp);
        CardInfo videoCard = new CardInfo("Videos", "I need something to keep me entertained on my travels.", R.drawable.ic_play_circle_filled_black_24dp);
        CardInfo toyCard = new CardInfo("Instagram/Facebook", "I like to keep updated on my friends.", R.drawable.ic_toys_black_24dp);
        CardInfo gameCard = new CardInfo("Gaming", "CandyCrush is my shit! I mean... Clash of Clans? Fuck you, don't judge me.", R.drawable.ic_gamepad_black_24dp);
        multimediaCardList.add(musicCard); multimediaCardList.add(videoCard); multimediaCardList.add(toyCard); multimediaCardList.add(gameCard);

        CardInfo photographyCard = new CardInfo("Photography", "Pictures of food, scenery, people, and food.", R.drawable.ic_camera_alt_black_24dp);
        CardInfo videographyCard = new CardInfo("Videography", "Videos of dogs, cats, parties, and more dogs", R.drawable.ic_videocam_black_24dp);
        CardInfo sharingCard = new CardInfo("Sharing", "Snapchat and Facebook are about all I use my camera for", R.drawable.ic_share_black_24dp);
        cameraCardList.add(photographyCard); cameraCardList.add(videographyCard); cameraCardList.add(sharingCard);

        CardInfo callCard = new CardInfo("Calls", "I make a lot of calls and the quality needs to be crystal", R.drawable.ic_hearing_black_24dp);
        CardInfo normalCard = new CardInfo("Normal stuff", "If it can text, make calls, and stuff, it will be fine", R.drawable.ic_perm_phone_msg_black_24dp);
        phoneCardList.add(callCard); phoneCardList.add(normalCard);

        // Not really an interest, but I reuse this method because it's easier than re-writing the exact same thing.
        CardInfo sizeCard = new CardInfo("Screen Size", "Some people like it big... others like it small. I won't judge you.", R.drawable.ic_settings_overscan_black_24dp);
        CardInfo displaycard = new CardInfo("Display Quality", "I just want it to look pretty!", R.drawable.ic_settings_brightness_black_24dp);
        CardInfo pixelCard = new CardInfo("Pixel Density", "I want the pixels so dense that it creates a black hole... or at least a neutron star.", R.drawable.ic_visibility_black_24dp);
        displayCardList.add(sizeCard); displayCardList.add(displaycard); displayCardList.add(pixelCard);

        CardInfo ruggedCard = new CardInfo("Rugged", "I don't want some delicate device, I need something that can follow me into Mordor!", R.drawable.ic_terrain_black_24dp);
        CardInfo trackingCard = new CardInfo("Tracking", "I want a phone that stalks me so closely that it knows exactly how many steps it took for me to get to the bathroom", R.drawable.ic_gps_fixed_black_24dp);
        fitnessCardList.add(ruggedCard); fitnessCardList.add(trackingCard);

        CardInfo stylusCard = new CardInfo("Stylus", "I need to be able to write and draw with my phone like it was a notepad. A very expensive notepad.", R.drawable.ic_mode_edit_black_24dp);
        CardInfo enterpriseCard = new CardInfo("Enterprise", "I must assimilate with the cube and download its data.", R.drawable.ic_email_black_24dp);
        CardInfo multiCard = new CardInfo("Multitasking", "Do one thing. While doing another. So I can do something while doing something... or something", R.drawable.ic_call_split_black_24dp);
        productivityCardList.add(stylusCard); productivityCardList.add(enterpriseCard); productivityCardList.add(multiCard);

        CardInfo capacityCard = new CardInfo("Capacity", "Bigger is aaalllllwwwaaayyyyyssss better... right?", 0);
        CardInfo enduranceCard = new CardInfo("Endurance Rating", "Size doesn't always matter. It's all about how long you can last.", 0);
        batteryCardList.add(capacityCard); batteryCardList.add(enduranceCard);

        CardInfo battery = new CardInfo("Battery", "This phone can never die... ever.", R.drawable.ic_battery_charging_full_black_24dp, false);
        CardInfo display = new CardInfo("Display", "If I'm looking at the screen more often than I look at the world, I want it to be prettier than the rest of the world.", R.drawable.ic_smartphone_black_24dp, false);
        CardInfo speakers = new CardInfo("Speakers", "I give the people on public transportation the opportunity to hear what real music sounds like.", R.drawable.ic_speaker_phone_black_24dp, false);
        priorityCardList.add(battery); priorityCardList.add(display); priorityCardList.add(speakers);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbie);

        Button nextButton = (Button) findViewById(R.id.newbie_slide_1_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("newbieSplashShown", true);
                editor.commit();
                FirstScreen();
            }
        });

        if (newbieSplashShown) {
            FirstScreen();
        }
    }

    public void FirstScreen() {
        // Loads a list of cards that show what the user is looking for within each interest. Could probably recycle one of the other views instead of redrawing here.
        screenState = 1;
        setContentView(R.layout.newbie_first_screen);
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.first_screen_ll);
        TextView tv = (TextView) findViewById(R.id.first_screen_tv);
        tv.setText("What do you use your phone for?");

        RecyclerView recView = (RecyclerView) findViewById(R.id.cardList);
        recView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(llm);

        CardAdapter ca = new CardAdapter(cardInfoList);
        recView.setAdapter(ca);
        ca.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                InterestScreen(cardInfoList.get(position).getTitle());
            }
        });

        FloatingActionButton nextScreenFAB = (FloatingActionButton) findViewById(R.id.next_screen_fab);
        nextScreenFAB.attachToRecyclerView(recView);
        nextScreenFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PriorityScreen();
            }
        });

        if (!inputSpecMap.isEmpty() || !specificPhones.isEmpty()) {
            // Only allow progress to next screen with at least one spec added.
            nextScreenFAB.setVisibility(View.VISIBLE);
            tv.setText("Anything else?");
        } else {
            nextScreenFAB.setVisibility(View.GONE);
            tv.setText("What do you use your phone for?");
        }

    }

    public void InterestScreen(String interest) {
        // Pretty much the same as "FirstScreen()" method. Just named differently. Should probably be combined to a single method.
        screenState = 2;

        tempSpecificPhones = new LinkedList<String>();

        int display = 0;
        if (interest.equals("Display")) {
            display = 1;
            screenState = 4;
        }
        final int isDisplay = display;
        setContentView(R.layout.newbie_first_screen);
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.first_screen_ll);
        TextView tv = (TextView) findViewById(R.id.first_screen_tv);
        RecyclerView recView = (RecyclerView) findViewById(R.id.cardList);
        recView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(llm);

        FloatingActionButton nextScreenFAB = (FloatingActionButton) findViewById(R.id.next_screen_fab);
        nextScreenFAB.hide();

        tv.setText("Let's get into more specifics.");
        final List<CardInfo> interestCardList = getInterests(interest);
        final CardAdapter ca = new CardAdapter(interestCardList);
        recView.setAdapter(ca);
        ca.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                List<UserReq> userReqList = new LinkedList<>();
                String title = interestCardList.get(position).getTitle();
                if (!interestCardList.get(position).getClicked()) {
                    interestCardList.get(position).setClicked(true);
                }

                if (title.equals("Music")) {
                    // inputNumSpecMap.put("hardwarebuilt_in_storeage", new UserNumReq("hardware", "built_in_storage", (double) 64, (double) 128, "GB"));
                    // inputCatSpecMap.put("multimediaspeakers", new UserCatReq("multimedia", "speakers", Arrays.asList("Stereo")));
                    // inputCatSpecMap.put("multimediaradio", new UserCatReq("multimedia", "radio", Arrays.asList("FM")));

                    tempSpecificPhones.add("Apple_iPhone_6"); tempSpecificPhones.add("Apple_iPhone_6_Plus"); tempSpecificPhones.add("Apple_iPhone_5s"); tempSpecificPhones.add("HTC_One");
                    tempSpecificPhones.add("HTC_One_(M8)"); tempSpecificPhones.add("HTC_One_M9"); tempSpecificPhones.add("HTC_One_max"); tempSpecificPhones.add("Samsung_Galaxy_Alpha");
                    tempSpecificPhones.add("Samsung_Galaxy_S5"); tempSpecificPhones.add("Samsung_Galaxy_S6"); tempSpecificPhones.add("Samsung_Galaxy_S6_edge"); tempSpecificPhones.add("Samsung_Galaxy_Note_3");
                    tempSpecificPhones.add("Samsung_Galaxy_Note_3_Neo"); tempSpecificPhones.add("Samsung_Galaxy_Note_4"); tempSpecificPhones.add("Samsung_Galaxy_Note_Edge"); tempSpecificPhones.add("Samsung_Galaxy_S5_Active");
                    tempSpecificPhones.add("Sony_Xperia_Z2"); tempSpecificPhones.add("Sony_Xperia_Z3"); tempSpecificPhones.add("Sony_Xperia_Z3_Compact"); tempSpecificPhones.add("Sony_Xperia_Z4");
                    tempSpecificPhones.add("Apple_iPhone_5"); tempSpecificPhones.add("Apple_iPhone_5c"); tempSpecificPhones.add("LG_G2"); tempSpecificPhones.add("LG_G3"); tempSpecificPhones.add("LG_G4");
                    tempSpecificPhones.add("Nokia_Lumia_520"); tempSpecificPhones.add("Nokia_Lumia_1520"); tempSpecificPhones.add("Nokia_Lumia_1020"); tempSpecificPhones.add("Motorola_Moto_X");
                    tempSpecificPhones.add("Motorola_Moto_G"); tempSpecificPhones.add("Google_Nexus_5"); tempSpecificPhones.add("Samsung_Galaxy_S6_Active");
                } else if (title.equals("Videos")) {
                    userReqList.add(new UserReq("display", "Pixel_density", (double) 400, (double) 700, "ppi")); userReqList.add(new UserReq("display", "Technology", Arrays.asList("IPS", "AMOLED"), "or"));
                    userReqList.add(new UserReq("hardware", "Storage_expansion", Arrays.asList("micro"), "and"));

                } else if (title.equals("Instagram/Facebook")) {
                    userReqList.add(new UserReq("display", "Physical_size", (double) 0, (double) 6.2, "inches"));

                } else if (title.equals("Gaming")) {
                    userReqList.add(new UserReq("hardware", "System_chip", Arrays.asList("Exynos 5", "Exynos 6", "Exynos 7", "Exynos Octa 7", "Snapdragon 800",
                            "Snapdragon 801", "Snapdragon 805", "Snapdragon 808", "Snapdragon 810", "Snapdragon 620", "Snapdragon 820", "Apple A7", "Apple A8"), "or"));
                    userReqList.add(new UserReq("display", "Physical_size", (double) 4.6, (double) 6.2, "inches"));
                    userReqList.add(new UserReq("display", "Pixel_density", (double) 325, (double) 1000, "ppi"));
                    userReqList.add(new UserReq("hardware", "System_memory", (double) 2048, (double) 9001, "MB RAM"));
                    tempSpecificPhones.add("Apple_iPhone_6"); tempSpecificPhones.add("Apple_iPhone_6_Plus"); tempSpecificPhones.add("Apple_iPhone_5s");

                } else if (title.equals("Photography")) {
                    userReqList.add(new UserReq("camera", "Camera", (double) 12, (double) 9001, "megapixels"));
                    userReqList.add(new UserReq("camera", "Aperture_size", (double) 1, (double) 2.4, "F"));

                    // inputNumSpecMap.put("cameracamera", new UserNumReq("camera", "Camera", (double) 12, (double) 9001, "megapixels"));
                    // inputNumSpecMap.put("cameraaperture_size", new UserNumReq("camera", "Aperture_size", (double) 1, (double) 2.2, "F"));
                    // inputCatSpecMap.put("camerasettings", new UserCatReq("camera", "Settings", Arrays.asList("Exposure", "ISO"), "or"));
                    tempSpecificPhones.add("Apple_iPhone_6"); tempSpecificPhones.add("Apple_iPhone_6_Plus"); tempSpecificPhones.add("Apple_iPhone_5s");

                } else if (title.equals("Videography")) {
                    userReqList.add(new UserReq("camera", "Camcorder", "4K", Arrays.asList("4K", "60 fps", "120 fps")));
                    userReqList.add(new UserReq("camera", "CC_Features", Arrays.asList("stabilization"), "and"));
                    // inputNumSpecMap.put("cameracamcorder", new UserNumReq("camera", "Camcorder", "4K", Arrays.asList("4K", "60 fps", "120 fps")));
                    // inputCatSpecMap.put("cameracc_features", new UserCatReq("camera", "CC_Features", Arrays.asList("stabilization"), "and"));
                    tempSpecificPhones.add("Apple_iPhone_6"); tempSpecificPhones.add("Apple_iPhone_6_Plus"); tempSpecificPhones.add("Apple_iPhone_5s");

                } else if (title.equals("Sharing")) {
                    userReqList.add(new UserReq("camera", "Camera", (double) 8, (double) 20, "megapixels"));
                    userReqList.add(new UserReq("camera", "Front_facing_camera", (double) 2, (double) 9001, "megapixels"));
                    userReqList.add(new UserReq("camera", "Shooting_modes", Arrays.asList("HDR"), "and"));

                    // inputNumSpecMap.put("cameracamera", new UserNumReq("camera", "Camera", (double) 8, (double) 20, "megapixels"));
                    // inputNumSpecMap.put("camerafront_facing_camera", new UserNumReq("camera", "Front_facing_camera", (double) 2, (double) 9001, "megapixels"));
                    // inputCatSpecMap.put("camerashooting_modes", new UserCatReq("camera", "Shooting_modes", Arrays.asList("HDR"), "and"));
                    tempSpecificPhones.add("Apple_iPhone_5s"); tempSpecificPhones.add("Apple_iPhone_5"); tempSpecificPhones.add("Apple_iPhone_5c");

                } else if (title.equals("Calls")) {
                    userReqList.add(new UserReq("technology", "VoLTE", Arrays.asList("Yes"), "and"));
                    // inputCatSpecMap.put("technologyvolte", new UserCatReq("technology", "VoLTE", Arrays.asList("Yes"), "and"));

                } else if (title.equals("Normal stuff")) {
                    userReqList.add(new UserReq("display", "Physical_size", (double) 3.5, (double) 9001, "inches"));
                    // inputNumSpecMap.put("displayphysical_size", new UserNumReq("display", "Physical_size", (double) 3.5, (double) 9001, "inches"));

                } else if (title.equals("Screen Size")) {
                    inputNumericalSpec("display", "Physical_size");

                } else if (title.equals("Display Quality")) {
                    userReqList.add(new UserReq("display", "Technology", Arrays.asList("IPS", "AMOLED"), "or"));
                    // inputCatSpecMap.put("displaytechnology", new UserCatReq("display", "Technology", Arrays.asList("IPS", "AMOLED"), "or"));

                } else if (title.equals("Pixel Density")) {
                    userReqList.add(new UserReq("display", "Pixel_density", (double) 325, (double) 9001, "PPI"));
                    // inputNumSpecMap.put("displaypixel_density", new UserNumReq("display", "Pixel_density", (double) 325, (double) 9001, "PPI"));

                } else if (title.equals("Rugged")) {
                    userReqList.add(new UserReq("design", "Rugged", Arrays.asList("Water", "Dust", "Splash", "Shock"), "or"));
                    // inputCatSpecMap.put("designrugged", new UserCatReq("design", "Rugged", Arrays.asList("Water", "Dust", "Splash", "Shock"), "or"));

                } else if (title.equals("Tracking")) {
                    userReqList.add(new UserReq("other_features", "Sensors", Arrays.asList("Pedometer", "Step"), "or"));
                    userReqList.add(new UserReq("technology", "Positioning", Arrays.asList("GPS, Glonass"), "and"));

                    // inputCatSpecMap.put("other_featuressensors", new UserCatReq("other_features", "Sensors", Arrays.asList("Pedometer", "Step"), "or"));
                    // inputCatSpecMap.put("technologypositioning", new UserCatReq("technology", "Positioning", Arrays.asList("GPS, Glonass"), "and"));
                    tempSpecificPhones.add("Apple_iPhone_5"); tempSpecificPhones.add("Apple_iPhone_5s"); tempSpecificPhones.add("Apple_iPhone_6");
                    tempSpecificPhones.add("Apple_iPhone_6_Plus"); tempSpecificPhones.add("Samsung_Galaxy_S5"); tempSpecificPhones.add("Samsung_Galaxy_S6");
                    tempSpecificPhones.add("HTC_One"); tempSpecificPhones.add("HTC_One_(M8)"); tempSpecificPhones.add("HTC_One_M9");
                    tempSpecificPhones.add("Samsung_Galaxy_S4"); tempSpecificPhones.add("Google_Nexus_5"); tempSpecificPhones.add("Google_Nexus_6");
                    tempSpecificPhones.add("Samsung Galaxy_Note_3"); tempSpecificPhones.add("Samsung_Galaxy_Note_4"); tempSpecificPhones.add("Samsung_Galaxy_Note_Edge");
                    tempSpecificPhones.add("Samsung_Galaxy_S5_Active"); tempSpecificPhones.add("Samsung_Galaxy_S6_Active"); tempSpecificPhones.add("Samsung_Galaxy_S6_Edge");
                    tempSpecificPhones.add("Samsung_Galaxy_Alpha"); tempSpecificPhones.add("Sony_Xperia_Z1"); tempSpecificPhones.add("Sony_Xperia_Z1_Compact");
                    tempSpecificPhones.add("Sony_Xperia_Z2"); tempSpecificPhones.add("Sony_Xperia_Z3"); tempSpecificPhones.add("Sony_Xperia_Z3_Compact");
                    tempSpecificPhones.add("LG_G2"); tempSpecificPhones.add("LG_G3"); tempSpecificPhones.add("LG_G4");
                    tempSpecificPhones.add("Motorola_Moto_X"); tempSpecificPhones.add("Motorola_Moto_X_(2014"); tempSpecificPhones.add("LG_G2");
                    tempSpecificPhones.add("Sony_Xperia_Z3+"); tempSpecificPhones.add("Sony_Xperia_Z4v");

                }  else if (title.equals("Stylus")) {
                    tempSpecificPhones.add("Samsung_Galaxy_Note"); tempSpecificPhones.add("Samsung_Galaxy_Note_2"); tempSpecificPhones.add("Samsung_Galaxy_Note_3");
                    tempSpecificPhones.add("Samsung_Galaxy_Note_4"); tempSpecificPhones.add("Samsung_Galaxy_Note_Edge");

                } else if (title.equals("Multitasking")) {
                    tempSpecificPhones.add("Samsung_Galaxy_Note"); tempSpecificPhones.add("Samsung_Galaxy_Note_2"); tempSpecificPhones.add("Samsung_Galaxy_Note_3");
                    tempSpecificPhones.add("Samsung_Galaxy_Note_4"); tempSpecificPhones.add("Samsung_Galaxy_Note_Edge"); tempSpecificPhones.add("LG_G_Flex");
                    tempSpecificPhones.add("LG_G_Flex_2"); tempSpecificPhones.add("Apple_iPhone_6_Plus");

                } else if (title.equals("Enterprise")) {
                    tempSpecificPhones.add("Apple_iPhones_4"); tempSpecificPhones.add("Apple_iPhones_5"); tempSpecificPhones.add("Apple_iPhones_5s");
                    tempSpecificPhones.add("Apple_iPhones_6"); tempSpecificPhones.add("Apple_iPhones_6_Plus"); tempSpecificPhones.add("Apple_iPhones_4s");
                    tempSpecificPhones.add("Samsung_Galaxy_S4"); tempSpecificPhones.add("Samsung_Galaxy_S5"); tempSpecificPhones.add("Samsung_Galaxy_S6");
                    tempSpecificPhones.add("Samsung_Galaxy_S5_Active"); tempSpecificPhones.add("Samsung_Galaxy_S6_Edge"); tempSpecificPhones.add("Samsung_Galaxy_S6_Active");
                    tempSpecificPhones.add("Samsung_Galaxy_Note"); tempSpecificPhones.add("Samsung_Galaxy_Note_2"); tempSpecificPhones.add("Samsung_Galaxy_Note_3");
                    tempSpecificPhones.add("Samsung_Galaxy_Note_4"); tempSpecificPhones.add("Samsung_Galaxy_Note_Edge"); tempSpecificPhones.add("Samsung_Galaxy_Note_3_Neo");
                }

                if (!userReqList.isEmpty()) {
                    // Add each individual list as a new specGroup to ensure each specification is followed.
                    for (UserReq userReq : userReqList) {
                        inputSpecMap.put(specGroup, new LinkedList<UserReq>(Arrays.asList(userReq)));
                        specGroup++;
                    }
                }

                ca.notifyDataSetChanged();

                if (specificPhones.isEmpty()) {
                    specificPhones = new LinkedList<String>(tempSpecificPhones);
                    Log.d("NewbieActivity", "Temp > Specific:" + specificPhones.toString());
                } else if (!tempSpecificPhones.isEmpty()){
                    specificPhones.retainAll(tempSpecificPhones);
                    Log.d("NewbieActivity", "Retaining phones: " + specificPhones.toString());
                }
                if (isDisplay == 1 && !title.equals("Screen Size")) {
                    PriorityScreen();
                } else if (title.equals("Screen Size")) {

                } else {
                    FirstScreen();
                }

            }

        });

    }

    public void PriorityScreen() {
        // Same as InterestScreen & FirstScreen().
        screenState = 3;

        setContentView(R.layout.newbie_first_screen);
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.first_screen_ll);
        TextView tv = (TextView) findViewById(R.id.first_screen_tv);
        RecyclerView recView = (RecyclerView) findViewById(R.id.cardList);
        recView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(llm);

        tv.setText("Let's get into more specifics.");

        final CardAdapter ca = new CardAdapter(priorityCardList);

        FloatingActionButton nextScreenFAB = (FloatingActionButton) findViewById(R.id.next_screen_fab);
        nextScreenFAB.attachToRecyclerView(recView);
        nextScreenFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (List<UserReq> userReqList1 : inputSpecMap.values()) {
                    for (UserReq userReq : userReqList1) {
                        Log.i("Newbie.PriorityScreen", "Requirement: " + userReq.getSpec());
                    }
                }
                Intent intent = new Intent(NewbieActivity.this, KnownActivity.class);
                intent.putExtra("activity", "interest");
                intent.putStringArrayListExtra("phoneList", new ArrayList<String>(db.getCandidatePhones(inputSpecMap, specificPhones)));
                startActivity(intent);
                finish();
            }
        });

        recView.setAdapter(ca);

        ca.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                // Add custom specs or phones based on whatever is selected.
                String title = priorityCardList.get(position).getTitle();
                if (!title.equals("Display")) {
                    if (!priorityCardList.get(position).getClicked()) {
                        priorityCardList.get(position).setClicked(true);
                    }
                }
                if (title.equals("Battery")) {
                    List<UserReq> userReqList = new LinkedList<UserReq>();
                    userReqList.add(new UserReq("battery", "Capacity", (double) 3000, (double) 9001, "mAh"));
                    userReqList.add(new UserReq("battery", "Endurance_Rating", (double) 60, (double) 9001, "hours"));
                    inputSpecMap.put(specGroup, userReqList);
                    specGroup++;
                    // inputNumSpecMap.put("batterycapacity", new UserNumReq("battery", "Capacity", (double) 3000, (double) 9001, "mAh"));
                } else if (title.equals("Display")) {
                    InterestScreen("Display");
                } else if (title.equals("Speakers")) {
                    tempSpecificPhones = new LinkedList<String>();
                    tempSpecificPhones.add("Apple_iPhone_6"); tempSpecificPhones.add("Apple_iPhone_6_Plus"); tempSpecificPhones.add("Apple_iPhone_5s"); tempSpecificPhones.add("HTC_One");
                    tempSpecificPhones.add("HTC_One_(M8)"); tempSpecificPhones.add("HTC_One_M9"); tempSpecificPhones.add("HTC_One_max"); tempSpecificPhones.add("Samsung_Galaxy_Alpha");
                    tempSpecificPhones.add("Samsung_Galaxy_S5"); tempSpecificPhones.add("Samsung_Galaxy_S6"); tempSpecificPhones.add("Samsung_Galaxy_S6_edge"); tempSpecificPhones.add("Samsung_Galaxy_Note_3");
                    tempSpecificPhones.add("Samsung_Galaxy_Note_3_Neo"); tempSpecificPhones.add("Samsung_Galaxy_Note_4"); tempSpecificPhones.add("Samsung_Galaxy_Note_Edge"); tempSpecificPhones.add("Samsung_Galaxy_S5_Active");
                    tempSpecificPhones.add("Sony_Xperia_Z2"); tempSpecificPhones.add("Sony_Xperia_Z3"); tempSpecificPhones.add("Sony_Xperia_Z3_Compact"); tempSpecificPhones.add("Sony_Xperia_Z4");
                    tempSpecificPhones.add("Apple_iPhone_5"); tempSpecificPhones.add("Apple_iPhone_5c"); tempSpecificPhones.add("LG_G2"); tempSpecificPhones.add("LG_G3"); tempSpecificPhones.add("LG_G4");
                    tempSpecificPhones.add("Nokia_Lumia_520"); tempSpecificPhones.add("Nokia_Lumia_1520"); tempSpecificPhones.add("Nokia_Lumia_1020"); tempSpecificPhones.add("Motorola_Moto_X");
                    tempSpecificPhones.add("Motorola_Moto_G"); tempSpecificPhones.add("Google_Nexus_5"); tempSpecificPhones.add("HTC_Desire_826"); tempSpecificPhones.add("HTC_Desire_820");
                    tempSpecificPhones.add("HTC_Desire_816"); tempSpecificPhones.add("HTC_One_(E8)");
                }

                if (specificPhones.isEmpty()) {
                    specificPhones = new LinkedList<String>(tempSpecificPhones);
                    Log.d("NewbieActivity", "Temp > Specific:" + specificPhones.toString());
                } else if (!tempSpecificPhones.isEmpty()) {
                    specificPhones.retainAll(tempSpecificPhones);
                    Log.d("NewbieActivity", "Retaining phones: " + specificPhones.toString());
                }
                ca.notifyDataSetChanged();
            }
        });
    }


    private List<CardInfo> getInterests(String interest) {
        List<CardInfo> interestCardList = new LinkedList<CardInfo>();
        // Loads sub-categories.
        if (interest.equals("Multimedia")) {
            return multimediaCardList;
        }  else if (interest.equals("Camera")) {
            return cameraCardList;
        } else if (interest.equals("Phone")) {
            return phoneCardList;
        } else if (interest.equals("Display")) {
            // Not really an interest, but I reuse this method because it's easier than re-writing the exact same thing.
            return displayCardList;
        } else if (interest.equals("Fitness")) {
            return fitnessCardList;
        } else if (interest.equals("Productivity")) {
            return productivityCardList;
        }
        return interestCardList;
    }

    private void inputNumericalSpec(final String category, final String spec) {
        // Layout that allows the user to select numerical specs - either with or without a maximum.
        setContentView(R.layout.input_spec);
        com.melnykov.fab.FloatingActionButton addSpecButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.confirm_spec_button);
        com.melnykov.fab.FloatingActionButton linkSpecButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.link_spec_button);
        linkSpecButton.setVisibility(View.GONE);

        NumericalSpec ns = db.getMinMax(category, spec);

        final String unit = ns.getUnit();

        final Integer min = ns.getMin();
        final Integer max = ns.getMax();
        final Integer magnitude = ns.getMagnitude();                                                // Magnitude aka power of ten that the original value was in before it was converted to an integer for the seekBar.

        final Map<Integer, Integer> choiceMap = ns.getChoiceMap();                                  // An ordered HashMap of choices for easy selection using seekBar becase the seekBar can only return
        // integer values which can be used to get the actual value in the choiceMap.
        final Map<String, List<String>> resolutions = ns.getResolutions();
        final List<String> resolutionKeyList = new ArrayList(resolutions.keySet());                 // keySet holds the name of the standard resolution.

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
                        minTv.setText("Minimum: " + Double.toString(minInput) + " " + unit);

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
                    maxTv.setText("Maximum: " + Double.toString(maxInput) + " " + unit);
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
                Log.d("Specs.AddSpec", "Add spec button clicked!");
                Log.d("Specs.AddSpec", "Min: " + minInput + " | Max: " + maxInput);
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
                        List<Integer> choiceList = new LinkedList<Integer>();
                        for (int i = minInput.intValue(); i < choiceMap.size(); i++) {
                            // Add all selected values and higher to the list by iterating through the choiceMap.
                            choiceList.add(choiceMap.get(i));
                        }
                        userReq = new UserReq(category, spec, choiceList, unit);
                    }

                    Map<Integer, List<UserReq>> specMapCopy = new LinkedHashMap<>(inputSpecMap);
                    for (int specGroup1 : specMapCopy.keySet()) {
                        List<UserReq> reqSpecList = inputSpecMap.get(specGroup1);
                        for (UserReq userReq1 : reqSpecList) {
                            if (userReq1.getSpec().equals("Physical_size")) {
                                reqSpecList.remove(userReq1);

                            }
                        }
                        if (reqSpecList.isEmpty()) {
                            inputSpecMap.remove(specGroup1);
                            continue;
                        }
                    }

                    inputSpecMap.put(specGroup, new ArrayList<UserReq>(Arrays.asList(userReq)));
                    specGroup++;

                    PriorityScreen();
                }
            }
        });

    }

    /*
    * Useful for changing screens within the activity.
    * screenState 1 = Screen showing a list of all activities the user might use the phone for.
    * screenState 2 = The subcategories of each activity (e.g. Fitness has Ruggedness & Tracking).
    * screenState 3 = List of priorities for physical attributes of the phones.
    * screenState 4 = Display Specs is the only priority that has subcategories so is given an individual screenState.
     */
    @Override
    public void onBackPressed() {
        if (screenState == 1) {
            finish();
        } else if (screenState == 2 || screenState == 3) {
            FirstScreen();
        } else if (screenState == 4) {
            PriorityScreen();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_newbie, menu);
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
