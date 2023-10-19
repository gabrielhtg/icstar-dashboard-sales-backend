package gabrielhtg.icstardashboardsalesbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.LoginRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.LoginResponseModel;
import gabrielhtg.icstardashboardsalesbackend.model.LoginReturnModel;
import gabrielhtg.icstardashboardsalesbackend.model.WebResponse;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.service.AuthService;
import gabrielhtg.icstardashboardsalesbackend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    final private AuthService authService;

    final private UserRepository userRepository;

    final private ObjectMapper objectMapper;

    final private UserService userService;

    public AuthController(AuthService authService, UserRepository userRepository, ObjectMapper objectMapper, UserService userService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @PostMapping (
            path = "/api/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> login (@RequestBody LoginRequestModel requestModel, HttpServletResponse response) throws JsonProcessingException {
        LoginReturnModel loginReturnModel = authService.login(requestModel);

        // Login success
        if (loginReturnModel.getLoginStatus() == 1) {

            Cookie tokenCookie = new Cookie("token", loginReturnModel.getLoginToken());

            tokenCookie.setMaxAge(3600 * 24 * 3);
            tokenCookie.setPath("/");

            response.addCookie(tokenCookie);

            return ResponseEntity.ok(objectMapper.writeValueAsString(WebResponse.builder().success(true).message(null).data(null).build()));
        }

        // Wrong credentials
        else if (loginReturnModel.getLoginStatus() == 2) {
            return ResponseEntity.status(401).body(objectMapper.writeValueAsString(new WebResponse<>(false, "Wrong Credentials", null)));
        }

        return ResponseEntity.status(400).body(objectMapper.writeValueAsString(new WebResponse<>(false, "User Not Found", null)));
    }

    @DeleteMapping(
            path = "/api/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> logout (@CookieValue(name = "token") String token) throws JsonProcessingException {
        User user = userRepository.findBySessionToken(token);

        try {
            authService.logout(user);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Already Logout", null)));
        }

        return ResponseEntity.ok("Logout Success");
    }

    @GetMapping(
            path = "/api/islogin"
    )
    public ResponseEntity<String> isLogin (@CookieValue(name = "token") String token) {
        User user = userRepository.findBySessionToken(token);

        if (userService.isSessionTokenActive(user)) {
            return ResponseEntity.ok("");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }
}
