package com.android.projectphone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.projectphone.data.PhoneDbHelperTest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    static ArrayList<String> phoneArray = new ArrayList<String>();
    static List<String> phoneURLArray = new LinkedList<String>();
    static ArrayList<String> phoneImageURLArray = new ArrayList<String>();
    public static List<String> sectionTitles = new ArrayList<String>();
    static ArrayList<String> specList = new ArrayList<String>();
    static HashMap<String, List> map = new HashMap<String, List>();
    // static HashMap<String, List> phoneSpecMap = new HashMap<String, List>();
    static HashMap<String, String> phoneURLMap = new HashMap<String, String>();
    static HashMap<String, ArrayList> phoneImageURLMap = new HashMap<>();
    static Map<String, List<String>> phoneSpecListMap = new LinkedHashMap<>();
    static List<String> allPhones = null;
    static LruCache<String, Bitmap> memCache;
    final static Object diskCacheLock = new Object();
    final static String DISK_CACHE_SUBDIR = "thumbnails";

    static long downloadStartTime;
    static long downloadEndTime;

    private Button interest_btn;
    private Button known_btn;
    private Button specs_btn;

    private ProgressBar progress;

    private static Context mContext;
    private List<String> newPhoneList = new LinkedList<String>();
    private String DB_PATH;
    // private String DB_PATH = "/data/data/com.android.Phone4Me/databases/";
    private String DB_NAME = "phonedb.db";

    boolean downloadCheck = true;

    private CardInfo interestCard = new CardInfo("Interest", "Help me find a device based on how to I use my device.", 0);
    private CardInfo knownCard = new CardInfo("Search", "I know what phone I'm looking for... I think.", 0);
    private CardInfo specCard = new CardInfo("Requirements", "I have a certain set of requirements that I need fulfilled.", 0);
    private List<CardInfo> mainCardList = new LinkedList<>();


    PhoneDbHelperTest db = new PhoneDbHelperTest(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debug", "MainCreated");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.mContext = getApplicationContext();

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);      // Get total memory in kilobyes.
        final int cacheSize = maxMemory / 8;                                        // Set cache to 1/8th total memory.
        memCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // Return size of cache in kilobytes instead of number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        mainCardList.add(interestCard); mainCardList.add(knownCard); mainCardList.add(specCard);
        // db.createDatabase(getApplication());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        CardAdapter cardAdapter = new CardAdapter(mainCardList);
        cardAdapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                Intent intent = null;
                if (position == 0) {
                    intent = new Intent(MainActivity.this, NewbieActivity.class);
                } else if (position == 1) {
                    intent = new Intent(MainActivity.this, KnownActivity.class);
                    intent.putExtra("search", "search");
                } else if (position == 2) {
                    intent = new Intent(MainActivity.this, SpecsActivity.class);
                }
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(cardAdapter);
        // db.returnPhones(getApplication(), db.returnSectionTitles());
        // new PhoneDownload().execute();
    }

    protected void onStart() {
        Log.d("debug", "MainStart");
        super.onStart();
    }

    public static Context getAppContext() {
        return mContext;
    }

    private void phoneSpecDownload(List<String> newPhoneList) {
        long downloadStartTime = System.nanoTime();
        progress.setVisibility(View.VISIBLE);
        List<String> tempList = new LinkedList<>();
        // tempList.add(newPhoneList.get(0));
        Log.d("Main.phoneSpecDownload", "Download specs for " + newPhoneList.size() + " phones!");
        if (!newPhoneList.isEmpty()) {
            new SpecDownload().execute(newPhoneList);
        }

    }

    public void onClick(View v) {
        Intent intent = null;
       if (v.getId() == interest_btn.getId()) {
            intent = new Intent(this, NewbieActivity.class);
        }
        if (v.getId() == known_btn.getId()) {
            intent = new Intent(this, KnownActivity.class);
            intent.putExtra("search", "search");
        }
        if (v.getId() == specs_btn.getId()) {
            intent = new Intent(this, SpecsActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private class PhoneDownload extends AsyncTask<Void,Void,Void> {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // List<String> newPhones = new LinkedList<String>();


        @Override
        protected Void doInBackground(Void...arg0) {

            // Download the section titles of the specs and save it to an ArrayList to be used to generate the Tables of the SQLite DB

            try {
                for (int p = 1; p < 4; p++) {

                    Document doc = Jsoup.connect("http://www.phonearena.com/phones/sort/popular/page/" + p + ".html").get();
                    Log.d("MainActivity.Download", "Connected to page " + p + "!");

                    String phoneURL = null;                                                                         // Holds the URL that selecting the phone will direct to
                    String phone = null;                                                                            // Holds the name of the phone
                    for (Element popularPhone : doc.select("div.s_block_4")) {
                        // Loop to select each phone, it's URL, and the thumbnail URL to be used as the button's background

                        phone = popularPhone.select("h3 a[href]").first().text().replace(" ", "_");                 // Selects the phone's candidate title text
                        String phoneImageURL = popularPhone.select("img").attr("src");                              // Selects the thumbnail URL
                        phoneImageURL = "http:" + phoneImageURL;
                        // Log.d("MainActivity.Download", "Phone: " + phone);
                        // Log.d("MainActivity.Download", "Img URL: " + phoneImageURL);

                        ArrayList<String> tempArray = new ArrayList<String>();
                        tempArray.add(phoneImageURL);
                        phoneImageURLMap.put(phone + " availability " + "image_url", tempArray);

                        phoneURL = popularPhone.select("a.s_thumb").first().attr("abs:href"); // Selects the phone's specification URL // Re-instate when connecting to actual website
                        phoneURLArray.add(phoneURL);
                        // Log.d("MainActivity.Download", "phoneURL: " + phoneURL);

                        phoneURLMap.put(phone, phoneURL); // Add URLs to an ArrayList to be accessed when creating the buttons
                        newPhoneList.add(phone); // Add phone title's to Arraylist
                    }
                }
                allPhones = new LinkedList<String>(newPhoneList);
                List<String> existingPhones = new LinkedList<>(db.returnPhones(getApplication(), db.returnSectionTitles(getApplication())));
                List<String> deprecatedPhones = new LinkedList<String>(existingPhones);
                deprecatedPhones.removeAll(newPhoneList);
                allPhones.addAll(deprecatedPhones);

                newPhoneList.removeAll(existingPhones);
                Log.d("Main.phoneDownload", "Existing phones: " + existingPhones.toString());
                Log.d("Main.phoneDownload", "Deprecated phones: " + deprecatedPhones.toString());
                Log.d("Main.phoneDownload", "New phones: " + newPhoneList.toString());
                if (newPhoneList.isEmpty()){
                    Log.d("Main.SpecDownload", "No new phones! Spec Download skipped.");
                    downloadCheck = false;
                    return null;
                }
                else {
                    Log.d("Main.SpecDownload", "New phones added! Downloading phone specs.");
                    /* for (String newPhone : newPhones) {
                        tempPhoneArray.add(newPhone.toString());
                    } */
                }


            } catch (IOException ioe) {
                Log.d("Main.SpecsDownload", "IOException!");
                ioe.printStackTrace();
            }
            InputStream urlStream = null;
            // int i = 0;

            String phone = newPhoneList.get(0);
            try {
                Document doc = Jsoup.connect(phoneURLMap.get(phone)).get();
                if (db.returnPhones(getApplication(), sectionTitles).size() == 0) {
                    // Download a list of sections to be used as individual tables
                    for (Element sectionTitleE : doc.select("h2.htitle")) {
                        sectionTitles.add(sectionTitleE.ownText().trim().toLowerCase().replace(" ", "_"));
                    }
                } else {
                    sectionTitles = db.returnSectionTitles(getApplication());

                }
                Log.d("Main.SpecsDownload", "Section titles: " + sectionTitles.toString());
                String spec = null;
                for (String sectionTitle : sectionTitles) {
                    ArrayList<String> tempSpecList = new ArrayList<String>();

                    int features = 0; // Camera section has multiple "features" columns, this renames each one to be unique

                    for (Element specE : doc.select("h2:containsOwn(" + sectionTitle.replace("_", " ") + ") ~ ul strong[class*=s_lv]")) {
                        // Downloads the list of specs for each section (to be the columns of each table)
                        spec = specE.ownText();
                        if (spec.equals("Music player:")) {
                            // Stupid spec - consider getting rid of this section altogether

                        } else if (!spec.equals("")) { // Check to make sure the spec isn't blank

                            spec = spec.replace(":", "").replace(" ", "_").replace("-", "_").replace("(", "").replace(")", "");

                            // See comment for "int features" above
                            if (sectionTitle.equals("camera")) {

                                if (spec.equals("Features")) {
                                    if (features == 0) {
                                        features++;
                                    } else if (features == 1) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("CC_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                        features++;
                                    } else if (features == 2) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("FFC_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                    }
                                }
                            } else if (sectionTitle.equals("multimedia")) {
                                if (spec.equals("Features")) {

                                    if (features == 0) {
                                        features++;
                                    } else if (features == 1) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("VP_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                        features++;
                                    } else if (features == 2) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("FFC_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                    }
                                }
                            }
                            Log.d("Main.SpecDownload", "Spec: " + spec);
                            tempSpecList.add(spec);

                            // Occasionally the spec is listed under another attribute; this gets it in that case
                        } else {
                            specE = specE.select("span[class*=s_tooltip]").first();
                            spec = specE.ownText();
                            spec = spec.replace(":", "").replace(" ", "_").replace("-", "_").replace("(", "").replace(")", "");

                            if (sectionTitle.equals("multimedia")) {
                                if (spec.equals("Features")) {
                                    if (features == 0) {
                                        features++;
                                    } else if (features == 1) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("VP_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                        features++;
                                    } else if (features == 2) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("FFC_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                    }
                                }
                            }
                            tempSpecList.add(spec);
                        }
                    }

                    String phoneSpec = null;
                    int k = 0; // To be used in the loop below to correlate the phone's spec with the list of specs

                    // Add each of the phone's specifications to an ArrayList to be added to the db
                    for (Element phoneSpecE : doc.select("h2:containsOwn(" + sectionTitle.replace("_", " ") + ") ~ ul ul[class*=s_lv] > li:not([class*=s_lv])")) {
                        List<String> tempPhoneSpecList = new LinkedList<>();
                        String phoneSpecArrayTitle;
                        /* if (sectionTitle.equals("other_features") && k == 0) {
                            tempSpecList.add("voice");
                        } else if (sectionTitle.equals("multimedia") && k == 0) {
                            tempSpecList.add("radio");
                        } else if (sectionTitle.equals("technology") && k == 0) {
                            tempSpecList.add("hd_voice");
                            tempSpecList.add("multiple_sim_cards");
                            tempSpecList.add("cdma");
                            tempSpecList.add("tdd_lte");
                            tempSpecList.add("micro_sim");
                        } else if (sectionTitle.equals("battery") && k == 0) {
                            // tempSpecList.add("not_user_replaceable");
                            // tempSpecList.add("wireless_charging");
                            tempSpecList.add("stand_by_time");
                            tempSpecList.add("stand_by_time_3g");
                            tempSpecList.add("talk_time");
                            tempSpecList.add("stand_by_time_4g");

                        } else if (sectionTitle.equals("camera") && k == 0) {
                            // tempSpecList.add("ffc_features");
                        } else if (sectionTitle.equals("design") && k == 0) {
                            tempSpecList.add("rugged");
                            tempSpecList.add("ip_certified");
                            tempSpecList.add("mil_std_810_certified");
                        } else if (sectionTitle.equals("hardware") && k == 0) {
                            tempSpecList.add("maximum_user_storage");
                            tempSpecList.add("storage_expansion");
                        } else if (sectionTitle.equals("internet_browsing") && k == 0) {
                            tempSpecList.add("browser");
                        } else if (sectionTitle.equals("connectivity") && k == 0) {
                            tempSpecList.add("hdmi");
                        } else if (sectionTitle.equals("shopping_information") && k == 0) {
                            tempSpecList.add("accessories");
                        } */

                        // phoneSpecArrayTitle = phone + " " + sectionTitle + " " + tempSpecList.get(k);
                        phoneSpecListMap.put(phone + " " + sectionTitle, tempSpecList);

                        phoneSpec = phoneSpecE.ownText();
                        // Skip empty specs
                        if (phoneSpec.equals("") || phoneSpec == null) {
                            continue;
                        }
                        tempPhoneSpecList.add(phoneSpec);

                        k++;
                    }

                    // Store each of section's worth of specifications as an individually HashMapped Arraylist to be used as the columns of the SQLite tables
                    if (map.get(sectionTitle) == null || map.get(sectionTitle).size() < tempSpecList.size()) {
                        map.put(sectionTitle, new LinkedList<String>(tempSpecList));
                    }
                }

            } catch (IOException ioe) {
                Log.d("Main.SpecsDownload", phone + " Spec List Failed to Download!");
                ioe.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {

            // db.refresh();
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(Void result) {
            if (downloadCheck) {
                // db.addPhones(getApplication(), newPhoneList, allPhones);
                Log.d("Main.Download.Post", "Phones added successfully!");
                phoneSpecDownload(newPhoneList);

                //new SpecDownload().execute(newPhones);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("INITIALIZED", true);
                editor.apply();
            } else {
                Log.d("Main.SpecDownload", "No new phones added to db!");
                progress.setVisibility(View.GONE);
            }
        }
    }

    private class SpecDownload extends AsyncTask<List<String>,Void,Void> {
        String phone = null;
        List<String> phoneList = new LinkedList<>();
        Map<String, List<String>> phoneSpecMap = new LinkedHashMap<>();
        boolean lastPhone = false;
        NumberFormat decimalFormatter = new DecimalFormat("#0.00");
        long startTime = System.nanoTime();

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(List<String>...arg) {

            phoneList = arg[0];
            phone = phoneList.get(0);
            if (phoneList.size() == 1) {
                lastPhone = true;
            }
            Log.d("Main.SpecDownload", "Downloading specs for " + phone + "!");

            InputStream urlStream = null;
            try {
                Document doc = Jsoup.connect(phoneURLMap.get(phone)).get();
                long startTime2 = System.nanoTime();
                long connectTime = System.nanoTime();
                long duration = connectTime - startTime2;
                Log.d("SpecDownload", "Time to connect " + phone + " page: " + decimalFormatter.format(duration/1000000000.) + " seconds");
                // Download a list of sections to be used as individual tables

                String spec = null;
                for (String sectionTitle : sectionTitles) {
                    ArrayList<String> tempSpecList = new ArrayList<String>();

                    int features = 0; // Camera section has multiple "features" columns, this renames each one to be unique

                    for (Element specE : doc.select("h2:containsOwn(" + sectionTitle.replace("_", " ") + ") ~ ul strong[class*=s_lv]")) {
                        // Downloads the list of specs for each section (to be the columns of each table)
                        spec = specE.ownText();
                        if (spec.equals("Music player:")) {
                            // Stupid spec - consider getting rid of this section altogether

                        } else if (!spec.equals("")) { // Check to make sure the spec isn't blank

                            spec = spec.replace(":", "").replace(" ", "_").replace("-", "_").replace("(", "").replace(")", "");

                            // See comment for "int features" above
                            if (sectionTitle.toLowerCase().equals("camera")) {

                                if (spec.equals("Features")) {
                                    Log.d("Main.SpecDownload", "Features caught!");
                                    if (features == 0) {
                                        features++;
                                    } else if (features == 1) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("CC_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                        features++;
                                    } else if (features == 2) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("FFC_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                    }
                                }
                            } else if (sectionTitle.equals("multimedia")) {
                                if (spec.equals("Features")) {
                                    if (features == 0) {
                                        features++;
                                    } else if (features == 1) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("VP_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                        features++;
                                    }
                                }
                            }
                            Log.d("Main.SpecDownload", "Adding spec: " + spec);
                            tempSpecList.add(spec);

                        // Occasionally the spec is listed under another attribute; this gets it in that case
                        } else {
                            specE = specE.select("span[class*=s_tooltip]").first();
                            spec = specE.ownText();
                            spec = spec.replace(":", "").replace(" ", "_").replace("-", "_").replace("(", "").replace(")", "");

                            if (sectionTitle.equals("multimedia")) {
                                if (spec.equals("Features")) {
                                    if (features == 0) {
                                        features++;
                                    } else if (features == 1) {
                                        StringBuilder specSB = new StringBuilder();
                                        specSB.append("VP_");
                                        specSB.append(spec);
                                        spec = specSB.toString();
                                        features++;
                                    }
                                }
                            }
                            tempSpecList.add(spec);
                        }
                    }

                    Log.d("Main.SpecDownload", "List of Specs: " + tempSpecList);
                    String phoneSpec = null;
                    int k = 0; // To be used in the loop below to correlate the phone's spec with the list of specs

                    // Add each of the phone's specifications to an ArrayList to be added to the db
                    for (Element phoneSpecE : doc.select("h2:containsOwn(" + sectionTitle.replace("_", " ") + ") ~ ul ul[class*=s_lv] > li:not([class*=s_lv])")) {
                        ArrayList<String> tempPhoneSpecList = new ArrayList<String>();
                        String phoneSpecArrayTitle;
                        if (sectionTitle.equals("other_features") && k == 0) {
                            tempSpecList.add("Voice");
                        } /* else if (sectionTitle.equals("multimedia") && k == 0) {
                            tempSpecList.add("Youtube_player");
                        } else if (sectionTitle.equals("technology") && k == 0) {
                            tempSpecList.add("Micro_SIM");
                            tempSpecList.add("Multiple_SIM_cards");
                            // tempSpecList.add("cdma");
                            tempSpecList.add("HD_Voice");
                        } else if (sectionTitle.equals("battery") && k == 0) {
                            tempSpecList.add("Not_user_replaceable");
                        } */
                        phoneSpecArrayTitle = phone + " " + sectionTitle + " " + tempSpecList.get(k);

                        phoneSpec = phoneSpecE.ownText();
                        // Skip empty specs
                        if (phoneSpec.equals("") || phoneSpec == null) {
                            continue;
                        }
                        tempPhoneSpecList.add(phoneSpec);
                        // Log.d("Main.SpecDownload", "Spec Title: " + phoneSpecArrayTitle);
                        // Log.d("Main.SpecDownload", "List of phone specs: " + tempPhoneSpecList);

                        phoneSpecE = doc.select("strong[class*=s_lv]:containsOwn(" + sectionTitle.replace("_", " ") + ") ~ ul ul[class*=s_lv] > li:not([class*=s_lv])").first();
                        if (phoneSpecE != null) {
                            // Log.d("TEST1", phoneSpecE.toString());
                        }
                        // Add the phone's specification to HashMapped ArrayList to populate rows when creating the SQLiteDB
                        phoneSpecMap.put(phoneSpecArrayTitle, new ArrayList(tempPhoneSpecList));

                        k++;
                    }

                }
                connectTime = System.nanoTime();
                duration = connectTime - startTime2;
                Log.d("SpecDownload", "Time to add specs to map for " + phone + ": " + decimalFormatter.format(duration/1000000000.) + " seconds");
                } catch (IOException ioe) {
                    Log.d("Main.SpecsDownload", phone + " Spec List Failed to Download!");
                    ioe.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // db.addPhones(getApplication(), new LinkedList<String> (Arrays.asList(phoneList.get(0))), allPhones);
            long startTime2 = System.nanoTime();
            Map<String, List<String>> tempSpecMap = new LinkedHashMap<>(phoneSpecMap);
            Pattern regex = Pattern.compile("([^\\s]+).([^\\s]+).([^\\s]+)");

            for (String imageURLTitle : phoneImageURLMap.keySet()) {
                Matcher tokens = regex.matcher(imageURLTitle);
                boolean found = tokens.matches();
                String tempPhone = tokens.group(1);
                if (tempPhone.equals(phone)) {
                    tempSpecMap.put(imageURLTitle, phoneImageURLMap.get(imageURLTitle));
                }
            }
            try {
                db.addSpec(tempSpecMap, sectionTitles, phone, allPhones, lastPhone);

            } catch (ConcurrentModificationException cme) {
                    Log.d("Main.SpecDownload", "Concurrent Modification Error!");
            }

            long addMapTime = System.nanoTime();
            long duration = addMapTime - startTime2;
            Log.d("SpecDownload", "Time to " + phone + " specs to database: " + decimalFormatter.format(duration/1000000000.) + " seconds");
            phoneList.remove(phone);

            Log.d("Main.SpecDownload", phone + " specs downloaded!");
            if (phoneList.size() == 0) {
                progress.setVisibility(View.GONE);
                Log.d("Main.SpecDownload", "All phone specs downloaded!");
                downloadEndTime = System.nanoTime();
                duration = downloadEndTime - downloadStartTime;
                Log.d("Main.SpecDownload", "Time to download all phone specs: " + decimalFormatter.format(duration/1000000000.) + " seconds");
                new EnduranceDownload().execute();
            } else {
                new SpecDownload().execute(phoneList);
            }
            long endTime = System.nanoTime();
            duration = endTime - startTime;
            Log.d("SpecDownload", "Time to download " + phone + " specs: " + decimalFormatter.format(duration/1000000000.) + " seconds");
        }

    }

    private class EnduranceDownload extends AsyncTask<Void, Void, Void> {
        Map<String, String> enduranceMap = new LinkedHashMap<>();

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void...arg) {
            try {
                Document doc = Jsoup.connect("http://www.gsmarena.com/battery-test.php3").get();
                // Log.d("Main.EnduranceDownload", "Test doc: " + doc.toString());
                for (Element section : doc.select("tr")) {
                    if (section.select("td a[href]").first() == null) {
                        continue;
                    }
                    String phoneModel = section.select("td a[href]").first().text();
                    if (phoneModel.equals("Samsung I9505 Galaxy S4")) {
                        phoneModel = "Samsung Galaxy S4";
                    } else if (phoneModel.equals("Motorola Nexus 6")) {
                        phoneModel = "Google Nexus 6";
                    } else if (phoneModel.equals("LG Nexus 5")) {
                        phoneModel = phoneModel.replace("LG", "Google");
                    } else if (phoneModel.equals("LG Nexus 4 E960")) {
                        phoneModel = "Google Nexus 4";
                    } else if (phoneModel.equals("Samsung Galaxy S6 active")) {
                        phoneModel = "Samsung Galaxy S6 Active";
                    } else if (phoneModel.equals("Samsung Galaxy A7 Duos")) {
                        phoneModel = "Samsung Galaxy A7";
                    } else if (phoneModel.equals("Asus Zenfone 2 ZE551ML")) {
                        phoneModel = "Asus ZenFone 2";
                    } else if (phoneModel.equals("Samsung Galaxy Note II N7100")) {
                        phoneModel = "Samsung GALAXY Note II";
                    } else if (phoneModel.equals("Motorola Moto X (2nd Gen)")) {
                        phoneModel = "Motorola Moto X (2014)";
                    } else if (phoneModel.equals("Samsung I9190 Galaxy S4 mini")) {
                        phoneModel = "Samsung Galaxy S4 mini";
                    } else if (phoneModel.equals("Microsoft Lumia 640 LTE")) {
                        phoneModel = "Microsoft Lumia 640";
                    } else if (phoneModel.equals("Samsung I9300 Galaxy S III")) {
                        phoneModel = "Samsung Galaxy S III";
                    } else if (phoneModel.equals("Samsung I9100 Galaxy S II")) {
                        phoneModel = "Samsung Galaxy S II";
                    } else if (phoneModel.equals("Motorola Moto G 4G")) {
                        phoneModel = "Motorola Moto G (2014)";
                    } else if (phoneModel.equals("Samsung Galaxy Nexus I9250")) {
                        phoneModel = "Samsung GALAXY Nexus";
                    } else if (phoneModel.equals("Microsoft Lumia 535 Dual SIM")) {
                        phoneModel = "Microsoft Lumia 535";
                    }

                    String enduranceRating = section.select("td ~ td a[href]").text();
                    enduranceMap.put(phoneModel, enduranceRating);
                    // Log.d("Main.EnduranceDownload", "Phone: " + phoneModel + " | Endurance rating : " + enduranceRating);
                }
            } catch (IOException ioe) {
                Log.d("Main.EnduranceDownload", "Unable to download Endurance Ratings");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Result) {
            db.addEnduranceRating(enduranceMap);
            Log.d("Main.EnduranceDownload", "Endurance ratings added to database!");
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                // Forces a download of new phones by changing the last download to never.
                SharedPreferences prefs = getSharedPreferences(SplashScreen.myPrefs, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("lastDownload", 0);
                editor.commit();

                // Starts the SplashScreen Activity to download specs.
                Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                startActivity(intent);
            case R.id.action_settings:
                return true;
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
