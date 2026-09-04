package com.example.myapplication.db;

import com.example.myapplication.model.Users;
import org.jboss.resteasy.client.ProxyFactory;
import javax.ws.rs.core.Response;

public class LoginImpl implements UserCheck {
    private final UserCheck remoteLoginAccessor;

    public LoginImpl(String baseUrl) {
        remoteLoginAccessor = ProxyFactory.create(UserCheck.class, baseUrl);
    }

    @Override
    public Response checkUser(Users credentials) {
        return remoteLoginAccessor.checkUser(credentials);
    }
}
