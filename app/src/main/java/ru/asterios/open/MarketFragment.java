package ru.asterios.open;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.asterios.open.adapter.MarketListAdapter;
import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.model.MarketItem;
import ru.asterios.open.util.CustomRequest;
import ru.asterios.open.util.Helper;

public class MarketFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    SearchView searchView = null;

    RecyclerView mRecyclerView;
    TextView mMessage, mHeaderText;

    FloatingActionButton mFabButton;

    ImageView mSplash;

    LinearLayout mHeaderContainer;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<MarketItem> itemsList;
    private MarketListAdapter itemsAdapter;

    public String queryText, currentQuery, oldQuery;

    public int itemCount;
    private int searchId = 0;
    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;
    private Boolean preload = true;

    int pastVisiblesItems = 0, visibleItemCount = 0, totalItemCount = 0;

    public MarketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_market, container, false);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new MarketListAdapter(getActivity(), itemsList);

            currentQuery = queryText = savedInstanceState.getString("queryText");

            preload = savedInstanceState.getBoolean("preload");
            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");
            searchId = savedInstanceState.getInt("searchId");
            itemCount = savedInstanceState.getInt("itemCount");

        } else {

            itemsList = new ArrayList<MarketItem>();
            itemsAdapter = new MarketListAdapter(getActivity(), itemsList);

            currentQuery = queryText = "";

            preload = true;
            restore = false;
            itemId = 0;
            searchId = 0;
            itemCount = 0;
        }

        mHeaderContainer = (LinearLayout) rootView.findViewById(R.id.container_header);
        mHeaderText = (TextView) rootView.findViewById(R.id.headerText);

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = (TextView) rootView.findViewById(R.id.message);
        mSplash = (ImageView) rootView.findViewById(R.id.splash);

        mFabButton = (FloatingActionButton) rootView.findViewById(R.id.fabButton);
        mFabButton.setImageResource(R.drawable.ic_action_new);

        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MarketNewItemActivity.class);
                startActivityForResult(intent, STREAM_NEW_POST);
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Helper.getGridSpanCount(getActivity()));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(itemsAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {

                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (!loadingMore) {

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && (viewMore) && !(mItemsContainer.isRefreshing())) {

                            Log.e("...", "Last Item Wow !");

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
                }
            }
        });

        itemsAdapter.setOnItemClickListener(new MarketListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, MarketItem obj, int position) {

                Intent intent = new Intent(getActivity(), MarketViewItemActivity.class);
                intent.putExtra("itemId", obj.getId());
                intent.putExtra("itemObj", (Parcelable) obj);
                startActivity(intent);
            }
        });

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        if (queryText.length() == 0) {

            if (itemsAdapter.getItemCount() == 0) {

                showMessage(getString(R.string.label_market_search_start_screen_msg));
                mHeaderText.setVisibility(View.GONE);
                mHeaderContainer.setVisibility(View.GONE);

            } else {

                if (preload) {

                    mHeaderText.setVisibility(View.GONE);
                    mHeaderContainer.setVisibility(View.GONE);

                } else {

                    mHeaderText.setVisibility(View.VISIBLE);
                    mHeaderContainer.setVisibility(View.VISIBLE);
                    mHeaderText.setText(getText(R.string.label_market_search_results) + " " + Integer.toString(itemCount));
                }

                hideMessage();
            }

        } else {

            if (itemsAdapter.getItemCount() == 0) {

                showMessage(getString(R.string.label_search_results_error));
                mHeaderText.setVisibility(View.GONE);
                mHeaderContainer.setVisibility(View.GONE);

            } else {

                mHeaderText.setVisibility(View.VISIBLE);
                mHeaderContainer.setVisibility(View.VISIBLE);
                mHeaderText.setText(getText(R.string.label_market_search_results) + " " + Integer.toString(itemCount));

                hideMessage();
            }
        }

        if (!restore) {

            if (preload) {

                preload();
            }
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == STREAM_NEW_POST && resultCode == getActivity().RESULT_OK && null != data) {

            itemId = 0;

            if (App.getInstance().isConnected() && currentQuery.length() != 0) {

                searchId = 0;
                search();

            } else {

                itemId = 0;
                preload();
            }

        }
    }

    @Override
    public void onRefresh() {

        currentQuery = queryText;

        currentQuery = currentQuery.trim();

        if (App.getInstance().isConnected() && currentQuery.length() != 0) {

            searchId = 0;
            search();

        } else {

            itemId = 0;
            preload();
        }
    }

    public String getCurrentQuery() {

        String searchText = searchView.getQuery().toString();
        searchText = searchText.trim();

        return searchText;
    }

    public void searchStart() {

        preload = false;

        currentQuery = getCurrentQuery();

        if (App.getInstance().isConnected()) {

            searchId = 0;
            search();

        } else {

            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putString("queryText", queryText);
        outState.putBoolean("restore", true);
        outState.putBoolean("preload", preload);
        outState.putInt("itemId", itemId);
        outState.putInt("searchId", searchId);
        outState.putInt("itemCount", itemCount);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

//        MenuInflater menuInflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_market_search, menu);

        MenuItem searchItem = menu.findItem(R.id.options_menu_main_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }

        if (searchView != null) {

            searchView.setQuery(queryText, false);

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);

            SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchAutoComplete.setHint(getText(R.string.placeholder_search));
            searchAutoComplete.setHintTextColor(getResources().getColor(R.color.white));

            searchView.clearFocus();

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

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_my_items: {

                Intent intent = new Intent(getActivity(), MarketMyItemsActivity.class);
                startActivityForResult(intent, 0);

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void search() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MARKET_SEARCH, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "MarketFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!loadingMore) {

                                itemsList.clear();
                            }

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemCount = response.getInt("itemCount");
                                oldQuery = response.getString("query");
                                searchId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray marketItemsArray = response.getJSONArray("items");

                                    arrayLength = marketItemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < marketItemsArray.length(); i++) {

                                            JSONObject marketItemObj = (JSONObject) marketItemsArray.get(i);

                                            MarketItem mMarketItem = new MarketItem(marketItemObj);

                                            itemsList.add(mMarketItem);
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

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "MarketFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
                Toast.makeText(getActivity(), getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("query", currentQuery);
                params.put("itemId", Integer.toString(searchId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void preload() {

        if (preload) {

            mItemsContainer.setRefreshing(true);

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MARKET_SEARCH_PRELOAD, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "MarketFragment Not Added to Activity");

                                return;
                            }

                            try {

                                if (!loadingMore) {

                                    itemsList.clear();
                                }

                                arrayLength = 0;

                                if (!response.getBoolean("error")) {

                                    itemId = response.getInt("itemId");

                                    if (response.has("items")) {

                                        JSONArray marketItemsArray = response.getJSONArray("items");

                                        arrayLength = marketItemsArray.length();

                                        if (arrayLength > 0) {

                                            for (int i = 0; i < marketItemsArray.length(); i++) {

                                                JSONObject marketItemObj = (JSONObject) marketItemsArray.get(i);

                                                MarketItem mMarketItem = new MarketItem(marketItemObj);

                                                itemsList.add(mMarketItem);
                                            }
                                        }
                                    }
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                Log.d("Success Response", response.toString());

                                loadingComplete();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (!isAdded() || getActivity() == null) {

                        Log.e("ERROR", "MarketFragment Not Added to Activity");

                        return;
                    }

                    loadingComplete();

                    Log.d("Error Response", error.toString());
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

        itemsAdapter.notifyDataSetChanged();

        loadingMore = false;

        mItemsContainer.setRefreshing(false);

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getString(R.string.label_market_results_error));
            mHeaderText.setVisibility(View.GONE);

        } else {

            hideMessage();

            if (preload) {

                mHeaderText.setVisibility(View.GONE);
                mHeaderContainer.setVisibility(View.GONE);

            } else {

                mHeaderContainer.setVisibility(View.VISIBLE);
                mHeaderText.setVisibility(View.VISIBLE);

                mHeaderText.setText(getText(R.string.label_market_search_results) + " " + Integer.toString(itemCount));
            }
        }
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        mSplash.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);

        mSplash.setVisibility(View.GONE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}