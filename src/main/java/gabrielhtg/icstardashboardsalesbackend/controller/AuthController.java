package gabrielhtg.icstardashboardsalesbackend.controller;

import gabrielhtg.icstardashboardsalesbackend.model.LoginRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.WebResponse;
import gabrielhtg.icstardashboardsalesbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping (
            path = "/api/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> login (@RequestBody LoginRequestModel requestModel) {
        if (authService.login(requestModel)) {
            return WebResponse.<String>builder().data("ok").error(false).build();
        }

        return WebResponse.<String>builder().error(true).errorMsg("Login Failed").build();
    }
}
