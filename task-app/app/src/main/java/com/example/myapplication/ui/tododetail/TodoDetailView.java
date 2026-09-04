package com.example.myapplication.ui.tododetail;

import com.example.myapplication.model.Todo;

public interface TodoDetailView {
    void showTodoData(Todo todo);
    void closeScreen();
    void showMessage(String message);
}
