package gabrielhtg.icstardashboardsalesbackend.service;

import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.RegisterUserRequestModel;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import jakarta.persistence.OrderBy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Test
    void testRegisterUserBerhasil() {
        RegisterUserRequestModel requestModel = new RegisterUserRequestModel();
        requestModel.setEmail("gabrielhutagalung970@gmail.com");
        requestModel.setPassword("test");
        requestModel.setFirstName("Gabriel");
        requestModel.setLastName("Hutagalung");
        requestModel.setProfilePicture(null);
        requestModel.setAdmin(true);

        Assertions.assertTrue(userService.registerUser(requestModel));

        // remove user
        User user = userRepository.findById(requestModel.getEmail()).orElse(null);

        Assertions.assertNotNull(user);
        userRepository.delete(user);

        // cek apakah user sudah berhasil diremove atau tidak
        Assertions.assertNull(userRepository.findById(requestModel.getEmail()).orElse(null));
    }

    @Test
    void testRegisterUserGagalAlreadyExist () {
        RegisterUserRequestModel requestModel = new RegisterUserRequestModel();
        requestModel.setEmail("gabrielhutagalung970@gmail.com");
        requestModel.setPassword("test");
        requestModel.setFirstName("Gabriel");
        requestModel.setLastName("Hutagalung");
        requestModel.setProfilePicture(null);
        requestModel.setAdmin(true);

        // cek apakah user berhasil register
        Assertions.assertTrue(userService.registerUser(requestModel));

        // mencoba untuk register user ulang
        Assertions.assertFalse(userService.registerUser(requestModel));

        // remove user
        User user = userRepository.findById(requestModel.getEmail()).orElse(null);

        Assertions.assertNotNull(user);
        userRepository.delete(user);

        // cek apakah user sudah berhasil diremove atau tidak
        Assertions.assertNull(userRepository.findById(requestModel.getEmail()).orElse(null));
    }

    @Test
    void testRemoveUserBerhasil() {
        RegisterUserRequestModel requestModel = new RegisterUserRequestModel();
        requestModel.setEmail("gabrielhutagalung970@gmail.com");
        requestModel.setPassword("test");
        requestModel.setFirstName("Gabriel");
        requestModel.setLastName("Hutagalung");
        requestModel.setProfilePicture(null);
        requestModel.setAdmin(true);

        // cek apakah user berhasil didaftarkan atau tidak
        Assertions.assertTrue(userService.registerUser(requestModel));

        // mencoba untuk remove user
        Assertions.assertTrue(userService.removeUser(requestModel.getEmail()));
    }

    @Test
    void testRemoveUserGagal () {
        RegisterUserRequestModel requestModel = new RegisterUserRequestModel();
        requestModel.setEmail("gabrielhutagalung970@gmail.com");
        requestModel.setPassword("test");
        requestModel.setFirstName("Gabriel");
        requestModel.setLastName("Hutagalung");
        requestModel.setProfilePicture(null);
        requestModel.setAdmin(true);

        // mencoba untuk menghapus user yang tidak ada
        Assertions.assertFalse(userService.removeUser(requestModel.getEmail()));
    }
}
