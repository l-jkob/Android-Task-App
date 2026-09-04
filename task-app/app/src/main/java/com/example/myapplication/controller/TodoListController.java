package com.example.myapplication.controller;

import android.os.Handler;
import com.example.myapplication.db.TodoCRUDAccessor;
import com.example.myapplication.ui.todolist.TodoListView;
import com.example.myapplication.model.Todo;

import java.util.List;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoListController {
    private TodoCRUDAccessor model;
    private TodoListView view;
    private List<Todo> currentTodos;

    private int activeSortMode = 1;
    private final int dateFirst = 1;
    private final int importanceFirst = 2;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(android.os.Looper.getMainLooper());

    public TodoListController(TodoCRUDAccessor model, TodoListView view) {
        this.model = model;
        this.view = view;
    }

    public void loadTodos() {
        executor.execute(() -> {
            List<Todo> todos = model.readAllTodos();
            mainHandler.post(() -> {
                this.currentTodos = todos;
                if (activeSortMode == dateFirst) {
                    applySortByDate();
                } else {
                    applySortByImportance();
                }
            });
        });
    }

    public void toggleTodoDone(Todo todo, boolean isDone) {
        todo.setDone(isDone);
        executor.execute(() -> {
            model.updateTodo(todo);
            mainHandler.post(() -> {
                loadTodos();
            });
        });
    }

    public void toggleTodoFavorite(Todo todo, boolean isFavorite) {
        todo.setFavorite(isFavorite);
        executor.execute(() -> {
            model.updateTodo(todo);
            mainHandler.post(() -> {
                loadTodos();
            });
        });
    }

    public void sortByDate() {
        this.activeSortMode = 1;
        if (currentTodos == null) return;
        applySortByDate();
    }

    public void sortByImportance() {
        this.activeSortMode = 2;
        if (currentTodos == null) return;
        applySortByImportance();
    }

    private long getTodoTimestamp(Todo todo) {
        String date = todo.getDueDate();
        if (date == null || date.isEmpty()) {
            return Long.MAX_VALUE;
        }
        String time = todo.getDueTime();
        if (time == null || time.isEmpty()) {
            time = "11:59 PM";
        }
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MM/dd/yyyy h:mm a", java.util.Locale.US);
            java.util.Date d = format.parse(date + " " + time);
            return d != null ? d.getTime() : Long.MAX_VALUE;
        } catch (java.text.ParseException e) {
            return Long.MAX_VALUE;
        }
    }

    private void applySortByDate() {
        Collections.sort(currentTodos, new java.util.Comparator<Todo>() {
            @Override
            public int compare(Todo firstTodo, Todo secondTodo) {
                int doneComparison = Boolean.compare(firstTodo.isDone(), secondTodo.isDone());
                if (doneComparison != 0) return doneComparison;

                long t1 = getTodoTimestamp(firstTodo);
                long t2 = getTodoTimestamp(secondTodo);

                if (t1 == Long.MAX_VALUE && t2 == Long.MAX_VALUE) {
                    return Boolean.compare(secondTodo.isFavorite(), firstTodo.isFavorite());
                }
                
                int dateComparison = Long.compare(t1, t2);
                if (dateComparison != 0) return dateComparison;

                return Boolean.compare(secondTodo.isFavorite(), firstTodo.isFavorite());
            }
        });
        view.displayTodos(currentTodos);
    }

    private void applySortByImportance() {
        Collections.sort(currentTodos, new java.util.Comparator<Todo>() {
            @Override
            public int compare(Todo firstTodo, Todo secondTodo) {
                int doneComparison = Boolean.compare(firstTodo.isDone(), secondTodo.isDone());
                if (doneComparison != 0) return doneComparison;

                int favoriteComparison = Boolean.compare(secondTodo.isFavorite(), firstTodo.isFavorite());
                if (favoriteComparison != 0) return favoriteComparison;

                long t1 = getTodoTimestamp(firstTodo);
                long t2 = getTodoTimestamp(secondTodo);

                return Long.compare(t1, t2);
            }
        });
        view.displayTodos(currentTodos);
    }
}