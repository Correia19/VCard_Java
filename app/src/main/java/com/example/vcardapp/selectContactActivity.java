package com.example.vcardapp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class selectContactActivity extends AppCompatActivity {

    private Button buttonLogout;
    private Button buttonNovoContacto;
    private LinearLayout linearLayout;
    private String selectedContact;
    private Integer selectedContactNumber;
    private Login currentUser;
    private EditText balanceInput;
    private TextView validationError;
    FirebaseDatabase database;
    DatabaseReference reference;
    private List<Button> registeredContacts = new ArrayList<>();
    private List<Button> unregisteredContacts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectcontactactivity);

        buttonLogout = findViewById(R.id.buttonLogout);
        linearLayout = findViewById(R.id.linearLayoutContactos);
        buttonNovoContacto = findViewById(R.id.buttonNovoContacto);
        balanceInput = findViewById(R.id.balanceInput);
        validationError = findViewById(R.id.validationErrorRegisted);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anterior();
            }
        });

        buttonNovoContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarContacto();
            }
        });

        RefreshBalance();

        if (checkPermissions()) {
            GetContacts();
        } else {
            requestPermissions();
        }

    }

    private boolean checkPermissions() {
        int readContactsPermission = ContextCompat.checkSelfPermission(selectContactActivity.this, Manifest.permission.READ_CONTACTS);
        int writeContactsPermission = ContextCompat.checkSelfPermission(selectContactActivity.this, Manifest.permission.WRITE_CONTACTS);

        return readContactsPermission == PackageManager.PERMISSION_GRANTED && writeContactsPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(selectContactActivity.this, new String[]{
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 1);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with accessing contacts
                GetContacts();
            } else {
                requestPermissions();
            }
        }
    }

    private void GetContacts() {

        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor.getCount() > 0) {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");
            processContacts(cursor, 0);

        } else {
            validationError.setText("Não existem contactos no dispositivo.");
            validationError.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No contacts available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void processContacts(Cursor cursor, int index) {
        if (index < cursor.getCount()) {
            cursor.moveToPosition(index);
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") Integer phoneNumber = parsePhoneNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

            if (!currentUser.getNumber().equals(phoneNumber)) {

                Query contactInfo = reference.orderByChild("phone_number").equalTo(phoneNumber);
                contactInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean userExists = dataSnapshot.exists();
                        addButtonToList(name, phoneNumber, userExists);
                        processContacts(cursor, index + 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(selectContactActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        processContacts(cursor, index + 1);
                    }
                });
            } else {
                processContacts(cursor, index + 1);
            }
        } else {
            cursor.close();
            addButtonsToLayout(registeredContacts, true);
            addButtonsToLayout(unregisteredContacts, false);
        }

    }

    private void addButtonToList(String name, Integer number, boolean userExists) {
        Button listItem = new Button(selectContactActivity.this);
        listItem.setBackgroundResource(R.drawable.custom_button_shape);
        listItem.setText(name + "\n" + number);
        listItem.setClickable(true);
        listItem.setId(number);

        // set an icon for the button if the user has vcard
        if (userExists) {
            Drawable iconDrawable = getResources().getDrawable(R.drawable.logotaes);
            iconDrawable.setBounds(0, 0, 180, 180);
            listItem.setCompoundDrawables(null, null, iconDrawable, null);
            registeredContacts.add(listItem);
        } else {
            unregisteredContacts.add(listItem);
        }
    }

    private void addButtonsToLayout(List<Button> buttons, boolean isRegisted) {

        for (Button button : buttons) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    200 // Height in pixels or dp
            );
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(layoutParams);
            marginParams.setMargins(0, 0, 0, 50);
            button.setLayoutParams(marginParams);
            button.setPadding(0, 0, 20, 0);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isRegisted) {
                        validationError.setVisibility(View.VISIBLE);
                        return;
                    }

                    Button button = (Button) view;
                    if (!button.isSelected()) {
                        // Button is not in the "clicked" state
                        button.setSelected(true);
                        int endIndex = button.getText().toString().length() - 10;
                        selectedContact = button.getText().toString().substring(0, endIndex);
                        selectedContactNumber = button.getId();
                        Seguinte();

                    } else {
                        // Button is already in the "clicked" state
                        button.setSelected(false);
                    }
                }
            });

            // Add the item to the LinearLayout
            linearLayout.addView(button);
        }
    }

    public Integer parsePhoneNumber(String phoneNumber) {
        // Remove non-numeric characters
        String cleanedNumber = phoneNumber.replaceAll("\\D", "");

        int startIndex = Math.max(cleanedNumber.length() - 9, 0);
        String lastNineDigits = cleanedNumber.substring(startIndex);

        return Integer.parseInt(lastNineDigits);
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

    public void Anterior() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void adicionarContacto() {
        Intent intent1 = new Intent(this, addContactOverlayActivity.class);
        intent1.putExtra("currentUser", currentUser);
        ActivityCompat.startActivityForResult(this, intent1, 1, null);
    }

    public void Seguinte() {
        Intent intent = new Intent(this, sendMoneyActivity.class);
        intent.putExtra("contacto", selectedContact);
        intent.putExtra("contactNumber", String.valueOf(selectedContactNumber));
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                selectedContact = data.getStringExtra("contactName");
                selectedContactNumber = Integer.parseInt(data.getStringExtra("contactNumber"));
                Seguinte();
            }
        }
    }

}
