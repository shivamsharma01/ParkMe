//package com.android.parkme;
//
//public class temp {
//}


package com.android.parkme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.nio.ByteBuffer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RaiseQueryFragment extends Fragment {
    private Spinner queryTypeDropdown;
    private EditText dateText, messageText, vehicleNumber;
    private ImageView clickedImage;
    private FloatingActionButton addImage;
    public static final int CAMERA_REQUEST = 9999;
    private Button resetBtn;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    MyTask asyc_Obj;
    Uri mImageuri;
    public String current_value;
    public ArrayAdapter<String> queryTypeAdaptor;
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
        queryTypeAdaptor = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
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
                if(checkAndRequestPermissions())
                {
                    CropImage.activity().start(getContext(), RaiseQueryFragment.this);
                }
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
    private  boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA);
        int ext_storage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ext_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        current_value = queryTypeDropdown.getSelectedItem().toString();
        Log.i("test", "value of result code: "+resultCode);
        if(requestCode==CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//
//            System.out.println("---------------------------------------------------------------------------------");
//            System.out.println("RUN");
//
//            int width = bitmap.getWidth();
//            int height = bitmap.getHeight();
//
//            int size = bitmap.getRowBytes() * bitmap.getHeight();
//            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
//            bitmap.copyPixelsToBuffer(byteBuffer);
//            byte[] byteArray = byteBuffer.array();
//            String conf = bitmap.getConfig().name();
//
//            asyc_Obj = new MyTask(byteArray, width, height, conf);
//            asyc_Obj.execute();

//            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
//                    bitmap, 420, 60, false);
//            clickedImage.setImageBitmap(resizedBitmap);
//            clickedImage.setVisibility(View.VISIBLE);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode==Activity.RESULT_OK){
                mImageuri = result.getUri();
//                clickedImage.setImageURI(mImageuri);
                clickedImage.setVisibility(View.VISIBLE);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageuri);

//                    System.out.println("---------------------------------------------------------------------------------");
//                    System.out.println("RUN");

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    int size = bitmap.getRowBytes() * bitmap.getHeight();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                    bitmap.copyPixelsToBuffer(byteBuffer);
                    byte[] byteArray = byteBuffer.array();
                    String conf = bitmap.getConfig().name();

                    asyc_Obj = new MyTask(byteArray, width, height, conf);
                    asyc_Obj.execute();
                    clickedImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("Click image again");

                }
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Toast.makeText(getContext(), "No App available for Cropping",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void runTextRecognition(Bitmap x) {
        InputImage image = InputImage.fromBitmap(x, 0);

        TextRecognizer recognizer = TextRecognition.getClient();

        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {

                                processTextRecognitionResult(texts);
//                                System.out.println("---------------------------------------------------------------------------------");
//                                System.out.println("SUCCESS");

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
//                                System.out.println("---------------------------------------------------------------------------------");
//                                System.out.println("FAILURE");
                                showToast("Please enter manually");
                                e.printStackTrace();
                            }
                        });
    }


    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        StringBuilder str = new StringBuilder();

        if (blocks.size() == 0) {
            showToast("Please enter manually");
            // No text found display toast
            return;
        }

        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
//                    System.out.println("-------------");
//                    System.out.println(elements.get(k).getText());
                    str.append(elements.get(k).getText());

                }
            }
        }
//        System.out.println("-------------");
//        System.out.println(str);
        int spinnerPosition1 = queryTypeAdaptor.getPosition(current_value);
        queryTypeDropdown.setSelection(spinnerPosition1);
        vehicleNumber.setText(str);

    }
    // Async Task to execute the machine learning operations
    private class MyTask extends AsyncTask<Void,String,String>
    {

        private final Bitmap bitmap_tmp;

        public MyTask(byte[] byteArray, int width, int height, String conf) {

            Bitmap.Config configBmp = Bitmap.Config.valueOf(conf);
            Bitmap bitmap_tmp = Bitmap.createBitmap(width, height, configBmp);
            ByteBuffer buffer = ByteBuffer.wrap(byteArray);
            bitmap_tmp.copyPixelsFromBuffer(buffer);
            this.bitmap_tmp = bitmap_tmp;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            System.out.println("---------------------------------------------------------------------------------");
//            System.out.println("Pre Execute");
        }


        @Override
        protected String doInBackground(Void... params) {
            runTextRecognition(this.bitmap_tmp);
//            System.out.println("---------------------------------------------------------------------------------");
//            System.out.println("Do In Back");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
        }
    }

}