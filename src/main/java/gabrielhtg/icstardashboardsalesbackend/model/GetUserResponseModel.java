package gabrielhtg.icstardashboardsalesbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponseModel {
    private String email;

    private String firstName;

    private String lastName;

    private String admin;

    private byte[] profilePicture;
}
