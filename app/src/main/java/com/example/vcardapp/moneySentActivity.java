package com.example.vcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class moneySentActivity extends AppCompatActivity {

    private Login currentUser;
    private TextView validationNotificacao;
    private Button buttonBackToDashboard;
    private TextView textTitle;
    private TextView textDescricao;

    private User user;
    private TextView textSpare;

    private String piggyMessage;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moneysentactivity);

        buttonBackToDashboard = findViewById(R.id.buttonBackToDashboard);
        textTitle = findViewById(R.id.textTitle);
        textDescricao = findViewById(R.id.textDescricao);
        textSpare = findViewById(R.id.validationSpareChange);

        Intent intent = getIntent();
        if (intent != null) {
            currentUser = (Login) intent.getSerializableExtra("currentUser");

            String contact = (String) intent.getSerializableExtra("contacto");
            String value = (String) intent.getSerializableExtra("value");
            String descricao = (String) intent.getSerializableExtra("descricao");
            piggyMessage = intent.getStringExtra("piggyMessage");

            if (currentUser != null) {
                textTitle.setText("Enviado " + value + "€ a "+ contact);
                textDescricao.setText(descricao);
            }

            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");
            Query userSending = reference.orderByChild("phone_number").equalTo(currentUser.getNumber());

            userSending.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Boolean spareChange = dataSnapshot.child(currentUser.getNumber().toString()).child("activePiggyRoundup").getValue(Boolean.class);

                        if(spareChange) {
                            textSpare.setText(piggyMessage);
                        }else {
                            textSpare.setText("O saldo não foi arredondado devido a ter desativado");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            textTitle.setText("Erro ao enviar dinheiro. Erro no Intent");
        }

        buttonBackToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(moneySentActivity.this, DashboardActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });
    }

}
