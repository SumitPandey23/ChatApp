package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    TextView logsignup;

    Button button;
    EditText email,password;
    FirebaseAuth auth;
    String emailpattern ="[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
    android.app.ProgressDialog progressDialog;
    static final String SHARED_PREFS ="sharedPrefs";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        //getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null){
            ChechkBox();
        }





        button = findViewById(R.id.logbutton);
        email = findViewById(R.id.editTextlogEmail);
        password = findViewById(R.id.editTextlogPassword);
        logsignup =findViewById(R.id.logsignup);

        logsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, registration.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString();
                String Password = password.getText().toString();

                if((TextUtils.isEmpty(Email))){
                    progressDialog.dismiss();
                    Toast.makeText(login.this,"Enter the Email",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(Password)) {
                    progressDialog.dismiss();
                    Toast.makeText(login.this,"Enter the Password",Toast.LENGTH_SHORT).show();
                } else if (!Email.matches(emailpattern)) {
                    progressDialog.dismiss();
                    email.setError("Give Proper Email Address");
                } else if (password.length()<6) {
                    progressDialog.dismiss();
                    password.setError("Password needs to be more then 6 character");
                    Toast.makeText(login.this, "Password needs to be longer then 6 character", Toast.LENGTH_SHORT).show();
                }else {
                    auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                /*SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name","true");
                                editor.apply();*/
                                progressDialog.show();
                                try{
                                    Intent intent = new Intent(login.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }catch(Exception e){
                                    Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });




    }

    private void ChechkBox() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String check = sharedPreferences.getString("name","");
        if(check.equals("true")){
            progressDialog.show();
            try{
                Intent intent = new Intent(login.this,MainActivity.class);
                startActivity(intent);
                finish();
            }catch(Exception e){
                Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}