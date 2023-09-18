package gabrielhtg.icstardashboardsalesbackend.controller;

import gabrielhtg.icstardashboardsalesbackend.model.RegisterUserRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.WebResponse;
import gabrielhtg.icstardashboardsalesbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping (
            path = "/api/register-user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<String> registerUser (@RequestBody RegisterUserRequestModel requestModel) {
        if (userService.registerUser(requestModel)) {
            return ResponseEntity.ok("Register Success");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Register Failed! User Exist");
    }

    @DeleteMapping (
            path = "/api/remove-user"
    )
    private ResponseEntity<String> removeUser (@RequestHeader(name = "email") String emailToRemove) {
        if (userService.removeUser(emailToRemove)) {
            return ResponseEntity.ok("Remove User Success");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Remove User Failed! User not found");
    }

}
