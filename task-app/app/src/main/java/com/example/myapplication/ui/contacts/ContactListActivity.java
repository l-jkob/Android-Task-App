package com.example.myapplication.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.ContactLoader;
import com.example.myapplication.db.LocalTodoAccessor;
import com.example.myapplication.db.RestEasyTodoAccessor;
import com.example.myapplication.db.TodoCRUDAccessor;
import com.example.myapplication.model.Todo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactListActivity extends AppCompatActivity {

    private ListView contactsListView;
    private TodoCRUDAccessor accessor;
    private boolean useRemote = false;

    private final java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
    private final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        contactsListView = findViewById(R.id.contactsListView);
        useRemote = getIntent().getBooleanExtra("USE_REMOTE", false);

        if (useRemote) {
            String baseUrl = "http://10.0.2.2:8080/backend-1.0-SNAPSHOT/rest";
            accessor = new RestEasyTodoAccessor(baseUrl, this);
        } else {
            accessor = new LocalTodoAccessor(this);
        }

        loadContactsWithTodos();
    }

    private void loadContactsWithTodos() {
        executor.execute(() -> {
            try {
                List<Todo> allTodos = accessor.readAllTodos();
                Set<String> uniqueContactIds = new HashSet<>();
                if (allTodos != null) {
                    for (Todo todo : allTodos) {
                        if (todo.getLinkedContacts() != null) {
                            uniqueContactIds.addAll(todo.getLinkedContacts());
                        }
                    }
                }

                if (uniqueContactIds.isEmpty()) {
                    mainHandler.post(() -> {
                        Toast.makeText(this, "No contacts found with associated todos", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }

                List<String> contactIdsList = new ArrayList<>(uniqueContactIds);
                List<String> displayStrings = new ArrayList<>();
                List<String> contactNamesList = new ArrayList<>();

                for (String id : contactIdsList) {
                    String name = ContactLoader.getContactNameById(this, id);
                    contactNamesList.add(name);
                    displayStrings.add(name + " (" + id + ")");
                }

                mainHandler.post(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_list_item_1,
                            displayStrings
                    );
                    contactsListView.setAdapter(adapter);

                    contactsListView.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedContactId = contactIdsList.get(position);
                        String selectedContactName = contactNamesList.get(position);

                        Intent intent = new Intent(ContactListActivity.this, ContactTodosActivity.class);
                        intent.putExtra("CONTACT_ID", selectedContactId);
                        intent.putExtra("CONTACT_NAME", selectedContactName);
                        intent.putExtra("USE_REMOTE", useRemote);
                        startActivity(intent);
                    });
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    Toast.makeText(this, "Error loading contacts: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
