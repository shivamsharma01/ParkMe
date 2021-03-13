package com.android.parkme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RaiseQueryFragment extends Fragment {
    private Spinner queryTypeDropdown;
    private EditText dateText;
    private ImageView clickedImage;

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

        dateText = getActivity().findViewById(R.id.date_value);
        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
        dateText.setText(sdf.format( new Date()));

        clickedImage = getActivity().findViewById(R.id.clicked_image);
    }

}