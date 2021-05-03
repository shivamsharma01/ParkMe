//package com.android.parkme;
//
//public class temp {
//}


package com.android.parkme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.parkme.util.Globals;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RaiseQueryFragment extends Fragment {
    private static final String TAG = "RaiseQuery";
    private static final String qid = "qid";
    private static final String queryType = "queryType";
    private static final String status = "status";
    private static final String message = "message";
    private static final String queryCreateDate = "queryCreateDate";
    private static final String vehicleRegistrationNumber = "vehicleRegistrationNumber";
    public static final int CAMERA_REQUEST = 9999;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public String current_value;
    public ArrayAdapter<String> queryTypeAdaptor;
    MyTask asyc_Obj;
    Uri mImageuri;
    private Spinner queryTypeDropdown;
    private EditText dateText, messageText, vehicleNumber;
    private ImageView clickedImage;
    private FloatingActionButton addImage;
    private Button resetBtn, sendBtn;
    Bitmap bitmap;
    byte[] bArray;

    private String queryTypeVal, statusVal, messageVal, vehicleNumberVal, dateTime;

    final String raiseQuery = "raise-query";
    private static final String sessionKey = "sessionKey";
    private static final String MyPREFERENCES = "ParkMe";
    RequestQueue queue = null;
    private SharedPreferences sharedpreferences;
    String responseBody;

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
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        queryTypeDropdown = getActivity().findViewById(R.id.dropdown_query_types);
        queryTypeAdaptor = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.query_types_array));
        queryTypeAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        queryTypeDropdown.setAdapter(queryTypeAdaptor);

        messageText = getActivity().findViewById(R.id.message_text);
        vehicleNumber = getActivity().findViewById(R.id.number_value);

        dateText = getActivity().findViewById(R.id.date_value);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        dateText.setText(sdf.format(new Date()));

        addImage = getActivity().findViewById(R.id.add_image_button);
        addImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    CropImage.activity().start(getContext(), RaiseQueryFragment.this);
                }
            }
        });

        clickedImage = getActivity().findViewById(R.id.clicked_image);

        sendBtn = getActivity().findViewById(R.id.send_button);
        sendBtn.setOnClickListener(v -> raiseQuery());

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

    private void raiseQuery() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        bArray = bos.toByteArray();

        if (network_check()) {
            String url = getResources().getString(R.string.url).concat(raiseQuery);
            Log.i(TAG, "Raising Query " + url);
            JSONObject raiseQueryObject = new JSONObject();
            try {
                queryTypeVal = queryTypeDropdown.getSelectedItem().toString();
                statusVal = "Open";
                messageVal = messageText.getText().toString();
                vehicleNumberVal = vehicleNumber.getText().toString();
                dateTime = dateText.getText().toString();
                String date = dateTime.split(" ")[0];
                String timestamp = dateTime.split(" ")[1];
                String dateTimeWithT = date.concat("T").concat(timestamp);
                Log.i(TAG, dateTimeWithT.toString());
                raiseQueryObject.put("queryType", queryTypeVal);
                raiseQueryObject.put("status", statusVal);
                raiseQueryObject.put("message", messageVal);
                raiseQueryObject.put("queryCreateDate", dateTimeWithT);
                raiseQueryObject.put("vehicleRegistrationNumber", vehicleNumberVal);
//                raiseQueryObject.put("fromUser", 100);
//                raiseQueryObject.put("toUser", 200);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, raiseQueryObject, response -> {
                Log.i(TAG, "Query Raised Successfully");
                if (null != response) {
//                            storeFields(response);
                    int qid = Integer.parseInt(response.toString());
                    onSuccess(qid);
            } }, error -> this.handleError(error)) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("session-id", sharedpreferences.getString(Globals.SESSION_KEY, ""));
                    return params;
                }};
            queue.add(request);
        } else {
            Toast.makeText(getActivity(), "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean network_check()
    {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void handleError(VolleyError error) {
        try {
            if (error == null || error.networkResponse == null) {
                Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            int status = data.getInt("status");
            String errorString = data.getString("trace");
            if (status == 409) {
                int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
//                emailInput.setError(errorString.substring(indexStart + 1, indexEnd));
            } else {
                int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
                if (indexStart != -1 && indexEnd != -1) {
                    String[] split = errorString.substring(indexStart + 1, indexEnd).split(":");
                    status = Integer.parseInt(split[0]);
                    switch (status) {
                        case 410:
                        case 411:
                        case 412:
                        case 413:
//                            passwordInput.setError(split[1]);
                            break;
                        case 500:
                            Toast.makeText(getActivity(), split[1], Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getActivity(), errorString.substring(indexStart + 1, indexEnd), Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void onSuccess(int qid) {
        Bundle bundle = new Bundle();
        bundle.putInt("queryNumber", qid);
        bundle.putString("status", statusVal);
        bundle.putString("message", messageVal);
        bundle.putString("queryCreateDate", dateTime);
        bundle.putByteArray("vehicleNumberImage", bArray);
        bundle.putString("vehicleRegistrationNumber", vehicleNumberVal);
        QueryDetailsFragment querydetailsFragment = new QueryDetailsFragment();
        querydetailsFragment.setArguments(bundle);
        openFragment(querydetailsFragment);
    }

    private boolean checkAndRequestPermissions() {

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
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        current_value = queryTypeDropdown.getSelectedItem().toString();
        Log.i("test", "value of result code: " + resultCode);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                mImageuri = result.getUri();
//                clickedImage.setImageURI(mImageuri);
                clickedImage.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageuri);

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
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getContext(), "No App available for Cropping", Toast.LENGTH_SHORT).show();
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
    private class MyTask extends AsyncTask<Void, String, String> {

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

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}