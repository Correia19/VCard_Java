package com.example.vcardapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private Button buttonLogout;
    private TextView validationError;
    private Button buttonSendMoney;
    private Button buttonHistory;
    private Button buttonAddPiggy;
    private Button buttonRemovePiggy;
    private EditText balanceInput;
    private EditText piggyBalanceInput;
    private EditText piggyQuantityInput;
    private Login currentUser;
    private TextView tituloHistorico;
    private Button buttonSettings;
    private Button buttonNotifications;
    private ImageView hasnotifications;

    LinearLayout linearLayout;
    DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);

    private ArrayList<Transacoes> transactionList = new ArrayList<>();
    private ArrayList<Transacoes> latestTransactions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboardactivity);

        buttonLogout = findViewById(R.id.buttonLogout);
        buttonSendMoney = findViewById(R.id.buttonSendMoneyDashboard);
        balanceInput = findViewById(R.id.balanceInput);
        buttonHistory = findViewById(R.id.buttonHistory);
        tituloHistorico = findViewById(R.id.tituloHistorico);
        validationError = findViewById(R.id.validationErrorPiggy);
        buttonSettings = findViewById(R.id.buttonSettings);
        linearLayout = findViewById(R.id.linearLayoutHistorico);
        buttonNotifications = findViewById(R.id.buttonNotifications);
        hasnotifications = findViewById(R.id.hasnotificationsIcon);

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history();
            }
        });
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings();
            }
        });
        piggyBalanceInput = findViewById(R.id.SaldoPiggyBank);
        buttonAddPiggy = findViewById(R.id.piggyButtonDepositar);
        buttonRemovePiggy = findViewById(R.id.piggyButtonDebitar);
        piggyQuantityInput = findViewById(R.id.piggyInputQuantidade);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        buttonNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notofications();
            }
        });
        buttonSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMoney();
            }
        });

        buttonAddPiggy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!piggyQuantityInput.getText().toString().trim().isEmpty()) {
                    addPiggy();
                } else {
                    validationError.setText("Deve inserir um valor.");
                    validationError.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonRemovePiggy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!piggyQuantityInput.getText().toString().trim().isEmpty()) {
                    removePiggy();
                } else {
                    validationError.setText("Deve inserir um valor.");
                    validationError.setVisibility(View.VISIBLE);
                }
            }
        });

        RefreshBalance();
        GetHistory();
        if (checkPermissions()) {

        } else {
            requestPermissions();
        }


      checkNotifications();

    }

    private void checkNotifications() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notificacoes");
        Query checkNotifications = reference.orderByChild("nRecebeu").equalTo(currentUser.getNumber().toString());
        checkNotifications.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    boolean lida = notificationSnapshot.child("lida").getValue(Boolean.class);

                    if (!lida) {
                        hasnotifications.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, new IntentFilter("incomingNotification"));

    }
    //Define the BroadcastReceiver
    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data from the broadcast
            String title = intent.getStringExtra("title");
            String body = intent.getStringExtra("body");
            String value = intent.getStringExtra("value");

            // Show the notification overlay using the extracted data
            showNotificationOverlay(title, body, value);
        }
    };

    private void notofications() {
        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    private boolean checkPermissions() {
        int notificationPermission = ContextCompat.checkSelfPermission(DashboardActivity.this, android.Manifest.permission.POST_NOTIFICATIONS);
        int networkStatePermission = ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.INTERNET);
        return notificationPermission == PackageManager.PERMISSION_GRANTED && networkStatePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.INTERNET}, 1);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed
                return;
            } else {
                requestPermissions();
            }
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
                            Double piggy_balance = snapshot.child(currentUser.getNumber().toString()).child("piggy_balance").getValue(Double.class);
                            balanceInput.setText(balance.toString() + "€");
                            piggyBalanceInput.setText(piggy_balance.toString() + "€");

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

    public void logout() {
        Intent intent = new Intent(this, PinActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void sendMoney() {
        Intent intent = new Intent(this, selectContactActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void history() {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("filtros", false);
        startActivity(intent);
    }
    private void Settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void addPiggy(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("phone_number").equalTo(currentUser.getNumber());

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //buscar balance
                    Double balance = dataSnapshot.child(currentUser.getNumber().toString()).child("balance").getValue(Double.class);
                    //buscar piggy_balance
                    Double piggyBalance = dataSnapshot.child(currentUser.getNumber().toString()).child("piggy_balance").getValue(Double.class);

                    Double balanceTotal = balance - piggyBalance;

                    if (balanceTotal < Double.parseDouble(piggyQuantityInput.getText().toString())) {
                        //validationError.setVisibility(View.VISIBLE);
                        piggyQuantityInput.setError("Saldo insuficiente");
                        return;
                    }

                    Double newPiggyBalance = piggyBalance + Double.parseDouble(piggyQuantityInput.getText().toString());
                    DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
                    String roundedVal = decimalFormat.format(newPiggyBalance);
                    newPiggyBalance = Double.parseDouble(roundedVal);

                    reference.child(currentUser.getNumber().toString()).child("piggy_balance").setValue(newPiggyBalance);
                    RefreshBalance();

                } else {
                    // O user nao existe
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });


    }
    public void removePiggy(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("phone_number").equalTo(currentUser.getNumber());

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double balance = dataSnapshot.child(currentUser.getNumber().toString()).child("balance").getValue(Double.class);
                    Double piggyBalance = dataSnapshot.child(currentUser.getNumber().toString()).child("piggy_balance").getValue(Double.class);


                    if (piggyBalance < Double.parseDouble(piggyQuantityInput.getText().toString())) {
                        //validationError.setVisibility(View.VISIBLE);
                        validationError.setText("Saldo insuficiente");
                        validationError.setVisibility(View.VISIBLE);
                        piggyQuantityInput.setError("Saldo insuficiente");
                        return;
                    }

                    Double newPiggyBalance = piggyBalance - Double.parseDouble(piggyQuantityInput.getText().toString());
                    DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
                    String roundedVal = decimalFormat.format(newPiggyBalance);
                    newPiggyBalance = Double.parseDouble(roundedVal);

                    reference.child(currentUser.getNumber().toString()).child("piggy_balance").setValue(newPiggyBalance);
                    RefreshBalance();

                } else {
                    // O user nao existe
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

    }

    public void GetHistory() {
        Intent intent = getIntent();
        if (intent == null) return;
        currentUser = (Login) intent.getSerializableExtra("currentUser");
        if (currentUser == null) return;
        String userNumber = currentUser.getNumber().toString();
        //
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
                    transactionList = Transacoes.sortTransactionList(transactionList);
                    latestTransactions = new ArrayList<>(transactionList.subList(0, Math.min(transactionList.size(), 3)));

                    processTransactionList();
                    // Continue with the rest of your code
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("FirebaseError", "Error in onDataChange: " + e.getMessage());
                });
    }


    public void processTransactionList() {

        if (latestTransactions.size() == 0) {
            // Create a new TextView
            TextView label = new TextView(DashboardActivity.this);
            label.setText("Não existem transações");

            // Set the text style to bold
            label.setTypeface(null, Typeface.BOLD);

            // Set layout parameters to center the TextView vertically within the LinearLayout
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(0, 250, 0, 0);

            // Apply the layout parameters
            label.setLayoutParams(layoutParams);

            // Add the TextView to the LinearLayout
            linearLayout.addView(label);
        } else {
            for (Transacoes transaction : latestTransactions) {
                transaction.dataParsed = Transacoes.convertTimestampToDate(transaction.data);
            }

            Map<String, String> dataDicionario = new HashMap<>();

            for (Transacoes transaction : latestTransactions) {
                String doesDataExist = dataDicionario.get(transaction.dataParsed);

                if (doesDataExist == null) {
                    dataDicionario.put(transaction.dataParsed, "yes");

                    TextView label = new TextView(DashboardActivity.this);
                    label.setText(transaction.dataParsed);

                    // Set the text style to bold
                    label.setTypeface(null, Typeface.BOLD);

                    // Set margin-bottom to 30px
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(50, 10, 0, 10); // 0 for left, top, right; 30 for bottom
                    label.setLayoutParams(params);

                    linearLayout.addView(label);
                }
                Button listItem = new Button(DashboardActivity.this);

                // Set layout parameters
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        200 // Height in pixels or dp
                );
                listItem.setBackgroundResource(R.drawable.custom_button_shape);
                listItem.setClickable(false);
                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(layoutParams);
                marginParams.setMargins(0, 20, 0, 0);
                listItem.setLayoutParams(marginParams);
                listItem.setGravity(Gravity.CENTER_VERTICAL); // or Gravity.LEFT

                int tipo = transaction.nEnviou.equals(currentUser.getNumber().toString()) ? 0 : 1; // 0 enviou, 1 recebeu


                String tipoTransacao = "-";
                String tipoTransacaoDe = "Para";
                String user = transaction.nRecebeu;
                Double valorAtual = transaction.saldoAposEnvio;
                String tipoSaldo = "Saldo Após envio: ";

                if (tipo == 1) {
                    tipoTransacaoDe = "De";
                    tipoTransacao = "+";
                    user = transaction.nEnviou;
                    valorAtual = transaction.saldoAposReceber;
                    tipoSaldo = "Saldo Após Receber: ";
                }

                listItem.setText("\t\t\t\t\t" + tipoTransacao + transaction.valor + "€ \t\t\t\t\t\t\t\t\t\t " + tipoTransacaoDe + " " + user + "\n\t\t\t\t\t" + tipoSaldo + valorAtual + "€");


                // Add the item to the LinearLayout
                linearLayout.addView(listItem);
            }
        }
    }

    public void showNotificationOverlay(String title, String body, String valor) {

        Intent intent = new Intent(this, notificationOverlayActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("title", title);
        intent.putExtra("body", body);
        intent.putExtra("value", valor);
        startActivity(intent);
    }
}


