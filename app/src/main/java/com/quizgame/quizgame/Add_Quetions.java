package com.quizgame.quizgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quizgame.quizgame.User.Class.Pertanyaan_Model;

public class Add_Quetions extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText et_pertanyaan, et_pilihanA, et_pilihanB, et_pilihanC, et_pilihanD, et_jawaban, et_penjelasan;
    private Button btn_submit;
    private String Pertanyaan, PilihanA, PilihanB, PilihanC, PilihanD, Jawaban, Penjelasan, Kesulitan = "empty";
    private DatabaseReference dbPertanyaan;
    private Spinner spin_Kesulitan;
    private long x = 0;
    private ArrayAdapter<CharSequence> adapterKesulitan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quetions);

        dbPertanyaan = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Pertanyaan");

        getID();
        getKesulitan();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmpty();

            }
        });

    }

    private void getID() {
        et_jawaban = findViewById(R.id.et_jawaban);
        et_pilihanA = findViewById(R.id.et_PilihanA);
        et_pilihanB = findViewById(R.id.et_PilihanB);
        et_pilihanC = findViewById(R.id.et_PilihanC);
        et_pilihanD = findViewById(R.id.et_PilihanD);
        et_pertanyaan = findViewById(R.id.et_Pertanyaan);
        et_penjelasan = findViewById(R.id.et_Penjelasan);
        btn_submit = findViewById(R.id.btn_submit);
        spin_Kesulitan = findViewById(R.id.spin_kesulitan);
    }

    private void setString(){
        Pertanyaan = et_pertanyaan.getText().toString();
        Jawaban = et_jawaban.getText().toString();
        Penjelasan = et_penjelasan.getText().toString();
        PilihanA = et_pilihanA.getText().toString();
        PilihanB = et_pilihanB.getText().toString();
        PilihanC = et_pilihanC.getText().toString();
        PilihanD = et_pilihanD.getText().toString();
    }

    private void checkEmpty(){
        setString();
        if (Pertanyaan.isEmpty() || Jawaban.isEmpty() || Penjelasan.isEmpty() || PilihanA.isEmpty() ||
        PilihanB.isEmpty() || PilihanC.isEmpty() || PilihanD.isEmpty() || Kesulitan.equals("empty")){
            Toast.makeText(this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog alertDialog = new AlertDialog.Builder(Add_Quetions.this)
                    .setMessage("Yakin membuat pertanyaan ini ?")
                    .setPositiveButton("Ya", null)
                    .setNegativeButton("Tidak", null)
                    .show();

            Button positiveButton = alertDialog.getButton(alertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getCount();
                    Toast.makeText(Add_Quetions.this, "Pertanyaan berhasil di buat", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    setEmpty();
                }
            });
        }
    }

    private void getCount() {
        dbPertanyaan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                x = snapshot.getChildrenCount();
                inputPertanyaan();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inputPertanyaan(){
        Pertanyaan_Model pertanyaanModel = new Pertanyaan_Model(Pertanyaan, PilihanA, PilihanB, PilihanC, PilihanD, Jawaban, Kesulitan, Penjelasan);
        dbPertanyaan.child(String.valueOf(x+1)).setValue(pertanyaanModel);
    }

    private void setEmpty(){
        et_jawaban.setText("");
        et_pilihanA.setText("");
        et_pilihanB.setText("");
        et_pilihanC.setText("");
        et_pilihanD.setText("");
        et_pertanyaan.setText("");
        et_penjelasan.setText("");
    }

    private void getKesulitan(){
        adapterKesulitan = ArrayAdapter.createFromResource(this, R.array.Kesulitan, android.R.layout.simple_spinner_item);
        adapterKesulitan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_Kesulitan.setAdapter(adapterKesulitan);
        spin_Kesulitan.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spin_Kesulitan =(Spinner)adapterView;
        if (spin_Kesulitan.getId() == R.id.spin_kesulitan){
            if (!adapterView.getItemAtPosition(i).equals("Pilih kesulitan...")){
                Kesulitan = adapterView.getItemAtPosition(i).toString();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}