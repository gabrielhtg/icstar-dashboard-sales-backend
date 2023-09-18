package gabrielhtg.icstardashboardsalesbackend.service;

import gabrielhtg.icstardashboardsalesbackend.model.LoginRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.RegisterUserRequestModel;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthServiceTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testLoginSuccess() {
        String emailUserTest = "usertest@example.com";
        RegisterUserRequestModel registerUserRequestModel = new RegisterUserRequestModel();
        registerUserRequestModel.setEmail(emailUserTest);
        registerUserRequestModel.setPassword("testpw");
        registerUserRequestModel.setFirstName("User");
        registerUserRequestModel.setLastName("Test");
        registerUserRequestModel.setAdmin(true);

        userService.registerUser(registerUserRequestModel);

        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setEmail(registerUserRequestModel.getEmail());
        loginRequestModel.setPassword("testpw");

        Assertions.assertEquals(1, authService.login(loginRequestModel));
    }

    @Test
    void testLoginFailedWrongCredentials() {
        String emailUserTest = "usertest@example.com";
        RegisterUserRequestModel registerUserRequestModel = new RegisterUserRequestModel();
        registerUserRequestModel.setEmail(emailUserTest);
        registerUserRequestModel.setPassword("testpw");
        registerUserRequestModel.setFirstName("User");
        registerUserRequestModel.setLastName("Test");
        registerUserRequestModel.setAdmin(true);

        userService.registerUser(registerUserRequestModel);

        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setEmail(registerUserRequestModel.getEmail());
        loginRequestModel.setPassword("testpwsalah");

        Assertions.assertEquals(2, authService.login(loginRequestModel));
    }

    @Test
    void testLoginFailedUserNotFound() {
        String emailUserTest = "usertest@example.com";
        RegisterUserRequestModel registerUserRequestModel = new RegisterUserRequestModel();
        registerUserRequestModel.setEmail(emailUserTest);
        registerUserRequestModel.setPassword("testpw");
        registerUserRequestModel.setFirstName("User");
        registerUserRequestModel.setLastName("Test");
        registerUserRequestModel.setAdmin(true);

        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setEmail(registerUserRequestModel.getEmail());
        loginRequestModel.setPassword(registerUserRequestModel.getPassword());

        Assertions.assertEquals(3, authService.login(loginRequestModel));
    }
}
