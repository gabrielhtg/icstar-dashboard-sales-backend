package gabrielhtg.icstardashboardsalesbackend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabrielhtg.icstardashboardsalesbackend.model.LoginRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.RegisterUserRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.WebResponse;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Test
    void testLoginAPISuccess() throws  Exception {
        String emailUserTest = "usertest@example.com";
        RegisterUserRequestModel registerUserRequestModel = new RegisterUserRequestModel();
        registerUserRequestModel.setEmail(emailUserTest);
        registerUserRequestModel.setPassword("testpw");
        registerUserRequestModel.setFirstName("User");
        registerUserRequestModel.setLastName("Test");
        registerUserRequestModel.setAdmin(true);

        userService.registerUser(registerUserRequestModel);

        LoginRequestModel requestModel = new LoginRequestModel();
        requestModel.setEmail(registerUserRequestModel.getEmail());
        requestModel.setPassword(registerUserRequestModel.getPassword());

        mockMvc.perform(
                post("/api/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            Assertions.assertNotNull(result);
            Assertions.assertEquals("Login Success", result.getResponse().getContentAsString());
        });
    }

    @Test
    void testLoginAPIFailedPasswordSalah() throws  Exception {
        String emailUserTest = "usertest@example.com";
        RegisterUserRequestModel registerUserRequestModel = new RegisterUserRequestModel();
        registerUserRequestModel.setEmail(emailUserTest);
        registerUserRequestModel.setPassword("testpw");
        registerUserRequestModel.setFirstName("User");
        registerUserRequestModel.setLastName("Test");
        registerUserRequestModel.setAdmin(true);

        userService.registerUser(registerUserRequestModel);

        LoginRequestModel requestModel = new LoginRequestModel();
        requestModel.setEmail(registerUserRequestModel.getEmail());
        requestModel.setPassword("ini password salah :v");

        mockMvc.perform(
                post("/api/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel))
        ).andExpectAll(
                status().is(401)
        ).andDo(result -> {
            Assertions.assertNotNull(result);
            Assertions.assertEquals("Wrong Credentials", result.getResponse().getContentAsString());

        });
    }

    @Test
    void testLoginAPIFailedUserTidakDitemukan() throws  Exception {
        LoginRequestModel requestModel = new LoginRequestModel();
        requestModel.setEmail("lidahbuaya@gmail.com");
        requestModel.setPassword("ini password salah :v");

        mockMvc.perform(
                post("/api/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel))
        ).andExpectAll(
                status().is(400)
        ).andDo(result -> {
            Assertions.assertNotNull(result);
            Assertions.assertEquals("User Not Found", result.getResponse().getContentAsString());
        });
    }
}
