package com.example.myapplication.db;


import com.example.myapplication.model.Todo;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/todos")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface TodoCRUDAccessor {

    @GET
    @Produces("application/json")
    List<Todo> readAllTodos();

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    Todo createTodo(Todo item);

    @DELETE
    @Path("/{id}")
    boolean deleteTodo(@PathParam("id") long todoId);

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    Todo updateTodo(Todo item);

    @GET
    @Path("/{id}")
    @Produces("application/json")
    Todo readTodo(@PathParam("id") long id);
}