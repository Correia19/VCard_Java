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
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class sendMoneyActivity extends AppCompatActivity {

    private Button buttonLogout;
    private Button buttonSendMoney;
    private TextView textViewContacto;
    private EditText editTextValor;
    private EditText editTextDescricao;
    private String selectedContact;
    private String selectedContactNumber;
    FirebaseDatabase database;
    DatabaseReference reference;
    private Login currentUser;
    private EditText balanceInput;
    private TextView validationError;

    private String piggyMessage;
    DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
    private static final int PIN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmoneyactivity);

        buttonLogout = findViewById(R.id.buttonLogout);
        textViewContacto = findViewById(R.id.NotificacoesLabel);
        editTextValor = findViewById(R.id.inputNomeAdd);
        editTextDescricao = findViewById(R.id.descricaoInput);
        buttonSendMoney = findViewById(R.id.OnPiggy);
        balanceInput = findViewById(R.id.balanceInput);
        validationError = findViewById(R.id.validationErrorTxt);

        Intent intent = getIntent();
        if (intent != null) {
            selectedContact = intent.getStringExtra("contacto");
            selectedContactNumber = intent.getStringExtra("contactNumber");
            textViewContacto.setText("Enviar a " + selectedContact);
        }

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anterior();
            }
        });

        RefreshBalance();

        buttonSendMoney.setOnClickListener(new View.OnClickListener() { // enviar dinheiro (retirar ao user e adicionar ao user selecionado
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(sendMoneyActivity.this, PinActivitySend.class);
                intent1.putExtra("currentUser", currentUser);
                ActivityCompat.startActivityForResult(sendMoneyActivity.this, intent1, PIN_REQUEST_CODE, null);
            }
        });

    }

    private void RefreshBalance() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // PIN validation successful, continue with the money transfer logic
                if (!editTextValor.getText().toString().trim().isEmpty()) {
                    // Your existing money transfer logic here...
                    // ...
                    if (!editTextValor.getText().toString().trim().isEmpty()) {

                        database = FirebaseDatabase.getInstance();
                        reference = database.getReference("users");
                        Query userSending = reference.orderByChild("phone_number").equalTo(currentUser.getNumber());
                        Query userToSend = reference.orderByChild("phone_number").equalTo(Integer.parseInt(selectedContactNumber));

                        userSending.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Double balanceCurrentUser = dataSnapshot.child(currentUser.getNumber().toString()).child("balance").getValue(Double.class);
                                    Double piggy_balanceCurrentUser = dataSnapshot.child(currentUser.getNumber().toString()).child("piggy_balance").getValue(Double.class);


                                    //saldo disponivel
                                    Double availableBalance = balanceCurrentUser - piggy_balanceCurrentUser;

                                    Double valorAEnviar = Double.parseDouble(editTextValor.getText().toString());

                                    //se saldo disponivel for menor que o valor a enviar -> erro
                                    if (availableBalance < valorAEnviar) {
                                        validationError.setVisibility(View.VISIBLE);
                                        editTextValor.setError("Saldo insuficiente");
                                        return;
                                    }

                                    Boolean piggyRoundup = dataSnapshot.child(currentUser.getNumber().toString()).child("activePiggyRoundup").getValue(Boolean.class);
                                    Double valorArredondado = 0.0;

                                    // roudup piggy se ativado
                                    if (piggyRoundup) {
                                        // arredondar valor
                                        valorArredondado = (Double) Math.ceil(valorAEnviar);

                                        //se o valor arredondando for maior que o saldo disponivel, entao o valor arredondado é o valor a enviar sem arredondar
                                        if (valorArredondado > availableBalance) {
                                            valorArredondado = valorAEnviar;
                                            //mudar mensagem
                                            piggyMessage = "Não há saldo suficiente para arredondar para o PiggyBank";

                                        } else {

                                            //senao arreronda
                                            Double valorParaPiggy = valorArredondado - valorAEnviar;


                                            DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
                                            String roundedVal = decimalFormat.format(valorParaPiggy);
                                            valorParaPiggy = Double.parseDouble(roundedVal);

                                            //atualizar piggy balance
                                            Double newPiggyBalance = piggy_balanceCurrentUser + valorParaPiggy;
                                            roundedVal = decimalFormat.format(newPiggyBalance);
                                            newPiggyBalance = Double.parseDouble(roundedVal);

                                            reference.child(currentUser.getNumber().toString()).child("piggy_balance").setValue(newPiggyBalance);

                                            piggyMessage = "O resto (" + valorParaPiggy + "€) foi adicionado ao PiggyBank";
                                        }

                                    } else {
                                        valorArredondado = valorAEnviar;
                                    }

                                    Double newBalance = balanceCurrentUser - valorArredondado;
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
                                    String roundedVal = decimalFormat.format(newBalance);

                                    Double ValueEnviou = Double.parseDouble(roundedVal); // valor com que ficou apos enviar
                                    reference.child(currentUser.getNumber().toString()).child("balance").setValue(ValueEnviou);

                                    RefreshBalance();

                                    userToSend.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Double balanceUserAReceber = dataSnapshot.child(selectedContactNumber).child("balance").getValue(Double.class);
                                                Double newBalance = balanceUserAReceber + Double.parseDouble(editTextValor.getText().toString());

                                                DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
                                                String roundedVal = decimalFormat.format(newBalance);
                                                Double ValueRecebeu = Double.parseDouble(roundedVal); // valor com que ficou apos receber
                                                reference.child(selectedContactNumber).child("balance").setValue(ValueRecebeu);

                                                //criar registo no historico
                                                reference = FirebaseDatabase.getInstance().getReference("transacoes");

                                                Transacoes transacao = new Transacoes(editTextDescricao.getText().toString().trim().isEmpty() ? "" : editTextDescricao.getText().toString(), currentUser.getNumber().toString(), selectedContactNumber, ValueEnviou, ValueRecebeu, Double.parseDouble(editTextValor.getText().toString()));
                                                reference.child(currentUser.getNumber().toString() + "_" + String.valueOf(transacao.data)).setValue(transacao);

                                                reference = FirebaseDatabase.getInstance().getReference("users");
                                                Boolean notifications = dataSnapshot.child(selectedContactNumber).child("activeNotifications").getValue(Boolean.class);

                                                // enviar notificacao ao recetor se ativado

                                                String token = dataSnapshot.child(selectedContactNumber).child("token").getValue(String.class);
                                                MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
                                                Boolean response = myFirebaseMessagingService.sendNotification("Recebeu " + Double.parseDouble(editTextValor.getText().toString()) + "€ de " + currentUser.getNumber().toString(), editTextDescricao.getText().toString(), token, editTextValor.getText().toString(), notifications, getApplicationContext());
                                                //Toast.makeText(sendMoneyActivity.this, "Notificacao enviada para: " + token, Toast.LENGTH_SHORT).show();

                                                // registar a notificacao no historico
                                                reference = FirebaseDatabase.getInstance().getReference("notificacoes");
                                                Notificacoes notificacao = new Notificacoes(transacao.descricao, transacao.nEnviou, transacao.nRecebeu, transacao.valor, false);
                                                reference.child(selectedContactNumber + "_" + String.valueOf(notificacao.data)).setValue(notificacao);


                                                Intent intent = new Intent(sendMoneyActivity.this, moneySentActivity.class);
                                                intent.putExtra("currentUser", currentUser);
                                                intent.putExtra("value", editTextValor.getText().toString());
                                                intent.putExtra("contacto", selectedContact);
                                                intent.putExtra("descricao", editTextDescricao.getText().toString());
                                                intent.putExtra("piggyMessage", piggyMessage);
                                                startActivity(intent);

                                            } else {
                                                // O user nao existe
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(sendMoneyActivity.this, "Erro a receber", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    // O user nao existe
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(sendMoneyActivity.this, "Erro a enviar", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        editTextValor.setError("Insira um montante");
                        validationError.setVisibility(View.VISIBLE);
                    }

                    // Move the logic for money transfer here or call a separate method
                } else {
                    editTextValor.setError("Insira um montante");
                    validationError.setVisibility(View.VISIBLE);
                }
            } else {
                // Handle the case where PIN validation failed or was canceled
                // You may want to display an error message or take appropriate action
            }
        }
    }

    public void Anterior() {
        Intent intent = new Intent(this, selectContactActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }
}
