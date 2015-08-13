package com.android.projectphone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

/**
 * Created by hnoct on 8/7/2015.
 */
public class SplashScreen extends Activity {

    static List<String> sectionTitles = new ArrayList<>();                          // List of tables currently in the database.
    static List<String> allPhones = null;                                           // List of all phones in the database and phones to be downloaded.

    static Map<String, List> map = new HashMap<>();                                 // Map of (key) section to (value) list of specs. Used to create the initial database.
    static Map<String, String> phoneURLMap = new HashMap<>();                       // Links the phone to its spec sheet URL.
    static Map<String, ArrayList> phoneImageURLMap = new HashMap<>();               // Links the phone to its thumbnail URL.
    static Map<String, List<String>> phoneSpecListMap = new LinkedHashMap<>();      // Map of (key) phone, table, & spec to (value) the phone's spec.


    private int downloadListSize;                                                   // Used as the absolute list of the size of phones to be downloaded in the textView.

    private static Context mContext;                                                // Allows any function to access the getApplication Context.
    private List<String> newPhoneList = new LinkedList<String>();                        // List of phones to be downloaded.

    boolean downloadCheck = true;                                                   // A check for whether any phone specs need to be downloaded.

    public static String myPrefs = "com.android.phone4me.prefs";                    // My Preferences file.

    MySQLiteHelper db = new MySQLiteHelper(this);                                   // Initialize the MySQLiteHelper.

    com.gc.materialdesign.views.ProgressBarCircularIndeterminate progressBar;       // Progress bar :D
    TextView textView;                                                              // Used to update the user as to the progress of the download.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        setContentView(R.layout.splashscreen);

        // Initialize the views
        progressBar = (com.gc.materialdesign.views.ProgressBarCircularIndeterminate) findViewById(R.id.splash_screen_progress);
        textView = (TextView) findViewById(R.id.splash_screen_textview);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check whether the database already exists, whether it needs to be copied from assets, and when the last download happened.
        SharedPreferences prefs = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean dbCopied = prefs.getBoolean("dbCopied", false);     // Status of whether the database has been copied so it is not copied every time.
        long lastDownload = prefs.getLong("lastDownload", 0);       // Logs when the last download occurred.

        Log.d("Splash", "dbCopied: " + dbCopied + " | lastDownload " + lastDownload);

        if (!dbCopied) {
            // Copy the database from assets on first run.
            db.createDatabase(getApplication());
            editor.putBoolean("dbCopied", true);
            editor.putLong("lastDownload", 0);
            textView.setText("Injecting existing database from assets...");
        } else {
            /*
             * If the database is missing for some reason and has been "copied," then create a new database from scratch.
             * This is usually used when the database is manually deleted to create a new database on purpose.
              */
            if (!db.dbExists(getApplication())) {
                editor.putLong("lastDownload", 0);
                db.createDatabase(getApplication());
            }
        }
        editor.commit();

