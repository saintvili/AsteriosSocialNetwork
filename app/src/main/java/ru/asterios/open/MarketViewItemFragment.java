package ru.asterios.open;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.dialogs.ItemDeleteDialog;
import ru.asterios.open.model.MarketItem;
import ru.asterios.open.util.Api;
import ru.asterios.open.util.Helper;


public class MarketViewItemFragment extends Fragment implements Constants {

    private ProgressDialog pDialog;

    RelativeLayout mLoadingScreen;
    LinearLayout mContentScreen;

    Button mItemViewAuthorProfile;

    TextView mItemDate, mItemTitle, mItemPrice, mItemDescription;
    ImageView mItemImg;

    long itemId = 0;
    int arrayLength = 0;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

    MarketItem item;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;
    private Boolean loadingComplete = false;

    public MarketViewItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        Intent i = getActivity().getIntent();

        itemId = i.getLongExtra("itemId", 0);
        item = (MarketItem) i.getParcelableExtra("itemObj");

        getActivity().setTitle(getString(R.string.title_activity_market_view_item));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_market_view_item, container, false);

        if (savedInstanceState != null) {

            restore = savedInstanceState.getBoolean("restore");
            loading = savedInstanceState.getBoolean("loading");
            preload = savedInstanceState.getBoolean("preload");

        } else {

            restore = false;
            loading = false;
            preload = false;
        }

        if (loading) {

            showpDialog();
        }

        mLoadingScreen = (RelativeLayout) rootView.findViewById(R.id.loadingScreen);

        mContentScreen = (LinearLayout) rootView.findViewById(R.id.contentScreen);

        mItemViewAuthorProfile = (Button) rootView.findViewById(R.id.itemViewAuthorProfile);

        mItemPrice = (TextView) rootView.findViewById(R.id.itemPrice);
        mItemDescription = (TextView) rootView.findViewById(R.id.itemDescription);

        mItemDate = (TextView) rootView.findViewById(R.id.itemDate);
        mItemTitle = (TextView) rootView.findViewById(R.id.itemTitle);
        mItemImg = (ImageView) rootView.findViewById(R.id.itemImg);

        if (!restore) {

            if (App.getInstance().isConnected()) {

                showLoadingScreen();

                loadingComplete();
                updateItem();
            }

        } else {

            if (App.getInstance().isConnected()) {

                if (!preload) {

                    loadingComplete();
                    updateItem();

                } else {

                    showLoadingScreen();
                }
            }
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putBoolean("loading", loading);
        outState.putBoolean("preload", preload);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateItem() {

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        getActivity().setTitle(item.getTitle());

        mItemDate.setText(item.getDate());

        mItemViewAuthorProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", item.getFromUserId());
                getActivity().startActivity(intent);
            }
        });

        if (item.getTitle().length() > 0) {

            mItemTitle.setText(item.getTitle());

            mItemTitle.setVisibility(View.VISIBLE);

        } else {

            mItemTitle.setVisibility(View.GONE);
        }

        if (item.getContent().length() != 0) {

            mItemDescription.setText(item.getContent().replaceAll("<br>", "\n"));
            mItemDescription.setVisibility(View.VISIBLE);

        } else {

            mItemDescription.setVisibility(View.GONE);
        }

        Helper helper = new Helper();

        mItemPrice.setText(getString(R.string.label_currency) + helper.getFormatedAmount(item.getPrice()));

        if (item.getImgUrl().length() > 0) {

            imageLoader.get(item.getImgUrl(), ImageLoader.getImageListener(mItemImg, R.drawable.img_loading, R.drawable.img_loading));
            mItemImg.setVisibility(View.VISIBLE);

            mItemImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getActivity(), PhotoViewActivity.class);
                    i.putExtra("imgUrl", item.getImgUrl());
                    getActivity().startActivity(i);
                }
            });

        } else {

            mItemImg.setVisibility(View.GONE);
        }
    }

    public void onItemDelete(final int position) {

        Api api = new Api(getActivity());

        api.marketItemDelete(item.getId());

        getActivity().finish();
    }

    public void onItemRemove(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        ItemDeleteDialog alert = new ItemDeleteDialog();

        Bundle b = new Bundle();
        b.putInt("position", 0);

        alert.setArguments(b);
        alert.show(fm, "alert_dialog_post_delete");
    }

    public void loadingComplete() {

        showContentScreen();
    }

    public void showLoadingScreen() {

        preload = true;

        mContentScreen.setVisibility(View.GONE);

        mLoadingScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showContentScreen() {

        preload = false;

        mLoadingScreen.setVisibility(View.GONE);

        mContentScreen.setVisibility(View.VISIBLE);

        loadingComplete = true;

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.menu_market_view_item, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        if (loadingComplete) {

            if (App.getInstance().getId() != item.getFromUserId()) {

                hideMenuItems(menu, false);

            } else {

                //show all menu items
                hideMenuItems(menu, true);
            }

        } else {

            //hide all menu items
            hideMenuItems(menu, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_remove: {

                // remove item

                onItemRemove(0);

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void hideMenuItems(Menu menu, boolean visible) {

        for (int i = 0; i < menu.size(); i++){

            menu.getItem(i).setVisible(visible);
        }
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