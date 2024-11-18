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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationsActivity extends AppCompatActivity{
    private EditText balanceInput;
    private Login currentUser;
    private Button buttonLogout;
    ArrayList<Notificacoes> notificationsList = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationsactivity);

        balanceInput = findViewById(R.id.balanceInput);
        buttonLogout = findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anterior();
            }
        });

        RefreshBalance();

        Intent intent = getIntent();
        if (intent == null) return;
        currentUser = (Login) intent.getSerializableExtra("currentUser");
        if (currentUser == null) return;
        String userNumber=currentUser.getNumber().toString();
        //
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notificacoes");
        Query query = reference.orderByChild("nRecebeu").equalTo(userNumber);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notificacoes notification = new Notificacoes();

                    notification.setData(snapshot.child("data").getValue(Long.class));
                    notification.setnEnviou(snapshot.child("nEnviou").getValue(String.class));
                    notification.setnRecebeu(snapshot.child("nRecebeu").getValue(String.class));
                    notification.setValor(snapshot.child("valor").getValue(Double.class));
                    notification.setDescricao(snapshot.child("descricao").getValue(String.class));
                    notification.setLida(snapshot.child("lida").getValue(Boolean.class));

                    notificationsList.add(notification);
                }


                processNotificationsList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle failure
                Log.e("FirebaseError", "Error in onDataChange: " + databaseError.getMessage());
            }
        });

    }
    public void Anterior() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }
    public void processNotificationsList() {
        LinearLayout linearLayout = findViewById(R.id.linearLayoutHistorico);

        if (notificationsList.size() == 0) {
            // Create a new TextView
            TextView label = new TextView(NotificationsActivity.this);
            label.setText("Não existem notificacoes");

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

        for (Notificacoes notification : notificationsList) {
            notification.dataParsed = Notificacoes.convertTimestampToDate(notification.data);
        }

        notificationsList = Notificacoes.sortNotificationsList(notificationsList);
        Map<String, String> dataDicionario = new HashMap<>();

        for(Notificacoes notification : notificationsList){
            // Create a new Button for each iteration
            String doesDataExist = dataDicionario.get(notification.dataParsed);

            if(doesDataExist==null){
                dataDicionario.put(notification.dataParsed,"yes");

                TextView label = new TextView(NotificationsActivity.this);
                label.setText(notification.dataParsed);

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
            Button listItem = new Button(NotificationsActivity.this);

            // Set layout parameters
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    200 // Height in pixels or dp
            );
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(layoutParams);
            marginParams.setMargins(0, 0, 0, 50);
            listItem.setLayoutParams(marginParams);
            listItem.setGravity(Gravity.CENTER_VERTICAL); // or Gravity.LEFT
            String notificationId = notification.nRecebeu + "_" + notification.data;

            String user = notification.nEnviou;
            String descricao = notification.descricao;
            Boolean lida = notification.lida;

            listItem.setTextColor(getResources().getColor(R.color.black));
            listItem.setText("\t\t\t\t\tRecebeu " + notification.valor + "€ De " + user + "\n\t\t\t\t\t" + '"' + descricao + '"');

            if (lida) {
                listItem.setBackgroundResource(R.drawable.custom_button_shape_read);
            } else {
                listItem.setBackgroundResource(R.drawable.custom_button_shape);

            }

            //fazer evento Onclick com query á bd lida=true e a atualizar a cor do botao
            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listItem.setBackgroundResource(R.drawable.custom_button_shape_read);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notificacoes");
                    reference.child(notificationId).child("lida").setValue(true);
                }
            });

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
