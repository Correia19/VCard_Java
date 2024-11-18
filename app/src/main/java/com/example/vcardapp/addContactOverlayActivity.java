package com.example.vcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class addContactOverlayActivity extends AppCompatActivity {

    private TextView validationError;
    private Login currentUser;
    private Button buttonGuardar;
    private Button buttonEnviar;
    private EditText name;
    private EditText phoneNumber;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontactoverlay);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width), (int) (height * .8));

        validationError = findViewById(R.id.validationErrorAdd);
        buttonGuardar = findViewById(R.id.OnPiggy);
        buttonEnviar = findViewById(R.id.buttonAplicar);
        name = findViewById(R.id.inputNomeAdd);
        phoneNumber = findViewById(R.id.inputNumberAdd);

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newContact(true);
            }
        });

        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newContact(false);
            }
        });
    }

    public void newContact(Boolean save) {
        if (!phoneNumber.getText().toString().trim().isEmpty()) {
            if (!name.getText().toString().trim().isEmpty()) {
                if (phoneNumber.length() == 9) {
                    Intent intent = getIntent();
                    if (intent != null) {
                        currentUser = (Login) intent.getSerializableExtra("currentUser");
                        if (currentUser != null) {
                            database = FirebaseDatabase.getInstance();
                            reference = database.getReference("users");

                            Query contactInfo = reference.orderByChild("phone_number").equalTo(Integer.parseInt(phoneNumber.getText().toString()));
                            contactInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        if (!currentUser.getNumber().equals(Integer.parseInt(phoneNumber.getText().toString()))) {
                                            if (save) {
                                                ContactManager.addContact(addContactOverlayActivity.this, name.getText().toString(), phoneNumber.getText().toString());
                                            }
                                        } else {
                                            validationError.setVisibility(View.VISIBLE);
                                            phoneNumber.setError("Não pode adicionar o seu próprio número.");
                                            return;
                                        }
                                        Intent intent1 = new Intent();
                                        intent1.putExtra("contactName", name.getText().toString());
                                        intent1.putExtra("contactNumber", phoneNumber.getText().toString());
                                        setResult(RESULT_OK, intent1);
                                        finish(); // Close Overlay
                                    } else {
                                        validationError.setText("Este contacto não está registado na Vcard.");
                                        validationError.setVisibility(View.VISIBLE);
                                        phoneNumber.setError("Este contacto não está registado na Vcard.");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(addContactOverlayActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // make a throw exception
                        validationError.setText("User Not Logged in. Please Log In Again");
                        validationError.setVisibility(View.VISIBLE);
                    }
                } else {
                    validationError.setText("Dados Invalidos ou incorretos");
                    validationError.setVisibility(View.VISIBLE);
                    phoneNumber.setError("O número de telefone deve ter 9 dígitos.");
                }
            } else {
                validationError.setText("Dados Invalidos ou incorretos");
                validationError.setVisibility(View.VISIBLE);
                name.setError("Deve inserir um nome para o contacto.");
            }
        } else {
            validationError.setText("Dados Invalidos ou incorretos");
            validationError.setVisibility(View.VISIBLE);
            name.setError("O nome não pode estar vazio.");
        }

    }
}
