package com.android.parkme.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.parkme.LoginActivity;
import com.android.parkme.R;

import java.util.ArrayList;
import java.util.List;

public class Functions {
    private static final String TAG = "Functions";

    public static String parseDateText(String dateText) {
        String[] dateParts = dateText.split(" ");
        int date = Integer.parseInt(dateParts[1]);
        if (date == 1 || date == 21 || date == 31)
            return dateText + "st";
        else if (date == 2 || date == 22)
            return dateText + "nd";
        else if (date == 3 || date == 23)
            return dateText + "rd";
        else
            return dateText + "th";
    }

    public static boolean networkCheck(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    public static void setCurrentFragment(FragmentActivity fragmentActivity, Fragment fragment) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        Log.i(TAG, "setCurrentFragment");
        String fragmentTag = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(fragmentTag, 0);

        if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.flFragment, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(fragmentTag);
            ft.commit();
        }
    }

    public static boolean checkAndRequestPermissions(Activity activity) {
        int permissionCamera = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        int ext_storage = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ext_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Globals.REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public static void exit(Context context, SharedPreferences sharedpreferences, String msg) {
        if (msg == null)
            Toast.makeText(context, "Session has ended. Please login again.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String token = sharedpreferences.getString(Globals.TOKEN, null);
        editor.clear();
        editor.apply();
        editor.putString(Globals.TOKEN, token);
        editor.commit();
        Log.i(TAG, token);
        context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
