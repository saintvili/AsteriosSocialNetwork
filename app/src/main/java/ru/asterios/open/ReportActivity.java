package ru.asterios.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.asterios.open.app.App;
import ru.asterios.open.common.ActivityBase;
import ru.asterios.open.util.CustomRequest;

public class ReportActivity extends ActivityBase {

    Toolbar mToolbar;

    RadioGroup mRadioGroup;
    RadioButton mSpam, mHate, mNudity, mFake;

    Button mCancel, mReport;

    private long profileId;
    private int reason;
    private Boolean restore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mSpam = (RadioButton) findViewById(R.id.radioButton_spam);
        mHate = (RadioButton) findViewById(R.id.radioButton_hate);
        mNudity = (RadioButton) findViewById(R.id.radioButton_nudity);
        mFake = (RadioButton) findViewById(R.id.radioButton_fake);

        mCancel = (Button) findViewById(R.id.actionCancel);
        mReport = (Button) findViewById(R.id.actionReport);

        if (savedInstanceState != null) {

            restore = savedInstanceState.getBoolean("restore");
            reason = savedInstanceState.getInt("reason");
            profileId = savedInstanceState.getLong("profileId");

        } else {

            restore = false;

            reason = 0;

            Intent i = getIntent();
            profileId = i.getLongExtra("profileId", 0);
        }

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                report();
            }
        });

        switch (reason) {

            case 0: {

                mSpam.setChecked(true);

                break;
            }

            case 1: {

                mHate.setChecked(true);

                break;
            }

            case 2: {

                mNudity.setChecked(true);

                break;
            }

            default: {

                mFake.setChecked(true);

                break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putLong("profileId", profileId);
        outState.putInt("reason", reason);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {

            case R.id.radioButton_spam: {

                if (checked) {

                    reason = 0;
                }

                break;
            }

            case R.id.radioButton_hate: {

                if (checked) {

                    reason = 1;
                }

                break;
            }

            case R.id.radioButton_nudity: {

                if (checked) {

                    reason = 2;
                }

                break;
            }

            default: {

                if (checked) {

                    reason = 3;
                }

                break;
            }
        }
    }

    public void report() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_REPORT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                Toast.makeText(ReportActivity.this, getString(R.string.label_profile_reported), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            hidepDialog();

                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profileId));
                params.put("reason", Integer.toString(reason));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
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
