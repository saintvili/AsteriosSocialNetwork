package ru.asterios.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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


public class FollowingsActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    Toolbar mToolbar;

    SwipeRefreshLayout mItemsContainer;

    ListView mListView;
    TextView mMessage;

    private ArrayList<Profile> usersList;

    private PeopleListAdapter usersAdapter;

    long itemId = 0, profileId = 0;
    int arrayLength = 0;
    Boolean loadingMore = false;
    Boolean viewMore = false;
    private Boolean restore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mItemsContainer = (SwipeRefreshLayout) findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = (TextView) findViewById(R.id.message);
        mListView = (ListView) findViewById(R.id.listView);

        if (savedInstanceState != null) {

            usersList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            usersAdapter = new PeopleListAdapter(FollowingsActivity.this, usersList);

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getLong("itemId");
            profileId = savedInstanceState.getLong("profileId");

        } else {

            usersList = new ArrayList<Profile>();
            usersAdapter = new PeopleListAdapter(FollowingsActivity.this, usersList);

            restore = false;

            Intent i = getIntent();

            profileId = i.getLongExtra("profileId", 0);
            itemId = 0;
        }

        mListView.setAdapter(usersAdapter);

        if (usersAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Profile profile = (Profile) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(FollowingsActivity.this, ProfileActivity.class);
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

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getFollowings();
                    }
                }
            }
        });

        if (!restore) {

            getFollowings();
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putLong("itemId", itemId);
        outState.putLong("profileId", profileId);
        outState.putParcelableArrayList(STATE_LIST, usersList);
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            itemId = 0;
            getFollowings();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    public void getFollowings() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_FOLLOWINGS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!loadingMore) {

                            usersList.clear();
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("id");

                                if (response.has("friends")) {

                                    JSONArray usersArray = response.getJSONArray("friends");

                                    arrayLength = usersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject userObj = (JSONObject) usersArray.get(i);

                                            Profile profile = new Profile(userObj);

                                            usersList.add(profile);
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
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profileId));
                params.put("itemId", Long.toString(itemId));
                params.put("language", "en");

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

        usersAdapter.notifyDataSetChanged();

        if (loadingMore) {

            loadingMore = false;
        }

        if (mListView.getAdapter().getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        if (mItemsContainer.isRefreshing()) {

            mItemsContainer.setRefreshing(false);
        }
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);
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
}
