package com.example.currensee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesActivity extends AppCompatActivity {
    private static final String LOG_TAG = CurrenciesActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth auth;
    private boolean isPortrait;

    private RecyclerView recyclerView;
    private ArrayList<Currency> currencies;
    private CurrencyAdapter adapter;

    private FirebaseFirestore firestore;
    private CollectionReference collectionRef;

    private SharedPreferences preferences;

    private int grids = 1;

//    private boolean viewRow = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currencies);
        setTitle("Currencies");

        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You need to log in first.", Toast.LENGTH_SHORT).show();
            finish();
        }
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, grids));
        currencies = new ArrayList<>();
        adapter = new CurrencyAdapter(this, currencies);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        collectionRef = firestore.collection("Currencies");

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(...);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG_TAG, "onCreateOptionsMenu: YAHOOOOOOOOOOOO");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.currencies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_create:
                Intent intent = new Intent(this, CurrencyFormActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeData() {
        Log.w(LOG_TAG, "INIT DATA");
        String[] currencyNames = getResources().getStringArray(R.array.currency_names);
        String[] currencyAbbreviations = getResources().getStringArray(R.array.currency_abbreviations);
        String[] currencySymbols = getResources().getStringArray(R.array.currency_symbols);
        TypedArray currencyValues = getResources().obtainTypedArray(R.array.currency_values);

        for (int i = 0; i < currencyNames.length; i++) {
            collectionRef.add(new Currency(
                    currencyNames[i],
                    currencyAbbreviations[i],
                    currencySymbols[i],
                    currencyValues.getFloat(i, 0)));
        }


        // Recycle the typed array.
        currencyValues.recycle();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void queryData() {
        currencies.clear();
        collectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Currency item = document.toObject(Currency.class);
                currencies.add(item);
            }

            if (currencies.size() == 0) {
                initializeData();
                queryData();
            }

            // Notify the adapter of the change.
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        recyclerView.setLayoutManager(new GridLayoutManager(this, isPortrait ? 1 : 2));
    }


    public void editCurrency(String abbr) {
        collectionRef
                .whereEqualTo("abbreviation", abbr)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Currency c = document.toObject(Currency.class);
                        Intent intent = new Intent(this, CurrencyFormActivity.class);
                        intent.putExtra("currency", c);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Failed to edit currency document with abbreviation: " + abbr, e);
                    Toast.makeText(this, "An error occurred...", Toast.LENGTH_SHORT).show();
                });
    }

    public void deleteCurrency(String abbr) {
        collectionRef
                .whereEqualTo("abbreviation", abbr)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    for (int i = 0; i < currencies.size(); i++) {
                        if (currencies.get(i).getAbbreviation().equals(abbr)) {
                            currencies.remove(i);
                            adapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                    Log.i(LOG_TAG, "Deleted currency document(s) with abbreviation: " + abbr);
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Failed to delete currency document with abbreviation: " + abbr, e);
                    Toast.makeText(this, "An error occurred...", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh data in recycler
        this.queryData();
    }
}