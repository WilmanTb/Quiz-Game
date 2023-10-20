package com.quizgame.quizgame.User.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.quizgame.quizgame.R;
import com.quizgame.quizgame.User.Activity.Kepercayaan;
import com.quizgame.quizgame.User.Activity.PakaianAdat;
import com.quizgame.quizgame.User.Activity.RumahAdat;
import com.quizgame.quizgame.User.Activity.SenjataPerang;

public class Materi extends Fragment {

    ViewGroup root;
    CardView cv_RumahAdat, cv_senjataPerang, cv_PakaianAdat, cv_Kepercayaan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_materi, container, false);

        cv_RumahAdat = root.findViewById(R.id.cv_RumahAdat);
        cv_senjataPerang = root.findViewById(R.id.cv_SenjataPerang);
        cv_PakaianAdat = root.findViewById(R.id.cv_PakaianAdat);
        cv_Kepercayaan = root.findViewById(R.id.cv_kepercayaan);


        cv_RumahAdat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RumahAdat.class));
            }
        });

        cv_Kepercayaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Kepercayaan.class));
            }
        });

        cv_PakaianAdat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PakaianAdat.class));
            }
        });

        cv_senjataPerang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SenjataPerang.class));
            }
        });

        return root;
    }

}
