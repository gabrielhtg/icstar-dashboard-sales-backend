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
    public boolean login (LoginRequestModel requestModel) {
        User user = userRepository.findById(requestModel.getEmail()).orElse(null);

        // jika user ditemukan, maka lanjut cek password
        return user != null && BCrypt.checkpw(requestModel.getPassword(), user.getPassword());
    }
}
