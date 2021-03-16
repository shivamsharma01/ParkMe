package com.android.parkme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RaiseQueryFragment extends Fragment {
    private Spinner queryTypeDropdown;
    private EditText dateText, messageText, vehicleNumber;
    private ImageView clickedImage;
    private FloatingActionButton addImage;
    public static final int CAMERA_REQUEST=9999;
    private Button resetBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_raise_query, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        queryTypeDropdown = getActivity().findViewById(R.id.dropdown_query_types);
        ArrayAdapter<String> queryTypeAdaptor = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.query_types_array));
        queryTypeAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        queryTypeDropdown.setAdapter(queryTypeAdaptor);

        messageText = getActivity().findViewById(R.id.message_text);
        vehicleNumber = getActivity().findViewById(R.id.number_value);

        dateText = getActivity().findViewById(R.id.date_value);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dateText.setText(sdf.format(new Date()));

        addImage = getActivity().findViewById(R.id.add_image_button);
        addImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OpenCamera(v);
            }
        });

        clickedImage = getActivity().findViewById(R.id.clicked_image);

        resetBtn = getActivity().findViewById(R.id.reset_button);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String compareValue = "--Select Query Type--";
                int spinnerPosition = queryTypeAdaptor.getPosition(compareValue);
                queryTypeDropdown.setSelection(spinnerPosition);
                messageText.setText("");
                vehicleNumber.setText("");
                clickedImage.setVisibility(View.GONE);
            }
        });
    }
    public void OpenCamera(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("test", "value of result code: "+resultCode);
        if(requestCode==CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    bitmap, 420, 60, false);
            clickedImage.setImageBitmap(resizedBitmap);
            clickedImage.setVisibility(View.VISIBLE);
        }
    }
}