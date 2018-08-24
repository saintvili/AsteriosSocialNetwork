package ru.asterios.open;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
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

public class PrivacySettingsFragment extends PreferenceFragment implements Constants {

    private CheckBoxPreference mAllowInfo, mAllowVideos, mAllowFriends, mAllowPhotos, mAllowGifts;

    private ProgressDialog pDialog;

    int allowPhotos, allowFriends, allowInfo, allowGifts, allowVideos;

    private Boolean loading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.privacy_settings);

        mAllowPhotos = (CheckBoxPreference) getPreferenceManager().findPreference("allowPhotos");

        mAllowPhotos.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        allowPhotos = 1;

                    } else {

                        allowPhotos = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        saveSettings();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        mAllowFriends = (CheckBoxPreference) getPreferenceManager().findPreference("allowFriends");

        mAllowFriends.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        allowFriends = 1;

                    } else {

                        allowFriends = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        saveSettings();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        mAllowVideos = (CheckBoxPreference) getPreferenceManager().findPreference("allowVideos");

        mAllowVideos.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        allowVideos = 1;

                    } else {

                        allowVideos = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        saveSettings();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        mAllowGifts = (CheckBoxPreference) getPreferenceManager().findPreference("allowGifts");

        mAllowGifts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        allowGifts = 1;

                    } else {

                        allowGifts = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        saveSettings();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        mAllowInfo = (CheckBoxPreference) getPreferenceManager().findPreference("allowInfo");

        mAllowInfo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        allowInfo = 1;

                    } else {

                        allowInfo = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        saveSettings();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        checkAllowPhotos(App.getInstance().getAllowShowMyPhotos());
        checkAllowFriends(App.getInstance().getAllowShowMyFriends());
        checkAllowVideos(App.getInstance().getAllowShowMyVideos());
        checkAllowGifts(App.getInstance().getAllowShowMyGifts());
        checkAllowInfo(App.getInstance().getAllowShowMyInfo());
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }

        if (loading) {

            showpDialog();
        }
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", loading);
    }

    public void checkAllowPhotos(int value) {

        if (value == 1) {

            mAllowPhotos.setChecked(true);
            allowPhotos = 1;

        } else {

            mAllowPhotos.setChecked(false);
            allowPhotos = 0;
        }
    }

    public void checkAllowFriends(int value) {

        if (value == 1) {

            mAllowFriends.setChecked(true);
            allowFriends = 1;

        } else {

            mAllowFriends.setChecked(false);
            allowFriends = 0;
        }
    }

    public void checkAllowVideos(int value) {

        if (value == 1) {

            mAllowVideos.setChecked(true);
            allowVideos = 1;

        } else {

            mAllowVideos.setChecked(false);
            allowVideos = 0;
        }
    }

    public void checkAllowGifts(int value) {

        if (value == 1) {

            mAllowGifts.setChecked(true);
            allowGifts = 1;

        } else {

            mAllowGifts.setChecked(false);
            allowGifts = 0;
        }
    }

    public void checkAllowInfo(int value) {

        if (value == 1) {

            mAllowInfo.setChecked(true);
            allowInfo = 1;

        } else {

            mAllowInfo.setChecked(false);
            allowInfo = 0;
        }
    }

    public void saveSettings() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_PRIVACY, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setAllowShowMyPhotos(response.getInt("allowShowMyPhotos"));
                                App.getInstance().setAllowShowMyGifts(response.getInt("allowShowMyGifts"));
                                App.getInstance().setAllowShowMyFriends(response.getInt("allowShowMyFriends"));
                                App.getInstance().setAllowShowMyVideos(response.getInt("allowShowMyVideos"));
                                App.getInstance().setAllowShowMyInfo(response.getInt("allowShowMyInfo"));

                                checkAllowPhotos(App.getInstance().getAllowShowMyPhotos());
                                checkAllowGifts(App.getInstance().getAllowShowMyGifts());
                                checkAllowFriends(App.getInstance().getAllowShowMyFriends());
                                checkAllowVideos(App.getInstance().getAllowShowMyVideos());
                                checkAllowInfo(App.getInstance().getAllowShowMyInfo());
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();

                            Log.e("Privacy Success", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();

                Log.d("Privacy Error", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("allowShowMyPhotos", Integer.toString(allowPhotos));
                params.put("allowShowMyGifts", Integer.toString(allowGifts));
                params.put("allowShowMyFriends", Integer.toString(allowFriends));
                params.put("allowShowMyVideos", Integer.toString(allowVideos));
                params.put("allowShowMyInfo", Integer.toString(allowInfo));

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

        if (!pDialog.isShowing())
            pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}