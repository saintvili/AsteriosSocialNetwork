package ru.asterios.open;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import ru.asterios.open.adapter.CommunityListAdapter;
import ru.asterios.open.app.App;
import ru.asterios.open.common.ActivityBase;
import ru.asterios.open.model.Group;
import ru.asterios.open.util.CustomRequest;


public class GroupSearchActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    Toolbar mToolbar;

    SearchView searchView = null;

    SwipeRefreshLayout mItemsContainer;

    LinearLayout mHeaderContainer;

    ListView mListView;
    TextView mMessage, mHeaderText;

    private ArrayList<Group> searchList;

    private CommunityListAdapter adapter;

    public String queryText, currentQuery, oldQuery;

    public int itemsCount;
    Boolean loadingMore = false;
    Boolean viewMore = false;
    private int arrayLength = 0;
    private int itemId = 0;
    private Boolean restore = false;
    private Boolean preload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);

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

        searchList = new ArrayList<Group>();
        adapter = new CommunityListAdapter(GroupSearchActivity.this, searchList);

        if (savedInstanceState != null) {

            searchList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            adapter = new CommunityListAdapter(GroupSearchActivity.this, searchList);

            preload = savedInstanceState.getBoolean("preload");
            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");
            itemsCount = savedInstanceState.getInt("itemsCount");
            currentQuery = queryText = savedInstanceState.getString("queryText");

        } else {

            searchList = new ArrayList<Group>();
            adapter = new CommunityListAdapter(GroupSearchActivity.this, searchList);

            preload = true;
            restore = false;
            itemId = 0;
            itemsCount = 0;
            currentQuery = queryText = "";
        }

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Group group = (Group) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(GroupSearchActivity.this, GroupActivity.class);
                intent.putExtra("groupId", group.getId());
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

                    if (preload) {

                        loadingMore = true;

                        preload();

                    } else {

                        currentQuery = getCurrentQuery();

                        if (currentQuery.equals(oldQuery)) {

                            loadingMore = true;

                            search();
                        }
                    }
                }
            }
        });

        if (queryText.length() == 0) {

            if (mListView.getAdapter().getCount() == 0) {

                showMessage(getString(R.string.label_group_search_start_screen_msg));
                mHeaderContainer.setVisibility(View.GONE);

            } else {

                if (preload) {

                    mHeaderText.setVisibility(View.GONE);
                    mHeaderContainer.setVisibility(View.GONE);

                } else {

                    mHeaderContainer.setVisibility(View.VISIBLE);
                    mHeaderText.setVisibility(View.VISIBLE);
                    mHeaderText.setText(getText(R.string.label_group_search_results) + " " + Integer.toString(itemsCount));
                }

                hideMessage();
            }

        } else {

            if (mListView.getAdapter().getCount() == 0) {

                showMessage(getString(R.string.label_search_results_error));
                mHeaderContainer.setVisibility(View.GONE);

            } else {

                mHeaderContainer.setVisibility(View.VISIBLE);
                mHeaderText.setText(getText(R.string.label_group_search_results) + " " + Integer.toString(itemsCount));

                hideMessage();
            }
        }

        if (!restore) {

            if (preload) {

                preload();
            }
        }

//        mSwipeRefreshLayout.setVisibility(View.GONE);
//        searchLoadScreen.setVisibility(View.GONE);
//        searchStartScreen.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("preload", preload);
        outState.putBoolean("restore", true);
        outState.putInt("itemId", itemId);
        outState.putInt("itemsCount", itemsCount);
        outState.putString("queryText", queryText);
        outState.putParcelableArrayList(STATE_LIST, searchList);
    }

    @Override
    public void onRefresh() {

        currentQuery = queryText;

        currentQuery = currentQuery.trim();

        if (App.getInstance().isConnected() && currentQuery.length() != 0) {

            itemId = 0;
            search();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    public void searchStart() {

        preload = false;

        currentQuery = getCurrentQuery();

        if (App.getInstance().isConnected()) {

            itemId = 0;
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

        SearchManager searchManager = (SearchManager) GroupSearchActivity.this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }

        if (searchView != null) {

            searchView.setQuery(queryText, false);

            searchView.setSearchableInfo(searchManager.getSearchableInfo(GroupSearchActivity.this.getComponentName()));
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

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_GROUP_SEARCH, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!loadingMore) {

                                searchList.clear();
                            }

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemsCount = response.getInt("itemsCount");
                                oldQuery = response.getString("query");
                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray usersArray = response.getJSONArray("items");

                                    arrayLength = usersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject profileObj = (JSONObject) usersArray.get(i);

                                            Group group = new Group(profileObj);

                                            searchList.add(group);
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
                params.put("itemId", Integer.toString(itemId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void preload() {

        if (preload) {

            mItemsContainer.setRefreshing(true);

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_GROUP_SEARCH_PRELOAD, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!loadingMore) {

                                    searchList.clear();
                                }

                                arrayLength = 0;

                                if (!response.getBoolean("error")) {

                                    itemsCount = response.getInt("itemsCount");
                                    itemId = response.getInt("itemId");

                                    if (response.has("items")) {

                                        JSONArray usersArray = response.getJSONArray("items");

                                        arrayLength = usersArray.length();

                                        if (arrayLength > 0) {

                                            for (int i = 0; i < usersArray.length(); i++) {

                                                JSONObject profileObj = (JSONObject) usersArray.get(i);

                                                Group group = new Group(profileObj);

                                                searchList.add(group);
                                            }
                                        }
                                    }
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                loadingComplete();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    loadingComplete();

                    Log.e("GROUPS PRELOAD ERROR", error.toString());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());
                    params.put("itemId", Integer.toString(itemId));

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }
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

            if (preload) {

                mHeaderContainer.setVisibility(View.GONE);
                mHeaderText.setVisibility(View.GONE);

            } else {

                mHeaderContainer.setVisibility(View.VISIBLE);
                mHeaderText.setVisibility(View.VISIBLE);

                mHeaderText.setText(getText(R.string.label_group_search_results) + " " + Integer.toString(itemsCount));
            }
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
