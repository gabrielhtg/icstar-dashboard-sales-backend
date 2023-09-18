package gabrielhtg.icstardashboardsalesbackend.service;

import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.LoginRequestModel;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.security.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;

    @Transactional
    public int login (LoginRequestModel requestModel) {
        /*
        * Return 1 - Login Success
        * Return 2 - Wrong Credentials
        * Return 3 - User not found
        */

        User user = userRepository.findById(requestModel.getEmail()).orElse(null);

        // jika user ditemukan, maka lanjut cek password
        if (user == null) {
            return 3;
        }

        else if (BCrypt.checkpw(requestModel.getPassword(), user.getPassword())) {
            return 1;
        }

        return 2;
    }
}
