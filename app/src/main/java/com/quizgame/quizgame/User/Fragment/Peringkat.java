package com.quizgame.quizgame.User.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quizgame.quizgame.R;
import com.quizgame.quizgame.User.Adapter.LeaderBoard_Adapter;
import com.quizgame.quizgame.User.Class.User_Model;

import java.util.ArrayList;
import java.util.Collections;

public class Peringkat extends Fragment {

    ViewGroup root;
    RecyclerView rc_leaderboard;
    LeaderBoard_Adapter adapterLeaderBoard;
    ArrayList<User_Model> userModels;
    DatabaseReference dbUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_peringkat, container, false);

        dbUser = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        rc_leaderboard = root.findViewById(R.id.rc_leaderboard);
        rc_leaderboard.setHasFixedSize(true);
        rc_leaderboard.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        userModels = new ArrayList<>();
        adapterLeaderBoard = new LeaderBoard_Adapter(getActivity(), userModels);
        rc_leaderboard.setAdapter(adapterLeaderBoard);

        getData();

        return root;

    }

    private void getData(){
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModels.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User_Model userModel = dataSnapshot.getValue(User_Model.class);
                        userModels.add(userModel);
                        Collections.sort(userModels);
                    }
                    adapterLeaderBoard.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
