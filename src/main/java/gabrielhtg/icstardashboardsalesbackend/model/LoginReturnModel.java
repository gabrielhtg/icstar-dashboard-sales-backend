package gabrielhtg.icstardashboardsalesbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginReturnModel {

    private int loginStatus;

    private String email;

    private String loginToken;

    private String firstName;

    private byte[] profilePicture;

    private Boolean admin;

}
