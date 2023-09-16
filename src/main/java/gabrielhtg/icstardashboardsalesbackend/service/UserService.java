package gabrielhtg.icstardashboardsalesbackend.service;

import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.RegisterUserRequestModel;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.security.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Transactional
    public boolean registerUser (RegisterUserRequestModel requestModels) {
        if (userRepository.existsById(requestModels.getEmail())) {
            return false;
        }

        User user = new User();
        user.setEmail(requestModels.getEmail());
        user.setPassword(BCrypt.hashpw(requestModels.getPassword(), BCrypt.gensalt()));
        user.setFirstName(requestModels.getFirstName());
        user.setLastName(requestModels.getLastName());
        user.setProfilePicture(requestModels.getProfilePicture());

        if (requestModels.getAdmin() == null) {
            user.setAdmin(false);
        }

        user.setAdmin(true);
        user.setUserToken(UUID.randomUUID().toString());

        userRepository.save(user);

        return true;
    }

    @Transactional
    public boolean removeUser (String email) {
        User user = userRepository.findById(email).orElse(null);

        if (user != null) {
            userRepository.delete(user);
            return true;
        }

        return false;
    }

}