        lastDownload = prefs.getLong("lastDownload", 0);
        if (System.currentTimeMillis() - lastDownload > 86400000) {
            // Download only once a day to cut down on bandwidth usage and unnecessary checks.
            editor.putLong("lastDownload", System.currentTimeMillis());
            new PhoneDownload().execute();
            editor.commit();
        } else {
            // If has been downloaded in the last 24hrs, then go straight to MainActivity.
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void phoneSpecDownload(List<String> newPhoneList) {
        // Iterate through list of phones to download. This is done recursively to minimize I/O errors.
        long downloadStartTime = System.nanoTime();
        progressBar.setVisibility(View.VISIBLE);

        Log.d("Splash.phoneSpecDownload", "Download specs for " + newPhoneList.size() + " phones!");
        textView.setText("Downloading specs for " + newPhoneList.size() + " phones!");

        downloadListSize = newPhoneList.size();
        if (!newPhoneList.isEmpty()) {
            new SpecDownload().execute(newPhoneList);
        }

    }

    public static Context getAppContext() {
        // Universal static context for any function that may need it in any class.
        return mContext;
    }

    private class PhoneDownload extends AsyncTask<Void,Void,Void> {
        // Downloads list of phones that need to be added to the database.

        @Override
        protected Void doInBackground(Void...arg0) {
            // Download the section titles of the specs and save it to an ArrayList to be used to generate the Tables of the SQLite DB

            try {
                for (int p = 1; p < 4; p++) {

                    Document doc = Jsoup.connect("http://www.phonearena.com/phones/sort/popular/page/" + p + ".html").get();
                    Log.d("SplashActivity.Download", "Connected to page " + p + "!");

                    String phoneURL = null;                                                                         // Holds the URL that selecting the phone will direct to
                    String phone = null;                                                                            // Holds the name of the phone
                    for (Element popularPhone : doc.select("div.s_block_4")) {
                        // Loop to select each phone, it's URL, and the thumbnail URL to be used as the button's background

                        phone = popularPhone.select("h3 a[href]").first().text().replace(" ", "_");                 // Selects the phone's candidate title text
                        String phoneImageURL = popularPhone.select("img").attr("src");                              // Selects the thumbnail URL
                        phoneImageURL = "http:" + phoneImageURL;

                        ArrayList<String> tempArray = new ArrayList<String>();
                        tempArray.add(phoneImageURL);
                        phoneImageURLMap.put(phone + " availability " + "image_url", tempArray);

                        phoneURL = popularPhone.select("a.s_thumb").first().attr("abs:href"); // Selects the phone's specification URL // Re-instate when connecting to actual website

                        phoneURLMap.put(phone, phoneURL);   // Add URLs to an ArrayList to be accessed when creating the buttons
                        newPhoneList.add(phone);            // Add phone title's to Arraylist
                    }
                }

            } catch (IOException ioe) {
                Log.d("Splash.SpecsDownload", "IOException!");
                ioe.printStackTrace();
            }
            InputStream urlStream = null;
            // int i = 0;

            String phone = newPhoneList.get(0);
            try {
                Document doc = Jsoup.connect(phoneURLMap.get(phone)).get();

                for (Element sectionTitleE : doc.select("h2.htitle")) {
                    // Download a list of sections to be used as individual tables
                    sectionTitles.add(sectionTitleE.ownText().trim().toLowerCase().replace(" ", "_"));
                }

                // Log.d("Splash.SpecsDownload", "Section titles: " + sectionTitles.toString());
                String spec = null;     // Holds the current spec being added to the map corresponding the Section and its specs.
                for (String sectionTitle : sectionTitles) {
                    ArrayList<String> tempSpecList = new ArrayList<String>();

                    int features = 0; // Camera section has multiple "features" columns, this renames each one to be unique

                    for (Element specE : doc.select("h2:containsOwn(" + sectionTitle.replace("_", " ") + ") ~ ul strong[class*=s_lv]")) {
                        // Downloads the list of specs for each section (to be the columns of each table)
                        spec = specE.ownText();
                        if (spec.equals("Music player:")) {
                            // Stupid spec - consider getting rid of this section altogether

                        } else if (!spec.equals("")) {
                            // Check to make sure the spec isn't blank
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
                            Log.d("Splash.SpecDownload", "Spec: " + spec);
                            tempSpecList.add(spec);

                        } else {
                            // Occasionally the spec is listed under another attribute; this gets it in that case.
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

                /* Creates three lists:
                *   allPhones:          A list of all phones: new and old.
                *   oldPhones:          A list of phones old phones that already exist in the database.
                *   deprecatedPhones:   A list of phones that are no longer in the most popular list of phones, but are still in the database.
                *   newPhones:          A list of phones that still need to download specs (The list that is downloaded minus old and deprecated phones).
                */

                allPhones = new LinkedList<String>(newPhoneList);
                List<String> existingPhones = new LinkedList<>(db.returnPhones(getApplication(), db.returnSectionTitles(getApplication())));
                List<String> deprecatedPhones = new LinkedList<String>(existingPhones);
                deprecatedPhones.removeAll(newPhoneList);
                allPhones.addAll(deprecatedPhones);

                newPhoneList.removeAll(existingPhones);
                Log.i("Splash.phoneDownload", "Existing phones: " + existingPhones.toString());
                Log.i("Splash.phoneDownload", "Deprecated phones: " + deprecatedPhones.toString());
                Log.i("Splash.phoneDownload", "New phones: " + newPhoneList.toString());
                if (newPhoneList.isEmpty()){
                    Log.i("Splash.phoneDownload", "No new phones! Spec Download skipped.");
                    downloadCheck = false;
                    return null;
                }
                else {
                    Log.i("Splash.SpecDownload", "New phones added! Downloading phone specs.");
                }

            } catch (IOException ioe) {
                Log.d("Splash.SpecsDownload", phone + " Spec List Failed to Download!");
                ioe.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(Void result) {
            if (downloadCheck) {
                // If new phones need to be downloaded, begins the process of downloading specs.
                phoneSpecDownload(newPhoneList);
            } else {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                Log.i("Splash.SpecDownload", "No new phones added to db!");
                progressBar.setVisibility(View.GONE);
                startActivity(intent);
            }
        }
    }

    int phoneProgress = 1;

    /*
    * Downloads all the specs for each phone.
    * This function recursively goes through the newPhoneList and downloads specs for it, then removes it from the list, then begins the another SpecDownload
    * for the next phone.
     */
    private class SpecDownload extends AsyncTask<List<String>,Void,Void> {
        String phone = null;                                                // The current phone being processed | The first phone in the parameter's list.
        List<String> phoneList = new LinkedList<>();                        // The list of phones being passed from the parameters.
        Map<String, List<String>> phoneSpecMap = new LinkedHashMap<>();     // Map of the specs to be added to the database.
        boolean lastPhone = false;                                          // Check for whether it is the last phone in the list. If so, used to update the UI and move to the MainActivity.
        NumberFormat decimalFormatter = new DecimalFormat("#0.00");         // Used to format the debug message re: time required to download each phone.
        long startTime = System.nanoTime();                                 // Used to calculate time needed to download each phone.

        @Override
        protected void onPreExecute() {
            // Updates UI to inform the user about the spec download progress.
            textView.setText("Downloading " + phoneProgress + " of " + downloadListSize + " phones...");
            progressBar.setVisibility(View.VISIBLE);
            phoneProgress++;
        }

        @Override
        protected Void doInBackground(List<String>...arg) {
            phoneList = arg[0];
            phone = phoneList.get(0);
            if (phoneList.size() == 1) {
                // If only one phone left in the list, it is the last phone. Duh.
                lastPhone = true;
            }
            Log.i("Splash.SpecDownload", "Downloading specs for " + phone + "!");

            try {
                Document doc = Jsoup.connect(phoneURLMap.get(phone)).get();
                long connectTime = System.nanoTime();       // Used to log the start time of the connection.


                String spec = null;

                /*
                * Download each section's worth of specs and adds it to the phoneSpecMap.
                * phoneSpecMap is formatted so that the key contains: phone model, table, and column.
                * Regex is used to select each variable in the addSpec function of MySQLiteHelper.
                * The value of phoneSpecMap contains only the phone's individual spec.
                 */
                for (String sectionTitle : sectionTitles) {
                    ArrayList<String> tempSpecList = new ArrayList<String>();       // Contains all the specs of each section.

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
                                    Log.d("Splash.SpecDownload", "Features caught!");
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
                            tempSpecList.add(spec);
                        } else {
                            // Occasionally the spec is listed under another attribute; this gets it in that case
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

                    String phoneSpec = null;
                    int k = 0; // To be used in the loop below to correlate the phone's spec with the list of specs

                    // Add each of the phone's specifications to an ArrayList to be added to the db
                    for (Element phoneSpecE : doc.select("h2:containsOwn(" + sectionTitle.replace("_", " ") + ") ~ ul ul[class*=s_lv] > li:not([class*=s_lv])")) {
                        ArrayList<String> tempPhoneSpecList = new ArrayList<String>();
                        String phoneSpecArrayTitle;
                        if (sectionTitle.equals("other_features") && k == 0) {
                            // The first time the download encounters "other_features," the Voice spec needs to be created as it is not labeled on the website.
                            tempSpecList.add("Voice");
                        }

                        phoneSpecArrayTitle = phone + " " + sectionTitle + " " + tempSpecList.get(k);

                        phoneSpec = phoneSpecE.ownText();

                        if (phoneSpec.equals("") || phoneSpec == null) {
                            // Skip empty specs
                            continue;
                        }
                        tempPhoneSpecList.add(phoneSpec);


                        phoneSpecE = doc.select("strong[class*=s_lv]:containsOwn(" + sectionTitle.replace("_", " ") + ") ~ ul ul[class*=s_lv] > li:not([class*=s_lv])").first();
                        if (phoneSpecE != null) {
                            // Skip empty specs.
                        }
                        // Add the phone's specification to HashMapped ArrayList to populate rows when creating the SQLiteDB
                        phoneSpecMap.put(phoneSpecArrayTitle, new ArrayList(tempPhoneSpecList));

                        k++;
                    }
                }
            } catch (IOException ioe) {
                Log.d("Splash.SpecsDownload", phone + " Spec List Failed to Download!");
                ioe.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
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
                Log.d("Splash.SpecDownload", "Concurrent Modification Error!");
            } catch (SQLiteException sqe) {
                db.onUpgrade(db.getWritableDatabase(), 1, 1);
            }

            /* long addMapTime = System.nanoTime();
            long duration = addMapTime - startTime2;
            Log.d("SpecDownload", "Time to " + phone + " specs to database: " + decimalFormatter.format(duration/1000000000.) + " seconds"); */
            phoneList.remove(phone);        // Remove each phone from the list as the specs are added to the database.

            Log.d("Splash.SpecDownload", phone + " specs downloaded!");
            if (phoneList.size() == 0) {
                // After the last phone has been downloaded, add Endurance ratings from another website.
                progressBar.setVisibility(View.GONE);
                textView.setText("");
                Log.d("Splash.SpecDownload", "All phone specs downloaded!");
                /* downloadEndTime = System.nanoTime();
                duration = downloadEndTime - downloadStartTime;
                Log.d("Splash.SpecDownload", "Time to download all phone specs: " + decimalFormatter.format(duration/1000000000.) + " seconds"); */
                new EnduranceDownload().execute();
            } else {
                // If more phones are in the list, download their specs. <- Recursion! Whoo, I finally used it.
                new SpecDownload().execute(phoneList);
            }
        }

    }

    /*
    * Add Endurance Rating from another website.
    * These rankings are usually very useful for calculating the ratio of how long one phone will last compared to another phone.
    * Many phones are named slightly differently than they are from the primary website, so their models have to be changed.
    * Currently this is done manually as new phones are released and very time consuming. Consider implementing fuzziness to add instead.
     */
    private class EnduranceDownload extends AsyncTask<Void, Void, Void> {
        Map<String, String> enduranceMap = new LinkedHashMap<>();       // Links the phone model to its endurance rating.

        @Override
        protected void onPreExecute() {
            // Update the user as to the progress of the download.
            textView.setText("Adding battery endurance specs for existing phones!");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void...arg) {
            try {
                Document doc = Jsoup.connect("http://www.gsmarena.com/battery-test.php3").get();
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
                }
            } catch (IOException ioe) {
                Log.d("Splash.EnduranceDownload", "Unable to download Endurance Ratings");
                ioe.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Result) {
            // Because this is the last download (for now), update the UI and launch the MainActivity.
            db.addEnduranceRating(enduranceMap);
            textView.setText("");
            progressBar.setVisibility(View.GONE);

            Log.i("Splash.EnduranceDownload", "Endurance ratings added to database!");

            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
