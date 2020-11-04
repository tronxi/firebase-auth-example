package com.example.firebaseauthexample;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/api/countries")
    Call<Example> getCountries();
}
