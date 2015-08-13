package com.android.projectphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    static ArrayList<String> phoneArray = new ArrayList<String>();
    static ArrayList<String> phoneURLArray = new ArrayList<String>();
    static ArrayList<Bitmap> phoneBitmapArray = new ArrayList<Bitmap>();
    static ArrayList<String> phoneImageURLArray = new ArrayList<String>();
    static ArrayList<String> sectionTitles = new ArrayList<String>();
    static ArrayList<String> specList = new ArrayList<String>();
    static HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();
    static HashMap<String, ArrayList> phoneSpecMap = new HashMap<String, ArrayList>();
    static HashMap<String, String> phoneURLMap = new HashMap<String, String>();
    MySQLiteHelper db = new MySQLiteHelper(this);
    private Button interest_btn;
    private Button known_btn;
    private Button specs_btn;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debug", "MainCreated");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        progress = (ProgressBar) this.findViewById(R.id.main_progress);
        progress.setVisibility(View.GONE);
        interest_btn = (Button) findViewById(R.id.interest_btn);
        known_btn = (Button) findViewById(R.id.known_btn);
        specs_btn = (Button) findViewById(R.id.specs_btn);
        interest_btn.setOnClickListener(this);
        known_btn.setOnClickListener(this);
        specs_btn.setOnClickListener(this);
        new SpecDownload().execute();

    }

    protected void onStart() {
        Log.d("debug", "MainStart");
        super.onStart();
    }

    public void onClick(View v) {
        Intent intent = null;
       /* if (v.getId() == interest_btn.getId()) {
            intent = new Intent(this, InterestActivity.class);
        } */
        if (v.getId() == known_btn.getId()) {
            intent = new Intent(this, KnownActivity.class);
        }
        if (v.getId() == specs_btn.getId()) {
            intent = new Intent(this, SpecsActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
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
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("INITIALIZED", false);
                editor.apply();
                return true;
            case R.id.action_settings:
                return true;
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private class SpecDownload extends AsyncTask<Void, Void, Void> {
        Boolean downloadCheck = true;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> newPhones = new LinkedHashSet<String>();

        @Override
        protected void onPreExecute() {

            // db.refresh();
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Download the section titles of the specs and save it to an ArrayList to be used to generate the Tables of the SQLite DB
            InputStream testdoc = null;
            Set<String> tempPhoneArray = new HashSet<>();
            try {
                for (int p = 1; p < 4; p++) {
                    // testdoc = getAssets().open("www.phonearena.com/phones/sort/popular.html");
                    testdoc = getAssets().open("www.phonearena.com/phones/sort/popular/page/" + p + ".html");
                    Document doc = Jsoup.parse(testdoc, "UTF-8", "www.phonearena.com/");
                    // Document doc = Jsoup.connect("http://www.phonearena.com/phones/sort/popular").get();
                    Log.d("MainActivity.Download", "Connected to page " + p + "!");
                    String phoneURL = null; // Holds the URL that selecting the phone will direct to
                    String phone = null; // Holds the name of the phone
                    for (Element popularPhone : doc.select("div.s_block_4")) {
                        // Loop to select each phone, it's URL, and the thumbnail URL to be used as the button's background
                        phone = popularPhone.select("span.title").first().text().replace(" ", "_"); // Selects the phone's candidate title text
                        // String candidatePhone = popularPhone.select("a[href]").text(); // Selects the phone's candidate title text
                        String phoneImageURL = popularPhone.select("img").attr("src"); // Selects the thumbnail URL

                        phoneImageURL = phoneImageURL.substring(2); // Removes the 2 prepending /s
                        StringBuilder builder = new StringBuilder(); // Used to build a full http:// website for BitmapFactory
                        builder.append("http://" + phoneImageURL);
                        phoneImageURL = builder.toString();
                        phoneImageURLArray.add(phoneImageURL);

                        // phoneURL = popularPhone.select("a.s_thumb").first().attr("abs:href"); // Selects the phone's specification URL // Re-instate when connecting to actual website
                        phoneURL = popularPhone.select("span.s_thumb").first().attr("href").toString().substring(9);
                        // Log.d("Test", phoneURL);
                        // phoneURL = popularPhone.select("a.s_thumb").first().attr("href").toString().substring(3);


                        int i = 0;
                        /* if (candidatePhone.contains("Reviewed")) {
                            // Cycles through the string to remove any unnecessary words from the phone's title string
                            while (i < 3) {
                                candidatePhone = candidatePhone.substring(1, candidatePhone.length());
                                if (candidatePhone.charAt(0) == ' ') {
                                    i++;
                                }
                            }
                            phone = candidatePhone.trim().replace(" ", "_");
                        } else {
                            // Remove extraneous words from phone's title string as well as leading/trailing spaces
                            phone = candidatePhone.replace("Compare ", "").trim().replace(" ", "_");
                        } */
                        phoneURLMap.put(phone, phoneURL); // Add URLs to an ArrayList to be accessed when creating the buttons
                        newPhones.add(phone); // Add phone title's to Arraylist

                        // phoneArray.add(phone);
                    }
                }
                // newPhones = tempPhoneArray;


                ArrayList<String> existingPhones = new ArrayList(db.returnPhones(getApplication()));
                ArrayList<String> deprecatedPhones = new ArrayList<String>(existingPhones);
                deprecatedPhones.removeAll(newPhones);

                newPhones.removeAll(existingPhones);
                Log.d("existingPhones", existingPhones.toString());
                Log.d("deprecatedPhones", deprecatedPhones.toString());
                Log.d("newPhones", newPhones.toString());
                if (newPhones.isEmpty()) {
                    Log.d("Main.SpecDownload", "No new phones! Spec Download skipped.");
                    downloadCheck = false;
                    return null;
                } else {
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
            int i = 0;
            for (String phone : newPhones) {
                try {
                    // Connect to each phone's spec URL
                    urlStream = getAssets().open("www.phonearena.com/phones/" + phoneURLMap.get(phone));
                    Document doc = Jsoup.parse(urlStream, "UTF-8", "www.phonearena.com/");
                    //Document doc = Jsoup.connect(phoneURLArray.get(i)).get();

                    // Download a list of sections to be used as individual tables
                    for (Element sectionTitleE : doc.select("h2.htitle")) {
                        if (i == 0) {
                            sectionTitles.add(sectionTitleE.ownText().trim().toLowerCase().replace(" ", "_"));
                        }
                        String spec = null;
                        for (String sectionTitle : sectionTitles) {
                            ArrayList<String> tempSpecList = new ArrayList<String>();

                            int features = 0; // Camera section has multiple "features" columns, this renames each one to be unique
                            // Downloads the list of specs for each section (to be the columns of each table)
                            for (Element specE : doc.select("h2:containsOwn(" + sectionTitle.replace("_", " ") + ") ~ ul strong[class*=s_lv]")) {
                                spec = specE.ownText();
                                if (spec.equals("Music player:")) { // Stupid spec - consider getting rid of this section altogether
                                } else if (!spec.equals("")) { // Check to make sure the spec isn't blank
                                    spec = spec.replace(":", "").replace(" ", "_").replace("-", "_").toLowerCase().replace("(", "").replace(")", "");
                                    // See comment for int features above
                                    if (sectionTitle.equals("camera")) {
                                        if (spec.equals("features")) {
                                            if (features == 0) {
                                                features++;
                                            } else if (features == 1) {
                                                StringBuilder specSB = new StringBuilder();
                                                specSB.append("cc_");
                                                specSB.append(spec);
                                                spec = specSB.toString();
                                                features++;
                                            } else if (features == 2) {
                                                StringBuilder specSB = new StringBuilder();
                                                specSB.append("ffc_");
                                                specSB.append(spec);
                                                spec = specSB.toString();
                                            }
                                        }
                                    }
                                    if (sectionTitle.equals("multimedia")) {
                                        if (spec.equals("features")) {
                                            if (features == 0) {
                                                features++;
                                            } else if (features == 1) {
                                                StringBuilder specSB = new StringBuilder();
                                                specSB.append("vp_");
                                                specSB.append(spec);
                                                spec = specSB.toString();
                                                features++;
                                            } else if (features == 2) {
                                                StringBuilder specSB = new StringBuilder();
                                                specSB.append("ffc_");
                                                specSB.append(spec);
                                                spec = specSB.toString();
                                            }
                                        }
                                    }
                                    tempSpecList.add(spec);

                                    // Occasionally the spec is listed under another attribute; this gets it in that case
                                } else {
                                    specE = specE.select("span[class*=s_tooltip]").first();
                                    spec = specE.ownText();
                                    spec = spec.replace(":", "").replace(" ", "_").replace("-", "_").toLowerCase().replace("(", "").replace(")", "");

                                    if (sectionTitle.equals("multimedia")) {
                                        if (spec.equals("features")) {
                                            if (features == 0) {
                                                features++;
                                            } else if (features == 1) {
                                                StringBuilder specSB = new StringBuilder();
                                                specSB.append("vp_");
                                                specSB.append(spec);
                                                spec = specSB.toString();
                                                features++;
                                            } else if (features == 2) {
                                                StringBuilder specSB = new StringBuilder();
                                                specSB.append("ffc_");
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
                                ArrayList<String> tempPhoneSpecList = new ArrayList<String>();
                                String phoneSpecArrayTitle;
                                if (sectionTitle.equals("other_features") && k == 0) {
                                    tempSpecList.add("voice");
                                } else if (sectionTitle.equals("multimedia") && k == 0) {
                                    tempSpecList.add("youtube_player");
                                } else if (sectionTitle.equals("technology") && k == 0) {
                                    tempSpecList.add("micro_sim");
                                    tempSpecList.add("multiple_sim_cards");
                                    tempSpecList.add("cdma");
                                    tempSpecList.add("hd_voice");
                                } else if (sectionTitle.equals("battery") && k == 0) {
                                    tempSpecList.add("not_user_replaceable");
                                }
                                phoneSpecArrayTitle = phone + " " + sectionTitle + " " + tempSpecList.get(k);

                                phoneSpec = phoneSpecE.ownText();
                                // Skip empty specs
                                if (phoneSpec.equals("") || phoneSpec == null) {
                                    continue;
                                }
                                tempPhoneSpecList.add(phoneSpec);
                                // Add the phone's specification to HashMapped ArrayList to populate rows when creating the SQLiteDB
                                phoneSpecMap.put(phoneSpecArrayTitle, new ArrayList(tempPhoneSpecList));
                                // db.addSpec(phoneSpecArrayTitle, tempPhoneSpecList);
                                // Log.d("WTF IS THIS SHIT?", phoneSpecArrayTitle+ ": " + phoneSpec);
                                k++;
                                // Log.d("Why did this break?", tempPhoneSpecList.toString());
                            }

                            // Store each of section's worth of specifications as an individually HashMapped Arraylist to be used as the columns of the SQLite tables
                            if (map.get(sectionTitle) == null || map.get(sectionTitle).size() < tempSpecList.size()) {
                                map.put(sectionTitle, new ArrayList(tempSpecList));
                            }

                        }
                    }
                    for (String key : map.keySet()) {
                        // Log.d("Main.SpecDownload", key + " specs: " + map.get(key));
                    }

                    i++;
                } catch (IOException ioe) {
                    Log.d("Main.SpecsDownload", phone + " Spec List Failed to Download!");
                    ioe.printStackTrace();
                }
            }
            for (String key : phoneSpecMap.keySet()) {
                // Log.d("HashMap", "Key: " + key + "| Value: " + phoneSpecMap.get(key));;
            }

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            if (downloadCheck) {
                db.addPhones(getApplication(), newPhones);
                Log.d("Main.Download.Post", "Phones added successfully!");
                for (String newPhone : newPhones) {
                    for (String phoneSpecArrayTitle : phoneSpecMap.keySet()) {
                        if (phoneSpecArrayTitle.contains(newPhone)) {
                            db.addSpec(phoneSpecArrayTitle, phoneSpecMap.get(phoneSpecArrayTitle));
                        }
                    }
                }
                Log.d("Main.Download.Post", "Phone specs added successfully!");
                progress.setVisibility(View.GONE);
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

    private class ThumbnailDownload extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Load the loading animation (not yet implemented)
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            for (int i = 0; i < phoneImageURLArray.size(); i++) {
                try {
                    URL url = new URL(phoneImageURLArray.get(i).trim()); // Turns the phone's image URL string to URL
                    // Log.d("ImageURL", phoneImageURL);
                    try {
                        // Downloads and saves each bitmap to an ArrayList to be accessed when creating buttons
                        URLConnection conn = url.openConnection();
                        // Log.d("Conn URL", conn.toString());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Open a connection and store it's value
                        connection.setDoInput(true);
                        connection.connect(); // Connect to the previously defined connection
                        InputStream bmpStream = conn.getInputStream(); // Store InputStream as bmpStream
                        Bitmap bmp = BitmapFactory.decodeStream(bmpStream);  // Grab the bitmap
                        phoneBitmapArray.add(bmp); // Add bitmap to an array to be accessed when creating buttons

                        BitmapDrawable bmpDrawable = new BitmapDrawable(bmp); // An attempt to change to drawable to image can be set as background w/ overlaying text
                        bmpStream.close(); // Close the InputStream

                    } catch (IOException ioe) {
                        // Catch any IOE and place a null in the Bitmap Arraylist to prevent out of boundary on button generation
                        phoneBitmapArray.add(null);
                        Log.d("Image Download", "IO Exception!");
                        ioe.printStackTrace(); // Debug purposes
                    }
                } catch (MalformedURLException mue) {
                    Log.d("onPostExecute()", "Malformed URL");
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
        }
    }
}
