package com.android.parkme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RaiseQueryActivity extends AppCompatActivity {
    private Spinner queryTypeDropdown;
    private EditText dateText;
    private ImageView clickedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_query);

        queryTypeDropdown = findViewById(R.id.dropdown_query_types);
        ArrayAdapter<String> queryTypeAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.query_types_array));
        queryTypeAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        queryTypeDropdown.setAdapter(queryTypeAdaptor);

        dateText = findViewById(R.id.date_value);
        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
        dateText.setText(sdf.format( new Date()));

        clickedImage = findViewById(R.id.clicked_image);
    }

}