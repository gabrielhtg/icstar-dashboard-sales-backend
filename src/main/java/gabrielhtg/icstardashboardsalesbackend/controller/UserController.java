package gabrielhtg.icstardashboardsalesbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.*;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    public UserController(UserService userService, UserRepository userRepository, ObjectMapper objectMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping (
            path = "/api/register-user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> registerUser (@CookieValue(name = "token") String token, @RequestBody RegisterUserRequestModel requestModel) throws JsonProcessingException {
        User user = userRepository.findBySessionToken(token);

        if (userService.isAdmin(user) && userService.isSessionTokenActive(user) && userService.registerUser(requestModel)) {
            RegisterResponseModel registerResponseModel = new RegisterResponseModel();
            registerResponseModel.setRegisterMsg("Register Success");
            registerResponseModel.setEmail(requestModel.getEmail());
            registerResponseModel.setFirstName(requestModel.getFirstName());
            registerResponseModel.setLastName(requestModel.getLastName());
            registerResponseModel.setAdmin(requestModel.getAdmin());

            return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, null, registerResponseModel)));
        }

        else if (!userService.isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Register Failed! You're Not Admin", null)));
        }

        else if (!userService.isSessionTokenActive(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Register Failed! Invalid Token", null)));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Register Failed! User Exist", null)));
    }

    @DeleteMapping (
            path = "/api/remove-user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> removeUser (@CookieValue(name = "token") String token, @RequestHeader(name = "email") String emailToRemove) throws JsonProcessingException {
        User user = userRepository.findBySessionToken(token);

        try {
            String userEmail = user.getEmail();

            if (userService.isAdmin(user) && userService.isSessionTokenActive(user) && userService.removeUser(emailToRemove)) {
                return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<Void>(
                        true,
                        String.format("Remove user %s success!", userEmail),
                        null
                )));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Remove User Failed! User not found", null)));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Invalid Token", null)));
        }
    }

//    @GetMapping(
//            path = "/api/get-all-users",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<String> getAllUsers (@RequestHeader(name = "dev-pass") String password) throws JsonProcessingException {
//        if (password.equals("12345")) {
//            List<User> users = userRepository.findAll();
//
//            StringBuilder stringBuilder = new StringBuilder(objectMapper.writeValueAsString(users.get(0)));
//
//            for (int i = 1; i < users.size(); i++) {
//                stringBuilder.append("\n").append(objectMapper.writeValueAsString(users.get(i)));
//            }
//
//            return ResponseEntity.ok(stringBuilder.toString());
//        }
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed");
//    }

