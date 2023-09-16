package gabrielhtg.icstardashboardsalesbackend.controller;

import gabrielhtg.icstardashboardsalesbackend.model.RegisterUserRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.WebResponse;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping (
            path = "/api/register-user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private WebResponse<String> registerUser (@RequestBody RegisterUserRequestModel requestModel) {
        if (userService.registerUser(requestModel)) {
            return WebResponse.<String>builder().data("ok").error(false).build();
        }

        return WebResponse.<String>builder().errorMsg("RegisterUser Failed").error(true).build();
    }

    @DeleteMapping (
            path = "/api/remove-user"
    )
    private WebResponse<String> removeUser (@RequestHeader(name = "email") String emailToRemove) {
        if (userService.removeUser(emailToRemove)) {
            return WebResponse.<String>builder().data("ok").error(false).build();
        }

        return WebResponse.<String>builder().error(true).errorMsg("RemoveUser Failed").build();
    }

}
