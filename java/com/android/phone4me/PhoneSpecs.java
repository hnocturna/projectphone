package com.android.projectphone;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;


public class PhoneSpecs extends ActionBarActivity {
    ArrayList<String> specList = new ArrayList<String>();
    ArrayList<String> phoneSpecList = new ArrayList<String>();

    String phoneURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_specs);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phoneURL = extras.getString("phoneURL");
            Log.d("URL", phoneURL);
        }

        new Download().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone_specs, menu);
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

    private class Download extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... arg0) {
            try {
                Document doc = null;
                doc = Jsoup.connect(phoneURL).get();
                Log.d("URL", doc.toString());
                for (Element specClassE : doc.select("div.s_specs_box")) {
                    String spec = null;
                    for (Element specE : specClassE.select("strong[class*=s_lv]")) {
                        spec = specE.ownText();
                        if (spec.equals("Music player:")) {
                            // Log.d("spec", "Music Player skipped!");
                        } else if (!spec.equals("")) {
                            specList.add(spec);
                        } else {
                            specE = specE.select("span[class*=s_tooltip]").first();
                            spec = specE.ownText();
                            specList.add(spec);
                        }
                    }
                    String phoneSpec = null;

                    for (Element phoneSpecE : specClassE.select("ul[class*=s_lv] > li:not([class*=s_lv])")) {
                        phoneSpec = phoneSpecE.text();
                        // Log.d("PhoneSpecs",phoneSpec + " (" + i + ")");
                        phoneSpecList.add(phoneSpec);
                    }
                }
                // for (int i = 0; i < specList.size(); i++) {
                //    Log.d("PhoneSpecsOutput",specList.get(i) + " " + phoneSpecList.get(i) + " (" + i + ")");
                // }
            } catch (IOException ieo) {
                Log.d("TestOutput", "Connection Failed...");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("PhoneSpecs", "onPostExecute()");
            super.onPostExecute(result);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < specList.size(); i++) {
                builder.append(specList.get(i) + " " + phoneSpecList.get(i) + "\n");
            }
            // Log.d("builder",builder.toString());
            TextView tv = (TextView) findViewById(R.id.phoneURL);
            tv.setText(builder.toString().trim());
        }
    }
}
