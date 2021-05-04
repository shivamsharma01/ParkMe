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
import androidx.room.Room;

public class QueryDetailsFragment extends Fragment {

    private TextView queryNumber, dateText, messageText, vehicleNumber;
    private ImageView vehicleNumberImage;
    private String queryNumberVal;
    private String statusVal, messageVal, dateTime, vehicleNumberVal;
    byte[] vehicleNumberImageBArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query_details, container, false);
        int queryNumberInt = (getArguments().getInt("queryNumber"));
        queryNumberVal = String.valueOf(queryNumberInt);
        statusVal = getArguments().getString("status");
        messageVal = getArguments().getString("message");
        dateTime = getArguments().getString("queryCreateDate");
        vehicleNumberImageBArray = getArguments().getByteArray("vehicleNumberImage");
        vehicleNumberVal = getArguments().getString("vehicleRegistrationNumber");

        return view;
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
