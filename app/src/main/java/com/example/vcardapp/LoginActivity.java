package com.example.vcardapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    private Button buttonLogin;
    private Button buttonRegister;
    private EditText number;
    private EditText password;
    private Integer pin;
    private TextView validationError;
    private String token;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        FirebaseApp.initializeApp(this);

        buttonLogin = findViewById(R.id.buttonLoginFromLogin);
        buttonRegister = findViewById(R.id.buttonRegisterFromLogin);
        number = findViewById(R.id.numberInputLogin);
        password = findViewById(R.id.passwordInputLogin);
        validationError = findViewById(R.id.validationErrorLogin);
        token = "";
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });

        // retrieve the phone registration token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        // Log and toast
                        //Toast.makeText(LoginActivity.this, "Token: " + token, Toast.LENGTH_SHORT).show();
                    }
                });

        // Check if the device is connected to the internet
        if (!isConnectedToInternet()) {
            // The device is not connected to the internet
            validationError.setText("No internet connection");
            validationError.setVisibility(View.VISIBLE);
        }

    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkSession();
    }

    private void checkSession() {
        //check if user is logged in
        //if user is logged in --> move to mainActivity

        SessionManager sessionManager = new SessionManager(LoginActivity.this);
        Integer userID = sessionManager.getSession();

        if (userID != -1) {
            //user id logged in and go to PinActivity
            userLoggedIn(userID);
        } else {
            //do nothing
        }
    }

    private void userLoggedIn(Integer userID) {
        Intent intent = new Intent(LoginActivity.this, PinActivity.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    public void checkUser() {

        if ((!number.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty())) {

            Integer user_phone_number = Integer.parseInt(number.getText().toString().trim());
            String user_password = password.getText().toString();

            if (String.valueOf(user_phone_number).length() == 9) {
                if (user_password.length() >= 10) {
                    String phone_number = number.getText().toString().trim();
                    String userPassword = password.getText().toString().trim();

                    Integer phone_number_int = Integer.parseInt(phone_number);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                    Query checkUserDatabase = reference.orderByChild("phone_number").equalTo(phone_number_int);

                    checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                number.setError(null);
                                String passwordFromDB = snapshot.child(phone_number).child("password").getValue(String.class);
                                Boolean deleted = snapshot.child(phone_number).child("deleted").getValue(Boolean.class);

                                if(deleted) {
                                    validationError.setText("This account has been deleted.");
                                    validationError.setVisibility(View.VISIBLE);

                                    return;
                                }
                                if (Objects.equals(passwordFromDB, userPassword)) {
                                    password.setError(null);
                                    Integer userPin = snapshot.child(phone_number).child("pin").getValue(Integer.class);

                                    // update token
                                    if (token != "") {
                                        reference.child(phone_number).child("token").setValue(token);
                                    } else {
                                        Intent intentToken = getIntent();
                                        if (intentToken != null) {
                                            token = intentToken.getStringExtra("token");
                                            reference.child(phone_number).child("token").setValue(token);
                                        }
                                    }

                                    // save session
                                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                    sessionManager.saveSession(phone_number_int);

                                    // go to dashboard
                                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                    Login currentUser = new Login(Integer.parseInt(phone_number), userPassword, userPin); // criar um objeto do tipo Login com os dados do current user
                                    intent.putExtra("currentUser", currentUser);
                                    startActivity(intent);

                                } else {
                                    password.setError("Incorrect password.");
                                    password.requestFocus();
                                }
                            } else {
                                number.setError("No such user exists");
                                number.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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
        }
    }
}
