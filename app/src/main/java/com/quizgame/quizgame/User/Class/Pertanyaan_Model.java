package com.quizgame.quizgame.User.Class;

public class Pertanyaan_Model {
    String pertanyaan;
    String a;
    String b;
    String c;
    String penjelasan;
    String gambar;

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getPenjelasan() {
        return penjelasan;
    }

    public void setPenjelasan(String penjelasan) {
        this.penjelasan = penjelasan;
    }

    public Pertanyaan_Model() {
    }

    public Pertanyaan_Model(String pertanyaan, String a, String b, String c, String penjelasan, String d, String jawaban) {
        this.pertanyaan = pertanyaan;
        this.a = a;
        this.b = b;
        this.c = c;
        this.penjelasan = penjelasan;
        this.d = d;
        this.jawaban = jawaban;
    }

    public Pertanyaan_Model(String pertanyaan, String a, String b, String c, String d, String jawaban, String kesulitan, String penjelasan) {
        this.pertanyaan = pertanyaan;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.jawaban = jawaban;
        this.kesulitan = kesulitan;
        this.penjelasan = penjelasan;
    }

    public String getPertanyaan() {
        return pertanyaan;
    }

    public void setPertanyaan(String pertanyaan) {
        this.pertanyaan = pertanyaan;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getJawaban() {
        return jawaban;
    }

    public void setJawaban(String jawaban) {
        this.jawaban = jawaban;
    }

    public String getKesulitan() {
        return kesulitan;
    }

    public void setKesulitan(String kesulitan) {
        this.kesulitan = kesulitan;
    }

    String d;
    String jawaban;
    String kesulitan;
}
