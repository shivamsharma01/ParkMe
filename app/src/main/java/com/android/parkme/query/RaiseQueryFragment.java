package com.android.parkme.query;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.parkme.R;
import com.android.parkme.database.DatabaseClient;
import com.android.parkme.database.Query;
import com.android.parkme.utils.APIs;
import com.android.parkme.utils.Functions;
import com.android.parkme.utils.Globals;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    private MaterialTextView addImageError;
    private ImageView clickedImage;
    private FloatingActionButton addImage;
    private Button resetBtn, sendBtn;
    private Bitmap bitmap;
    private byte[] bArray;
    private RequestQueue queue = null;
    private SharedPreferences sharedpreferences;
    private View view;
    private JSONObject requestObject, responseObject;

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
    public void onResume() {
        super.onResume();
        queryTypeDropdown.setSelection((int)sharedpreferences.getLong("dropDown", 0));
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedpreferences.edit().putLong("dropDown", queryTypeDropdown.getSelectedItemPosition()).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        queryTypeAdaptor = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.query_types_array));
        queryTypeAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        queryTypeDropdown = view.findViewById(R.id.dropdown_query_types);
        messageText = view.findViewById(R.id.message_text);
        vehicleNumber = view.findViewById(R.id.number_value);
        clickedImage = view.findViewById(R.id.clicked_image);
        sendBtn = view.findViewById(R.id.send_button);
        addImage = view.findViewById(R.id.add_image_button);
        dateText = view.findViewById(R.id.date_value);
        resetBtn = view.findViewById(R.id.reset_button);
        addImageError = view.findViewById(R.id.add_image_error);

        queryTypeDropdown.setAdapter(queryTypeAdaptor);
        dateText.setText(new SimpleDateFormat("YYYY-MM-dd HH:mm").format(new Date()));

        addImage.setOnClickListener(v -> {
            addImageError.setVisibility(View.GONE);
            if (Functions.checkAndRequestPermissions(getActivity())) {
                CropImage.activity().start(getContext(), RaiseQueryFragment.this);
            }
        });
        sendBtn.setOnClickListener(v -> raiseQuery());
        resetBtn.setOnClickListener(v -> {
            String compareValue = "--Select Query Type--";
            int spinnerPosition = queryTypeAdaptor.getPosition(compareValue);
            queryTypeDropdown.setSelection(spinnerPosition);
            messageText.getText().clear();
            vehicleNumber.getText().clear();
            clickedImage.setVisibility(View.GONE);
        });
    }

    public boolean checkValidation() {
        if (queryTypeDropdown.getSelectedItem().toString().equals("--Select Query Type--")) {
            TextView errorText = (TextView) queryTypeDropdown.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please Select a Query Type");//changes the selected item text to this
            queryTypeDropdown.requestFocus();
            return false;
        } else if (messageText.length() <= 0) {
            messageText.requestFocus();
            messageText.setError("Enter Message");
            return false;
        } else if (clickedImage.getDrawable() == null) {
            addImageError.setTextColor(Color.RED);
            addImageError.setText("Please Click Vehicle Number Plate Image");
            addImageError.setVisibility(View.VISIBLE);
            return false;
        } else if (vehicleNumber.length() <= 0) {
            vehicleNumber.requestFocus();
            vehicleNumber.setError("Enter vehicleNumber if not Extracted correctly by Image");
            return false;
        } else {
            return true;
        }
    }

    private void raiseQuery() {
        if (checkValidation()) {
            if (Functions.networkCheck(getContext())) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    bArray = bos.toByteArray();
                    String base64 = Base64.encodeToString(bArray, Base64.DEFAULT);
                    String url = getResources().getString(R.string.url).concat(APIs.raiseQuery);
                    Log.i(TAG, "Raising Query " + url);
                    requestObject = new JSONObject();
                    requestObject.put(Globals.QUERY_TYPE, queryTypeDropdown.getSelectedItem().toString());
                    requestObject.put(Globals.STATUS, Globals.QUERY_DEFAULT_STATUS);
                    requestObject.put(Globals.MESSAGE, messageText.getText().toString());
                    requestObject.put("check", bArray);
                    requestObject.put(Globals.QUERY_CREATE_DATE, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
                    requestObject.put(Globals.VEHICLE_REGISTRATION_NUMBER, vehicleNumber.getText().toString());

                    JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, requestObject, response -> {
                        responseObject = response;
                        Log.i(TAG, "Query Raised Successfully");
                        if (null != response)
                            onSuccess();
                    }, error ->
                            Functions.showToast(getActivity(), "An error occurred")) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put(Globals.SESSION_ID, sharedpreferences.getString(Globals.SESSION_KEY, ""));
                            return params;
                        }
                    };
                    queue.add(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else
                Functions.showToast(getActivity(), "Please connect to the Internet");
        }
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
                    (float) responseObject.getDouble(Globals.RATING),
                    requestObject.getString(Globals.MESSAGE),
                    requestObject.getString(Globals.VEHICLE_REGISTRATION_NUMBER));
            new QuerySave().execute(query);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void uploadBitmap(final Bitmap bitmap) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, getResources().getString(R.string.url).concat(APIs.raiseQuery),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);
    }

    class VolleyMultipartRequest extends Request<NetworkResponse> {


        private final String twoHyphens = "--";
        private final String lineEnd = "\r\n";
        private final String boundary = "apiclient-" + System.currentTimeMillis();

        private Response.Listener<NetworkResponse> mListener;
        private Response.ErrorListener mErrorListener;
        private Map<String, String> mHeaders;


        public VolleyMultipartRequest(int method, String url,
                                      Response.Listener<NetworkResponse> listener,
                                      Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return (mHeaders != null) ? mHeaders : super.getHeaders();
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data;boundary=" + boundary;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            try {
                // populate text payload
                Map<String, String> params = getParams();
                if (params != null && params.size() > 0) {
                    textParse(dos, params, getParamsEncoding());
                }

                // populate data byte payload
                Map<String, DataPart> data = getByteData();
                if (data != null && data.size() > 0) {
                    dataParse(dos, data);
                }

                // close multipart form data after text and file data
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                return bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Custom method handle data payload.
         *
         * @return Map data part label with data byte
         * @throws AuthFailureError
         */
        protected Map<String, DataPart> getByteData() throws AuthFailureError {
            return null;
        }

        @Override
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            try {
                return Response.success(
                        response,
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            mListener.onResponse(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }

        /**
         * Parse string map into data output stream by key and value.
         *
         * @param dataOutputStream data output stream handle string parsing
         * @param params           string inputs collection
         * @param encoding         encode the inputs, default UTF-8
         * @throws IOException
         */
        private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
                }
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: " + encoding, uee);
            }
        }

        /**
         * Parse data into data output stream.
         *
         * @param dataOutputStream data output stream handle file attachment
         * @param data             loop through data
         * @throws IOException
         */
        private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException {
            for (Map.Entry<String, DataPart> entry : data.entrySet()) {
                buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
            }
        }

        /**
         * Write string data into header and data output stream.
         *
         * @param dataOutputStream data output stream handle string parsing
         * @param parameterName    name of input
         * @param parameterValue   value of input
         * @throws IOException
         */
        private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(parameterValue + lineEnd);
        }

        /**
         * Write data file into header and data output stream.
         *
         * @param dataOutputStream data output stream handle data parsing
         * @param dataFile         data byte as DataPart from collection
         * @param inputName        name of data input
         * @throws IOException
         */
        private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                    inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
            if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
                dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
            }
            dataOutputStream.writeBytes(lineEnd);

            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
        }

        class DataPart {
            private String fileName;
            private byte[] content;
            private String type;

            public DataPart() {
            }

            DataPart(String name, byte[] data) {
                fileName = name;
                content = data;
            }

            String getFileName() {
                return fileName;
            }

            byte[] getContent() {
                return content;
            }

            String getType() {
                return type;
            }

        }
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public String getPath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    Uri picUri = data.getData();
                    String filePath = getPath(picUri);
                    if (filePath != null) {
                        try {
                            Log.d("filePath", String.valueOf(filePath));
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), picUri);
                            uploadBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(
                                getActivity(),"no image selected",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Functions.showToast(getActivity(), "Click image again");
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                Functions.showToast(getActivity(), "No App available for Cropping");
        }
    }

    private void runTextRecognition(Bitmap x) {
        TextRecognition.getClient().process(InputImage.fromBitmap(x, 0))
                .addOnSuccessListener(texts -> processTextRecognitionResult(texts))
                .addOnFailureListener(e -> {
                    Functions.showToast(getActivity(), "Please enter manually");
                    e.printStackTrace();
                });
    }

    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Functions.showToast(getActivity(), "Please enter manually");
            return;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++)
                    str.append(elements.get(k).getText());
            }
        }
        vehicleNumber.setText(str);
    }

    private void finishTask() {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(Globals.QID, Integer.parseInt(responseObject.getString(Globals.QID)));
            bundle.putString(Globals.STATUS, Globals.QUERY_DEFAULT_STATUS);
            bundle.putString(Globals.MESSAGE, requestObject.getString(Globals.MESSAGE));
            bundle.putLong(Globals.QUERY_CREATE_DATE, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(requestObject.getString(Globals.QUERY_CREATE_DATE)).getTime());
            bundle.putByteArray(Globals.VEHICLE_IMAGE_NUMBER, bArray);
            bundle.putString(Globals.VEHICLE_REGISTRATION_NUMBER, requestObject.getString(Globals.VEHICLE_REGISTRATION_NUMBER));
            QueryDetailsFragment querydetailsFragment = new QueryDetailsFragment();
            querydetailsFragment.setArguments(bundle);
            getActivity().runOnUiThread(() -> Functions.setCurrentFragment(getActivity(), querydetailsFragment));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private class BitmapTask extends AsyncTask<Void, Void, Void> {
        private final Bitmap bitmapTmp;

        public BitmapTask(byte[] byteArray, int width, int height, String conf) {
            Bitmap.Config configBmp = Bitmap.Config.valueOf(conf);
            Bitmap bitmapTmp = Bitmap.createBitmap(width, height, configBmp);
            ByteBuffer buffer = ByteBuffer.wrap(byteArray);
            bitmapTmp.copyPixelsFromBuffer(buffer);
            this.bitmapTmp = bitmapTmp;
        }

        @Override
        protected Void doInBackground(Void... params) {
            runTextRecognition(this.bitmapTmp);
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

}