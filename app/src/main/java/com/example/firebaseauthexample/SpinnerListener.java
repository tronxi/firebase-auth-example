package com.example.firebaseauthexample;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class SpinnerListener implements AdapterView.OnItemSelectedListener{
        private Context context;

    public SpinnerListener(Context context) {
        this.context = context;
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String spinnerLabel = adapterView.getItemAtPosition(i).toString();
        displayToast(spinnerLabel);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void displayToast(String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}
