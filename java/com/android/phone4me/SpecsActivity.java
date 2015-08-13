package com.android.projectphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;


public class SpecsActivity extends ActionBarActivity {
    RelativeLayout activitySpecsLayout;
    MySQLiteHelper db = new MySQLiteHelper(this);
    Double minInput = 0.0;
    Double maxInput = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specs);
        activitySpecsLayout = (RelativeLayout) findViewById(R.id.activity_specs_layout);

        Button addSpecButton = new Button(this);
        addSpecButton.setText("Add another spec");
        Spinner specSpinner = new Spinner(SpecsActivity.this);
        RelativeLayout.LayoutParams addSpecButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addSpecButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addSpecButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);

        activitySpecsLayout.addView(addSpecButton, addSpecButtonParams);
        addSpecButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpecsActivity.this, SelectSpec.class);
                startActivity(intent);
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
