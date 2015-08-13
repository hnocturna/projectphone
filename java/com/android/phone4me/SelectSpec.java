package com.android.projectphone;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nocturna on 5/10/2015.
 */
public class SelectSpec extends ActionBarActivity {
    MySQLiteHelper db = new MySQLiteHelper(this);
    Double minInput = 0.0;
    Double maxInput = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> categoryTitles = new ArrayList<String>(db.returnSectionTitles());
        for (final String category : categoryTitles) {
            LinearLayout specCategoryLayout = (LinearLayout) findViewById(R.id.select_category_layout);
            Button categoryButton = new Button(this);
            RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            categoryButton.setText(category);
            specCategoryLayout.addView(categoryButton, buttonParams);
            categoryButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    setContentView(R.layout.select_spec);
                    loadSpecs(category);
                }
            });
        }
    }

    protected void OnStart() {
        super.onStart();
        Log.d("SelectSpec", "Started");

    }

    private void loadSpecs(final String category) {
        ArrayList<String> specs = new ArrayList<String>(db.returnColumns(category));
        for (final String spec : specs) {
            LinearLayout specLayout = (LinearLayout) findViewById(R.id.select_spec_layout);
            Button specButton = new Button(this);
            RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            specButton.setText(spec);
            specLayout.addView(specButton, buttonParams);
            specButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.input_spec);
                    inputSpecs(category, spec);
                }
            });
        }
    }

    private void inputSpecs(final String category, final String spec) {
        RelativeLayout inputSpecLayout = (RelativeLayout) findViewById(R.id.input_spec_layout);
        List<String> minMax = db.getMinMax(category, spec);
        RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        Button addSpecButton = (Button) findViewById(R.id.add_spec_button);

        final TextView maxTv = (TextView) findViewById(R.id.max_value);
        final TextView minTv = (TextView) findViewById(R.id.min_value);
        final Integer min = Integer.parseInt(minMax.get(0));
        final Integer max = Integer.parseInt(minMax.get(1));
        final Integer magnitude = Integer.parseInt(minMax.get(2));
        minInput = Double.parseDouble(minMax.get(0)) / (Math.pow(10, magnitude));
        maxInput = Double.parseDouble(minMax.get(1)) / (Math.pow(10, magnitude));

        if (magnitude == 0) {
            minTv.setText(Integer.toString(min));
            maxTv.setText(Integer.toString(max));
        } else {
            minTv.setText(Double.toString(min / (Math.pow(10, magnitude))));
            maxTv.setText(Double.toString(max / (Math.pow(10, magnitude))));

        }

        SeekBar minSeek = (SeekBar) findViewById(R.id.min_seek);
        minSeek.setMax(max - min);

        SeekBar maxSeek = (SeekBar) findViewById(R.id.max_seek);
        maxSeek.setMax(max - min);
        maxSeek.setProgress(max);

        minSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (min + progress);
                if (magnitude != 0) {
                    minInput = value / (Math.pow(10, magnitude));
                    minTv.setText(Double.toString(minInput));
                } else {
                    minTv.setText(Integer.toString(value));
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
                int value = (min + progress);
                if (magnitude != 0) {
                    maxInput = value / (Math.pow(10, magnitude));
                    maxTv.setText(Double.toString(maxInput));
                } else {
                    maxTv.setText(Integer.toString(value));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        CharSequence text = "Minimum value cannot be higher than maximum value!";
        int duration = Toast.LENGTH_SHORT;
        final Toast toast = Toast.makeText(getApplicationContext(), text, duration);

        addSpecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Specs.AddSpec", "Add spec button clicked!");
                Log.d("Specs.AddSpec", "Min: " + minInput + " | Max: " + maxInput);
                if (minInput > maxInput) {
                    toast.show();
                }
            }
        });

        // db.returnDecimalSpecs(category, spec, min, max);
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
