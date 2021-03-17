package com.android.parkme;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ChangePassword extends Fragment {
    Button csubmit;
    EditText email,old_p,new_p;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        csubmit = view.findViewById(R.id.cpassword_button);
        email = view.findViewById(R.id.cpassword_email_value);
        old_p = view.findViewById(R.id.cpassword_old_value);
        new_p = view.findViewById(R.id.cpassword_new_value);
        csubmit.setOnClickListener(v -> {
            onSubmit();
        });
        return view;
    }
    private void onSubmit()
    {
        Editable e = email.getText();
        Editable op = old_p.getText();
        Editable np = new_p.getText();

        Toast.makeText(getContext(),e,Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(),op,Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(),np,Toast.LENGTH_SHORT).show();
    }
}