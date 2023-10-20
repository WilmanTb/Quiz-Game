package com.quizgame.quizgame.User.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quizgame.quizgame.LandingPage.Login;
import com.quizgame.quizgame.LandingPage.MainActivity;
import com.quizgame.quizgame.R;
import com.quizgame.quizgame.User.Fragment.Dashboard_User_Fragment;
import com.quizgame.quizgame.User.Fragment.Materi;
import com.quizgame.quizgame.User.Fragment.Peringkat;
import com.quizgame.quizgame.User.Fragment.Petunjuk_permainan;
import com.quizgame.quizgame.User.Fragment.Tentang_aplikasi;

import java.util.Arrays;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard_User extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth userAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DatabaseReference dbUsers;
    private String namaUser, urlFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_user);

        userAuth = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.nama_user);
        textView.setText(namaUser);
        CircleImageView circleImageView = (CircleImageView) headerView.findViewById(R.id.foto_user);
        Glide.with(getApplicationContext()).load(urlFoto).into(circleImageView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Dashboard_User_Fragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = userAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(Dashboard_User.this, MainActivity.class));
            finish();
        } else {
            userData();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Dashboard_User_Fragment()).commit();
                break;

            case R.id.nav_petunjuk:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Petunjuk_permainan()).commit();
                break;

            case R.id.nav_tentang:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Tentang_aplikasi()).commit();
                break;

            case R.id.nav_leaderboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Peringkat()).commit();
                break;

            case R.id.nav_materi:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Materi()).commit();
                break;

            case R.id.nav_logout:
                userAuth.signOut();
                startActivity(new Intent(Dashboard_User.this, Login.class));
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void userData() {
        FirebaseUser currentUser = userAuth.getCurrentUser();
        String UID = currentUser.getUid();
        dbUsers.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    namaUser = snapshot.child("nama").getValue().toString();
                    drawerLayout = findViewById(R.id.drawer_layout);
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(Dashboard_User.this);
                    View headerView = navigationView.getHeaderView(0);
                    TextView textView = (TextView) headerView.findViewById(R.id.nama_user);
                    textView.setText(namaUser);
                    urlFoto = snapshot.child("foto").getValue().toString();
                    CircleImageView circleImageView = (CircleImageView) headerView.findViewById(R.id.foto_user);
                    if (!snapshot.child("foto").equals("empty")) {
                        Glide.with(getApplicationContext()).load(urlFoto).into(circleImageView);
                    }else{
                        urlFoto = "https://firebasestorage.googleapis.com/v0/b/quizgame-2b068.appspot.com/o/man.png?alt=media&token=4c9e26c9-d3f1-4923-9a06-5832b14a312c";
                        Glide.with(getApplicationContext()).load(urlFoto)
                                .into(circleImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}