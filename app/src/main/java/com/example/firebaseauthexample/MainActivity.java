package com.example.firebaseauthexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "LOG_TAG";

    //FirebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String token;
    private final int RC_SIGN_IN = 2020;

    //Firestore
    private FirebaseFirestore firebaseFirestore;
    ListenerRegistration registration;

    //Retrofit
    private ApiService apiService;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.label_spinner);

        initFirestore();

        initRetrofit();

        initFirebaseAuth();

        spinner.setOnItemSelectedListener(new SpinnerListener(this));


    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);

        addFirestoreListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);

        removeFirestoreListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "iniciada sesion", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "cancelada", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //FirebaseAuth
    private void initFirebaseAuth() {
        authStateListener = getAuthStateListener();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private FirebaseAuth.AuthStateListener getAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    user.getIdToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        token = task.getResult().getToken();
                                        Log.i(TAG, task.getResult().getToken());
                                    }
                                }
                            });
                } else {
                    launchAuth();
                }
            }
        };
    }

    public void logOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "logOut");
                        launchAuth();
                    }
                });
    }


    public void deleteAccount(View view) {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "delete");
                        launchAuth();
                    }
                });
    }

    private void launchAuth() {
        startActivityForResult(
                AuthUI.getInstance().
                        createSignInIntentBuilder().
                        setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build()
                        )).
                        build(),
                RC_SIGN_IN
        );
    }

    //Retrofit
    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.covid19tracking.narrativa.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void find(View view) {
        Call<Example> call_async = apiService.getCountries();

        call_async.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                List<Country> countryList = response.body().getCountries();
                List<String> stringList = new ArrayList<>();
                for(Country country : countryList) {
                    Log.i(TAG, country.toString());
                    stringList.add(country.getNameEs());
                }
                setAdapter(stringList);
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    private void setAdapter(List<String> strings) {
        ArrayAdapter<String> adp = new ArrayAdapter<> (this,android.R.layout.simple_spinner_dropdown_item,strings);
        spinner.setAdapter(adp);
    }

    //Firestore
    private void initFirestore() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void saveData(View view) {
        User user = new User("Sergio", "garcia", 1997, new Address("a", "madrid"));
        firebaseFirestore.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void readData(View view) {
        firebaseFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                Log.d(TAG, document.getId() + " => " + user.toString());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void addFirestoreListener() {
        registration = firebaseFirestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.getMetadata().hasPendingWrites())
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        User user = dc.getDocument().toObject(User.class);
                        Log.i(TAG, "EVENTO " + user.toString());
                    }
                else {
                    Log.i(TAG, "local change");
                }
            }
        });
    }

    private void removeFirestoreListener() {
        registration.remove();
    }
}