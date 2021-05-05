package com.android.parkme.utils;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ErrorHandler {
    private static final String TAG = "ErrorHandler";

    public static ErrorResponse parseAndGetErrorLogin(VolleyError error) {
        ErrorResponse errorResponse;
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String errorString = data.getString(Globals.TRACE);
            int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
            String[] split = errorString.substring(indexStart + 1, indexEnd).split(":");
            Log.i(TAG, split[0] +" : "+split[1]);
            errorResponse = new ErrorResponse(Integer.parseInt(split[0]), split[1]);
        } catch (Exception e) {
            errorResponse = new ErrorResponse(0, "An error occurred");
        }
        return errorResponse;
    }

    public static ErrorResponse parseAndGetErrorChangePassword(VolleyError error) {
        ErrorResponse errorResponse;
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String errorString = data.getString(Globals.TRACE);
            int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
            String[] split = errorString.substring(indexStart + 1, indexEnd).split(":");
            Log.i(TAG, split[0] +" : "+split[1]);
            errorResponse = new ErrorResponse(Integer.parseInt(split[0]), split[1]);
        } catch (Exception e) {
            errorResponse = new ErrorResponse(0, "An error occurred");
        }
        return errorResponse;
    }

}


