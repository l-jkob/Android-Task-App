package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.myapplication.model.Todo;
import java.util.List;

@Dao
public interface RoomTodoDAO {

    @Insert
    long insert(Todo todo);

    @Delete
    void delete(Todo todo);

    @Update
    void update(Todo todo);

    @Query("SELECT * FROM todos")
    List<Todo> getAllTodos();

    @Query("SELECT * FROM todos WHERE id = :id")
    Todo getTodoById(long id);

    @Query("DELETE FROM todos")
    void deleteAllTodos();
}
