package com.android.projectphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SpecsPhoneResults extends ActionBarActivity {
    // Activity displays all phones that meet the user requirements.
    MySQLiteHelper db = new MySQLiteHelper(this);
    private int itemsPerPage = 12;
    private int loadNextItemsThreshold = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specs_phone_results);
        final GridView gridView = (GridView) findViewById(R.id.results_gv);

        List<String> phones = null;
        String search = null;
        Map<String, String> imageURLMap;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phones = new LinkedList<String> (extras.getStringArrayList("phones"));
        }

        final List<String> phoneList = new LinkedList<String>(phones);
        /*if (phones != null) {
            imageURLMap = db.getImageURL(phones);
        } */
        // List<String> phoneList = new LinkedList(imageURLMap.keySet());

        final ThumbnailLoader thumbnailLoader = new ThumbnailLoader(phones);
        thumbnailLoader.LoadMoreItems(itemsPerPage);

        final ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter(SpecsPhoneResults.this, thumbnailLoader);
        gridView.setAdapter(thumbnailAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SpecsPhoneResults.this, PhoneSpecs.class);
                Log.d("SpecsPhoneResults", position + " clicked!");
                intent.putExtra("phone", phoneList.get(position));
                startActivity(intent);
            }
        });

        int i = 0;

        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemCount) {
                
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (view.getLastVisiblePosition() >= thumbnailLoader.getPhoneListSize() - 2 && thumbnailLoader.canLoadMoreItems && !thumbnailLoader.isBusy) {
                    thumbnailLoader.isBusy = true;
                    thumbnailLoader.LoadMoreItems(itemsPerPage);
                    thumbnailAdapter.notifyDataSetChanged();
                    // gridView.invalidateViews();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_specs_phone_results, menu);
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
