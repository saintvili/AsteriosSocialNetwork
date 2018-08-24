package ru.asterios.open;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.util.CustomRequest;

public class ServicesFragment extends Fragment implements Constants {

    CallbackManager callbackManager;

    LoginButton loginButton;

    private ProgressDialog pDialog;

    Button mDisconnectBtn;
    TextView mConnectPrompt, mDisconnectPrompt;

    String facebookId = "";

    private Boolean loading = false;

    public ServicesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (AccessToken.getCurrentAccessToken()!= null) LoginManager.getInstance().logOut();

        callbackManager = CallbackManager.Factory.create();

        initpDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_services, container, false);

        if (loading) {

            showpDialog();
        }

        loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends, email");

        mConnectPrompt = (TextView) rootView.findViewById(R.id.connectPrompt);
        mDisconnectPrompt = (TextView) rootView.findViewById(R.id.disconnectPrompt);
        mDisconnectBtn = (Button) rootView.findViewById(R.id.disconnectBtn);

        mDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                disconnectFromFacebook();
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                if (App.getInstance().isConnected()) {

                    loading = true;

                    showpDialog();

                    GraphRequest request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {

                                    // Application code

                                    try {

                                        facebookId = object.getString("id");

                                    } catch (Throwable t) {

                                        Log.e("Profile", "Could not parse malformed JSON: \"" + object.toString() + "\"");

                                    } finally {

                                        if (AccessToken.getCurrentAccessToken() != null) LoginManager.getInstance().logOut();

                                        Log.d("Profile", object.toString());

                                        if (App.getInstance().isConnected()) {

                                            if (!facebookId.equals("")) {

                                                connectToFacebook();

                                            } else {

                                                loading = false;

                                                hidepDialog();
                                            }

                                        } else {

                                            loading = false;

                                            hidepDialog();
                                        }
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,link,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }

            @Override
            public void onCancel() {

                // App code
                // Cancel
            }

            @Override
            public void onError(FacebookException exception) {

                // App code
                // error
            }
        });

        if (App.getInstance().getFacebookId().length() != 0) {

            showDisconnectScreen();

        } else {

            showConnectScreen();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void disconnectFromFacebook() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_DISCONNECT_FROM_FACEBOOK, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.has("error")) {

                                if (!response.getBoolean("error")) {

                                    App.getInstance().setFacebookId("");
                                    showConnectScreen();

                                    Toast.makeText(getActivity(), getString(R.string.msg_connect_to_facebook_removed), Toast.LENGTH_SHORT).show();
                                }
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

                Toast.makeText(getActivity(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("accountId", Long.toString(App.getInstance().getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void connectToFacebook() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_CONNECT_TO_FACEBOOK, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.has("error")) {

                                if (!response.getBoolean("error")) {

                                    if (response.has("error_code")) {

                                        if (response.getInt("error_code") == ERROR_FACEBOOK_ID_TAKEN) {

                                            Toast.makeText(getActivity(), getString(R.string.msg_connect_to_facebook_error), Toast.LENGTH_SHORT).show();

                                        } else {

                                            App.getInstance().setFacebookId(facebookId);
                                            showDisconnectScreen();

                                            Toast.makeText(getActivity(), getString(R.string.msg_connect_to_facebook_success), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
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

                Toast.makeText(getActivity(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("facebookId", facebookId);
                params.put("clientId", CLIENT_ID);
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("accountId", Long.toString(App.getInstance().getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void showDisconnectScreen() {

        mConnectPrompt.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);

        mDisconnectPrompt.setVisibility(View.VISIBLE);
        mDisconnectBtn.setVisibility(View.VISIBLE);
    }

    public void showConnectScreen() {

        mConnectPrompt.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);

        mDisconnectPrompt.setVisibility(View.GONE);
        mDisconnectBtn.setVisibility(View.GONE);
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