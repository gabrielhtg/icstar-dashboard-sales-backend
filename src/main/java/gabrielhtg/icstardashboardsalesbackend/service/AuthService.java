package gabrielhtg.icstardashboardsalesbackend.service;

import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.LoginRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.LoginReturnModel;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.security.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service    
public class AuthService {
    final
    UserRepository userRepository;

    final
    UserService userService;

    public AuthService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public LoginReturnModel login(LoginRequestModel requestModel) {
        /*
         * Return 1 - Login Success
         * Return 2 - Wrong Credentials
         * Return 3 - User not found
         */

        User user = userRepository.findById(requestModel.getEmail()).orElse(null);

        if (user == null) {
            return new LoginReturnModel(3, null, null, null, null, null);
        } else if (BCrypt.checkpw(requestModel.getPassword(), user.getPassword())) {
            user.setSessionToken(UUID.randomUUID().toString());

            if (requestModel.isRememberMe()) {
                userService.addSessionToken(user);
            } else {
                userService.updateSessionTime(user);
            }

            return new LoginReturnModel(1, user.getEmail(), user.getSessionToken(), user.getFirstName(), user.getProfilePicture(), user.getAdmin());
        }

        return new LoginReturnModel(2, null, null, null, null, null);
    }

    @Transactional
    public void logout(User user) {
        user.setSessionTokenActiveUntil(null);
        user.setSessionToken(null);
    }
}
