package ru.asterios.open;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import ru.asterios.open.adapter.PeopleListAdapter;
import ru.asterios.open.app.App;
import ru.asterios.open.common.ActivityBase;
import ru.asterios.open.model.Profile;
import ru.asterios.open.util.CustomRequest;


public class SearchActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    Toolbar mToolbar;

    SearchView searchView = null;

    SwipeRefreshLayout mItemsContainer;

    LinearLayout mHeaderContainer;

    ListView mListView;
    TextView mMessage, mHeaderText;

    private ArrayList<Profile> searchList;

    private PeopleListAdapter adapter;

    public String queryText, currentQuery, oldQuery;

    public int itemCount;
    Boolean loadingMore = false;
    Boolean viewMore = false;
    private int arrayLength = 0;
    private int userId = 0;
    private Boolean restore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");

        mHeaderContainer = (LinearLayout) findViewById(R.id.container_header);

        mItemsContainer = (SwipeRefreshLayout) findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mHeaderText = (TextView) findViewById(R.id.headerText);
        mMessage = (TextView) findViewById(R.id.message);
        mListView = (ListView) findViewById(R.id.listView);

        searchList = new ArrayList<Profile>();
        adapter = new PeopleListAdapter(SearchActivity.this, searchList);

        if (savedInstanceState != null) {

            searchList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            adapter = new PeopleListAdapter(SearchActivity.this, searchList);

            restore = savedInstanceState.getBoolean("restore");
            userId = savedInstanceState.getInt("userId");
            itemCount = savedInstanceState.getInt("itemCount");
            currentQuery = queryText = savedInstanceState.getString("queryText");

        } else {

            searchList = new ArrayList<Profile>();
            adapter = new PeopleListAdapter(SearchActivity.this, searchList);

            restore = false;
            userId = 0;
            itemCount = 0;
            currentQuery = queryText = "";
        }

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Profile profile = (Profile) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra("profileId", profile.getId());
                startActivity(intent);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mItemsContainer.isRefreshing())) {

                    currentQuery = getCurrentQuery();

                    if (currentQuery.equals(oldQuery) ) {

                        loadingMore = true;

                        search();
                    }
                }
            }
        });

        if (queryText.length() == 0) {

            if (mListView.getAdapter().getCount() == 0) {

                showMessage(getString(R.string.label_search_start_screen_msg));
                mHeaderContainer.setVisibility(View.GONE);

            } else {

                mHeaderContainer.setVisibility(View.VISIBLE);
                mHeaderText.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));

                hideMessage();
            }

        } else {

            if (mListView.getAdapter().getCount() == 0) {

                showMessage(getString(R.string.label_search_results_error));
                mHeaderContainer.setVisibility(View.GONE);

            } else {

                mHeaderContainer.setVisibility(View.VISIBLE);
                mHeaderText.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));

                hideMessage();
            }
        }

//        mSwipeRefreshLayout.setVisibility(View.GONE);
//        searchLoadScreen.setVisibility(View.GONE);
//        searchStartScreen.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putInt("userId", userId);
        outState.putInt("itemCount", itemCount);
        outState.putString("queryText", queryText);
        outState.putParcelableArrayList(STATE_LIST, searchList);
    }

    @Override
    public void onRefresh() {

        currentQuery = queryText;

        currentQuery = currentQuery.trim();

        if (App.getInstance().isConnected() && currentQuery.length() != 0) {

            userId = 0;
            search();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    public void searchStart() {

        currentQuery = getCurrentQuery();

        if (App.getInstance().isConnected()) {

            userId = 0;
            search();

        } else {

            Toast.makeText(getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public String getCurrentQuery() {

        String searchText = searchView.getQuery().toString();
        searchText = searchText.trim();

        return searchText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.options_menu_main_search);

        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }

        if (searchView != null) {

            searchView.setQuery(queryText, false);

            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);

            SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchAutoComplete.setHint(getText(R.string.placeholder_search));
            searchAutoComplete.setHintTextColor(getResources().getColor(R.color.white));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {

                    queryText = newText;

                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {

                    queryText = query;
                    searchStart();

                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed(){

        finish();
    }

    public void search() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_APP_SEARCH, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!loadingMore) {

                                searchList.clear();
                            }

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemCount = response.getInt("itemCount");
                                oldQuery = response.getString("query");
                                userId = response.getInt("userId");

                                if (response.has("users")) {

                                    JSONArray usersArray = response.getJSONArray("users");

                                    arrayLength = usersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject profileObj = (JSONObject) usersArray.get(i);

                                            Profile profile = new Profile(profileObj);

                                            searchList.add(profile);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();

//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
                Toast.makeText(getApplicationContext(), getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("query", currentQuery);
                params.put("userId", Integer.toString(userId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        adapter.notifyDataSetChanged();

        loadingMore = false;

        mItemsContainer.setRefreshing(false);

        if (mListView.getAdapter().getCount() == 0) {

            showMessage(getString(R.string.label_search_results_error));
            mHeaderContainer.setVisibility(View.GONE);

        } else {

            hideMessage();
            mHeaderContainer.setVisibility(View.VISIBLE);

            mHeaderText.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));
        }
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);
    }
}
