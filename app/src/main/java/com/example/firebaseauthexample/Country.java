package com.example.firebaseauthexample;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name_es")
    @Expose
    private String nameEs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getNameEs() {
        return nameEs;
    }

    public void setNameEs(String nameEs) {
        this.nameEs = nameEs;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id='" + id + '\'' +
                ", nameEs='" + nameEs + '\'' +
                '}';
    }
}

