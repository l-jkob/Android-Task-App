package com.example.myapplication.ui.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.controller.TodoListController;
import com.example.myapplication.db.LocalTodoAccessor;
import com.example.myapplication.db.RestEasyTodoAccessor;
import com.example.myapplication.db.TodoCRUDAccessor;
import com.example.myapplication.model.Todo;
import com.example.myapplication.ui.tododetail.TodoDetailActivity;


import java.util.ArrayList;
import java.util.List;

public class TodoListActivity extends AppCompatActivity implements TodoListView {

    private ListView todoListView;
    private TodoAdapter adapter;
    private TodoListController controller;

    private Button addTodoButton;
    private Button sortByDateButton;
    private Button favoritesFirstButton;

    private Button showContactsButton;

    private boolean useRemote = false;
    private ActivityResultLauncher<Intent> detailScreenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        useRemote = getIntent().getBooleanExtra("USE_REMOTE", false);

        findViews();

        TodoCRUDAccessor activeAccessor;
        if (useRemote) {
            String baseUrl = "http://10.0.2.2:8080/backend-1.0-SNAPSHOT/rest";
            activeAccessor = new RestEasyTodoAccessor(baseUrl, this);
        } else {
            activeAccessor = new LocalTodoAccessor(this);
        }
        controller = new TodoListController(activeAccessor, this);

        setupLauncher();
        setupClickListeners();

        controller.loadTodos();
    }

    private void findViews(){
        todoListView = findViewById(R.id.todoListView);
        addTodoButton = findViewById(R.id.buttonAddTodo);
        sortByDateButton = findViewById(R.id.buttonSortByDate);
        favoritesFirstButton = findViewById(R.id.buttonFavoritesFirst);

        showContactsButton = findViewById(R.id.buttonShowContacts);
    }

    private void setupLauncher(){
        detailScreenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> controller.loadTodos()
        );
    }

    private void setupClickListeners(){
        addTodoButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(TodoListActivity.this, TodoDetailActivity.class);
                intent.putExtra("TODO_ID", -1L);
                intent.putExtra("USE_REMOTE", useRemote);
                detailScreenLauncher.launch(intent);
            }
        });

        todoListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                Todo clickedTodo = adapter.getItem(position);
                if (clickedTodo != null) {
                    Intent intent = new Intent(TodoListActivity.this, TodoDetailActivity.class);
                    intent.putExtra("TODO_ID", clickedTodo.getId());
                    intent.putExtra("USE_REMOTE", useRemote);
                    detailScreenLauncher.launch(intent);
                }
            }
        });

        sortByDateButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                controller.sortByDate();
            }
        });

        favoritesFirstButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                controller.sortByImportance();
            }
        });



        showContactsButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(TodoListActivity.this, android.Manifest.permission.READ_CONTACTS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    openContactListActivity();
                } else {
                    androidx.core.app.ActivityCompat.requestPermissions(TodoListActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS}, 100);
                }
            }
        });
    }

    private void openContactListActivity() {
        Intent intent = new Intent(TodoListActivity.this, com.example.myapplication.ui.contacts.ContactListActivity.class);
        intent.putExtra("USE_REMOTE", useRemote);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openContactListActivity();
            } else {
                showPopup("Contacts permission denied.");
            }
        }
    }

    @Override
    public void displayTodos(List<Todo> todos) {
        adapter = new TodoAdapter(this, new ArrayList<>(todos), new TodoAdapter.TodoActionListener() {
            @Override
            public void onDoneToggled(Todo todo, boolean isDone) {
                controller.toggleTodoDone(todo, isDone);
            }

            @Override
            public void onFavoriteToggled(Todo todo, boolean isFavorite) {
                controller.toggleTodoFavorite(todo, isFavorite);
            }
        });

        todoListView.setAdapter(adapter);
    }

    @Override
    public void showPopup(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}