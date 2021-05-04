//package com.android.parkme;
//
//public class temp {
//}


package com.android.parkme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.parkme.database.DatabaseClient;
import com.android.parkme.database.Query;
import com.android.parkme.util.APIs;
import com.android.parkme.util.Functions;
import com.android.parkme.util.Globals;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaiseQueryFragment extends Fragment {
    private static final String TAG = "RaiseQueryFragment";
    private ArrayAdapter<String> queryTypeAdaptor;
    private Spinner queryTypeDropdown;
    private EditText dateText, messageText, vehicleNumber;
    private ImageView clickedImage;
    private FloatingActionButton addImage;
    private Button resetBtn, sendBtn;
    private Bitmap bitmap;
    private byte[] bArray;
    private String queryTypeVal, current_value;
    private RequestQueue queue = null;
    private SharedPreferences sharedpreferences;
    private View view;
    private JSONObject requestObject,  responseObject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_raise_query, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        queryTypeAdaptor = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.query_types_array));
        queryTypeAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        queryTypeDropdown = (Spinner) view.findViewById(R.id.dropdown_query_types);
        messageText = view.findViewById(R.id.message_text);
        vehicleNumber = view.findViewById(R.id.number_value);
        clickedImage = view.findViewById(R.id.clicked_image);
        sendBtn = view.findViewById(R.id.send_button);
        addImage = view.findViewById(R.id.add_image_button);
        dateText = view.findViewById(R.id.date_value);
        resetBtn = view.findViewById(R.id.reset_button);

        queryTypeDropdown.setAdapter(queryTypeAdaptor);
        dateText.setText(new SimpleDateFormat("YYYY-MM-dd HH:mm").format(new Date()));

        addImage.setOnClickListener(v -> {
            if (Functions.checkAndRequestPermissions(getActivity())) {
                CropImage.activity().start(getContext(), RaiseQueryFragment.this);
            }
        });
        sendBtn.setOnClickListener(v -> raiseQuery());
        resetBtn.setOnClickListener(v -> {
            String compareValue = "--Select Query Type--";
            int spinnerPosition = queryTypeAdaptor.getPosition(compareValue);
            queryTypeDropdown.setSelection(spinnerPosition);
            messageText.setText("");
            vehicleNumber.setText("");
            clickedImage.setVisibility(View.GONE);
        });
    }

    private void raiseQuery() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        bArray = bos.toByteArray();

        if (Functions.networkCheck(getContext())) {
            String url = getResources().getString(R.string.url).concat(APIs.raiseQuery);
            Log.i(TAG, "Raising Query " + url);
            requestObject = new JSONObject();
            try {
                queryTypeVal = queryTypeDropdown.getSelectedItem().toString();
                requestObject.put(Globals.QUERY_TYPE, queryTypeVal);
                requestObject.put(Globals.STATUS, Globals.QUERY_DEFAULT_STATUS);
                requestObject.put(Globals.MESSAGE, messageText.getText().toString());
                requestObject.put(Globals.QUERY_CREATE_DATE, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
                requestObject.put(Globals.VEHICLE_REGISTRATION_NUMBER, vehicleNumber.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, requestObject, response -> {
                responseObject = response;
                Log.i(TAG, "Query Raised Successfully");
                if (null != response)
                    onSuccess();
            }, error -> this.handleError(error)) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Globals.SESSION_ID, sharedpreferences.getString(Globals.SESSION_KEY, ""));
                    return params;
                }
            };
            queue.add(request);
        } else {
            Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(VolleyError error) {
        Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
    }

    private void onSuccess() {
        try {
            Query query = new Query(Integer.parseInt(responseObject.getString(Globals.QID)),
                    Globals.QUERY_DEFAULT_STATUS,
                    sharedpreferences.getString(Globals.NAME, ""),
                    sharedpreferences.getInt(Globals.ID, 0),
                    responseObject.getString(Globals.TO_USER_NAME),
                    responseObject.getInt(Globals.TO_USER_ID),
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(requestObject.getString(Globals.QUERY_CREATE_DATE)).getTime(),
                    (float) responseObject.getDouble(Globals.RATING));
            new QuerySave().execute(query);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        current_value = queryTypeDropdown.getSelectedItem().toString();
        Log.i(TAG, "value of result code: " + resultCode);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                clickedImage.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), result.getUri());
                    int width = bitmap.getWidth(), height = bitmap.getHeight(), size = bitmap.getRowBytes() * bitmap.getHeight();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                    bitmap.copyPixelsToBuffer(byteBuffer);
                    byte[] byteArray = byteBuffer.array();
                    new BitmapTask(byteArray, width, height, bitmap.getConfig().name()).execute();
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
                .addOnSuccessListener(texts -> processTextRecognitionResult(texts))
                .addOnFailureListener(e -> {
                    showToast("Please enter manually");
                    e.printStackTrace();
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
            return;
        }
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++)
                    str.append(elements.get(k).getText());
            }
        }
        int spinnerPosition1 = queryTypeAdaptor.getPosition(current_value);
        queryTypeDropdown.setSelection(spinnerPosition1);
        vehicleNumber.setText(str);
    }

    private class BitmapTask extends AsyncTask<Void, Void, Void> {
        private final Bitmap bitmap_tmp;

        public BitmapTask(byte[] byteArray, int width, int height, String conf) {
            Bitmap.Config configBmp = Bitmap.Config.valueOf(conf);
            Bitmap bitmap_tmp = Bitmap.createBitmap(width, height, configBmp);
            ByteBuffer buffer = ByteBuffer.wrap(byteArray);
            bitmap_tmp.copyPixelsFromBuffer(buffer);
            this.bitmap_tmp = bitmap_tmp;
        }

        @Override
        protected Void doInBackground(Void... params) {
            runTextRecognition(this.bitmap_tmp);
            return null;
        }

    }

    private class QuerySave extends AsyncTask<Query, Void, Void> {

        @Override
        protected Void doInBackground(Query... params) {
            DatabaseClient.getInstance(getContext()).getAppDatabase().parkMeDao().insert(params[0]);
            finishTask();
            return null;
        }

    }
    private void finishTask() {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(Globals.QUERY_NUMBER, Integer.parseInt(responseObject.getString(Globals.QID)));
            bundle.putString(Globals.STATUS, Globals.QUERY_DEFAULT_STATUS);
            bundle.putString(Globals.MESSAGE, requestObject.getString(Globals.MESSAGE));
            bundle.putLong(Globals.QUERY_CREATE_DATE, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(requestObject.getString(Globals.QUERY_CREATE_DATE)).getTime());
            bundle.putByteArray(Globals.VEHICLE_IMAGE_NUMBER, bArray);
            bundle.putString(Globals.VEHICLE_REGISTRATION_NUMBER, requestObject.getString(Globals.VEHICLE_REGISTRATION_NUMBER));
            QueryDetailsFragment querydetailsFragment = new QueryDetailsFragment();
            querydetailsFragment.setArguments(bundle);
            Functions.openFragment(querydetailsFragment, getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}