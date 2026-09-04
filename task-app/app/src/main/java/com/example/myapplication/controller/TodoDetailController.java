package com.example.myapplication.controller;

import com.example.myapplication.db.TodoCRUDAccessor;
import com.example.myapplication.model.Todo;
import com.example.myapplication.ui.tododetail.TodoDetailView;

import java.util.concurrent.ExecutorService;

public class TodoDetailController {

    private TodoCRUDAccessor model;
    private TodoDetailView view;

    private final ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
    private final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    public TodoDetailController(TodoCRUDAccessor model, TodoDetailView view) {
        this.model = model;
        this.view = view;
    }

    public void loadTodo(long id) {
        if (id == -1) {
            // ID -1 means "Add" button was clicked/pressed
            view.showTodoData(new Todo());
        } else {
            executor.execute(() -> {
                Todo todo = model.readTodo(id);
                mainHandler.post(() -> {
                    if (todo != null) {
                        view.showTodoData(todo);
                    } else {
                        view.showMessage("Error loading Todo");
                        view.closeScreen();
                    }
                });
            });
        }
    }

    public void saveTodo(Todo todo) {
        if (todo.getName() == null || todo.getName().trim().isEmpty()) {
            view.showMessage("Name cannot be empty");
            return;
        }
        executor.execute(() -> {
            if (todo.getId() == 0) {
                model.createTodo(todo);
                mainHandler.post(() -> {
                    view.showMessage("Todo created");
                    view.closeScreen();
                });
            } else {
                model.updateTodo(todo);
                mainHandler.post(() -> {
                    view.showMessage("Todo updated");
                    view.closeScreen();
                });
            }
        });
    }

    public void deleteTodo(long id) {
        if (id != 0) {
            executor.execute(() -> {
                model.deleteTodo(id);
                mainHandler.post(() -> {
                    view.showMessage("Todo deleted");
                    view.closeScreen();
                });
            });
        } else {
            view.closeScreen();
        }
    }
}

