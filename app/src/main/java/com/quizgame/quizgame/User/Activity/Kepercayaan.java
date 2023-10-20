package com.quizgame.quizgame.User.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quizgame.quizgame.R;

public class Kepercayaan extends AppCompatActivity {

    private TextView tv_kepercayaan;
    private DatabaseReference dbMateri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kepercayaan);

        tv_kepercayaan = findViewById(R.id.tv_kepercayaan);

        dbMateri = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Materi");

        dbMateri.child("Upacara Adat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_kepercayaan.setText(snapshot.child("materi").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}