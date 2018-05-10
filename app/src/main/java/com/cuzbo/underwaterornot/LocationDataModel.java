package com.cuzbo.underwaterornot;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationDataModel {

    private boolean onWater;

    public static LocationDataModel fromJson(JSONObject jsonObject){

        try {
            LocationDataModel locationData = new LocationDataModel();
            locationData.onWater = jsonObject.getBoolean("water");
            return locationData;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean isOnWater() {
        return onWater;
    }
}
