package com.example.myapplication.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.controller.TodoListController;
import com.example.myapplication.db.LocalTodoAccessor;
import com.example.myapplication.db.RestEasyTodoAccessor;
import com.example.myapplication.db.TodoCRUDAccessor;
import com.example.myapplication.model.Todo;
import com.example.myapplication.ui.tododetail.TodoDetailActivity;
import com.example.myapplication.ui.todolist.TodoAdapter;
import com.example.myapplication.ui.todolist.TodoListView;

import java.util.ArrayList;
import java.util.List;

public class ContactTodosActivity extends AppCompatActivity implements TodoListView {

    private ListView todoListView;
    private TodoAdapter adapter;
    private TodoListController controller;
    private String contactId;
    private String contactName;
    private boolean useRemote = false;

    private Button addTodoButton;
    private Button sortByDateButton;
    private Button favoritesFirstButton;

    private Button showContactsButton;

    private ActivityResultLauncher<Intent> detailScreenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        contactId = getIntent().getStringExtra("CONTACT_ID");
        contactName = getIntent().getStringExtra("CONTACT_NAME");
        useRemote = getIntent().getBooleanExtra("USE_REMOTE", false);

        setTitle("Todos for " + contactName);
        findViews();

        addTodoButton.setVisibility(View.GONE);

        showContactsButton.setVisibility(View.GONE);

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
    private void setupLauncher() {
        detailScreenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> controller.loadTodos()
        );
    }

    private void setupClickListeners() {
        todoListView.setOnItemClickListener((parent, view, position, id) -> {
            Todo clickedTodo = adapter.getItem(position);
            if (clickedTodo != null) {
                Intent intent = new Intent(ContactTodosActivity.this, TodoDetailActivity.class);
                intent.putExtra("TODO_ID", clickedTodo.getId());
                intent.putExtra("USE_REMOTE", useRemote);
                detailScreenLauncher.launch(intent);
            }
        });

        sortByDateButton.setOnClickListener(v -> controller.sortByDate());
        favoritesFirstButton.setOnClickListener(v -> controller.sortByImportance());
    }

    @Override
    public void displayTodos(List<Todo> todos) {
        ArrayList<Todo> filteredTodos = new ArrayList<>();
        if (todos != null) {
            for (Todo todo : todos) {
                if (todo.getLinkedContacts() != null && todo.getLinkedContacts().contains(contactId)) {
                    filteredTodos.add(todo);
                }
            }
        }

        adapter = new TodoAdapter(this, filteredTodos, new TodoAdapter.TodoActionListener() {
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
