package com.android.projectphone;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.projectphone.data.PhoneDbHelperTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PhoneSpecs extends ActionBarActivity {
    ArrayList<String> phoneSpecList = new ArrayList<String>();
    PhoneDbHelperTest db = new PhoneDbHelperTest(this);

    String phoneURL = null;
    String phone = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_specs);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // phoneURL = extras.getString("phoneURL");
            phone = extras.getString("phone");
            // Log.d("URL",phoneURL);
        }
        Phone selectedPhone = db.getPhoneSpecs(phone);
        Map<String, List<String>> categorySpecMap = new LinkedHashMap<>(selectedPhone.getCategorySpecMap());
        Map<String, String> specPhoneSpecMap = new LinkedHashMap<>(selectedPhone.getSpecPhoneSpecMap());

        Log.d("TEST", specPhoneSpecMap.values().toString());

        TextView titleText = (TextView) findViewById(R.id.phone_title_text);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        titleText.setText(phone.replaceAll("_", " "));
        List<CardInfo> specCardList = new LinkedList<>();
        for (String section : categorySpecMap.keySet()) {
            List<String> specList = new LinkedList<>(categorySpecMap.get(section));
            if (specList.isEmpty()) {
                continue;
            }
            specCardList.add(new CardInfo(section, specList, specPhoneSpecMap));
        }

        SpecCardAdapter specCardAdapter = new SpecCardAdapter(specCardList);
        recyclerView.setAdapter(specCardAdapter);
        Log.d("TEST2", specCardList.toString());

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
}
