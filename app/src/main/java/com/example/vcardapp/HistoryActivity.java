package com.example.vcardapp;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HistoryActivity extends AppCompatActivity{
    private EditText balanceInput;
    private Login currentUser;
    private String userNumber;
    private Filtros filtros;
    private Button buttonLogout;
    private Button filters;
    ArrayList<Transacoes> transactionList = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historyactivity);

        balanceInput = findViewById(R.id.balanceInput);
        buttonLogout = findViewById(R.id.buttonLogout);
        filters = findViewById(R.id.filters);

        buttonLogout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anterior();
            }
        });

        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, filtersOverlayActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivityForResult(intent, 1); // Use any positive integer as the request code
            }
        });

        RefreshBalance();

        Intent intent = getIntent();
        if (intent == null) return;
        currentUser = (Login) intent.getSerializableExtra("currentUser");
        if (currentUser == null) return;
        userNumber=currentUser.getNumber().toString();

        // get transactions from firebase
        getTransactions(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // This block will be executed when filtersOverlayActivity finishes and returns a result
            getTransactions(data);
        }
    }
    public void getTransactions(Intent intent){
        // carregar os filtros se existirem
        transactionList.clear();

        if (intent.getBooleanExtra("filtros", false)) {
            filtros = new Filtros(intent.getStringExtra("transactionType"), intent.getStringExtra("ascOUdesc"), intent.getStringExtra("ordenationType"), intent.getLongExtra("DateDe", 0), intent.getLongExtra("DateAte", 0));
            if (filtros.getDateAte() < 1) {
                filtros.setDateAte(null);
            } else {
                filtros.setDateAte(filtros.getDateAte());
            }
            if (filtros.getDateDe() < 1) {
                filtros.setDateDe(null);
            } else {
                filtros.setDateDe(filtros.getDateDe());
            }
        } else {
            filtros = new Filtros("all", "desc", "data", null, null);
        }

        // carregar as transações com base nos filtros
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("transacoes");
        Query queryNEnviou = reference.orderByChild("nEnviou").equalTo(userNumber);
        Query queryNRecebeu = reference.orderByChild("nRecebeu").equalTo(userNumber);

        Task<DataSnapshot> taskNEnviou = queryNEnviou.get();
        Task<DataSnapshot> taskNRecebeu = queryNRecebeu.get();

        Task<List<DataSnapshot>> allTasks = Tasks.whenAllSuccess(taskNEnviou, taskNRecebeu);

        allTasks.addOnSuccessListener(dataSnapshots -> {
                    // Process the results of both queries

                    for (DataSnapshot dataSnapshot : dataSnapshots) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Transacoes transaction = new Transacoes();

                            transaction.setData(snapshot.child("data").getValue(long.class));
                            transaction.setnEnviou(snapshot.child("nEnviou").getValue(String.class));
                            transaction.setnRecebeu(snapshot.child("nRecebeu").getValue(String.class));
                            transaction.setSaldoAposEnvio(snapshot.child("saldoAposEnvio").getValue(Double.class));
                            transaction.setSaldoAposReceber(snapshot.child("saldoAposReceber").getValue(Double.class));
                            transaction.setValor(snapshot.child("valor").getValue(Double.class));
                            transaction.setDescricao(snapshot.child("descricao").getValue(String.class));

                            transactionList.add(transaction);
                        }
                    }
                    processTransactionList();
                    // Continue with the rest of your code
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("FirebaseError", "Error in onDataChange: " + e.getMessage());
                });
    }
    public void Anterior() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }
    public void processTransactionList() {
        LinearLayout linearLayout = findViewById(R.id.linearLayoutHistorico);

        // clear linearLayout
        linearLayout.removeAllViews();

        for (Transacoes transaction : transactionList) {
            transaction.dataParsed = Transacoes.convertTimestampToDate(transaction.data);
        }

        //transactionList = Transacoes.sortTransactionList(transactionList);

        if (filtros.getOrdenationType().equals("data")) {
            // sort por Data Desc  (por default)
            transactionList = Transacoes.sortTransactionList(transactionList);

        }else if (filtros.getOrdenationType().equals("valor")){
            // sort por Valor Desc  (por default)
            transactionList = Transacoes.sortTransactionListByValue(transactionList);
        }

        // codigo para o ordenar por asc caso nao seja o default (desc)
        if (filtros.getAscOUdesc().equals("asc")){
            // invert transactionList
            Collections.reverse(transactionList);
        }

        // aplicar filtros
        transactionList = Transacoes.applyFilters(transactionList, filtros, currentUser.getNumber().toString());

        if (transactionList.size() == 0) {
            // Create a new TextView
            TextView label = new TextView(HistoryActivity.this);
            label.setText("Não existem transações");

            // Set the text style to bold
            label.setTypeface(null, Typeface.BOLD);

            // Set layout parameters to center the TextView vertically within the LinearLayout
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            // Apply the layout parameters
            label.setLayoutParams(layoutParams);

            // Add the TextView to the LinearLayout
            linearLayout.addView(label);
            return;
        }

        Map<String, String> dataDicionario = new HashMap<>();

        for(Transacoes transaction : transactionList){
            // Create a new Button for each iteration
            String doesDataExist = dataDicionario.get(transaction.dataParsed);

            if(doesDataExist==null){
                dataDicionario.put(transaction.dataParsed,"yes");

                TextView label = new TextView(HistoryActivity.this);
                label.setText(transaction.dataParsed);

                // Set the text style to bold
                label.setTypeface(null, Typeface.BOLD);

                // Set margin-bottom to 30px
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(50, 30, 0, 30); // 0 for left, top, right; 30 for bottom
                label.setLayoutParams(params);

                linearLayout.addView(label);
            }
            Button listItem = new Button(HistoryActivity.this);

            // Set layout parameters
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    200 // Height in pixels or dp
            );
            listItem.setBackgroundResource(R.drawable.custom_button_shape);
            listItem.setClickable(false);
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(layoutParams);
            marginParams.setMargins(0, 0, 0, 50);
            listItem.setLayoutParams(marginParams);
            listItem.setGravity(Gravity.CENTER_VERTICAL); // or Gravity.LEFT

            int tipo = transaction.nEnviou.equals(currentUser.getNumber().toString()) ? 0 : 1; // 0 enviou, 1 recebeu

            String tipoTransacao = "-";
            String tipoTransacaoDe = "Para";
            String user=transaction.nRecebeu;
            Double valorAtual=transaction.saldoAposEnvio;
            String tipoSaldo = "Saldo Após envio: ";

            if (tipo == 1) {
                tipoTransacaoDe = "De";
                tipoTransacao = "+";
                user=transaction.nEnviou;
                valorAtual=transaction.saldoAposReceber;
                tipoSaldo = "Saldo Após Receber: ";
            }

            listItem.setText("\t\t\t\t\t" + tipoTransacao + transaction.valor + "€ \t\t\t\t\t\t\t\t\t\t " + tipoTransacaoDe + " " + user + "\n\t\t\t\t\t" + tipoSaldo + valorAtual+"€");

            // Add the item to the LinearLayout
            linearLayout.addView(listItem);
        }
    }
    public void RefreshBalance(){
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
                        if(snapshot.exists()){
                            Double balance = snapshot.child(currentUser.getNumber().toString()).child("balance").getValue(Double.class);
                            balanceInput.setText(balance.toString() + "€");

                        }else{
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

}
