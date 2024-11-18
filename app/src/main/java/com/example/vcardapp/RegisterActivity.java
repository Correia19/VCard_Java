package com.example.vcardapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {

    private Button button;
    private EditText number;
    private EditText password;
    private EditText pin;
    private TextView validationError;
    private Button buttonGotToLogin;
    private String token;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);

        button = findViewById(R.id.buttonLoginFromRegister);
        number = findViewById(R.id.numberInputRegister);
        password = findViewById(R.id.passwordInputRegister);
        pin = findViewById(R.id.pinInputRegister);
        validationError = findViewById(R.id.validationError);
        buttonGotToLogin = findViewById(R.id.buttonLoginFromRegist);
        token = "";
        buttonGotToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!number.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty() && !pin.getText().toString().trim().isEmpty()) {

                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("users");

                    Integer user_phone_number = Integer.parseInt(number.getText().toString().trim());
                    Integer user_pin_number = Integer.parseInt(pin.getText().toString().trim());
                    String user_password = password.getText().toString();



                    if (String.valueOf(user_phone_number).length() == 9) {
                        if (user_password.length() >= 10) {
                            if (String.valueOf(user_pin_number).length() == 4) {
                                // Verificar se o número de telefone já existe na base de dados
                                DatabaseReference userReference = reference.child(number.getText().toString());


                                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {


                                            Boolean deleted = dataSnapshot.child("deleted").getValue(Boolean.class);
                                            if(deleted) {
                                                validationError.setText("This account has been deleted.");
                                                validationError.setVisibility(View.VISIBLE);

                                            }else{
                                                validationError.setText("Phone number already in use");
                                                validationError.setVisibility(View.VISIBLE);
                                            }

                                        } else {

                                            // create token
                                            if (token == "") {

                                                Intent intentToken = getIntent();
                                                if (intentToken != null) {
                                                    token = intentToken.getStringExtra("token");
                                                }
                                            }

                                            //codigo para adicionar ao topico

                                            // O número de telefone não está em uso, criar uma nova conta
                                            User user = new User(token, user_phone_number, user_password, user_pin_number);
                                            reference.child(number.getText().toString()).setValue(user);

                                            //Toast.makeText(RegisterActivity.this, "User created", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                                            Login currentUser = new Login(user_phone_number, user_password, user_pin_number); // criar um objeto do tipo Login com os dados do current user
                                            intent.putExtra("currentUser", currentUser);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle potential errors here
                                    }
                                });
                            } else {
                                pin.setError("Pin must be 4 digits long");
                                validationError.setVisibility(View.VISIBLE);
                            }
                        } else {
                            password.setError("Password must be at least 10 characters long");
                            validationError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        number.setError("Phone number must be 9 digits long");
                        validationError.setVisibility(View.VISIBLE);
                    }
                } else {
                    validationError.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });
    }
}