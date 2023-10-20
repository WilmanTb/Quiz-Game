package com.quizgame.quizgame.LandingPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quizgame.quizgame.Add_Quetions;
import com.quizgame.quizgame.R;
import com.quizgame.quizgame.User.Activity.Dashboard_User;

public class Login extends AppCompatActivity {

    private EditText et_Email, et_Password;
    private Button btn_login;
    private ImageView arrow_back;
    private TextView txt_belumPunyaAkun;
    private DatabaseReference dbUsers;
    private FirebaseAuth userAuth;
    private String Email, Password, Uid, mPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getID();

        userAuth = FirebaseAuth.getInstance();

        dbUsers = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        });

        txt_belumPunyaAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                Email = et_Email.getText().toString();
                Password = et_Password.getText().toString();
                if (Email.isEmpty() || Password.isEmpty()) {
                    Toast.makeText(Login.this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (Email.equals("admin") && Password.equals("admin")){
                    startActivity(new Intent(Login.this, Add_Quetions.class));
                    finish();
                } else {
                    getUid(new FirebaseCallback() {
                        @Override
                        public void onCallback(String Uid) {
                            checkPassword(Uid);
                        }
                    });
                }
            }
        });
    }

    private void getID() {
        et_Email = findViewById(R.id.et_Email);
        et_Password = findViewById(R.id.et_Password);
        arrow_back = findViewById(R.id.arrow_back);
        btn_login = findViewById(R.id.btn_login);
        txt_belumPunyaAkun = findViewById(R.id.txt_belumPunyaAkun);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mohon menunggu...");
        progressDialog.setCancelable(false);
    }

    private void loginUser(){
        userAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    startActivity(new Intent(Login.this, Dashboard_User.class));
                    finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Login gagal\nMohon cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback(String Uid);
    }

    private void getUid(FirebaseCallback firebaseCallback) {
        Email = et_Email.getText().toString();
        dbUsers.orderByChild("email")
                .equalTo(Email)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.exists()) {
                                    Uid = dataSnapshot.getKey();
                                }
                                break;
                            }
                            firebaseCallback.onCallback(Uid);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Email tidak terdaftar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkPassword(String UID) {
        Password = et_Password.getText().toString();
        dbUsers.child(UID).child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPassword = snapshot.getValue().toString();
                if (mPassword.equals(Password)) {
                    loginUser();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Password salah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}