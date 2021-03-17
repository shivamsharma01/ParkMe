package com.android.parkme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPassword extends AppCompatActivity {
    Button submit;
    EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        submit = findViewById(R.id.submit_button);
        email = findViewById(R.id.fpassword_email_value);
        submit.setOnClickListener(v->{
            fpassword_module();
        });
    }
    private void fpassword_module()
    {
        Editable s = email.getText();
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}