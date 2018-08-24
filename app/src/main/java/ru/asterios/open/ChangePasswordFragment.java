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
import android.widget.EditText;
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
import ru.asterios.open.util.Helper;

public class ChangePasswordFragment extends Fragment implements Constants {

    private ProgressDialog pDialog;

    EditText mCurrentPassword, mNewPassword;

    String sCurrentPassword, sNewPassword;

    private Boolean loading = false;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);

        if (loading) {

            showpDialog();
        }

        mCurrentPassword = (EditText) rootView.findViewById(R.id.currentPassword);
        mNewPassword = (EditText) rootView.findViewById(R.id.newPassword);


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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_set_password: {

                sCurrentPassword = mCurrentPassword.getText().toString();
                sNewPassword = mNewPassword.getText().toString();

                if (checkCurrentPassword(sCurrentPassword)) {

                    if (checkNewPassword(sNewPassword)) {

                        if (App.getInstance().isConnected()) {

                            accountSetPassword();

                        } else {

                            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                return true;
            }

            default: {

                break;
            }
        }

        return false;
    }

    public void accountSetPassword() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SETPASSWORD, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.has("error")) {

                                if (!response.getBoolean("error")) {

                                    Toast.makeText(getActivity(), getText(R.string.msg_password_changed), Toast.LENGTH_SHORT).show();
                                    getActivity().finish();

                                } else {

                                    Toast.makeText(getActivity(), getText(R.string.error_password), Toast.LENGTH_SHORT).show();
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

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("currentPassword", sCurrentPassword);
                params.put("newPassword", sNewPassword);

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public Boolean checkCurrentPassword(String password) {

        Helper helper = new Helper();

        if (password.length() == 0) {

            mCurrentPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() < 6) {

            mCurrentPassword.setError(getString(R.string.error_small_password));

            return false;
        }

        if (!helper.isValidPassword(password)) {

            mCurrentPassword.setError(getString(R.string.error_wrong_format));

            return false;
        }

        mCurrentPassword.setError(null);

        return true;
    }

    public Boolean checkNewPassword(String password) {

        Helper helper = new Helper();

        if (password.length() == 0) {

            mNewPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() < 6) {

            mNewPassword.setError(getString(R.string.error_small_password));

            return false;
        }

        if (!helper.isValidPassword(password)) {

            mNewPassword.setError(getString(R.string.error_wrong_format));

            return false;
        }

        mNewPassword.setError(null);

        return true;
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