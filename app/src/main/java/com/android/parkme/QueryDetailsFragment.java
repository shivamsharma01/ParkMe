package com.android.parkme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.parkme.util.Globals;

public class QueryDetailsFragment extends Fragment {
    private static final String TAG = "QueryDetailsFragment";
    private TextView queryNumber, dateText, messageText, vehicleNumber;
    private ImageView vehicleNumberImage;
    private String queryNumberVal, statusVal, messageVal, dateTime, vehicleNumberVal;
    private byte[] vehicleNumberImageBArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        queryNumberVal = String.valueOf(getArguments().getInt(Globals.QUERY_NUMBER));
        statusVal = getArguments().getString(Globals.STATUS);
        messageVal = getArguments().getString(Globals.MESSAGE);
        dateTime = getArguments().getString(Globals.QUERY_CREATE_DATE);
        vehicleNumberImageBArray = getArguments().getByteArray(Globals.VEHICLE_IMAGE_NUMBER);
        vehicleNumberVal = getArguments().getString(Globals.VEHICLE_REGISTRATION_NUMBER);
        return inflater.inflate(R.layout.fragment_query_details, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        queryNumber = getActivity().findViewById(R.id.query_number_qd);
        messageText = getActivity().findViewById(R.id.message_text_qd);
        dateText = getActivity().findViewById(R.id.date_value_qd);
        vehicleNumberImage = getActivity().findViewById(R.id.clicked_image_qd);
        vehicleNumber = getActivity().findViewById(R.id.vehicle_number_qd);

        queryNumber.setText(queryNumberVal);
        messageText.setText(messageVal);
        dateText.setText(dateTime);
        Bitmap vehicleNumberImageBitmap = BitmapFactory.decodeByteArray(vehicleNumberImageBArray, 0, vehicleNumberImageBArray.length);
        vehicleNumberImage.setImageBitmap(vehicleNumberImageBitmap);
        vehicleNumber.setText(vehicleNumberVal);
    }
}
