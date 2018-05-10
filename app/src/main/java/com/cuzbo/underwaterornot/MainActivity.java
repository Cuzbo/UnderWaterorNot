package com.cuzbo.underwaterornot;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

//    MapView mMapView;
//    GoogleMap mGoogleMap;
    private final String BASIC_URL = "https://api.onwater.io/api/v1/results/";
    private final String API_KEY = "s5bxVPqoKsWHHMzKnGzy";
    private URL onWaterUrl;

    private EditText mLatEditText, mLonEditText;
    private TextView mResultTextView;
    private Button mOkButton, mOnMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatEditText = (EditText) findViewById(R.id.latET);
        mLonEditText = (EditText) findViewById(R.id.lonET);
        mResultTextView = (TextView) findViewById(R.id.result);
        mOkButton = (Button) findViewById(R.id.okButton);
        mOnMapButton = (Button) findViewById(R.id.on_map_button);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latString = mLatEditText.getText().toString();
                String lonString = mLonEditText.getText().toString();

                long latLong = Long.parseLong(latString);
                long lonLong = Long.parseLong(lonString);

                if (TextUtils.isEmpty(latString) || latLong > 90 || latLong < -90){
                    mResultTextView.setText("Invalid Latitude");
                    Toast.makeText(getApplicationContext(),"Invalid Latitude", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(lonString) || lonLong > 180 || lonLong < -180){
                    Toast.makeText(getApplicationContext(), "Invalid Longitude", Toast.LENGTH_SHORT).show();
                    mResultTextView.setText("Invalid Longitude");
                } else {
                    String urlString = buildUriString(latString, lonString);
                    makeHttpRequest(urlString);
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latString = mLatEditText.getText().toString();
                String lonString = mLonEditText.getText().toString();
                Uri uri = Uri.parse("geo:" + latString + "," + lonString +"?z=5");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getPackageManager())!= null){
                    startActivity(intent);
                }
            }
        });

    }

    public String buildUriString (String lat, String lon){

        String urlString = BASIC_URL + lat + "," + lon + "?access_token=" + API_KEY;

        return urlString;

    }

    public void makeHttpRequest (String urlString){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(urlString, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                LocationDataModel locationDataModel = LocationDataModel.fromJson(response);
                updateUI(locationDataModel);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                super.onFailure(statusCode, headers, throwable, response);
            }
        });
    }
    public void updateUI(LocationDataModel locationDataModel){

        if (locationDataModel.isOnWater()){
            mResultTextView.setText(R.string.under_water);
        } else {
            mResultTextView.setText(R.string.land);

        }
    }
}
