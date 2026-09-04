package com.example.myapplication.db;

import com.example.myapplication.model.Users;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/login")
public interface UserCheck {
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    Response checkUser(Users credentials);
}
