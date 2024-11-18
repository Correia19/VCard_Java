package com.example.vcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PinActivitySend extends AppCompatActivity {

    private Button button;
    private Button buttonZero;

    //declare button 1 to 9
    private Button buttonOne;
    private Button buttonTwo;
    private Button buttonThree;
    private Button buttonFour;
    private Button buttonFive;
    private Button buttonSix;
    private Button buttonSeven;
    private Button buttonEight;
    private Button buttonNine;
    private Button buttonDelete;
    private EditText pin;
    private Login currentUser;

    private TextView validationError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinactivitysend);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int)(height*.8));

        button = findViewById(R.id.buttonLoginFromPin);
        pin = findViewById(R.id.pinInput);

        //click button 1 to 9, make the value appear on the textview
        //click button 0, make the value appear on the textview
        buttonZero = findViewById(R.id.button16);
        buttonOne = findViewById(R.id.button2);
        buttonTwo = findViewById(R.id.button4);
        buttonThree = findViewById(R.id.button5);
        buttonFour = findViewById(R.id.button6);
        buttonFive = findViewById(R.id.button7);
        buttonSix = findViewById(R.id.button8);
        buttonSeven = findViewById(R.id.button12);
        buttonEight = findViewById(R.id.button13);
        buttonNine = findViewById(R.id.button14);
        buttonDelete = findViewById(R.id.button15);
        validationError = findViewById(R.id.validationErrorAdd);

        // region number buttons
        buttonZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "0" to R.id.pinInput
                pin.setText(pin.getText().toString() + "0");
            }
        });
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "1" to R.id.pinInput
                pin.setText(pin.getText().toString() + "1");
            }
        });
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "2" to R.id.pinInput
                pin.setText(pin.getText().toString() + "2");
            }
        });
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "3" to R.id.pinInput
                pin.setText(pin.getText().toString() + "3");
            }
        });
        buttonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "4" to R.id.pinInput
                pin.setText(pin.getText().toString() + "4");
            }
        });
        buttonFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "5" to R.id.pinInput
                pin.setText(pin.getText().toString() + "5");
            }
        });
        buttonSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "6" to R.id.pinInput
                pin.setText(pin.getText().toString() + "6");
            }
        });
        buttonSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "7" to R.id.pinInput
                pin.setText(pin.getText().toString() + "7");
            }
        });
        buttonEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "8" to R.id.pinInput
                pin.setText(pin.getText().toString() + "8");
            }
        });
        buttonNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the value "9" to R.id.pinInput
                pin.setText(pin.getText().toString() + "9");
            }
        });
        // endregion

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the last value of R.id.pinInput
                String pinString = pin.getText().toString();
                if (pinString.length() > 0) {
                    pinString = pinString.substring(0, pinString.length() - 1);
                    pin.setText(pinString);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePin();
            }
        });
    }

    public void validatePin() {

        if (!pin.getText().toString().trim().isEmpty()) {
            if (pin.getText().toString().length() == 4) {

                Intent intent = getIntent();
                if (intent != null) {
                    currentUser = (Login) intent.getSerializableExtra("currentUser");
                    if (currentUser != null) {
                        if (currentUser.getPin().equals(Integer.parseInt(pin.getText().toString().trim()))) {
                            Intent intent1 = new Intent();
                            setResult(RESULT_OK, intent1);
                            finish(); // Close PinActivitySend
                        } else {
                            validationError.setVisibility(View.VISIBLE);
                            pin.setError("Incorrect pin.");
                        }
                    }
                } else {
                    // make a throw exception
                    validationError.setVisibility(View.VISIBLE);
                    pin.setError("User Not Logged in. Please Log In Again");
                }
            } else {
                pin.setError("Pin must be 4 digits long");
                validationError.setVisibility(View.VISIBLE);
            }
        } else {
            validationError.setVisibility(View.VISIBLE);
        }
    }
}
