package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class CadProvider implements Parcelable {
    String nome;
    String email;
    String url;
    String password;

    public CadProvider(String nome, String email, String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    public CadProvider(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }
    public CadProvider(){}

    protected CadProvider(Parcel in) {
        nome = in.readString();
        email = in.readString();
        password = in.readString();
    }

    public static final Creator<CadProvider> CREATOR = new Creator<CadProvider>() {
        @Override
        public CadProvider createFromParcel(Parcel in) {
            return new CadProvider(in);
        }

        @Override
        public CadProvider[] newArray(int size) {
            return new CadProvider[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(email);
        dest.writeString(password);
    }
}
