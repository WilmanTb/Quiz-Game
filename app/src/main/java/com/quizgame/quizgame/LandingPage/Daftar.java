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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quizgame.quizgame.R;
import com.quizgame.quizgame.User.Activity.Dashboard_User;
import com.quizgame.quizgame.User.Class.User_Model;


public class Daftar extends AppCompatActivity {

    private EditText et_nama, et_email, et_password, et_confirmPass;
    private Button btn_daftar;
    private ImageView arrow_back;
    private String Nama, Email, Password, KonfirmasiPassword, Foto = "https://firebasestorage.googleapis.com/v0/b/quizgame-2b068.appspot.com/o/man.png?alt=media&token=4c9e26c9-d3f1-4923-9a06-5832b14a312c", bermain = "0", skorTertinggi = "0";
    private DatabaseReference dbUsers;
    private FirebaseAuth userAuth;
    private ProgressDialog progressDialog;
    private TextView txt_sudahPunyaAkun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        dbUsers = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        getID();

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progressDialog.show();
                checkEmpty();
            }
        });

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Daftar.this, MainActivity.class));
                finish();
            }
        });

        txt_sudahPunyaAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Daftar.this, Login.class));
                finish();
            }
        });
    }

    private void getID(){
        et_nama = findViewById(R.id.et_Nama);
        et_email = findViewById(R.id.et_Email);
        et_password = findViewById(R.id.et_Password);
        et_confirmPass = findViewById(R.id.et_confirmPassword);
        btn_daftar = findViewById(R.id.btn_daftar);
        arrow_back = findViewById(R.id.arrow_back);
        txt_sudahPunyaAkun = findViewById(R.id.txt_sudahPunyaAkun);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mohon menunggu...");
        progressDialog.setCancelable(false);
    }

    private void getString(){
        Nama = et_nama.getText().toString();
        Email = et_email.getText().toString();
        Password = et_password.getText().toString();
        KonfirmasiPassword = et_confirmPass.getText().toString();
    }

    private void checkEmpty(){
        getString();
        if (Nama.isEmpty() || Email.isEmpty() || Password.isEmpty() || KonfirmasiPassword.isEmpty()){
            Toast.makeText(this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
            //progressDialog.dismiss();
        } else {
            checkPassword();
        }
    }

    private void checkPassword(){
        if (Password.length() < 8){
            Toast.makeText(this, "Panjang password minimal 8 karakter", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!Password.equals(KonfirmasiPassword)){
            progressDialog.dismiss();
            Toast.makeText(this, "Password tidak sesuai", Toast.LENGTH_SHORT).show();
        } else {
            sendData(Nama, Email, Password, Foto);
        }
    }

    private void sendData(String nama, String email, String password, String foto) {
        User_Model userModel = new User_Model(Nama, Email, Password, Foto,bermain, skorTertinggi);
        userAuth = FirebaseAuth.getInstance();
        userAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser currentUser = userAuth.getCurrentUser();
                String UID = currentUser.getUid();
                dbUsers.child(UID).setValue(userModel);
                //progressDialog.dismiss();
                startActivity(new Intent(Daftar.this, Dashboard_User.class));
                finish();
            }
        });
    }
}