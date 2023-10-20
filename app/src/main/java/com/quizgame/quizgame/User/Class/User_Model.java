package com.quizgame.quizgame.User.Class;

public class User_Model implements Comparable<User_Model>{
    String nama;
    String email;
    String password;
    String bermain;
    String skorTertinggi;

    public String getSkorTertinggi() {
        return skorTertinggi;
    }

    public void setSkorTertinggi(String skorTertinggi) {
        this.skorTertinggi = skorTertinggi;
    }

    public User_Model() {
    }

    public String getNama() {
        return nama;
    }

    public String getBermain() {
        return bermain;
    }

    public void setBermain(String bermain) {
        this.bermain = bermain;
    }

    public User_Model(String nama, String email, String password, String foto, String bermain, String skorTertinggi) {
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.foto = foto;
        this.bermain = bermain;
        this.skorTertinggi = skorTertinggi;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    String foto;

    @Override
    public int compareTo(User_Model user_model) {
        return Integer.parseInt(user_model.getSkorTertinggi()) - Integer.parseInt(this.skorTertinggi);
    }
}
