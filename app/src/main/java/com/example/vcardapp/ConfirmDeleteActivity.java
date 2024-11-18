package com.example.vcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ConfirmDeleteActivity extends AppCompatActivity {

    private Button yesButton;
    private Button noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmdeleteactivity);

        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);


        yesButton.setOnClickListener(new View.OnClickListener() { // confirma que quer apagar a conta
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent();
                setResult(RESULT_OK, intent1);
                finish(); // Close PinActivitySend
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() { // cancela o apagar a conta
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent();
                setResult(RESULT_CANCELED, intent1);
                finish(); // Close PinActivitySend
            }
        });

    }

}
