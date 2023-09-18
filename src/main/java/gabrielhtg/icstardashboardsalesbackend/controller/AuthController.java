package gabrielhtg.icstardashboardsalesbackend.controller;

import gabrielhtg.icstardashboardsalesbackend.model.LoginRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.WebResponse;
import gabrielhtg.icstardashboardsalesbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
public class AuthController {
    final
    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping (
            path = "/api/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> login (@RequestBody LoginRequestModel requestModel) {
        int loginStatus = authService.login(requestModel);

        // Login success
        if (loginStatus == 1) {
            return ResponseEntity.ok("Login Success");
        }

        // Wrong credentials
        else if (loginStatus == 2) {
            return ResponseEntity.status(401).body("Wrong Credentials");
        }

        return ResponseEntity.status(400).body("User Not Found");
    }
}
