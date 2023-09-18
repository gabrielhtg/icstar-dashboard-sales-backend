package gabrielhtg.icstardashboardsalesbackend.model;

import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequestModel {
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private byte[] profilePicture;

    private Boolean admin;
}
