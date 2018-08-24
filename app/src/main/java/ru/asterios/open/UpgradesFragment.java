package ru.asterios.open;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.util.CustomRequest;

public class UpgradesFragment extends Fragment implements Constants {

    private ProgressDialog pDialog;

    Button mGetCreditsButton, mGhostModeButton, mVerifiedBadgeButton, mDisableAdsButton;
    TextView mLabelCredits, mLabelGhostModeStatus, mLabelVerifiedBadgeStatus, mLabelDisableAdsStatus;

    private Boolean loading = false;

    public UpgradesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_upgrades, container, false);

        if (loading) {

            showpDialog();
        }

        mLabelCredits = (TextView) rootView.findViewById(R.id.labelCredits);

        mLabelGhostModeStatus = (TextView) rootView.findViewById(R.id.labelGhostModeStatus);
        mLabelVerifiedBadgeStatus = (TextView) rootView.findViewById(R.id.labelVerifiedBadgeStatus);
        mLabelDisableAdsStatus = (TextView) rootView.findViewById(R.id.labelDisableAdsStatus);

        mGhostModeButton = (Button) rootView.findViewById(R.id.ghostModeBtn);
        mVerifiedBadgeButton = (Button) rootView.findViewById(R.id.verifiedBadgeBtn);
        mDisableAdsButton = (Button) rootView.findViewById(R.id.disableAdsBtn);

        mGetCreditsButton = (Button) rootView.findViewById(R.id.getCreditsBtn);

        mGetCreditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), BalanceActivity.class);
                startActivityForResult(i, 1945);
            }
        });

        mGhostModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getBalance() >= GHOST_MODE_COST) {

                    setGhostMode();

                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_credits), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mVerifiedBadgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getBalance() >= VERIFIED_BADGE_COST) {

                    setVerifiedBadge();

                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_credits), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDisableAdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getBalance() >= DISABLE_ADS_COST) {

                    setDisableAds();

                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_credits), Toast.LENGTH_SHORT).show();
                }
            }
        });

        update();

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1945 && resultCode == getActivity().RESULT_OK && null != data) {

            update();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    @Override
    public void onStart() {

        super.onStart();

        update();
    }

    public void update() {

        mLabelCredits.setText(getString(R.string.label_credits) + " (" + Integer.toString(App.getInstance().getBalance()) + ")");

        mGhostModeButton.setText(getString(R.string.action_enable) + " (" + Integer.toString(GHOST_MODE_COST) + ")");
        mVerifiedBadgeButton.setText(getString(R.string.action_enable) + " (" + Integer.toString(VERIFIED_BADGE_COST) + ")");
        mDisableAdsButton.setText(getString(R.string.action_enable) + " (" + Integer.toString(DISABLE_ADS_COST) + ")");

        if (App.getInstance().getGhost() == 0) {

            mLabelGhostModeStatus.setVisibility(View.GONE);
            mGhostModeButton.setEnabled(true);
            mGhostModeButton.setVisibility(View.VISIBLE);

        } else {

            mLabelGhostModeStatus.setVisibility(View.VISIBLE);
            mGhostModeButton.setEnabled(false);
            mGhostModeButton.setVisibility(View.GONE);
        }

        if (App.getInstance().getVerify() == 0) {

            mLabelVerifiedBadgeStatus.setVisibility(View.GONE);
            mVerifiedBadgeButton.setEnabled(true);
            mVerifiedBadgeButton.setVisibility(View.VISIBLE);

        } else {

            mLabelVerifiedBadgeStatus.setVisibility(View.VISIBLE);
            mVerifiedBadgeButton.setEnabled(false);
            mVerifiedBadgeButton.setVisibility(View.GONE);
        }

        if (App.getInstance().getAdmob() == ADMOB_ENABLED) {

            mLabelDisableAdsStatus.setVisibility(View.GONE);
            mDisableAdsButton.setEnabled(true);
            mDisableAdsButton.setVisibility(View.VISIBLE);

        } else {

            mLabelDisableAdsStatus.setVisibility(View.VISIBLE);
            mDisableAdsButton.setEnabled(false);
            mDisableAdsButton.setVisibility(View.GONE);
        }
    }

    public void setGhostMode() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_GHOST_MODE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setBalance(App.getInstance().getBalance() - GHOST_MODE_COST);
                                App.getInstance().setGhost(1);

                                Toast.makeText(getActivity(), getString(R.string.msg_success_ghost_mode), Toast.LENGTH_SHORT).show();

                                update();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();

                            update();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                update();

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("cost", Integer.toString(GHOST_MODE_COST));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void setVerifiedBadge() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_VERIFIED_BADGE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setBalance(App.getInstance().getBalance() - VERIFIED_BADGE_COST);
                                App.getInstance().setVerify(1);

                                Toast.makeText(getActivity(), getString(R.string.msg_success_verified_badge), Toast.LENGTH_SHORT).show();

                                update();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();

                            update();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                update();

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("cost", Integer.toString(VERIFIED_BADGE_COST));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void setDisableAds() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_DISABLE_ADS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setBalance(App.getInstance().getBalance() - DISABLE_ADS_COST);
                                App.getInstance().setAdmob(ADMOB_DISABLED);

                                Toast.makeText(getActivity(), getString(R.string.msg_success_disable_ads), Toast.LENGTH_SHORT).show();

                                ((MainActivity)getActivity()).hideAds();

                                update();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();

                            update();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                update();

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("cost", Integer.toString(DISABLE_ADS_COST));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}