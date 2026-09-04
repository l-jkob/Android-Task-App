package com.example.myapplication.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.myapplication.model.Todo;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncEngine {

    private LocalTodoAccessor localAccessor;
    private RestEasyTodoAccessor remoteAccessor;

    public interface SyncCallback {
        void onSyncComplete();
        void onOfflineMode();
    }

    public SyncEngine(Context context) {
        this.localAccessor = new LocalTodoAccessor(context);
        String baseUrl = "http://10.0.2.2:8080/backend-1.0-SNAPSHOT/rest";
        this.remoteAccessor = new RestEasyTodoAccessor(baseUrl);
    }

    public void synchronize(SyncCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            List<Todo> remoteTodos;
            try {
                remoteTodos = remoteAccessor.readAllTodos();
            } catch (Exception e) {
                android.util.Log.e("SyncEngine", "Server connection failed: " + e.getMessage(), e);
                mainThreadHandler.post(() -> callback.onOfflineMode());
                return;
            }


            List<Todo> localTodos = localAccessor.readAllTodos();

            if (!localTodos.isEmpty()) {

                for (Todo remoteTodo : remoteTodos) {
                    remoteAccessor.deleteTodo(remoteTodo.getId());
                }

                for (Todo localTodo : localTodos) {
                    remoteAccessor.createTodo(localTodo);
                }

            } else {

                for (Todo remoteTodo : remoteTodos) {
                    localAccessor.createTodo(remoteTodo);
                }
            }
            mainThreadHandler.post(() -> callback.onSyncComplete());
        });
    }
}