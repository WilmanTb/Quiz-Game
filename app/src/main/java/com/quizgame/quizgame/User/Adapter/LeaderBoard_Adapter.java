package com.quizgame.quizgame.User.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.quizgame.quizgame.R;
import com.quizgame.quizgame.User.Class.User_Model;

import java.util.ArrayList;

public class LeaderBoard_Adapter extends RecyclerView.Adapter<LeaderBoard_Adapter.MyViewHolder> {

    Context context;
    ArrayList<User_Model> list;

    public LeaderBoard_Adapter(Context context, ArrayList<User_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_leaderboard, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User_Model userModel = list.get(position);
        holder.skor_user.setText(userModel.getSkorTertinggi());
        holder.nama_user.setText(userModel.getNama());
        Glide.with(context).load(list.get(position).getFoto()).into(holder.foto_user);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView foto_user;
        TextView nama_user, skor_user;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto_user = itemView.findViewById(R.id.foto_user);
            nama_user = itemView.findViewById(R.id.nama_user);
            skor_user = itemView.findViewById(R.id.skor_user);
        }
    }
}
