package ru.asterios.open;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.LinearLayout;
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

public class SettingsFragment extends PreferenceFragment implements Constants {

    private Preference logoutPreference, itemContactUs, aboutPreference, changePassword, itemServices, itemTerms, itemThanks, itemReferralsList, itemBlackList, itemNotifications, itemDeactivateAccount, itemPrivacy;
    private CheckBoxPreference allowComments, allowMessages, allowPhotosComments;

    private ProgressDialog pDialog;

    int mAllowComments, mAllowMessages, mAllowPhotosComments;

    LinearLayout aboutDialogContent;
    TextView aboutDialogAppName, aboutDialogAppVersion, aboutDialogAppCopyright;

    private Boolean loading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        Preference pref = findPreference("settings_version");

        pref.setTitle(getString(R.string.app_name) + " v" + getString(R.string.app_version));

        pref = findPreference("settings_logout");

        pref.setSummary(App.getInstance().getUsername());

//        pref = findPreference("settings_copyright_info");
//
//        pref.setSummary(APP_COPYRIGHT + " © " + APP_YEAR);

        logoutPreference = findPreference("settings_logout");
        aboutPreference = findPreference("settings_version");
        changePassword = findPreference("settings_change_password");
        itemDeactivateAccount = findPreference("settings_deactivate_account");
        itemServices = findPreference("settings_services");
        itemTerms = findPreference("settings_terms");
        itemThanks = findPreference("settings_thanks");
        itemBlackList = findPreference("settings_blocked_list");
        itemReferralsList = findPreference("settings_referrals_list");
        itemNotifications = findPreference("settings_push_notifications");
        itemPrivacy = findPreference("settings_privacy");
        itemContactUs = findPreference("settings_contact_us");

        itemContactUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), SupportActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemPrivacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), PrivacySettingsActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), NotificationsSettingsActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemBlackList.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), BlackListActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemReferralsList.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), ReferralsActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemThanks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_THANKS);
                i.putExtra("title", getText(R.string.settings_thanks));
                startActivity(i);

                return true;
            }
        });

        itemTerms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_TERMS);
                i.putExtra("title", getText(R.string.settings_terms));
                startActivity(i);

                return true;
            }
        });

        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getText(R.string.action_about));

                aboutDialogContent = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);

                alertDialog.setView(aboutDialogContent);

                aboutDialogAppName = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppName);
                aboutDialogAppVersion = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppVersion);
                aboutDialogAppCopyright = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppCopyright);

                aboutDialogAppName.setText(getString(R.string.app_name));
                aboutDialogAppVersion.setText("Version " + getString(R.string.app_version));
                aboutDialogAppCopyright.setText("Copyright © " + getString(R.string.app_year) + " " + getString(R.string.app_copyright));

//                    alertDialog.setMessage("Version " + APP_VERSION + "/r/n" + APP_COPYRIGHT);
                alertDialog.setCancelable(true);
                alertDialog.setNeutralButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                alertDialog.show();

                return false;
            }
        });

        logoutPreference.setSummary(App.getInstance().getUsername());

        logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

                    loading = true;

                    showpDialog();

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_LOGOUT, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        if (!response.getBoolean("error")) {

                                            App.getInstance().removeData();
                                            App.getInstance().readData();

                                            Intent intent = new Intent(getActivity(), AppActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        loading = false;

                                        hidepDialog();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            loading = false;

                            hidepDialog();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("clientId", CLIENT_ID);
                            params.put("accountId", Long.toString(App.getInstance().getId()));
                            params.put("accessToken", App.getInstance().getAccessToken());

                            return params;
                        }
                    };

                    App.getInstance().addToRequestQueue(jsonReq);
                }

                return true;
            }
        });

        changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemDeactivateAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(getActivity(), DeactivateActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemServices.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(getActivity(), ServicesActivity.class);
                startActivity(i);

                return true;
            }
        });

        allowComments = (CheckBoxPreference) getPreferenceManager().findPreference("allowComments");

        allowComments.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        mAllowComments = 1;

                    } else {

                        mAllowComments = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        setAllowComments();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        allowPhotosComments = (CheckBoxPreference) getPreferenceManager().findPreference("allowPhotosComments");

        allowPhotosComments.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        mAllowPhotosComments = 1;

                    } else {

                        mAllowPhotosComments = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        setAllowPhotosComments();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        allowMessages = (CheckBoxPreference) getPreferenceManager().findPreference("allowMessages");

        allowMessages.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        mAllowMessages = 1;

                    } else {

                        mAllowMessages = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        setAllowMessages();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        checkAllowComments(App.getInstance().getAllowComments());
        checkAllowPhotosComments(App.getInstance().getAllowPhotosComments());
        checkAllowMessages(App.getInstance().getAllowMessages());
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

    public void checkAllowComments(int value) {

        if (value == 1) {

            allowComments.setChecked(true);
            mAllowComments = 1;

        } else {

            allowComments.setChecked(false);
            mAllowComments = 0;
        }
    }

    public void checkAllowPhotosComments(int value) {

        if (value == 1) {

            allowPhotosComments.setChecked(true);
            mAllowPhotosComments = 1;

        } else {

            allowPhotosComments.setChecked(false);
            mAllowPhotosComments = 0;
        }
    }

    public void checkAllowMessages(int value) {

        if (value == 1) {

            allowMessages.setChecked(true);
            mAllowMessages = 1;

        } else {

            allowMessages.setChecked(false);
            mAllowMessages = 0;
        }
    }

    public void setAllowComments() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_ALLOW_COMMENTS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setAllowComments(response.getInt("allowComments"));

                                checkAllowComments(App.getInstance().getAllowComments());
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();

                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("allowComments", Integer.toString(mAllowComments));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void setAllowPhotosComments() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_ALLOW_PHOTOS_COMMENTS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setAllowPhotosComments(response.getInt("allowPhotosComments"));

                                checkAllowPhotosComments(App.getInstance().getAllowPhotosComments());
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();

                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("allowPhotosComments", Integer.toString(mAllowPhotosComments));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void setAllowMessages() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_ALLOW_MESSAGES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setAllowMessages(response.getInt("allowMessages"));

                                checkAllowMessages(App.getInstance().getAllowMessages());
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();

                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("allowMessages", Integer.toString(mAllowMessages));

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