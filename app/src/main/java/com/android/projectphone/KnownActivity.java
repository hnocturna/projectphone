package com.android.projectphone;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class KnownActivity extends ActionBarActivity implements View.OnClickListener {
    MySQLiteHelper db = new MySQLiteHelper(this);
    int itemsPerPage = 12;
    int loadNextItemsThreshold = 2;

    boolean scrolling = false;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 6;
    int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;

    String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_known);
        // Pass the phone values and bitmaps to the UI

        final List<CardInfo> phoneCardList = new LinkedList<>();
        List<String> phones;

        Bundle extras = getIntent().getExtras();
        if (extras.getStringArrayList("phoneList") != null) {
            phones = new LinkedList<>(extras.getStringArrayList("phoneList"));
        } else {
            phones = new LinkedList<String>(db.getPhones());
        }
        if (extras.getString("activity") != null) {
            activity = extras.getString("activity");
        }

        loadThumbnails(phones);
        /*Map<String, String> phoneURLMap = new LinkedHashMap<>(db.getImageURL(phones));
        for (String phone : phoneURLMap.keySet()) {
            String phoneURL = phoneURLMap.get(phone);
            phoneCardList.add(new CardInfo(phone, phoneURL));
        }

        /*TextView noPhonesTV = (TextView) findViewById(R.id.no_phones_tv);
        noPhonesTV.setVisibility(View.GONE);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(false);
        final GridLayoutManager glm = new GridLayoutManager(KnownActivity.this, 2);
        recyclerView.setLayoutManager(glm);



        ThumbnailCardAdapter thumbnailCardAdapter = new ThumbnailCardAdapter(phoneCardList);
        recyclerView.setAdapter(thumbnailCardAdapter);
        thumbnailCardAdapter.setOnItemClickListener(new ThumbnailCardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                Intent intent = new Intent(KnownActivity.this, PhoneSpecs.class);
                intent.putExtra("phone", phoneCardList.get(position).getTitle());
                startActivity(intent);
            }
        });


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recView, int dx, int dy) {
                super.onScrolled(recView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = glm.getItemCount();
                firstVisibleItem = glm.findFirstCompletelyVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {


                    loading = true;
                }
            }
        }); */



        /* final GridView gridView = (GridView) findViewById(R.id.known_gv);


        final ThumbnailLoader thumbnailLoader = new ThumbnailLoader(phones);
        thumbnailLoader.LoadMoreItems(itemsPerPage);

        final ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter(KnownActivity.this, thumbnailLoader);
        gridView.setAdapter(thumbnailAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(KnownActivity.this, PhoneSpecs.class);
                Log.d("KnownActivity", position + " clicked!");
                intent.putExtra("phone", phones.get(position));
                startActivity(intent);
            }
        });

        /* FloatingActionButton searchFAB = (FloatingActionButton) findViewById(R.id.search_fab);
        searchFAB.attachToListView(gridView);
        searchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        }); */
    }

    public void loadThumbnails(List<String> phoneList) {
        TextView noPhonesTV = (TextView) findViewById(R.id.no_phones_tv);


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(false);
        final GridLayoutManager glm = new GridLayoutManager(KnownActivity.this, 2);
        recyclerView.addItemDecoration(new ThumbnailItemDecoration(48, 2));
        recyclerView.setLayoutManager(glm);

        final FloatingActionButton resetSearchFAB = (FloatingActionButton) findViewById(R.id.search_again_fab);
        resetSearchFAB.attachToRecyclerView(recyclerView);

        if (activity != null) {
            resetSearchFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_reply_white_24dp));
        }

        resetSearchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    Intent intent;
                    if (activity.equals("interest")) {
                        intent = new Intent(KnownActivity.this, NewbieActivity.class);
                        startActivity(intent);
                    } else if (activity.equals("specs")) {
                        intent = new Intent(KnownActivity.this, SpecsActivity.class);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    loadThumbnails(db.getPhones());
                }


            }
        });

        final List<CardInfo> phoneCardList = new LinkedList<>();

        if (phoneList == null || phoneList.isEmpty()) {
            noPhonesTV.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noPhonesTV.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            Map<String, String> phoneURLMap = new LinkedHashMap<>(db.getImageURL(phoneList));
            for (String phone : phoneURLMap.keySet()) {
                String phoneURL = phoneURLMap.get(phone);
                phoneCardList.add(new CardInfo(phone, phoneURL));
            }

            final ThumbnailCardLoader thumbnailCardLoader = new ThumbnailCardLoader(phoneCardList);
            thumbnailCardLoader.loadMoreItems(itemsPerPage);

            final ThumbnailCardAdapter thumbnailCardAdapter = new ThumbnailCardAdapter(thumbnailCardLoader);
            recyclerView.setAdapter(thumbnailCardAdapter);
            thumbnailCardAdapter.setOnItemClickListener(new ThumbnailCardAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View v, int position) {
                    Intent intent = new Intent(KnownActivity.this, PhoneSpecs.class);
                    intent.putExtra("phone", phoneCardList.get(position).getTitle());
                    startActivity(intent);
                }
            });

            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recView, int dx, int dy) {
                    super.onScrolled(recView, dx, dy);
                    AsyncTask asynctask = new TimedShowFAB();
                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = glm.getItemCount();
                    lastVisibleItem = glm.findLastCompletelyVisibleItemPosition();
                    firstVisibleItem = glm.findFirstCompletelyVisibleItemPosition();

                    if (dy > 0) {
                        resetSearchFAB.hide();
                        if (!scrolling) {
                            scrolling = true;
                            new TimedShowFAB().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, resetSearchFAB);
                        }

                    } else {
                        resetSearchFAB.show();
                    }
                    if (lastVisibleItem >= loadNextItemsThreshold - thumbnailCardLoader.getPhoneCardListSize() && thumbnailCardLoader.canLoadMoreItems && !thumbnailCardLoader.isBusy) {
                        thumbnailCardLoader.isBusy = true;
                        thumbnailCardLoader.loadMoreItems(itemsPerPage);
                        thumbnailCardAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void startTask(AsyncTask asyncTask) {

    }

    public class TimedShowFAB extends AsyncTask<FloatingActionButton, Void, Void> {
        FloatingActionButton searchFAB;
        boolean skip = false;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(FloatingActionButton... arg0) {
            searchFAB = arg0[0];

            try {
                Thread.sleep(1800);
            } catch (InterruptedException ie) {

            } finally {
                try {
                    searchFAB.show();
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            scrolling = false;
        }
    }

    public void onClick(View v) {

    }

    protected void OnStart() {
        super.onStart();
        Log.d("KnownActivity", "Started");

    }

    public void onSearch(String phoneSearch) {
        List<String> phoneList;
        if (phoneSearch != null ) {
            phoneList = db.searchPhones(phoneSearch);
        } else {
            phoneList = db.getPhones();
        }
        loadThumbnails(phoneList);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_known, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            searchView.setVisibility(View.GONE);
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                if (!query.isEmpty()) {
                    onSearch(query);
                } else {
                    onSearch(null);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    onSearch(newText);

                } else {
                    onSearch(null);
                }
                return false;
            }
        });
        return true;

    }

    @Override
    public void onBackPressed() {
        finish();
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
