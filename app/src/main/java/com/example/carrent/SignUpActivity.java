package com.example.carrent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private TextView clickHere;
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private CheckBox ShowPass1, ShowPass2;
    private Button btnNext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        clickHere = findViewById(R.id.tvClickHereSignUp);
        etUsername = findViewById(R.id.etUsernameSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPasswordSignUp);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ShowPass1 = findViewById(R.id.showPass1);
        ShowPass2 = findViewById(R.id.showPass2);
        btnNext = findViewById(R.id.btnNext);

        mAuth = FirebaseAuth.getInstance();

        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        ShowPass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ShowPass1.isChecked()){
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        ShowPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ShowPass2.isChecked()){
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUsername.getText().toString().length()>0 && etPassword.getText().toString().length()>0 && etEmail.getText().toString().length()>0 && etConfirmPassword.getText().toString().length()>0 && etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                    Intent intent = new Intent(SignUpActivity.this, FillUpDataActivity.class);
                    intent.putExtra("username", etUsername.getText().toString());
                    intent.putExtra("email", etEmail.getText().toString());
                    intent.putExtra("password", etPassword.getText().toString());
                    startActivity(intent);
                }else if (etUsername.getText().toString().length()>0 && etPassword.getText().toString().length()>0 && etEmail.getText().toString().length()>0 && etConfirmPassword.getText().toString().length()>0 && !etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Password and Confirm Password is not same", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reload(){
        startActivity(new Intent(SignUpActivity.this, DashboardActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
}