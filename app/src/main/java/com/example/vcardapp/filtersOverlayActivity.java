package com.example.vcardapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.Calendar;

public class filtersOverlayActivity extends AppCompatActivity {

    private TextView validationError;
    private Login currentUser;
    private Button btnTodos;
    private Button btnCredit;
    private Button btnDebit;
    private Button btnDateDe;
    private Button btnDateAte;
    private Button asc;
    private Button desc;
    private Button ordenacaoData;
    private Button ordenacaoValor;
    private Button buttonAplicar;
    FirebaseDatabase database;
    DatabaseReference reference;
    private String transactionType = "all";
    private String ascOUdesc = "desc";
    private String ordenationType = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtersoverlay);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width), (int) (height * .8));

        validationError = findViewById(R.id.validationErrorAdd);
        btnTodos = findViewById(R.id.All);
        btnCredit = findViewById(R.id.credit);
        btnDebit = findViewById(R.id.debit);
        btnDateDe = findViewById(R.id.dataDe);
        btnDateAte = findViewById(R.id.dataAte);
        asc = findViewById(R.id.asc);
        desc = findViewById(R.id.desc);
        ordenacaoData = findViewById(R.id.ordenacaoData);
        ordenacaoValor = findViewById(R.id.ordenacaoValor);
        buttonAplicar = findViewById(R.id.buttonAplicar);

        btnDateDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v, btnDateDe);
            }
        });
        btnDateAte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v, btnDateAte);
            }
        });
        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCredit.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                btnCredit.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                btnDebit.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                btnDebit.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                btnTodos.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.customBlue));
                btnTodos.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                transactionType = "all";
            }
        });
        btnCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCredit.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.customBlue));
                btnCredit.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                btnDebit.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                btnDebit.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                btnTodos.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                btnTodos.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                transactionType = "credit";
            }
        });
        btnDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCredit.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                btnCredit.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                btnDebit.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.customBlue));
                btnDebit.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                btnTodos.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                btnTodos.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                transactionType = "debit";
            }
        });

        asc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asc.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.customBlue));
                asc.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                desc.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                desc.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                ascOUdesc = "asc";
            }
        });
        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asc.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                asc.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                desc.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.customBlue));
                desc.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                ascOUdesc = "desc";
            }
        });
        ordenacaoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ordenacaoData.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.customBlue));
                ordenacaoData.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                ordenacaoValor.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                ordenacaoValor.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                ordenationType = "data";
            }
        });
        ordenacaoValor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ordenacaoData.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                ordenacaoData.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.black));
                ordenacaoValor.setBackgroundTintList(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.customBlue));
                ordenacaoValor.setTextColor(ContextCompat.getColorStateList(filtersOverlayActivity.this, R.color.white));
                ordenationType = "valor";
            }
        });
        buttonAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transactionType == null || ascOUdesc == null || ordenationType == null) {
                    validationError.setText("Erro nos campos");
                    return;
                }

                Intent intent = new Intent(filtersOverlayActivity.this, HistoryActivity.class);
                intent.putExtra("currentUser", currentUser);
                intent.putExtra("filtros", true);
                intent.putExtra("transactionType", transactionType);
                intent.putExtra("ascOUdesc", ascOUdesc);
                intent.putExtra("ordenationType", ordenationType);
                try {
                    intent.putExtra("DateDe", !btnDateDe.getText().toString().equals("Data De") ? Transacoes.convertDateToTimestamp(btnDateDe.getText().toString()) : null);
                    intent.putExtra("DateAte", !btnDateAte.getText().toString().equals("Data Ate") ? Transacoes.convertTimestampRoundUp(btnDateAte.getText().toString()) : null);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                setResult(RESULT_OK, intent);
                finish();

                //Toast.makeText(filtersOverlayActivity.this, "Transacoes: " + transactionType + " | Intervalo de " + btnDateDe.getText().toString() + " até " + btnDateAte.getText().toString() + " | Ordenado por " + ordenationType + " " + ascOUdesc, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showDatePickerDialog(View view, Button btn) {
        // Obtém a data atual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Cria uma instância do DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Este método é chamado quando uma data é selecionada
                        btn.setText(day + "-" + (month+1) + "-" + year);
                    }
                },
                year, month, dayOfMonth);

        // Exibe o DatePickerDialog
        datePickerDialog.show();
    }

}
