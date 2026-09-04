package com.example.myapplication.ui.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.myapplication.R;
import com.example.myapplication.controller.ContactController;
import com.example.myapplication.model.Contact;
import java.util.List;

public class ContactActivity extends AppCompatActivity implements ContactView {

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 101;
    private ContactController controller;

    private List<Contact> allContacts = new java.util.ArrayList<>();
    private android.widget.ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        listView = findViewById(R.id.contactListView);
        controller = new ContactController(this);
        checkAndLoadContacts();
    }

    private void checkAndLoadContacts() {
        boolean hasPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;

        controller.onFetchContactsRequested(this, hasPermission);
    }

    @Override
    public void displayContacts(List<Contact> contacts) {
        this.allContacts = contacts;
        listView = findViewById(R.id.contactListView);
        android.widget.Button confirmButton = findViewById(R.id.buttonConfirmContacts);

        List<String> displayStrings = new java.util.ArrayList<>();
        for (Contact c : contacts) {
            String detail = c.getName();
            if (c.getPhoneNumbers() != null && !c.getPhoneNumbers().isEmpty()) {
                detail += " (" + c.getPhoneNumbers().get(0) + ")";
            }
            displayStrings.add(detail);
        }

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                displayStrings
        );

        listView.setAdapter(adapter);

        java.util.ArrayList<String> preSelected = getIntent().getStringArrayListExtra("selected_contacts");
        if (preSelected != null) {
            for (int i = 0; i < allContacts.size(); i++) {
                if (preSelected.contains(String.valueOf(allContacts.get(i).getId()))) {
                    listView.setItemChecked(i, true);
                }
            }
        }

        confirmButton.setOnClickListener(v -> {
            java.util.ArrayList<String> selectedIds = new java.util.ArrayList<>();
            android.util.SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();

            if (checkedItemPositions != null) {
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    int position = checkedItemPositions.keyAt(i);
                    if (checkedItemPositions.valueAt(i)) {
                        selectedIds.add(String.valueOf(allContacts.get(position).getId()));
                    }
                }
            }

            android.content.Intent resultIntent = new android.content.Intent();
            resultIntent.putStringArrayListExtra("selected_contacts", selectedIds);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    public void showPermissionExplanation() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("This app needs access to your address book to link contacts to your tasks.")
                    .setPositiveButton("OK", (dialog, which) -> requestRuntimePermission())
                    .setNegativeButton("Cancel", (dialog, which) -> showAccessDeniedMessage())
                    .show();
        } else {
            requestRuntimePermission();
        }
    }

    @Override
    public void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
    }

    @Override
    public void showAccessDeniedMessage() {
        Toast.makeText(this, "Contacts access denied. Feature disabled.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkAndLoadContacts();
            } else {
                showAccessDeniedMessage();
            }
        }
    }
}