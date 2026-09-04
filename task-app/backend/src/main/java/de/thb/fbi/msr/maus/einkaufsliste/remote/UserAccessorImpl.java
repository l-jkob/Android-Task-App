package de.thb.fbi.msr.maus.einkaufsliste.remote;

import de.thb.fbi.msr.maus.einkaufsliste.model.UserCheck;
import de.thb.fbi.msr.maus.einkaufsliste.model.Users;
import javax.ws.rs.core.Response;

public class UserAccessorImpl implements UserCheck {

    @Override
    public Response checkUser(Users credentials) {
        boolean isValidUser = "a@a.a".equals(credentials.getEmail()) && "123456".equals(credentials.getPassword());
        if (isValidUser) {
            return Response.ok("{\"success\": true}").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"success\": false, \"message\": \"Invalid email or password.\"}")
                    .build();
        }
    }
}
