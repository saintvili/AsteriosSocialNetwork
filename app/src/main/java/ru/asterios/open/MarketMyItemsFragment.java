package ru.asterios.open;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class MarketMyItemsFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    RecyclerView mRecyclerView;
    TextView mMessage;

    FloatingActionButton mFabButton;

    ImageView mSplash;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<MarketItem> itemsList;
    private MarketListAdapter itemsAdapter;

    public int itemCount;
    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;
    private Boolean preload = true;

    int pastVisiblesItems = 0, visibleItemCount = 0, totalItemCount = 0;

    public MarketMyItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_market_my_items, container, false);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new MarketListAdapter(getActivity(), itemsList);

            preload = savedInstanceState.getBoolean("preload");
            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");
            itemCount = savedInstanceState.getInt("itemCount");

        } else {

            itemsList = new ArrayList<MarketItem>();
            itemsAdapter = new MarketListAdapter(getActivity(), itemsList);

            preload = true;
            restore = false;
            itemId = 0;
            itemCount = 0;
        }

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

                            loadingMore = true;

                            getItems();
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

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == STREAM_NEW_POST && resultCode == getActivity().RESULT_OK && null != data) {

            itemId = 0;

            if (App.getInstance().isConnected()) {

                itemId = 0;
                getItems();
            }

        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            itemId = 0;
            getItems();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putBoolean("preload", preload);
        outState.putInt("itemId", itemId);
        outState.putInt("itemCount", itemCount);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getItems() {

        if (preload) {

            mItemsContainer.setRefreshing(true);

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MARKET_GET_MY_ITEMS, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "MarketMyItemsFragment Not Added to Activity");

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

                        Log.e("ERROR", "MarketMyItemsFragment Not Added to Activity");

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

            showMessage(getString(R.string.label_empty_list));

        } else {

            hideMessage();
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