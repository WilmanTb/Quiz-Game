package com.quizgame.quizgame.User.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.quizgame.quizgame.R;
import com.quizgame.quizgame.User.Activity.Dashboard_User;
import com.quizgame.quizgame.User.Activity.Pertanyaan;
import com.quizgame.quizgame.User.Activity.Tambah_Foto;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard_User_Fragment extends Fragment {

    ViewGroup root;
    CardView cv_mudah, cv_sulit, cv_sedang;
    Button btn_bermain;
    String bermain, UID, urlFoto, skorTerbaik;
    FirebaseAuth userAuth;
    FirebaseUser currentUser;
    DatabaseReference dbData;
    public static CircleImageView img;
    TextView txt_bermain, txt_score, txt_point, nama_user;
    int color = 0;
    public static String Kesulitan = "empty";
    long Total = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard_user, container, false);

        getID();

        dbData = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        getDataPemain();
        getPoint();
        getFoto();

        cv_mudah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = 1;
                chCvColor();
            }
        });

        cv_sulit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = 3;
                chCvColor();
            }
        });

        cv_sedang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = 2;
                chCvColor();
            }
        });

        btn_bermain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValue();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Tambah_Foto.class));
            }
        });

        if (isAdded()){
            return root;
        }
        return root;
    }

    private void getID() {
        cv_mudah = root.findViewById(R.id.cv_mudah);
        cv_sulit = root.findViewById(R.id.cv_sulit);
        cv_sedang = root.findViewById(R.id.cv_sedang);
        btn_bermain = root.findViewById(R.id.btn_bermain);
        txt_bermain = root.findViewById(R.id.txt_bermain);
        txt_score = root.findViewById(R.id.txt_score);
        txt_point = root.findViewById(R.id.txt_point);
        nama_user = root.findViewById(R.id.nama_user);
        img = root.findViewById(R.id.img);
    }

    private void chCvColor() {
        if (color == 1) {
            cv_mudah.setCardBackgroundColor(Color.parseColor("#FF7B00"));
            cv_sedang.setCardBackgroundColor(Color.parseColor("#015A8C"));
            cv_sulit.setCardBackgroundColor(Color.parseColor("#015A8C"));
            Kesulitan = "Mudah";
        } else if (color == 2) {
            cv_mudah.setCardBackgroundColor(Color.parseColor("#015A8C"));
            cv_sedang.setCardBackgroundColor(Color.parseColor("#FF7B00"));
            cv_sulit.setCardBackgroundColor(Color.parseColor("#015A8C"));
            Kesulitan = "Sedang";
        } else if (color == 3) {
            cv_mudah.setCardBackgroundColor(Color.parseColor("#015A8C"));
            cv_sedang.setCardBackgroundColor(Color.parseColor("#015A8C"));
            cv_sulit.setCardBackgroundColor(Color.parseColor("#FF7B00"));
            Kesulitan = "Sulit";
        } else {
            Kesulitan = "empty";
            cv_mudah.setBackgroundColor(Color.parseColor("#015A8C"));
            cv_sedang.setCardBackgroundColor(Color.parseColor("#015A8C"));
            cv_sulit.setCardBackgroundColor(Color.parseColor("#015A8C"));
        }
    }

    private void checkValue() {
        if (Kesulitan.equals("empty")) {
            Toast.makeText(getActivity(), "Harus memilih tingkat kesulitan! ", Toast.LENGTH_SHORT).show();
        } else {
            checkPoint();
        }
    }

    private void checkPoint() {
        if (Kesulitan.equals("Sedang") && Total < 200) {
            Toast.makeText(getActivity(), "Anda harus mencapai 200 poin untuk memilih kesulitan ini", Toast.LENGTH_SHORT).show();
        } else if (Kesulitan.equals("Sulit") && Total < 400) {
            Toast.makeText(getActivity(), "Anda harus mencapai 400 poin untuk memilih kesulitan ini", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(getActivity(), Pertanyaan.class));
        }
    }

    private void getDataPemain() {
        getUID();
        dbData.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    bermain = snapshot.child("bermain").getValue().toString();
                    skorTerbaik = snapshot.child("skorTertinggi").getValue().toString();
                    nama_user.setText(snapshot.child("nama").getValue().toString());
                    txt_bermain.setText(bermain);
                    txt_score.setText(skorTerbaik);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPoint() {
        getUID();
        DatabaseReference dbData = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        dbData.child("DataJawaban").child(UID).orderByChild("skor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        long x = (long) dataSnapshot.child("skor").getValue();
                        Total = Total + x;
                        txt_point.setText(String.valueOf(Total));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFoto() {
        getUID();
        dbData.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                urlFoto = snapshot.child("foto").getValue().toString();
                Glide.with(requireActivity()).load(urlFoto).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUID() {
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        UID = currentUser.getUid();
    }

}
