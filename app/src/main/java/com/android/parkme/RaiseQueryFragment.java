package com.android.parkme;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RaiseQueryFragment extends Fragment {
    private Spinner queryTypeDropdown;
    private EditText dateText, messageText, vehicleNumber;
    private ImageView clickedImage;
    private FloatingActionButton addImage;
    public static final int CAMERA_REQUEST = 9999;
    private Button resetBtn;
    CropImageView cropImageView;
    Uri mImageuri;


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
        cropImageView = (CropImageView) getActivity().findViewById(R.id.cropImageView);
        addImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CropImage.activity().start(getContext(),RaiseQueryFragment.this);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode==Activity.RESULT_OK){
                mImageuri = result.getUri();
//                clickedImage.setImageURI(mImageuri);
                clickedImage.setVisibility(View.VISIBLE);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageuri);
                    clickedImage.setImageURI(mImageuri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Toast.makeText(getContext(), "No App available for Cropping",Toast.LENGTH_SHORT).show();
            }

//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            cropImageView.setImageBitmap(bitmap);
//            Bitmap cropped = cropImageView.getCroppedImage();
////            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
////                    cropped, 420, 60, false);
//            clickedImage.setImageBitmap(resizedBitmap);
//            clickedImage.setVisibility(View.VISIBLE);
        }
    }
}