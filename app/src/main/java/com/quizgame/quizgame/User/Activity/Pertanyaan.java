package com.quizgame.quizgame.User.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quizgame.quizgame.R;
import com.quizgame.quizgame.User.Class.Pertanyaan_Model;
import com.quizgame.quizgame.User.Fragment.Dashboard_User_Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Pertanyaan extends AppCompatActivity {

    private CardView cv_jawaban_pertama, cv_jawaban_kedua, cv_jawaban_ketiga, cv_jawaban_keempat, cv_penjelasan, cv_pertanyaan;
    private TextView pertanyaan, tv_jawaban_pertama, tv_jawaban_kedua, tv_jawaban_ketiga, tv_jawaban_keempat, tv_penjelasan;
    private Button btn_submit;
    private CountDownTimer countDownTimer;
    private int timerValue = 10;
    private ProgressBar progressPertanyaan;
    private ArrayList<Pertanyaan_Model> arrayPertanyaan;
    private Pertanyaan_Model pertanyaanModel;
    private ArrayList<Pertanyaan_Model> listPertanyaan;
    private DatabaseReference dbPertanyaan, dbDataJawaban;
    private Dialog readyDiaolog, dialog;
    private FirebaseAuth userAuth;
    private ImageView img_pertama, img_kedua, img_ketiga, img_keempat, img_penjelasan;
    String UID, Kesulitan, gambar;
    public static long totalPertanyaan;
    public static int Hasil;
    private int index = 0, jumlahBenar = 0, jumlahSalah = 0, jb = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pertanyaan);

        getID();

        dbPertanyaan = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Pertanyaan");

        Kesulitan = Dashboard_User_Fragment.Kesulitan;
        Toast.makeText(this, Kesulitan, Toast.LENGTH_SHORT).show();
        /*  showImage();*/

        arrayPertanyaan = new ArrayList<>();
        listPertanyaan = new ArrayList<>();
        index = 0;
        jumlahBenar = 0;
        jumlahSalah = 0;
        totalPertanyaan = 0;

        userAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = userAuth.getCurrentUser();
        UID = firebaseUser.getUid();

       /* readyDiaolog = new Dialog(Pertanyaan.this);
        readyDiaolog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        readyDiaolog.setContentView(R.layout.time_out);*/

        setDialog();

        btn_submit.setClickable(false);
        cv_penjelasan.setVisibility(View.GONE);

    }

    private void getID() {
        cv_jawaban_pertama = findViewById(R.id.cv_jawaban_pertama);
        cv_jawaban_kedua = findViewById(R.id.cv_jawaban_kedua);
        cv_jawaban_ketiga = findViewById(R.id.cv_jawaban_ketiga);
        cv_jawaban_keempat = findViewById(R.id.cv_jawaban_keempat);
        pertanyaan = findViewById(R.id.pertanyaan);
        tv_jawaban_pertama = findViewById(R.id.tv_jawaban_pertama);
        tv_jawaban_kedua = findViewById(R.id.tv_jawaban_kedua);
        tv_jawaban_ketiga = findViewById(R.id.tv_jawaban_ketiga);
        tv_jawaban_keempat = findViewById(R.id.tv_jawaban_keempat);
        btn_submit = findViewById(R.id.btn_submit);
        progressPertanyaan = findViewById(R.id.progressPertanyaan);
        tv_penjelasan = findViewById(R.id.txt_penjelasan);
        cv_penjelasan = findViewById(R.id.cv_penjelasan);
        img_pertama = findViewById(R.id.img_pertama);
        img_kedua = findViewById(R.id.img_kedua);
        img_ketiga = findViewById(R.id.img_ketiga);
        img_keempat = findViewById(R.id.img_keempat);
        img_penjelasan = findViewById(R.id.img_penjelasan);

    }

    private void getTimer() {
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                timerValue = timerValue - 1;
                progressPertanyaan.setProgress(timerValue);
            }

            @Override
            public void onFinish() {
                Dialog dialog = new Dialog(Pertanyaan.this);
                dialog.setContentView(R.layout.time_out);

                dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        setCvPenjelasan();
                        disabledButton();
                        hitungSalah2();
                        btn_submit.setClickable(true);
                    }
                });
                dialog.show();
            }
        }.start();
    }

    private void setDialog() {
        Dialog readyDiaolog = new Dialog(Pertanyaan.this);
        readyDiaolog.setContentView(R.layout.ready);

        readyDiaolog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showItem();
                getPertanyaan();
                getTimer();
                readyDiaolog.dismiss();
            }
        });
        readyDiaolog.show();
    }

    private void getPertanyaan() {
        dbPertanyaan.orderByChild("kesulitan").equalTo(Kesulitan).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Pertanyaan_Model pertanyaanModel = dataSnapshot.getValue(Pertanyaan_Model.class);
                        arrayPertanyaan.add(pertanyaanModel);
                        totalPertanyaan = snapshot.getChildrenCount();
                        setPertanyaan();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*<----- METODE SHUFFLE----->.*/
    private void setPertanyaan() {
        btn_submit.setClickable(false);
        listPertanyaan = arrayPertanyaan;
        Collections.shuffle(listPertanyaan);
        pertanyaanModel = listPertanyaan.get(index);
        gambar = pertanyaanModel.getGambar();
        if (gambar.equals("empty")) {
            showImage();
            pertanyaan.setText(pertanyaanModel.getPertanyaan());
            tv_jawaban_pertama.setText(pertanyaanModel.getA());
            tv_jawaban_kedua.setText(pertanyaanModel.getB());
            tv_jawaban_ketiga.setText(pertanyaanModel.getC());
            tv_jawaban_keempat.setText(pertanyaanModel.getD());

        } else {
            btn_submit.setClickable(false);
            listPertanyaan = arrayPertanyaan;
            Collections.shuffle(listPertanyaan);
            pertanyaanModel = listPertanyaan.get(index);
            pertanyaan.setText(pertanyaanModel.getPertanyaan());
            img_pertama.setVisibility(View.VISIBLE);
            img_kedua.setVisibility(View.VISIBLE);
            img_ketiga.setVisibility(View.VISIBLE);
            img_keempat.setVisibility(View.VISIBLE);
            img_penjelasan.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(gambar).into(img_penjelasan);
            Glide.with(getApplicationContext()).load(pertanyaanModel.getA()).into(img_pertama);
            Glide.with(getApplicationContext()).load(pertanyaanModel.getB()).into(img_kedua);
            Glide.with(getApplicationContext()).load(pertanyaanModel.getC()).into(img_ketiga);
            Glide.with(getApplicationContext()).load(pertanyaanModel.getD()).into(img_keempat);
        }
    }

    private void setPertanyaan2() {
        btn_submit.setClickable(false);
        pertanyaanModel = listPertanyaan.get(index);
        gambar = pertanyaanModel.getGambar();
        if (gambar.equals("empty")) {
            showImage();
            pertanyaan.setText(pertanyaanModel.getPertanyaan());
            tv_jawaban_pertama.setText(pertanyaanModel.getA());
            tv_jawaban_kedua.setText(pertanyaanModel.getB());
            tv_jawaban_ketiga.setText(pertanyaanModel.getC());
            tv_jawaban_keempat.setText(pertanyaanModel.getD());
        } else {
            btn_submit.setClickable(false);
            pertanyaanModel = listPertanyaan.get(index);
            pertanyaan.setText(pertanyaanModel.getPertanyaan());
            img_pertama.setVisibility(View.VISIBLE);
            img_kedua.setVisibility(View.VISIBLE);
            img_ketiga.setVisibility(View.VISIBLE);
            img_keempat.setVisibility(View.VISIBLE);
            img_penjelasan.setVisibility(View.VISIBLE);
            tv_jawaban_pertama.setText("");
            tv_jawaban_kedua.setText("");
            tv_jawaban_ketiga.setText("");
            tv_jawaban_keempat.setText("");
            Glide.with(getApplicationContext()).load(gambar).into(img_penjelasan);
            Glide.with(getApplicationContext()).load(pertanyaanModel.getA()).into(img_pertama);
            Glide.with(getApplicationContext()).load(pertanyaanModel.getB()).into(img_kedua);
            Glide.with(getApplicationContext()).load(pertanyaanModel.getC()).into(img_ketiga);
            Glide.with(getApplicationContext()).load(pertanyaanModel.getD()).into(img_keempat);
        }
    }

    private void showImage() {
        img_pertama.setVisibility(View.GONE);
        img_kedua.setVisibility(View.GONE);
        img_ketiga.setVisibility(View.GONE);
        img_keempat.setVisibility(View.GONE);
        img_penjelasan.setVisibility(View.GONE);
    }

    public void hitungBenar(CardView cardView) {
        cardView.setCardBackgroundColor(Color.GREEN);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumlahBenar++;
                if (index < arrayPertanyaan.size() - 1) {
                    index++;
                    pertanyaanModel = arrayPertanyaan.get(index);
                    setPertanyaan2();
                    resetColor();
                    enabledButton();
                    timerValue = 10;
                    getTimer();
                    cv_penjelasan.setVisibility(View.GONE);
                } else {
                    gameOver();
                }
            }
        });
    }

    public void hitungSalah(CardView cv_jawaban_pertama) {
        cv_jawaban_pertama.setCardBackgroundColor(Color.parseColor("#FF0000"));
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumlahSalah++;
                if (jumlahSalah < 3) {
                    if (index < arrayPertanyaan.size() - 1) {
                        index++;
                        pertanyaanModel = arrayPertanyaan.get(index);
                        setPertanyaan2();
                        resetColor();
                        enabledButton();
                        timerValue = 10;
                        getTimer();
                        cv_penjelasan.setVisibility(View.GONE);
                    } else {
                        gameOver();
                    }
                } else {
                    gameOver();
                }
            }
        });

    }

    public void hitungSalah2() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumlahSalah++;
                if (jumlahSalah < 3) {
                    if (index < arrayPertanyaan.size() - 1) {
                        index++;
                        pertanyaanModel = arrayPertanyaan.get(index);
                        setPertanyaan2();
                        resetColor();
                        enabledButton();
                        timerValue = 10;
                        getTimer();
                        cv_penjelasan.setVisibility(View.GONE);
                    } else {
                        gameOver();
                    }
                } else {
                    gameOver();
                }
            }
        });

    }

    public void gameOver() {
        resetColor();

        dbDataJawaban = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("DataJawaban");

        Hasil = jumlahBenar * 20;
        HashMap hashMap = new HashMap<>();
        hashMap.put("skor", Hasil);
        hashMap.put("benar", String.valueOf(jumlahBenar));
        hashMap.put("salah", String.valueOf(jumlahSalah));
        hashMap.put("kesulitan", Dashboard_User_Fragment.Kesulitan);
        hashMap.put("totalPertanyaan", String.valueOf(totalPertanyaan));
        dbDataJawaban.child(UID).push().setValue(hashMap);
        setBermain();
        Intent intent = new Intent(Pertanyaan.this, Result.class);
        intent.putExtra("benar", jumlahBenar);
        intent.putExtra("salah", jumlahSalah);
        intent.putExtra("hasil", Hasil);
        startActivity(intent);
    }

    public void enabledButton() {
        cv_jawaban_pertama.setClickable(true);
        cv_jawaban_kedua.setClickable(true);
        cv_jawaban_ketiga.setClickable(true);
        cv_jawaban_keempat.setClickable(true);
    }

    public void disabledButton() {
        cv_jawaban_pertama.setClickable(false);
        cv_jawaban_kedua.setClickable(false);
        cv_jawaban_ketiga.setClickable(false);
        cv_jawaban_keempat.setClickable(false);
    }

    public void showItem() {
        cv_jawaban_pertama.setVisibility(View.VISIBLE);
        cv_jawaban_kedua.setVisibility(View.VISIBLE);
        cv_jawaban_ketiga.setVisibility(View.VISIBLE);
        cv_jawaban_keempat.setVisibility(View.VISIBLE);
        btn_submit.setVisibility(View.VISIBLE);
        cv_jawaban_pertama.setVisibility(View.VISIBLE);
    }

    public void resetColor() {
        cv_jawaban_pertama.setCardBackgroundColor(Color.WHITE);
        cv_jawaban_kedua.setCardBackgroundColor(Color.WHITE);
        cv_jawaban_ketiga.setCardBackgroundColor(Color.WHITE);
        cv_jawaban_keempat.setCardBackgroundColor(Color.WHITE);
    }

    private void setCvPenjelasan() {
        cv_penjelasan.setVisibility(View.VISIBLE);
        tv_penjelasan.setText(pertanyaanModel.getPenjelasan());
    }

    public void clickA(View view) {
        disabledButton();
        btn_submit.setClickable(true);
        if (pertanyaanModel.getA().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_pertama.setCardBackgroundColor(Color.GREEN);
            hitungBenar(cv_jawaban_pertama);
        } else if (pertanyaanModel.getB().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_kedua.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_pertama);
        } else if (pertanyaanModel.getC().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_ketiga.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_pertama);
        } else if (pertanyaanModel.getD().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_keempat.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_pertama);
        }
        setCvPenjelasan();
        countDownTimer.cancel();
    }

    public void clickB(View view) {
        disabledButton();
        btn_submit.setClickable(true);
        if (pertanyaanModel.getB().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_kedua.setCardBackgroundColor(Color.GREEN);
            hitungBenar(cv_jawaban_kedua);
        } else if (pertanyaanModel.getA().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_pertama.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_kedua);
        } else if (pertanyaanModel.getC().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_ketiga.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_kedua);
        } else if (pertanyaanModel.getD().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_keempat.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_kedua);
        }
        setCvPenjelasan();
        countDownTimer.cancel();
    }

    public void clickC(View view) {
        disabledButton();
        btn_submit.setClickable(true);
        if (pertanyaanModel.getC().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_ketiga.setCardBackgroundColor(Color.GREEN);
            hitungBenar(cv_jawaban_ketiga);
        } else if (pertanyaanModel.getA().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_pertama.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_ketiga);
        } else if (pertanyaanModel.getB().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_kedua.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_ketiga);
        } else if (pertanyaanModel.getD().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_keempat.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_ketiga);
        }
        setCvPenjelasan();
        countDownTimer.cancel();
    }

    public void clickD(View view) {
        disabledButton();
        btn_submit.setClickable(true);
        if (pertanyaanModel.getD().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_keempat.setCardBackgroundColor(Color.GREEN);
            hitungBenar(cv_jawaban_keempat);
        } else if (pertanyaanModel.getA().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_pertama.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_keempat);
        } else if (pertanyaanModel.getB().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_kedua.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_keempat);
        } else if (pertanyaanModel.getC().equals(pertanyaanModel.getJawaban())) {
            cv_jawaban_ketiga.setCardBackgroundColor(Color.GREEN);
            hitungSalah(cv_jawaban_keempat);
        }
        setCvPenjelasan();
        countDownTimer.cancel();
    }

    private void setBermain() {
        DatabaseReference dbUsers = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        dbUsers.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x = Integer.parseInt(snapshot.child("bermain").getValue().toString());
                dbUsers.child(UID).child("bermain").setValue(String.valueOf(x + 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}