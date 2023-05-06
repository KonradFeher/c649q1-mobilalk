package com.example.currensee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;

    EditText usernameField;
    EditText emailField;
    EditText passwordField;
    EditText password2Field;

    private SharedPreferences preferences;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        usernameField = findViewById(R.id.username_field);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        password2Field = findViewById(R.id.password_field2);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String emailPref = preferences.getString("email", "");
        String passwordPref = preferences.getString("password", "");

        emailField.setText(emailPref);
        passwordField.setText(passwordPref);
    }

    public void register(View view) {
        String username = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String password2 = password2Field.getText().toString();

        if (!password.equals(password2)) {
            Toast.makeText(this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
            return;
        }

//        int accountTypeId = accountTypeGroup.getCheckedRadioButtonId();
//        View radioButton = accountTypeGroup.findViewById(accountTypeId);
//        int id = accountTypeGroup.indexOfChild(radioButton);
//        String accountType =  ((RadioButton)accountTypeGroup.getChildAt(id)).getText().toString();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d(LOG_TAG, "Registered successfully.");
                    Toast.makeText(RegisterActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();
                    goToCurrencies();
                } else {
                    Log.w(LOG_TAG, "An error occurred while registering: ", task.getException());
                    Toast.makeText(RegisterActivity.this, "An error occurred..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void goToCurrencies() {
        Intent intent = new Intent(this, CurrenciesActivity.class);
        startActivity(intent);
    }


}