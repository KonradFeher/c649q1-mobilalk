package com.example.currensee;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Locale;

public class CurrencyFormActivity extends AppCompatActivity {

    private final String LOG_TAG = CurrencyFormActivity.class.getName();

    private EditText fullNameEditText;
    private EditText abbreviationEditText;
    private EditText symbolEditText;
    private EditText valueEditText;
    private Button saveButton;

    private Currency currencyToEdit;
    private String currencyToEditAbbr;
    private boolean isEditing;

    private FirebaseFirestore firestore;
    private CollectionReference collectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_form);

        fullNameEditText = findViewById(R.id.full_name_edit_text);
        abbreviationEditText = findViewById(R.id.abbreviation_edit_text);
        symbolEditText = findViewById(R.id.symbol_edit_text);
        valueEditText = findViewById(R.id.value_edit_text);
        saveButton = findViewById(R.id.save_button);

        firestore = FirebaseFirestore.getInstance();
        collectionRef = firestore.collection("Currencies");

        // check if we're editing an existing currency or creating a new one
        currencyToEdit = (Currency) getIntent().getSerializableExtra("currency");
        currencyToEditAbbr = currencyToEdit.getAbbreviation();
        isEditing = currencyToEdit != null;
        if (isEditing) {
            fullNameEditText.setText(currencyToEdit.getFullName());
            abbreviationEditText.setText(currencyToEdit.getAbbreviation());
            symbolEditText.setText(currencyToEdit.getSymbol());
            valueEditText.setText(Float.toString(currencyToEdit.getValue()));
            saveButton.setText(R.string.update);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameEditText.getText().toString().trim();
                String abbreviation = abbreviationEditText.getText().toString().trim();
                String symbol = symbolEditText.getText().toString().trim();
                float value = Float.parseFloat(valueEditText.getText().toString().trim());

                if (fullName.isEmpty() || abbreviation.isEmpty() || symbol.isEmpty()) {
                    Toast.makeText(CurrencyFormActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isEditing) {
                    currencyToEdit.setFullName(fullName);
                    currencyToEdit.setAbbreviation(abbreviation);
                    currencyToEdit.setSymbol(symbol);
                    currencyToEdit.setValue(value);
                    currencyToEdit.updateAt();

                    collectionRef.whereEqualTo("abbreviation", currencyToEditAbbr).get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference()
                                            .set(currencyToEdit)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(CurrencyFormActivity.this, "Currency updated.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(CurrencyFormActivity.this, "Error updating currency: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(LOG_TAG, "Failed to update currency document with abbreviation: " + currencyToEditAbbr, e);
                                Toast.makeText(CurrencyFormActivity.this, "An error occurred...", Toast.LENGTH_SHORT).show();
                            });

                } else {
                    Currency currency = new Currency(fullName, abbreviation, symbol, value);

                    collectionRef.add(currency)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(CurrencyFormActivity.this, "Currency added.", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CurrencyFormActivity.this, "Error adding currency: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
    }

    public void finish(View view) {
        finish();
    }
}
