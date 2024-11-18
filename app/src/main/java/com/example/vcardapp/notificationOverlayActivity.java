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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class notificationOverlayActivity extends AppCompatActivity {

    private Login currentUser;
    private TextView txtValor;
    private Button buttonBackToDashboard;
    private TextView txtRemetente;
    private TextView textDescricao;
    private Double valor;
    FirebaseDatabase database;
    DatabaseReference reference;

    DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationoverlayactivity);

        buttonBackToDashboard = findViewById(R.id.buttonBackToDashboard);
        txtValor = findViewById(R.id.txtValor);
        txtRemetente = findViewById(R.id.txtRemetente);
        textDescricao = findViewById(R.id.textDescricao);

        Intent intent = getIntent();
        if (intent != null) {
            currentUser = (Login) intent.getSerializableExtra("currentUser");

            if (currentUser != null) {
                txtRemetente.setText(intent.getStringExtra("title") + " enviou-lhe");
                textDescricao.setText(intent.getStringExtra("body"));
                txtValor.setText(intent.getStringExtra("value") + "€");

                //valor = Double.parseDouble(intent.getStringExtra("value"));

            }

        } else {
            txtRemetente.setText("Erro ao receber dinheiro. Erro no Intent");
        }

        buttonBackToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(notificationOverlayActivity.this, DashboardActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });
    }

}

