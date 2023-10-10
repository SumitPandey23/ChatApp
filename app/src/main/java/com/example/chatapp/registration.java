package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {

    TextView loginbutton;
    EditText rgusername,rgemail,rgpassword,rgrepassword;
    Button rgsignup;
    CircleImageView rgprofileImg;
    FirebaseAuth auth;
    Uri imageUri;
    String imageuri;
    String emailpattern ="[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;
    static  final String SHARED_PREFS = "sharedPrefs";


    //@SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Establishing Your Account");
        progressDialog.setCancelable(false);
        //getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null) {
            CheckBox();
        }
        loginbutton = findViewById(R.id.loginbutton);
        rgusername = findViewById(R.id.rgusername);
        rgemail = findViewById(R.id.rgemail);
        rgpassword = findViewById(R.id.rgpassword);
        rgrepassword = findViewById(R.id.rgrepassword);
        rgsignup = findViewById(R.id.signupbutton);
        rgprofileImg = findViewById(R.id.profileimg);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registration.this,login.class);
                startActivity(intent);
                finish();
            }
        });

        rgsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = rgusername.getText().toString();
                String email = rgemail.getText().toString();
                String password = rgpassword.getText().toString();
                String cpassword = rgrepassword.getText().toString();
                String status ="I am using this Application";

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cpassword)){
                    progressDialog.dismiss();
                    Toast.makeText(registration.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailpattern)) {
                    progressDialog.dismiss();
                    rgemail.setError("Type a Valid Email Here");
                }else if (password.length()<6) {
                    progressDialog.dismiss();
                    rgpassword.setError("Password Must Be 6 Character Or More");
                }else if (!password.equals(cpassword)) {
                    progressDialog.dismiss();
                    rgpassword.setError("The Password Doesn't Match");
                }else{
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("upload").child(id);

                                if(imageUri!=null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri = uri.toString();
                                                        Users users = new Users(id,name,email,password,imageuri,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                   /* SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                    editor.putString("name","true");
                                                                    editor.apply();*/
                                                                    progressDialog.show();
                                                                    Intent intent = new Intent(registration.this,MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(registration.this, "Error In Creating The User", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else{
                                    String status ="I am using this Application";
                                    imageuri ="https://firebasestorage.googleapis.com/v0/b/chatapp-52dd2.appspot.com/o/man.png?alt=media&token=da6cc27b-e0a2-49d9-9e3d-96d4a923de2c";
                                    Users users = new Users(id,name,email,password,imageuri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.show();
                                                Intent intent = new Intent(registration.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(registration.this, "Error In Creating The User", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else {
                                Toast.makeText(registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        rgprofileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent,"Select Picture"),10);
            }
        });



    }

    private void CheckBox() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String check = sharedPreferences.getString("name","");
        if(check.equals("true")){
            progressDialog.show();
            Intent intent = new Intent(registration.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(data!=null){
                imageUri = data.getData();
                rgprofileImg.setImageURI(imageUri);
            }
        }
    }
}