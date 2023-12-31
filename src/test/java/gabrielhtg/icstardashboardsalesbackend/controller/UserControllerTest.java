package gabrielhtg.icstardashboardsalesbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.RegisterUserRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.WebResponse;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    
    // Memastikan usertest tidak ada di database sebelum API ditest
    @BeforeEach
    void setUp() {
        String emailUserTest = "usertest@example.com";
        User user = userRepository.findById(emailUserTest).orElse(null);

        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Test
    void testRegisterUserAPISuccess() throws Exception {
        String emailUserTest = "usertest@example.com";
        RegisterUserRequestModel requestModel = new RegisterUserRequestModel();
        requestModel.setEmail(emailUserTest);
        requestModel.setPassword("agustus163");
        requestModel.setFirstName("User");
        requestModel.setLastName("Test");
        requestModel.setAdmin(true);

        mockMvc.perform(
                post("/api/register-user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            Assertions.assertNotNull(result);
            Assertions.assertEquals("Register Success", result.getResponse().getContentAsString());
        });
    }

    @Test
    void testRegisterUserAPIFailed() throws Exception{
        String emailUserTest = "usertest@example.com";
        RegisterUserRequestModel requestModel = new RegisterUserRequestModel();
        requestModel.setEmail(emailUserTest);
        requestModel.setPassword("agustus163");
        requestModel.setFirstName("User");
        requestModel.setLastName("Test");
        requestModel.setAdmin(true);

        userService.registerUser(requestModel);

        mockMvc.perform(
                post("/api/register-user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel))
        ).andExpectAll(
                status().is(400)
        ).andDo(result -> {
            Assertions.assertNotNull(result);
            Assertions.assertEquals("Register Failed! User Exist", result.getResponse().getContentAsString());
        });
    }

    @Test
    void testRemoveUserSuccess() throws Exception {
        String emailUserTest = "usertest@example.com";
        RegisterUserRequestModel requestModel = new RegisterUserRequestModel();
        requestModel.setEmail(emailUserTest);
        requestModel.setPassword("agustus163");
        requestModel.setFirstName("User");
        requestModel.setLastName("Test");
        requestModel.setAdmin(true);

        userService.registerUser(requestModel);

        mockMvc.perform(
                delete("/api/remove-user")
                        .header("email", requestModel.getEmail())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            Assertions.assertNotNull(result);
            Assertions.assertEquals("Remove User Success", result.getResponse().getContentAsString());
        });
    }

    @Test
    void testRemoveUserFailed() throws Exception {
        String emailUserTest = "usertest@example.com";

        mockMvc.perform(
                delete("/api/remove-user")
                        .header("email", emailUserTest)
        ).andExpectAll(
                status().is(404)
        ).andDo(result -> {
            Assertions.assertNotNull(result);
            Assertions.assertEquals("Remove User Failed! User not found", result.getResponse().getContentAsString());

        });
    }
}
