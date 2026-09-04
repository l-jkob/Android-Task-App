package com.example.myapplication.db;

import android.content.Context;
import com.example.myapplication.model.Todo;
import java.util.List;

public class LocalTodoAccessor implements TodoCRUDAccessor {

    private RoomTodoDAO todoDao;

    public LocalTodoAccessor(Context context) {
        todoDao = AppDatabase.getInstance(context).todoDao();
    }

    @Override
    public Todo createTodo(Todo item) {
        long insertId = todoDao.insert(item);
        item.setId(insertId);
        return item;
    }

    @Override
    public Todo readTodo(long id) {
        return todoDao.getTodoById(id);
    }

    @Override
    public List<Todo> readAllTodos() {
        return todoDao.getAllTodos();
    }

    @Override
    public Todo updateTodo(Todo item) {
        todoDao.update(item);
        return item;
    }

    @Override
    public boolean deleteTodo(long id) {
        Todo item = todoDao.getTodoById(id);
        if (item != null) {
            todoDao.delete(item);
            return true;
        }
        return false;
    }
}