package com.android.projectphone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.http.util.ExceptionUtils;


public class KnownActivity extends ActionBarActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_known);
        // Pass the phone values and bitmaps to the UI
        GridLayout activity_known_gl = (GridLayout) findViewById(R.id.activity_known_gl); // GridLayout best used for stacked buttons

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) KnownActivity.this.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        final float width = metrics.widthPixels; // Opens service to retrieve width of phone in px
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()); // Converts px to dp
        // ViewGroup.LayoutParams buttonParam = new ViewGroup.LayoutParams( (int) ((width - (100 * pixels))/2) , (int) (120 * pixels)); // Set parameters for button size
        ViewGroup.LayoutParams buttonParam = new ViewGroup.LayoutParams((int) ((width - (60 * pixels)) / 2), (int) (120 * pixels));

        for (int i = 0; i < MainActivity.phoneArray.size(); i++) {
            ImageButton button = new ImageButton(KnownActivity.this);
            // button.setText(phoneArray.get(i));
            // button.setTextSize(10f);
            button.setId(i);
            if (MainActivity.phoneBitmapArray.get(i) == null) {
                // If image is null, show which image is having issues
                Log.d(MainActivity.phoneArray.get(i), "No Image Available");
            }
            button.setImageBitmap(MainActivity.phoneBitmapArray.get(i));
            button.setScaleType(ImageView.ScaleType.CENTER_CROP);
            button.setLayoutParams(buttonParam);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Override the main onClick() action to apply to each of the dynamically generated buttons
                    String phoneURL = MainActivity.phoneURLArray.get(v.getId());
                    String phone = MainActivity.phoneArray.get(v.getId());
                    // Log.d("TestOutput",phone + ": " + phoneURL); // Debug purposes
                    Intent intent = new Intent(KnownActivity.this, PhoneSpecs.class);
                    intent.putExtra("phoneURL", phoneURL); // Pass the phoneURL containing specs to the SpecActivity
                    intent.putExtra("phone", phone); // Pass the phone name to the SpecActivity to be used as the title
                    startActivity(intent);
                }
            });

            activity_known_gl.addView(button); // Add dynamically generated buttons to the gridview
        }
        TextView tv = (TextView) findViewById(R.id.sample_text); // Not used
    }
    // Spinner makeSpinner = (Spinner) findViewById(R.id.known_make_spinner);
    // final Spinner modelSpinner = (Spinner) findViewById(R.id.known_model_spinner);
    // modelSpinner.setVisibility(View.GONE);
    // final TextView modelTextView = (TextView) findViewById(R.id.known_model_prompt);
    // modelTextView.setVisibility(View.GONE);
    // ArrayAdapter<CharSequence> makeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.make_array, R.layout.my_spinner);
    // makeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // makeSpinner.setAdapter(makeSpinnerAdapter);

    // String title = doc.title();

        /* makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String makeSelection = parent.getSelectedItem().toString();
                if (makeSelection.equalsIgnoreCase("samsung")) {
                    modelTextView.setVisibility(View.VISIBLE);
                    modelSpinner.setVisibility(View.VISIBLE);
                    ArrayAdapter<CharSequence> modelAdapter = ArrayAdapter.createFromResource(KnownActivity.this, R.array.samsung_model_array, R.layout.my_spinner);
                    modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    modelSpinner.setAdapter(modelAdapter);
                }

                else {
                    modelTextView.setVisibility(View.GONE);
                    modelSpinner.setVisibility(View.GONE);
                    ArrayAdapter<CharSequence> modelAdapter = ArrayAdapter.createFromResource(KnownActivity.this, R.array.blank_array, R.layout.my_spinner);
                    modelSpinner.setAdapter(modelAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    } */

    public void onClick(View v) {

    }

    protected void OnStart() {
        super.onStart();
        Log.d("KnownActivity", "Started");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_known, menu);
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
