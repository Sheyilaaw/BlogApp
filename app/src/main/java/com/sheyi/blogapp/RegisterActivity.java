package com.sheyi.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;

    private Button mRegisterBtn;
    private Button loginButton;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth =   FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress =  new ProgressDialog(this);
        mNameField = findViewById(R.id.nameField);
        mEmailField = findViewById(R.id.emailField);
        mPasswordField = findViewById(R.id.passwordField);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
            }
        });

    }

    private void startRegister() {
        final String name        =  mNameField.getText().toString().trim();
        final String email       =  mEmailField.getText().toString().trim();
        final String password    =  mPasswordField.getText().toString().trim();
        if(TextUtils.isEmpty(name))
            Toast.makeText(getApplicationContext(),"Enter Name ",Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(email))
            Toast.makeText(getApplicationContext(),"Enter Email ",Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(password))
            Toast.makeText(getApplicationContext(),"Enter Password ",Toast.LENGTH_SHORT).show();
        else if(password.length() < 6)
            Toast.makeText(getApplicationContext(),"Password Must be At least 6 characters ",Toast.LENGTH_SHORT).show();
        else{
            mProgress.setMessage("Signing Up...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                String userId = user.getUid();
                                DatabaseReference current = mDatabase.child(userId);
                                current.child("name").setValue(name);
                                current.child("image").setValue("default");
                                mProgress.dismiss();
                                Toast.makeText(getApplicationContext(), "Registration Successful.",
                                        Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(), "Registration failed.",
                                        Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                            }
                        }
                    });

        }
    }

}
