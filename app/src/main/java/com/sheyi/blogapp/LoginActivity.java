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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText mLoginEmailField;
    private EditText mLoginPasswordField;
    private Button mLoginBtn;
    private Button registerLoginBtn;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseUsers;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mLoginEmailField = findViewById(R.id.loginEmailField);
        mLoginPasswordField = findViewById(R.id.loginPasswordField);
        registerLoginBtn = findViewById(R.id.registerBtn);
        registerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(),RegisterActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            }
        });
        mLoginBtn = findViewById(R.id.loginBtn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmailField.getText().toString().trim();
                String pass  = mLoginPasswordField.getText().toString().trim();
                 if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
                     Toast.makeText(getApplicationContext(),"Fill Both Information",Toast.LENGTH_SHORT).show();
                 }else{
                     progressDialog.setMessage("Signing In...");
                     progressDialog.show();
                      mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              if(task.isSuccessful()){
                                progressDialog.dismiss();
                                checkUserExist();
                              }else{
                                  progressDialog.dismiss();
                                  Toast.makeText(getApplicationContext(),"Login Details Incorrect",Toast.LENGTH_SHORT).show();
                              }
                          }
                      });
                 }
            }
        });

    }

    private void checkUserExist() {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : null;
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userId)){
                    Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"You need to Setup your account",Toast.LENGTH_SHORT).show();
                    Intent setupIntent = new Intent(getApplicationContext(),SetupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
