package com.example.myapplication;

public class LoginProvider {
    String uid;
    String email;
    String senha;

    public LoginProvider(String uid, String email, String senha){
        this.email = email;
        this.senha = senha;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
