package com.example.myapplication.ui.tododetail;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.controller.TodoDetailController;
import com.example.myapplication.db.LocalTodoAccessor;
import com.example.myapplication.db.RestEasyTodoAccessor;
import com.example.myapplication.db.TodoCRUDAccessor;
import com.example.myapplication.model.Todo;
import com.example.myapplication.ui.contacts.ContactActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TodoDetailActivity extends AppCompatActivity implements TodoDetailView {

    private static final int REQUEST_SELECT_CONTACTS = 3;
    private static final int REQUEST_CODE_CONTACT_MAIL = 4;
    private static final int REQUEST_CODE_CONTACT_SMS = 5;
    private static final int PERMISSION_REQUEST_SEND_SMS = 102;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private TextView linkedContactsTextView;



    private Button contactsButton, dateButton, timeButton, saveButton, deleteButton;
    private Button shareMailButton, shareSmsButton;
    private CheckBox doneCheckBox, favoriteCheckBox;

    private TodoDetailController controller;
    private Todo currentTodo;

    private String selectedDate = "";
    private String selectedTime = "";
    private ArrayList<String> selectedContacts = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        findViews();
        setButtonActions();

        long todoId = getIntent().getLongExtra("TODO_ID", -1L);
        boolean useRemote = getIntent().getBooleanExtra("USE_REMOTE", false);

        TodoCRUDAccessor activeAccessor;
        if (useRemote) {
            String baseUrl = "http://10.0.2.2:8080/backend-1.0-SNAPSHOT/rest";
            activeAccessor = new RestEasyTodoAccessor(baseUrl, this);
        } else {
            activeAccessor = new LocalTodoAccessor(this);
        }

        controller = new TodoDetailController(activeAccessor, this);
        controller.loadTodo(todoId);
    }

    private void findViews() {
        nameEditText = findViewById(R.id.editTodoName);
        descriptionEditText = findViewById(R.id.editTodoDescription);
        linkedContactsTextView = findViewById(R.id.textLinkedContacts);
        dateButton = findViewById(R.id.buttonDate);
        timeButton = findViewById(R.id.buttonTime);
        saveButton = findViewById(R.id.buttonSave);
        deleteButton = findViewById(R.id.buttonDelete);
        contactsButton = findViewById(R.id.buttonContacts);
        doneCheckBox = findViewById(R.id.checkDone);
        favoriteCheckBox = findViewById(R.id.checkFavorite);

        shareMailButton = findViewById(R.id.share_mail_button);
        shareSmsButton = findViewById(R.id.share_sms_button);
    }

    private void setButtonActions() {
        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());
        contactsButton.setOnClickListener(v -> openContactActivity());

        shareMailButton.setOnClickListener(v -> requestContactsPermissions(REQUEST_CODE_CONTACT_MAIL));
        shareSmsButton.setOnClickListener(v -> requestSmsPermission());

        saveButton.setOnClickListener(v -> {
            if (currentTodo != null) {
                currentTodo.setName(nameEditText.getText().toString());
                currentTodo.setDescription(descriptionEditText.getText().toString());
                currentTodo.setDueDate(selectedDate);
                currentTodo.setDueTime(selectedTime);
                currentTodo.setDone(doneCheckBox.isChecked());
                currentTodo.setFavorite(favoriteCheckBox.isChecked());
                currentTodo.setLinkedContacts(selectedContacts);

                controller.saveTodo(currentTodo);
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (currentTodo != null) {
                controller.deleteTodo(currentTodo.getId());
            }
        });
    }

    @Override
    public void showTodoData(Todo todo) {
        this.currentTodo = todo;

        nameEditText.setText(todo.getName() != null ? todo.getName() : "");
        descriptionEditText.setText(todo.getDescription() != null ? todo.getDescription() : "");
        doneCheckBox.setChecked(todo.isDone());
        favoriteCheckBox.setChecked(todo.isFavorite());

        selectedDate = todo.getDueDate() != null ? todo.getDueDate() : "";
        dateButton.setText(!selectedDate.isEmpty() ? selectedDate : getString(R.string.label_select_date));

        selectedTime = todo.getDueTime() != null ? todo.getDueTime() : "";
        timeButton.setText(!selectedTime.isEmpty() ? selectedTime : getString(R.string.label_select_time));


        if (todo.getLinkedContacts() != null) {
            selectedContacts = todo.getLinkedContacts();
        } else {
            selectedContacts = new ArrayList<>();
        }
        updateLinkedContactsText();


    }



    @Override
    public void closeScreen() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateLinkedContactsText() {
        if (selectedContacts == null || selectedContacts.isEmpty()) {
            linkedContactsTextView.setText("No contacts selected");
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String contactId : selectedContacts) {
            String contactName = com.example.myapplication.db.ContactLoader.getContactNameById(this, contactId);
            builder.append(contactName).append("\n");
        }
        linkedContactsTextView.setText(builder.toString().trim());
    }

    private void openContactActivity() {
        Intent intent = new Intent(TodoDetailActivity.this, ContactActivity.class);
        intent.putExtra("extra_todo_title", nameEditText.getText().toString());
        intent.putExtra("extra_todo_description", descriptionEditText.getText().toString());
        intent.putStringArrayListExtra("selected_contacts", selectedContacts);
        startActivityForResult(intent, REQUEST_SELECT_CONTACTS);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                TodoDetailActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format(Locale.US, "%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                    dateButton.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                TodoDetailActivity.this,
                (view, selectedHour, selectedMinute) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    selectedCalendar.set(Calendar.MINUTE, selectedMinute);
                    int hour12 = selectedCalendar.get(Calendar.HOUR);
                    if (hour12 == 0) hour12 = 12;
                    String amPm = selectedCalendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
                    selectedTime = String.format(Locale.US, "%d:%02d %s", hour12, selectedMinute, amPm);
                    timeButton.setText(selectedTime);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void requestContactsPermissions(int type) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, type);
        } else {
            pickContact(type);
        }
    }

    private void requestSmsPermission() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        } else {
            requestContactsPermissions(REQUEST_CODE_CONTACT_SMS);
        }
    }

    private void pickContact(int type) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, type);
    }

    private String toMessageString() {
        if (currentTodo == null) return null;
        StringBuilder sb = new StringBuilder();
        String strName = nameEditText.getText().toString().trim();
        String strDescription = descriptionEditText.getText().toString().trim();

        if (strName.isEmpty()) {
            showMessage("Name is required to share");
            return null;
        }

        sb.append("Hello, I want to share the following Todo:\n");
        sb.append(strName);
        if (selectedDate != null && !selectedDate.isEmpty()) {
            sb.append(" due until ").append(selectedDate);
            if (selectedTime != null && !selectedTime.isEmpty()) {
                sb.append(" ").append(selectedTime);
            }
        }
        if (!strDescription.isEmpty()) {
            sb.append(String.format(Locale.getDefault(), "\n\nDescription: %s", strDescription));
        }
        if (selectedContacts != null && !selectedContacts.isEmpty()) {
            sb.append("\n\nContacts:\n");
            for (String contactId : selectedContacts) {
                String contactName = com.example.myapplication.db.ContactLoader.getContactNameById(this, contactId);
                String email = com.example.myapplication.db.ContactLoader.getEmailByContactId(this, contactId);
                String phone = com.example.myapplication.db.ContactLoader.getPhoneNumberByContactId(this, contactId);

                sb.append(String.format(Locale.getDefault(), "%s : email: %s, phone number: %s\n",
                        contactName != null ? contactName : "N/A",
                        email != null ? email : "N/A",
                        phone != null ? phone : "N/A"));
            }
        }
        return sb.toString();
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
            java.util.ArrayList<String> messageParts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, null, null);
            showMessage("SMS sent.");
        } catch (Exception e) {
            showMessage("SMS failed, please try again.");
        }
    }

    private void sendMail(String emailAddress, String subject, String message) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(android.net.Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            try {
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
                showMessage("Starting email client.");
            } catch (android.content.ActivityNotFoundException ex) {
                showMessage("No email client found.");
            }
        } catch (Exception e) {
            showMessage("Email failed, please try again.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_CONTACTS && resultCode == RESULT_OK && data != null) {
            ArrayList<String> incoming = data.getStringArrayListExtra("selected_contacts");
            if (incoming != null) {
                this.selectedContacts = incoming;
                updateLinkedContactsText();
            }
        } else if ((requestCode == REQUEST_CODE_CONTACT_SMS || requestCode == REQUEST_CODE_CONTACT_MAIL) && resultCode == RESULT_OK && data != null) {
            android.net.Uri contactUri = data.getData();
            String[] projection = {android.provider.ContactsContract.Contacts._ID};
            android.database.Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID);
                if (idIndex != -1) {
                    String contactId = cursor.getString(idIndex);
                    cursor.close();
                    if (requestCode == REQUEST_CODE_CONTACT_SMS) {
                        String phoneNumber = com.example.myapplication.db.ContactLoader.getPhoneNumberByContactId(this, contactId);
                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            String message = toMessageString();
                            if (message != null) {
                                sendSMS(phoneNumber, message);
                            }
                        } else {
                            showMessage("No phone number found for this contact.");
                        }
                    } else {
                        String emailAddress = com.example.myapplication.db.ContactLoader.getEmailByContactId(this, contactId);
                        if (emailAddress != null && !emailAddress.isEmpty()) {
                            String message = toMessageString();
                            if (message != null) {
                                String subject = "Shared Todo: " + (currentTodo != null ? currentTodo.getName() : "");
                                sendMail(emailAddress, subject, message);
                            }
                        } else {
                            showMessage("No email address found for this contact.");
                        }
                    }
                } else {
                    cursor.close();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CONTACT_MAIL || requestCode == REQUEST_CODE_CONTACT_SMS) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                pickContact(requestCode);
            } else {
                showMessage("Permission denied. Contacts access is disabled.");
            }
        } else if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestContactsPermissions(REQUEST_CODE_CONTACT_SMS);
            } else {
                showMessage("Permission denied. Cannot send SMS.");
            }
        }
    }
}