//    @PostMapping (
//            path = "/api/upload-profile-picture",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<String> uploadProfilePicture (@CookieValue(name = "token") String token, MultipartFile fotoProfil) throws JsonProcessingException {
//        User user = userRepository.findBySessionToken(token);
//
//        if (userService.isAdmin(user) && userService.isSessionTokenActive(user) && userService.uploadProfilePhoto(user, fotoProfil)) {
//            return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<RegisterResponseModel>(true, "Success", null)));
//        }
//
//        else if (userService.isAdmin(user)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Upload Failed! Unauthorized", null)));
//        }
//
//        else if (userService.isSessionTokenActive(user)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Upload Failed! Invalid Token", null)));
//        }
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Upload Failed! User Exist", null)));
//    }

    @PostMapping (
            path = "/api/register-profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> registerProfilePicture (@CookieValue(name = "token") String token, MultipartFile fotoProfil, @RequestHeader(name = "email") String email) throws JsonProcessingException {
        User user = userRepository.findBySessionToken(token);

        if (userService.isAdmin(user) && userService.isSessionTokenActive(user)) {
            User userTujuan = userRepository.findById(email).orElse(null);

            if (userTujuan != null && userService.uploadProfilePhoto(userTujuan, fotoProfil, user)) {
                return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<RegisterResponseModel>(true, "Success", null)));
            }
        }

        else if (!userService.isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Upload Failed! You're not admin", null)));
        }

        else if (userService.isSessionTokenActive(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Upload Failed! Invalid Token", null)));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Upload Failed! User Exist", null)));
    }

    @GetMapping (
                path = "/api/get-firstname-by-token",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getFirstNameByToken (@CookieValue(name = "token") String token) throws JsonProcessingException {
        System.out.println(token);
        String temp = userService.getFirstNameByToken(token);

        if (temp != null) {
            return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "Success", temp)));
        }

        return ResponseEntity.status(404).body(objectMapper.writeValueAsString(new WebResponse<>(false, "Invalid Token", null)));
    }

    @GetMapping(
            path = "/api/get-profile-pict",
            produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> getProfilePict (@CookieValue(name = "token") String token) throws JsonProcessingException {
        try {
            byte[] fileData = userService.getProfilePicture(token);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(fileData);
        } catch (NullPointerException e) {
            return ResponseEntity.status(404).body(objectMapper.writeValueAsString(new WebResponse<>(false, "User Not Found", null)));
        }
    }

    @GetMapping (
            path = "/api/is-admin",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> isAdmin (@CookieValue(name = "token") String token) throws JsonProcessingException {
        if (userService.isAdmin(token)) {
            return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "admin", true)));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper.writeValueAsString(new WebResponse<>(false, "not admin", false)));
    }

    @PostMapping(
            path = "/api/edit-profile",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> editProfile (@CookieValue(name = "token") String token, @RequestBody EditProfileRequestModel requestModel) throws JsonProcessingException {
        if (userService.editProfile(token, requestModel)) {
            return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", null)));
        }
        return ResponseEntity.status(400).body(objectMapper.writeValueAsString(new WebResponse<>(false, "fail to edit profile", null)));
    }

    @PostMapping(
            path = "/api/edit-profile-by-email",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> editProfileByEmail (@CookieValue(name = "token") String token, @RequestParam(name = "email") String email, @RequestBody EditProfileRequestModel requestModel) throws JsonProcessingException {
        if (userService.editProfile(token, email, requestModel)) {
            return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", null)));
        }
        return ResponseEntity.status(400).body(objectMapper.writeValueAsString(new WebResponse<>(false, "fail to edit profile", null)));
    }

    @GetMapping (
            path = "/api/get-user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getUser (@CookieValue(name = "token") String token) throws JsonProcessingException {
        User user = userRepository.findBySessionToken(token);

        if (user != null) {
            GetUserResponseModel responseModel = new GetUserResponseModel();
            responseModel.setEmail(user.getEmail());
            responseModel.setFirstName(user.getFirstName());
            responseModel.setLastName(user.getLastName());
            responseModel.setAdmin(user.getAdmin().toString());

            return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", responseModel)));
        }
        return ResponseEntity.status(404).body(objectMapper.writeValueAsString(new WebResponse<>(false, "fail to get user", null)));
    }

    @GetMapping (
            path = "/api/get-user-by-email",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getUserByEmail (@CookieValue(name = "token") String token, @RequestParam(name = "email") String email) throws JsonProcessingException {
        User user = userRepository.findBySessionToken(token);
        User user2 = userRepository.findById(email).orElse(null);

        if (user != null && user2 != null && userService.isSessionTokenActive(user) && userService.isAdmin(user)) {
            GetUserResponseModel responseModel = new GetUserResponseModel();
            responseModel.setEmail(user2.getEmail());
            responseModel.setFirstName(user2.getFirstName());
            responseModel.setLastName(user2.getLastName());
            responseModel.setAdmin(user2.getAdmin().toString());

            return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", responseModel)));
        }
        return ResponseEntity.status(404).body(objectMapper.writeValueAsString(new WebResponse<>(false, "fail to get user", null)));
    }

    @GetMapping(
            path = "/api/get-all-users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getAllUsers (@CookieValue(name = "token") String token) throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", userService.getAllUsers(token))));
    }

    @DeleteMapping(
            path = "/api/delete-all-users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteAllUsers (@RequestHeader(name = "pw") String pw) throws JsonProcessingException {
        userService.deleteAllUsers(pw);

        return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, null, null)));
    }
}
