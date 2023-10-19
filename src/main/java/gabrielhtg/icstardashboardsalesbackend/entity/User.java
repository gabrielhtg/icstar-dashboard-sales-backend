package gabrielhtg.icstardashboardsalesbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @Column(name = "first_name")
    private String firstName;

    @JsonIgnore
    @Column(name = "last_name")
    private String lastName;

    @JsonIgnore
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @JsonIgnore
    private Boolean admin;

    @JsonIgnore
    @Column(name = "session_token")
    private String sessionToken;

    @JsonIgnore
    @Column(name = "session_token_active_until")
    private String sessionTokenActiveUntil;
}
