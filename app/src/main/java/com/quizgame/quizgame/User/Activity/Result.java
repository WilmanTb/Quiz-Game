package com.quizgame.quizgame.User.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.quizgame.quizgame.R;

public class Result extends AppCompatActivity {

    CircularProgressBar circularProgressBar;
    TextView tv_score, totalMenjawab;
    int Benar, Salah, Hasil;
    Button btn_dashboard;
    DatabaseReference dbSkor;
    FirebaseAuth userAuth;
    String UID,skorTertinggi, Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        userAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = userAuth.getCurrentUser();
        UID = firebaseUser.getUid();

        Benar = getIntent().getIntExtra("benar", 0);
        Salah = getIntent().getIntExtra("salah", 0);
        Hasil = getIntent().getIntExtra("hasil", 0);

        circularProgressBar = findViewById(R.id.circularProgressBar);
        tv_score = findViewById(R.id.tv_score);
        totalMenjawab = findViewById(R.id.totalMenjawab);
        btn_dashboard = findViewById(R.id.btn_dashboard);

        circularProgressBar.setProgress(Benar);
        circularProgressBar.setProgressMax(Pertanyaan.totalPertanyaan);
        totalMenjawab.setText(Benar + "/" + Pertanyaan.totalPertanyaan);
        tv_score.setText(String.valueOf(Hasil));

        dbSkor = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        btn_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSkorTertinggi();
                startActivity(new Intent(Result.this, Dashboard_User.class));
                finish();
            }
        });


    }

    private void getSkorTertinggi(){
        dbSkor.child("DataJawaban").child(UID).orderByChild("skor").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                       Key = dataSnapshot.getKey();
                       dbSkor.child("DataJawaban").child(UID).child(Key).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot1) {
                               skorTertinggi = snapshot1.child("skor").getValue().toString();
                               dbSkor.child("Users").child(UID).child("skorTertinggi").setValue(skorTertinggi);
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}