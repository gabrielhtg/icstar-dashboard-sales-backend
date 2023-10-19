package gabrielhtg.icstardashboardsalesbackend.model;

import lombok.Data;

@Data
public class RegisterResponseModel {
    private String registerMsg;

    private String email;

    private String firstName;

    private String lastName;

    private boolean isAdmin;
}
