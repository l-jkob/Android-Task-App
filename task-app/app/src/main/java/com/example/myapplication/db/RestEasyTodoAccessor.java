package com.example.myapplication.db;

import android.content.Context;
import com.example.myapplication.model.Todo;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class RestEasyTodoAccessor implements TodoCRUDAccessor {

    private final TodoCRUDAccessor proxy;
    private RoomTodoDAO todoDao;
    private final ExecutorService executorService = java.util.concurrent.Executors.newSingleThreadExecutor();

    public RestEasyTodoAccessor(String baseUrl) {
        this(baseUrl, null);
    }

    public RestEasyTodoAccessor(String baseUrl, Context context) {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, 5000);

        ConnManagerParams.setMaxTotalConnections(params, 100);

        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(100);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, registry);

        DefaultHttpClient httpClient = new DefaultHttpClient(cm, params);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient);

        this.proxy = ProxyFactory.create(
                com.example.myapplication.db.TodoCRUDAccessor.class,
                baseUrl,
                executor
        );
        
        if (context != null) {
            this.todoDao = AppDatabase.getInstance(context).todoDao();
        }
    }

    private void runRemote(final Runnable action) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    action.run();
                } catch (Exception e) {
                    android.util.Log.e("RestEasyTodoAccessor", "Remote sync failed: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public Todo createTodo(final Todo item) {
        if (todoDao != null) {
            long insertId = todoDao.insert(item);
            item.setId(insertId);
        }
        runRemote(new Runnable() {
            @Override
            public void run() {
                proxy.createTodo(item);
            }
        });
        return item;
    }

    @Override
    public List<Todo> readAllTodos() {
        if (todoDao != null) {
            return todoDao.getAllTodos();
        }
        return proxy.readAllTodos();
    }

    @Override
    public Todo readTodo(long id) {
        if (todoDao != null) {
            return todoDao.getTodoById(id);
        }
        return null;
    }

    @Override
    public Todo updateTodo(final Todo item) {
        if (todoDao != null) {
            todoDao.update(item);
        }
        runRemote(new Runnable() {
            @Override
            public void run() {
                proxy.updateTodo(item);
            }
        });
        return item;
    }

    @Override
    public boolean deleteTodo(final long id) {
        if (todoDao != null) {
            Todo localTodo = todoDao.getTodoById(id);
            if (localTodo != null) {
                todoDao.delete(localTodo);
            }
        }
        runRemote(new Runnable() {
            @Override
            public void run() {
                proxy.deleteTodo(id);
            }
        });
        return true;
    }
}