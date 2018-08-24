package ru.asterios.open;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.asterios.open.app.App;
import ru.asterios.open.constants.Constants;
import ru.asterios.open.util.CustomRequest;
import ru.asterios.open.util.Helper;

public class NewGroupFragment extends Fragment implements Constants {

    public static final int RESULT_OK = -1;

    private ProgressDialog pDialog;

    private String name, location, link, website, description;

    private int category, year = 2016, month = 0, day = 1, group_allow_posts = 1, group_allow_comments = 1;

    EditText mGroupName, mGroupLocation, mGroupWebsite, mGroupLink, mGroupDesc;
    Button mGroupFounded, mGroupCreate;
    Spinner mGroupCategory;
    CheckBox mGroupAllowPosts, mGroupAllowComments;

    private Boolean loading = false;

    public NewGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(false);

        initpDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_new_group, container, false);

        if (loading) {

            showpDialog();
        }

        mGroupName = (EditText) rootView.findViewById(R.id.groupFullname);
        mGroupLocation = (EditText) rootView.findViewById(R.id.groupLocation);
        mGroupLink = (EditText) rootView.findViewById(R.id.groupUsername);
        mGroupWebsite = (EditText) rootView.findViewById(R.id.groupWebsite);
        mGroupDesc = (EditText) rootView.findViewById(R.id.groupDesc);

        mGroupFounded = (Button) rootView.findViewById(R.id.groupFounded);
        mGroupCreate = (Button) rootView.findViewById(R.id.groupCreate);

        mGroupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                groupCreate();
            }
        });

        mGroupFounded.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog dpd = new DatePickerDialog(getActivity(), mDateSetListener, year, month, day);
                dpd.getDatePicker().setMaxDate(new Date().getTime());

                dpd.show();
            }
        });

        mGroupAllowPosts = (CheckBox) rootView.findViewById(R.id.groupAllowPosts);
        mGroupAllowComments = (CheckBox) rootView.findViewById(R.id.groupAllowComments);

        mGroupCategory = (Spinner) rootView.findViewById(R.id.groupCategory);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroupCategory.setAdapter(spinnerAdapter);
        spinnerAdapter.add(getString(R.string.group_category_1));
        spinnerAdapter.add(getString(R.string.group_category_2));
        spinnerAdapter.add(getString(R.string.group_category_3));
        spinnerAdapter.add(getString(R.string.group_category_4));
        spinnerAdapter.add(getString(R.string.group_category_5));
        spinnerAdapter.add(getString(R.string.group_category_6));
        spinnerAdapter.add(getString(R.string.group_category_7));
        spinnerAdapter.add(getString(R.string.group_category_8));
        spinnerAdapter.add(getString(R.string.group_category_9));
        spinnerAdapter.add(getString(R.string.group_category_10));
        spinnerAdapter.add(getString(R.string.group_category_11));
        spinnerAdapter.add(getString(R.string.group_category_12));
        spinnerAdapter.add(getString(R.string.group_category_13));
        spinnerAdapter.add(getString(R.string.group_category_14));
        spinnerAdapter.add(getString(R.string.group_category_15));
        spinnerAdapter.add(getString(R.string.group_category_16));
        spinnerAdapter.add(getString(R.string.group_category_17));
        spinnerAdapter.add(getString(R.string.group_category_18));
        spinnerAdapter.add(getString(R.string.group_category_19));
        spinnerAdapter.add(getString(R.string.group_category_20));
        spinnerAdapter.add(getString(R.string.group_category_21));
        spinnerAdapter.add(getString(R.string.group_category_22));
        spinnerAdapter.add(getString(R.string.group_category_23));
        spinnerAdapter.add(getString(R.string.group_category_24));
        spinnerAdapter.add(getString(R.string.group_category_25));
        spinnerAdapter.add(getString(R.string.group_category_26));
        spinnerAdapter.add(getString(R.string.group_category_27));
        spinnerAdapter.add(getString(R.string.group_category_28));
        spinnerAdapter.add(getString(R.string.group_category_29));
        spinnerAdapter.add(getString(R.string.group_category_30));
        spinnerAdapter.add(getString(R.string.group_category_31));
        spinnerAdapter.add(getString(R.string.group_category_32));
        spinnerAdapter.add(getString(R.string.group_category_33));
        spinnerAdapter.add(getString(R.string.group_category_34));
        spinnerAdapter.add(getString(R.string.group_category_35));
        spinnerAdapter.add(getString(R.string.group_category_36));
        spinnerAdapter.add(getString(R.string.group_category_37));
        spinnerAdapter.add(getString(R.string.group_category_38));
        spinnerAdapter.add(getString(R.string.group_category_39));
        spinnerAdapter.add(getString(R.string.group_category_40));
        spinnerAdapter.add(getString(R.string.group_category_41));
        spinnerAdapter.add(getString(R.string.group_category_42));
        spinnerAdapter.notifyDataSetChanged();

        mGroupCategory.setSelection(0);

        int mMonth1 = month + 1;

        mGroupFounded.setText(getString(R.string.label_group_founded) + ": " + new StringBuilder().append(day).append("/").append(mMonth1).append("/").append(year));

        // Inflate the layout for this fragment
        return rootView;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int mYear, int monthOfYear, int dayOfMonth) {

            year = mYear;
            month = monthOfYear;
            day = dayOfMonth;

            int mMonth1 = month + 1;

            mGroupFounded.setText(getString(R.string.label_group_founded) + ": " + new StringBuilder().append(day).append("/").append(mMonth1).append("/").append(year));

        }

    };

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

    public void saveSettings() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_GROUP_CREATE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.has("error")) {

                                if (!response.getBoolean("error")) {

                                    if (response.has("groupId")) {

                                        long group_id = response.getLong("groupId");

                                        Intent i = new Intent(getActivity(), GroupActivity.class);
                                        i.putExtra("groupId", group_id);
                                        startActivity(i);

                                        getActivity().finish();

                                    } else {

                                        getActivity().finish();
                                    }

                                } else {

                                    if (response.has("error_type")) {

                                        int error_type = response.getInt("error_type");

                                        if (error_type == 0 || error_type == 1) {

                                            Toast.makeText(getActivity(), getActivity().getString(R.string.msg_error_group_link), Toast.LENGTH_SHORT).show();
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

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("group_name", link);
                params.put("group_fullname", name);
                params.put("group_location", location);
                params.put("group_site", website);
                params.put("group_desc", description);
                params.put("group_category", Integer.toString(category));
                params.put("group_allow_posts", Integer.toString(group_allow_posts));
                params.put("group_allow_comments", Integer.toString(group_allow_comments));
                params.put("year", Integer.toString(year));
                params.put("month", Integer.toString(month));
                params.put("day", Integer.toString(day));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void groupCreate() {

        mGroupName.setError(null);
        mGroupLink.setError(null);

        if (checkFullname() && checkLink()) {

            name = mGroupName.getText().toString();
            link = mGroupLink.getText().toString();
            website = mGroupWebsite.getText().toString();
            location = mGroupLocation.getText().toString();
            description = mGroupDesc.getText().toString();

            category = mGroupCategory.getSelectedItemPosition();

            if (mGroupAllowPosts.isChecked()) {

                group_allow_posts = 1;

            } else {

                group_allow_posts = 0;
            }

            if (mGroupAllowComments.isChecked()) {

                group_allow_comments = 1;

            } else {

                group_allow_comments = 0;
            }

            saveSettings();
        }
    }

    public Boolean checkFullname() {

        name = mGroupName.getText().toString();

        if (name.length() == 0) {

            mGroupName.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (name.length() < 2) {

            mGroupName.setError(getString(R.string.error_small_fullname));

            return false;
        }

        mGroupName.setError(null);

        return  true;
    }

    public Boolean checkLink() {

        link = mGroupLink.getText().toString();

        Helper helper = new Helper();

        if (link.length() == 0) {

            mGroupLink.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (link.length() < 5) {

            mGroupLink.setError(getString(R.string.error_small_username));

            return false;
        }

        if (!helper.isValidLogin(link)) {

            mGroupLink.setError(getString(R.string.error_wrong_format));

            return false;
        }

        mGroupLink.setError(null);

        return  true;
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