package com.example.myapplication.ui.todolist;

import com.example.myapplication.model.Todo;
import java.util.List;

public interface TodoListView {
    void displayTodos(List<Todo> todos);

    void showPopup(String message);
}

