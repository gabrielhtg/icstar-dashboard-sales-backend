package gabrielhtg.icstardashboardsalesbackend.service;

import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.EditProfileRequestModel;
import gabrielhtg.icstardashboardsalesbackend.model.GetUserResponseModel;
import gabrielhtg.icstardashboardsalesbackend.model.RegisterUserRequestModel;
import gabrielhtg.icstardashboardsalesbackend.repository.ExcelFileRepository;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.security.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    final
    UserRepository userRepository;

    final
    OtherService otherService;

    final ExcelFileRepository excelFileRepository;
    public UserService(UserRepository userRepository, OtherService otherService, ExcelFileRepository excelFileRepository) {
        this.userRepository = userRepository;
        this.otherService = otherService;
        this.excelFileRepository = excelFileRepository;
    }

    // method ini digunakan untuk register user
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

        user.setAdmin(requestModels.getAdmin());
//        user.setSessionToken();
//        user.setSessionTokenActiveUntil();

        userRepository.save(user);

        return true;
    }

    // method ini digunakan untuk remove user
    @Transactional
    public boolean removeUser (String email) {
        User user = userRepository.findById(email).orElse(null);

        excelFileRepository.deleteAllByUserUploader(user);

        if (user != null) {
            userRepository.delete(user);
            return true;
        }

        return false;
    }

    /*
    * Digunakan untuk fitur Remember me
    * Defaultnya user akan tersimpan sessionnya selama 3 hari
    */
    @Transactional
    public void addSessionToken (User user) {
        user.setSessionTokenActiveUntil(otherService.convertMilisToStringWithHour(System.currentTimeMillis() + (1000L * 3600 * 24 * 3)));
    }

    // Menambahkan session time user sebanyak 1 jam
    @Transactional
    public void updateSessionTime (User user) {
        user.setSessionTokenActiveUntil(otherService.convertMilisToStringWithHour(System.currentTimeMillis() + (1000L * 3600)));
    }

    @Transactional
    public boolean cekPassword (User user, String plainPassword) {
        return BCrypt.checkpw(plainPassword, user.getPassword());
    }

    @Transactional
    public String getUserSessionToken (String email) {
        User user = userRepository.findById(email).orElse(null);

        if (user!= null) {
            return user.getSessionToken();
        }

        return null;
    }

    public boolean isSessionTokenActive (User user) {
        /*
        * Akan mengembalikan nilai true apabila token user tidak ditemukan dan tidak aktif lagi
        */

        return user != null && !(otherService.convertDateToMillisWithHour(user.getSessionTokenActiveUntil()) < System.currentTimeMillis());
    }

    public boolean isAdmin (User user) {
        return user != null && user.getAdmin();
    }

    @Transactional
    public boolean isAdmin (String token) {
        User user = userRepository.findBySessionToken(token);

        return user != null && user.getAdmin();
    }

    @Transactional
    public boolean uploadProfilePhoto (User userRegister, MultipartFile file, User userAdmin) {
        try {
            if (userRegister != null && isSessionTokenActive(userAdmin)) {
                userRegister.setProfilePicture(file.getBytes());
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Transactional
    public String getFirstNameByToken (String token) {
        User user = userRepository.findBySessionToken(token);

        if (user != null && isSessionTokenActive(user)) {
            return user.getFirstName();
        }

        return null;
    }

    @Transactional
    public byte[] getProfilePicture (String token) {
        User user = userRepository.findBySessionToken(token);

        if (user != null && isSessionTokenActive(user)) {
            return user.getProfilePicture();
        }

        return null;
    }

    @Transactional
    public boolean editProfile (String token, EditProfileRequestModel requestModel) {
        User user = userRepository.findBySessionToken(token);

        if (user != null && isSessionTokenActive(user)) {
            updateSessionTime(user);

            try{
                user.setPassword(BCrypt.hashpw(requestModel.getPassword(), BCrypt.gensalt()));
            } catch (NullPointerException e) {
                // do nothing :)
            }
            user.setFirstName(requestModel.getFirstName());
            user.setLastName(requestModel.getLastName());

            return true;
        }

        return false;
    }

    @Transactional
    public boolean editProfile (String token, String email, EditProfileRequestModel requestModel) {
        User user = userRepository.findBySessionToken(token);
        User user2 = userRepository.findById(email).orElse(null);

        if (user != null && user2 != null && isSessionTokenActive(user) && isAdmin(user)) {
            updateSessionTime(user);

            try{
                user2.setPassword(BCrypt.hashpw(requestModel.getPassword(), BCrypt.gensalt()));
            } catch (NullPointerException e) {
                // do nothing :)
            }
            user2.setFirstName(requestModel.getFirstName());
            user2.setLastName(requestModel.getLastName());
            user2.setAdmin(requestModel.isAdmin());

            return true;
        }

        return false;
    }

    public List<GetUserResponseModel> getAllUsers (String token) {
        User user = userRepository.findBySessionToken(token);

        if (user != null && isSessionTokenActive(user) && isAdmin(user)) {
            List<User> users = userRepository.findAll();
            List<GetUserResponseModel> userResponseModels = new ArrayList<>();

            for (User u : users) {
                userResponseModels.add(new GetUserResponseModel(u.getEmail(), u.getFirstName(), u.getLastName(), u.getAdmin().toString(), u.getProfilePicture()));
            }

            return userResponseModels;
        }

        return null;
    }

    @Transactional
    public void deleteAllUsers (String password) {
        if (password.equals("12345")) {
            userRepository.deleteAll();
            System.out.println("mama aku dieksekusi");
        }
    }
}
