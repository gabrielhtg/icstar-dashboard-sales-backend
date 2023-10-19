package gabrielhtg.icstardashboardsalesbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRequestModel {
    private String password;

    private String firstName;

    private String lastName;

    private boolean admin;
}
