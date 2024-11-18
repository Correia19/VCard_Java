package com.example.vcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class SettingsActivity extends AppCompatActivity {

    private Button buttonLogout;
    private TextView validationError;
    private TextView errorMessage;
    private EditText balanceInput;
    private Login currentUser;
    private Button OnNotificacoes;
    private Button OffNotificacoes;
    private Button OnPiggy;
    private Button OffPiggy;
    private Button deleteVcard;
    private static final int PIN_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingsactivity);

        buttonLogout = findViewById(R.id.buttonLogout);
        balanceInput = findViewById(R.id.balanceInput);
        validationError = findViewById(R.id.validationErrorPiggy);
        OnNotificacoes = findViewById(R.id.All);
        OffNotificacoes = findViewById(R.id.debit);
        OnPiggy = findViewById(R.id.OnPiggy);
        OffPiggy = findViewById(R.id.offPiggy);
        deleteVcard = findViewById(R.id.deleteVcard);
        errorMessage = findViewById(R.id.errorMessage);

        errorMessage.setVisibility(View.INVISIBLE);
        RefreshBalance();

        Intent intent = getIntent();


        OnNotificacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffNotificacoes.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                OffNotificacoes.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.black));
                OnNotificacoes.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.customBlue));
                OnNotificacoes.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                updateNotificationsState(true);
            }
        });
        OffNotificacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnNotificacoes.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                OnNotificacoes.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.black));
                OffNotificacoes.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.customBlue));
                OffNotificacoes.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                updateNotificationsState(false);
            }
        });
        OnPiggy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffPiggy.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                OffPiggy.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.black));
                OnPiggy.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.customBlue));
                OnPiggy.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                updatePiggyRoundupState(true);
            }
        });
        OffPiggy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnPiggy.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                OnPiggy.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.black));
                OffPiggy.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.customBlue));
                OffPiggy.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                updatePiggyRoundupState(false);
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        deleteVcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SettingsActivity.this, ConfirmDeleteActivity.class);
                ActivityCompat.startActivityForResult(SettingsActivity.this, intent1, PIN_REQUEST_CODE, null);
            }
        });

        getSettings();
    }

    private void getSettings() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("phone_number").equalTo(currentUser.getNumber());

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean notifications = snapshot.child(currentUser.getNumber().toString()).child("activeNotifications").getValue(Boolean.class);
                    Boolean piggy = snapshot.child(currentUser.getNumber().toString()).child("activePiggyRoundup").getValue(Boolean.class);
                    notifications = notifications != null ? notifications : false;
                    piggy = piggy != null ? piggy : false;

                    if (notifications) {
                        OnNotificacoes.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.customBlue));
                        OnNotificacoes.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                    } else {
                        OffNotificacoes.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.customBlue));
                        OffNotificacoes.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                    }
                    if (piggy) {
                        OnPiggy.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.customBlue));
                        OnPiggy.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                    } else {
                        OffPiggy.setBackgroundTintList(ContextCompat.getColorStateList(SettingsActivity.this, R.color.customBlue));
                        OffPiggy.setTextColor(ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Erro ao atualizar estado das notificações", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateNotificationsState(Boolean state) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("phone_number").equalTo(currentUser.getNumber());
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean notifications = snapshot.child(currentUser.getNumber().toString()).child("activeNotifications").getValue(Boolean.class);
                    if (notifications != state)
                        reference.child(currentUser.getNumber().toString()).child("activeNotifications").setValue(state);
                } else {
                    Toast.makeText(SettingsActivity.this, "Erro ao atualizar estado das notificações", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updatePiggyRoundupState(Boolean state) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("phone_number").equalTo(currentUser.getNumber());
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean piggy = snapshot.child(currentUser.getNumber().toString()).child("activePiggyRoundup").getValue(Boolean.class);
                    if (piggy != state)
                        reference.child(currentUser.getNumber().toString()).child("activePiggyRoundup").setValue(state);
                } else {
                    Toast.makeText(SettingsActivity.this, "Erro ao atualizar estado das notificações", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void RefreshBalance() {
        // atualizar balance
        Intent intent = getIntent();
        if (intent != null) {
            currentUser = (Login) intent.getSerializableExtra("currentUser");

            if (currentUser != null) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                Query checkUserDatabase = reference.orderByChild("phone_number").equalTo(currentUser.getNumber());
                checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Double balance = snapshot.child(currentUser.getNumber().toString()).child("balance").getValue(Double.class);
                            balanceInput.setText(balance.toString() + "€");
                        } else {
                            balanceInput.setText("0€");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }




    public void logout() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("phone_number").equalTo(currentUser.getNumber());

        if (requestCode == PIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Double balance = snapshot.child(currentUser.getNumber().toString()).child("balance").getValue(Double.class);
                            if(balance == 0.0) {
                                reference.child(currentUser.getNumber().toString()).child("deleted").setValue(true);
                                //go to login

                                SessionManager sessionManager = new SessionManager(SettingsActivity.this);
                                sessionManager.removeSession();

                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(intent);


                            }else
                                errorMessage.setVisibility(View.VISIBLE);
                                errorMessage.setText("Não pode apagar a conta com saldo diferente de 0€");

                        } else {
                            errorMessage.setVisibility(View.VISIBLE);
                            errorMessage.setText("Erro ao apagar conta vCard");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





            } else {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Cancelou o apagar da conta");

            }
        }
    }

}


